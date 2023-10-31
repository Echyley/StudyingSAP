/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.facades.impl;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;
import com.sap.sapcentralorderservices.services.CentralOrderService;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;

import java.util.Arrays;
import java.util.Comparator;

/**
 * DefaultCentralOrderFacade
 */
public class DefaultCentralOrderFacade extends DefaultOrderFacade
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCentralOrderFacade.class);
	private CentralOrderService centralOrderService;
	private Populator centralOrderListPopulator;
	private Map<String, String> orderListSortMap;
	private CoConfigurationService configurationService;



	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
			final OrderStatus... statuses)
	{
		final SearchPageData<OrderHistoryData> result;
		if (getConfigurationService().isCoActive())
		{
			result = new SearchPageData();
			try
			{
				final PaginationData pagination = new PaginationData();
				final List<SortData> sorts = getSorts(pageableData);
				final String sourceSystemId = getConfigurationService().getCoSourceSystemId();
				final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
				final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
				final ResponseEntity<CentralOrderListResponse[]> centralOrderListResponse = getCentralOrderService()
						.getCentalOrderList(currentCustomer, currentBaseStore, statuses, pageableData, sourceSystemId);
				if (centralOrderListResponse != null)
				{
					final List<OrderHistoryData> orderList = extractOrderHistoryFromCentralOrderResponse(centralOrderListResponse,
							pageableData, pagination);
					result.setPagination(pagination);

					result.setSorts(sorts);
					result.setResults(orderList);
				}
				else
				{
					LOG.warn(SapcentralorderfacadesConstants.ORDER_NOT_FOUND_FOR_USER);
				}

			}
			catch (final Exception e)
			{
				LOG.warn(String.format(SapcentralorderfacadesConstants.ORDER_NOT_FOUND_FOR_USER, e.getMessage()));
			}
		}
		else
		{
			result = super.getPagedOrderHistoryForStatuses(pageableData, statuses);
		}
		return result;
	}

	/**
	 * @param pagination
	 * @param pageableData
	 * @return
	 *
	 */
	private List<OrderHistoryData> extractOrderHistoryFromCentralOrderResponse(
			final ResponseEntity<CentralOrderListResponse[]> centralOrderListResponse, final PageableData pageableData,
			final PaginationData pagination)
	{
		final CentralOrderListResponse[] centralOrderList = centralOrderListResponse.getBody();
		// Sorting by pricing data manually
		if(!SapcentralorderfacadesConstants.DEFAULT_SORT_TYPE.contentEquals(pageableData.getSort()))
		{
			Arrays.sort(centralOrderList, new Comparator<CentralOrderListResponse>() {
				   public int compare(CentralOrderListResponse r1, CentralOrderListResponse r2) {
					      return r1.getPricingDate().compareTo(r2.getPricingDate());
					   }
					});
		}
		final List<OrderHistoryData> orderList = new ArrayList();
		final HttpHeaders httpHeaders = centralOrderListResponse.getHeaders();
		final Integer count = Integer
				.parseInt(Objects.requireNonNull(httpHeaders.getFirst(SapcentralorderfacadesConstants.ORDER_LIST_COUNT_HEADER)));

		pagination.setTotalNumberOfResults(count);
		pagination.setCurrentPage(pageableData.getCurrentPage());
		pagination.setPageSize(pageableData.getPageSize());
		final int finalPage = (count % pagination.getPageSize()) > 0 ? 1 : 0;
		pagination.setNumberOfPages(count / pagination.getPageSize() + finalPage);

		OrderHistoryData orderHistoryData;

		for (final CentralOrderListResponse centalOrder : centralOrderList)
		{
			orderHistoryData = new OrderHistoryData();
			getCentralOrderListPopulator().populate(centalOrder, orderHistoryData);
			orderList.add(orderHistoryData);
		}


		return orderList;

	}

	protected List<SortData> getSorts(final PageableData pageableData)
	{
		SortData sortData;
		final List<SortData> sorts = new ArrayList();
		for (final Map.Entry<String, String> entry : getOrderListSortMap().entrySet())
		{
			sortData = new SortData();
			sortData.setCode(entry.getKey());
			sorts.add(sortData);
		}

		final String sort = pageableData.getSort();
		if (sort == null || sort.isEmpty())
		{

			pageableData.setSort(SapcentralorderfacadesConstants.DEFAULT_SORT_TYPE);
			for (final SortData sortDataItem : sorts)
			{

				sortDataItem.setSelected(getOrderListSortMap().get(sortDataItem.getCode())
						.equals(SapcentralorderfacadesConstants.DEFAULT_SORT_TYPE));
			}
		}
		else
		{

			for (final SortData sortDataItem : sorts)
			{
				if (sortDataItem.getCode().equals(pageableData.getSort()))
				{
					pageableData.setSort(getOrderListSortMap().get(sortDataItem.getCode()));
					sortDataItem.setSelected(true);
				}
				else
				{
					sortDataItem.setSelected(false);
				}
			}

		}

		return sorts;

	}

	protected CentralOrderService getCentralOrderService()
	{
		return centralOrderService;
	}


	public void setCentralOrderService(final CentralOrderService centralOrderService)
	{
		this.centralOrderService = centralOrderService;
	}

	protected Map<String, String> getOrderListSortMap()
	{
		return orderListSortMap;
	}

	public void setOrderListSortMap(final Map<String, String> orderListSortMap)
	{
		this.orderListSortMap = orderListSortMap;
	}

	protected Populator getCentralOrderListPopulator()
	{
		return centralOrderListPopulator;
	}

	public void setCentralOrderListPopulator(final Populator centralOrderListPopulator)
	{
		this.centralOrderListPopulator = centralOrderListPopulator;
	}

	/**
	 * @return the configurationService
	 */
	public CoConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final CoConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}




}
