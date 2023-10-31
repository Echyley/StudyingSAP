/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilder;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMOutboundService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMOrderResult;
import de.hybris.platform.saps4omservices.dto.SAPS4OMOrders;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;


public class DefaultSapS4OMOrderService extends DefaultCustomerAccountService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMOrderService.class);
	
	private static final String DEFAULT_SORT_TYPE = "SalesOrderDate";
	
	private SapS4OMOutboundService sapS4OMOutboundService;
	private Converter<SAPS4OMResponseData, OrderModel> sapS4OrderConverter;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private Map<String, String> orderListSortMap;
	private SapS4OMOrderFilterBuilder sapS4OMOrderFilterBuilder;
	private SapS4OrderUtil sapS4OrderUtil;
	
	@Override
	public OrderModel getOrderForCode(final CustomerModel customerModel, final String code,
			final BaseStoreModel store) {
		LOG.debug("Entering: SapS4OMOrderServiceImpl.getOrderForCode(customerModel,code,store) method");
		if (!isSapS4OrderManagementEnabled()) {
			return super.getOrderForCode(customerModel, code, store);
		}
		SAPS4OMData sapS4omData;
		validateParameterNotNull(code, "Order code cannot be null");
		try {
			 sapS4omData = fetchOrderDetailsFromBackend(code);
		} catch (OutboundServiceException e) {
			if (!getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled()) {
				LOG.debug("Order Could not be found on backend and Synchronous order creation is disabled so fetch the order {} details from commerce", code);
				return super.getOrderForCode(customerModel, code, store);
			} else {
				LOG.debug("No order found in Back end for order code {}", code);
				throw new UnknownIdentifierException(e.getMessage());
			}
		}
		if (verifyCustomerOrder(customerModel, store, sapS4omData.getResult(), code)) {
			LOG.debug("Order is valid for the customer {} and code {}", customerModel.getCustomerID(), code);
			OrderModel order = getModelService().create(OrderModel.class);
			return getSapS4OrderConverter().convert(sapS4omData.getResult(), order);
		} else {
			LOG.debug("Order is invalid for the customer {} and code {}", customerModel.getCustomerID(), code);
			return null;
		}
	}
	
	@Override
	public OrderModel getOrderForCode(final String code, final BaseStoreModel store) {
		LOG.debug("Entering: SapS4OMOrderServiceImpl.getOrderForCode(code) method");
		if(!isSapS4OrderManagementEnabled()) {
			return super.getOrderForCode(code, store);
		} 
		validateParameterNotNull(code, "Order code cannot be null");
		
		SAPS4OMData sapS4omData = fetchOrderDetailsFromBackend(code);
		OrderModel order = getModelService().create(OrderModel.class);
		return getSapS4OrderConverter().convert(sapS4omData.getResult(), order);
	}
	
	@Override
	public SearchPageData<OrderModel> getOrderList(final CustomerModel customerModel, final BaseStoreModel store,
			final OrderStatus[] status, final PageableData pageableData) {
		LOG.debug("Entering: DefaultSapS4OMOrderService.getOrderList(customerModel,store,status) method");
		if(isSapS4OrderManagementEnabled()) {
			validateParameterNotNull(customerModel, "Customer model cannot be null");
			validateParameterNotNull(store, "Store must not be null");
			validateParameterNotNull(pageableData, "PageableData must not be null");
			String sortType = DEFAULT_SORT_TYPE;
			if(pageableData.getSort()!=null) {
				sortType = getS4omSortType(pageableData.getSort());
				LOG.debug("Sort type for order history {}", sortType);
			}
			Map<String,List<FilterData>> orderHistoryfilters = getSapS4OMOrderFilterBuilder().getOrderHistoryFilters(customerModel,status, pageableData,sortType);
			SAPS4OMOrders sapS4OMOrders = null;
			try {
				sapS4OMOrders = getSapS4OMOutboundService().fetchOrders(SapS4OrderUtil.S4_ORDER_DESTINATION, SapS4OrderUtil.S4_ORDER_DESTINATION_TARGET, orderHistoryfilters);
			} catch (OutboundServiceException e) {
				throw  new UnknownIdentifierException(e.getMessage());	
			}
			final SearchPageData<OrderModel> result;
			if (sapS4OMOrders != null &&  sapS4OMOrders.getSapS4OMOrderResult()!=null) {
				SAPS4OMOrderResult s4OrderHistoryData = sapS4OMOrders.getSapS4OMOrderResult();
				result = new SearchPageData<>();
				final Integer count = Integer.parseInt(s4OrderHistoryData.getOrderCount());
				
				final PaginationData pagination = new PaginationData();
				final List<SortData> sorts = getSorts(pageableData);
				final List<OrderModel> orderList = extractOrdersFromResponse(s4OrderHistoryData.getResults());
				
				pagination.setTotalNumberOfResults(count);
				pagination.setCurrentPage(pageableData.getCurrentPage());
				pagination.setPageSize(pageableData.getPageSize());
				pagination.setSort(getCommerceSortType(pageableData.getSort()));
				final int finalPage = (count % pagination.getPageSize()) > 0 ? 1 : 0;
				pagination.setNumberOfPages(count / pagination.getPageSize() + finalPage);
				
				
				result.setPagination(pagination);
				result.setSorts(sorts);
				result.setResults(orderList);
				LOG.debug("Total {} orders found for user with id {}", pagination.getTotalNumberOfResults(), customerModel.getCustomerID());
				
				return result;
			} else {
				LOG.warn("No Order found for current user");
			}
			return createEmptyResult();
		} else {
			return super.getOrderList(customerModel, store, status, pageableData);
		}
	}
	
	protected boolean isSapS4OrderManagementEnabled() {
		boolean isSapS4OrderManagementEnabled = getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled() || 
				getSapS4OrderManagementConfigService().isS4SynchronousOrderHistoryEnabled();
		
		LOG.debug("S4OM Synchronous Order Management enabled: {}", isSapS4OrderManagementEnabled);
		return isSapS4OrderManagementEnabled;
	}
	
	protected SAPS4OMData fetchOrderDetailsFromBackend(String code) {
		Map<String, List<FilterData>> orderDetailFilters = getSapS4OMOrderFilterBuilder().getOrderDetailFilters();
			return getSapS4OMOutboundService().fetchOrderDetails(SapS4OrderUtil.S4_ORDER_DESTINATION, SapS4OrderUtil.S4_ORDER_DESTINATION_TARGET, code, orderDetailFilters);
	}
	
	protected boolean verifyCustomerOrder(CustomerModel customerModel, BaseStoreModel store, SAPS4OMResponseData responseData, String code) {
		SAPConfigurationModel sapConfigModel = store.getSAPConfiguration();
		String soldToParty = getSapS4OrderUtil().getSoldToParty(customerModel);
		String salesOrg = sapConfigModel.getSapcommon_salesOrganization();
		String distChannel = sapConfigModel.getSapcommon_distributionChannel();
		String division = sapConfigModel.getSapcommon_division();
		
		boolean authorizedSoldToParty = Optional.ofNullable(responseData.getSoldToParty()).orElse("").equals(soldToParty);
		boolean authorizedSalesOrg = Optional.ofNullable(responseData.getSalesOrganization()).orElse("").equals(salesOrg);
		boolean authorizedDistChannel = Optional.ofNullable(responseData.getDistributionChannel()).orElse("").equals(distChannel);
		boolean authorizedDivision = Optional.ofNullable(responseData.getDivision()).orElse("").equals(division);
		if(!(authorizedSoldToParty && authorizedSalesOrg && authorizedDistChannel && authorizedDivision)) {
			LOG.warn("No Order found for user with code {} and sales org {}", code, salesOrg);
			return false;
		}
		return true;
	}
	
	protected List<OrderModel> extractOrdersFromResponse(List<SAPS4OMResponseData> s4OrderHistoryData) {
		final List<OrderModel> orderList = new ArrayList<>();
		for (final SAPS4OMResponseData s4omOrder : s4OrderHistoryData) {
			OrderModel order = getModelService().create(OrderModel.class);
			getSapS4OrderConverter().convert(s4omOrder, order);
			orderList.add(order);
		}
		return orderList;
	}
	
	protected SearchPageData<OrderModel> createEmptyResult() {
		final SearchPageData<OrderModel> searchResult;
		searchResult = new SearchPageData<>();
		searchResult.setResults(Collections.emptyList());
		searchResult.setPagination(createEmptyPagination());
		return searchResult;
	}
	
	protected PaginationData createEmptyPagination() {
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(0);
		paginationData.setNumberOfPages(0);
		paginationData.setPageSize(1);
		paginationData.setTotalNumberOfResults(0);
		return paginationData;
	}
	
	protected String getCommerceSortType(String s4omSortType) {
		for (final Map.Entry<String, String> entry : getOrderListSortMap().entrySet()) {
			if (Objects.equals(s4omSortType, entry.getValue())) {
				LOG.debug("Commerce sort type {}",entry.getValue());
	            return entry.getKey();
	        }
		}
		return null;
	}
	
	protected String getS4omSortType(String commerceSortType) {
		for (final Map.Entry<String, String> entry : getOrderListSortMap().entrySet()) {
			if (Objects.equals(commerceSortType, entry.getKey())) {
				LOG.debug("S4OM sort type {}",entry.getKey());
	            return entry.getValue();
	        }
		}
		return null;
	}
	
	protected List<SortData> getSorts(final PageableData pageableData) {
		SortData sortData;
		final List<SortData> sorts = new ArrayList<>();
		for (final Map.Entry<String, String> entry : getOrderListSortMap().entrySet()) {
			sortData = new SortData();
			sortData.setCode(entry.getKey());
			sorts.add(sortData);
		}

		final String sort = pageableData.getSort();
		if (sort == null || sort.isEmpty()) {
			LOG.debug("Sort is empty. Falling back to default sort type.");
			pageableData.setSort(DEFAULT_SORT_TYPE);
			for (final SortData sortDataItem : sorts) {
				sortDataItem.setSelected(getOrderListSortMap().get(sortDataItem.getCode())
						.equals(DEFAULT_SORT_TYPE));
			}
		} else {
			LOG.debug("Sort is not empty.");
			for (final SortData sortDataItem : sorts) {
				if (sortDataItem.getCode().equals(pageableData.getSort())) {
					pageableData.setSort(getOrderListSortMap().get(sortDataItem.getCode()));
					sortDataItem.setSelected(true);
				} else {
					sortDataItem.setSelected(false);
				}
			}
		}
		return sorts;
	}

	public Converter<SAPS4OMResponseData, OrderModel> getSapS4OrderConverter() {
		return sapS4OrderConverter;
	}

	public void setSapS4OrderConverter(Converter<SAPS4OMResponseData, OrderModel> sapS4OrderConverter) {
		this.sapS4OrderConverter = sapS4OrderConverter;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

	public SapS4OMOutboundService getSapS4OMOutboundService() {
		return sapS4OMOutboundService;
	}

	public void setSapS4OMOutboundService(SapS4OMOutboundService sapS4OMOutboundService) {
		this.sapS4OMOutboundService = sapS4OMOutboundService;
	}

	protected Map<String, String> getOrderListSortMap() {
		return orderListSortMap;
	}

	public void setOrderListSortMap(final Map<String, String> orderListSortMap) {
		this.orderListSortMap = orderListSortMap;
	}

	public SapS4OMOrderFilterBuilder getSapS4OMOrderFilterBuilder() {
		return sapS4OMOrderFilterBuilder;
	}

	public void setSapS4OMOrderFilterBuilder(SapS4OMOrderFilterBuilder sapS4OMOrderFilterBuilder) {
		this.sapS4OMOrderFilterBuilder = sapS4OMOrderFilterBuilder;
	}
	
	public SapS4OrderUtil getSapS4OrderUtil() {
		return sapS4OrderUtil;
	}

	public void setSapS4OrderUtil(SapS4OrderUtil sapS4OrderUtil) {
		this.sapS4OrderUtil = sapS4OrderUtil;
	}
}
