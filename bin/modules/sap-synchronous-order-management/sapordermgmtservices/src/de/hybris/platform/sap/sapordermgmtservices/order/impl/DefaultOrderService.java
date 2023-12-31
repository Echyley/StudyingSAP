/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.order.impl;

import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchFilterImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolOrderFacade;
import de.hybris.platform.sap.sapordermgmtservices.order.OrderService;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * DefaultOrderService
 */
public class DefaultOrderService implements OrderService
{
	/**
	 *
	 */
	private static final String MSG_NOT_SUPPORTED = "Not supported in the context of SAP order management";
	private static final Logger LOG = Logger.getLogger(DefaultOrderService.class);

	private Converter<Order, OrderData> orderConverter;
	private Converter<SearchResult, OrderHistoryData> orderHistoryConverter;

	private BolOrderFacade bolOrderFacade;

	private SapPartnerService sapPartnerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.ordermgmtservices.order.OrderService#getOrderForCode(java.lang.String)
	 */
	@Override
	public OrderData getOrderForCode(final String code)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getOrderForCode called for: " + code);
		}
		final Order order = bolOrderFacade.getSavedOrder(code);

		final OrderData orderData = orderConverter.convert(order);
		final B2BPaymentTypeData paymentType = new B2BPaymentTypeData();
		paymentType.setCode(CheckoutPaymentType.ACCOUNT.getCode());
		paymentType.setDisplayName(CheckoutPaymentType.ACCOUNT.getCode());
		orderData.setPaymentType(paymentType);
		orderData.setUnconsignedEntries(orderData.getEntries());
		orderData.setAppliedOrderPromotions(Collections.<PromotionResultData> emptyList());
		return orderData;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.ordermgmtservices.order.OrderService#getOrderHistoryForStatuses(de.hybris.platform.
	 * commerceservices.search.pagedata.PageableData, de.hybris.platform.core.enums.OrderStatus[])
	 */
	@Override
	public List<OrderHistoryData> getOrderHistoryForStatuses(final OrderStatus... statuses)
	{
		//Set Filter for Backend Call
		final SearchFilter filter = new SearchFilterImpl();
		filter.setSoldToId(sapPartnerService.getCurrentSapCustomerId());

		//Set Dummy Paging Data
		final PaginationData pageableData = new PaginationData();
		pageableData.setCurrentPage(0);
		pageableData.setNumberOfPages(1);
		pageableData.setPageSize(bolOrderFacade.getSearchResultsTotalNumber().intValue());

		//Read the Order history with the Filter and the Paging/sort data
		final List<SearchResult> searchResult = bolOrderFacade.performSearch(filter, pageableData);
		final List<OrderHistoryData> orderHistoryList = new ArrayList<OrderHistoryData>();

		//Convert SAP format to Hybris format
		for (final SearchResult result : searchResult)
		{
			final OrderHistoryData orderHisory = orderHistoryConverter.convert(result);
			orderHistoryList.add(orderHisory);
		}
		return orderHistoryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.ordermgmtservices.order.OrderService#getPagedOrderHistory(de.hybris.platform.
	 * commerceservices .search.pagedata.PageableData, de.hybris.platform.core.enums.OrderStatus[])
	 */
	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
			final OrderStatus... statuses)
	{

		//Check if order or quotation if Quote call exception
		for (final OrderStatus status : statuses)
		{
			if (status == OrderStatus.APPROVED_QUOTE || status == OrderStatus.PENDING_QUOTE || status == OrderStatus.REJECTED_QUOTE)
			{
				throw new ApplicationBaseRuntimeException(MSG_NOT_SUPPORTED);
			}
		}


		//Set Filter for Backend Call
		final SearchFilter filter = new SearchFilterImpl();
		filter.setSoldToId(sapPartnerService.getCurrentSapCustomerId());


		final SearchPageData<OrderHistoryData> resultHybrisFormat = new SearchPageData<>();

		//Check if sort options are valid and set the Checked property for the Frontend
		final PageableData pagingData = pageableData;

		//Set PaginData
		final PaginationData pagination = new PaginationData();
		pagination.setCurrentPage(pagingData.getCurrentPage());
		pagination.setPageSize(pagingData.getPageSize());

		//Read the Order history with the Filter and the Paging/sort data
		final List<SearchResult> searchResult = bolOrderFacade.performSearch(filter, pagingData);
		final List<OrderHistoryData> orderHistoryList = new ArrayList<OrderHistoryData>();

		//Convert SAP format to Hybris format
		for (final SearchResult result : searchResult)
		{
			final OrderHistoryData orderHisory = orderHistoryConverter.convert(result);
			orderHistoryList.add(orderHisory);
		}

		//Get the number of Total results and calculate the number of pages needed
		final int totalNumber = bolOrderFacade.getSearchResultsTotalNumber().intValue();
		pagination.setTotalNumberOfResults(totalNumber);
		pagination.setNumberOfPages(calculateNumberOfPages(totalNumber, pagingData.getPageSize()));
		resultHybrisFormat.setSorts(bolOrderFacade.getSearchSort());
		resultHybrisFormat.setPagination(pagination);
		resultHybrisFormat.setResults(orderHistoryList);
		resultHybrisFormat.setDateRange(String.valueOf(bolOrderFacade.getDateRange()));

		return resultHybrisFormat;
	}



	/**
	 * Calculates number of pages for a search result
	 *
	 * @param totalNumberOfResults
	 *           Total number
	 * @param pageSize
	 *           Page size
	 * @return Number of pages needed
	 */
	protected int calculateNumberOfPages(final int totalNumberOfResults, final int pageSize)
	{
		final double totalNumber = totalNumberOfResults;
		final double page = pageSize;

		final double result = Math.ceil(totalNumber / page);

		return (int) result;

	}

	String addLeadingZerosOrderID(final String productId)
	{

		return addLeadingZeros(productId, "0000000000");
	}

	String addLeadingZeros(final String input, final String fillString)
	{

		if (input == null)
		{
			return "";
		}

		if (input.length() > fillString.length())
		{
			return input;
		}

		if (!StringUtils.isNumeric(input))
		{
			return input.trim();
		}
		return fillString.substring(input.length()) + input;
	}



	/**
	 * Order convertor for order model and data
	 * 
	 * @return the orderConverter
	 */
	public Converter<Order, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	/**
	 * @param orderConverter
	 *           the orderConverter to set
	 */
	public void setOrderConverter(final Converter<Order, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}


	/**
	 * Retrieves partner service
	 *
	 * @return Partner service
	 */
	public SapPartnerService getSapPartnerService()
	{
		return sapPartnerService;
	}


	/**
	 * Sets partner service from spring definition
	 *
	 * @param sapPartnerService
	 */
	public void setSapPartnerService(final SapPartnerService sapPartnerService)
	{
		this.sapPartnerService = sapPartnerService;
	}



	/**
	 * Retrieves order history converter
	 *
	 * @return Order history converter
	 */
	public Converter<SearchResult, OrderHistoryData> getOrderHistoryConverter()
	{
		return orderHistoryConverter;
	}



	/**
	 * Sets order history converter from spring definition
	 *
	 * @param orderHistoryConverter
	 */
	public void setOrderHistoryConverter(final Converter<SearchResult, OrderHistoryData> orderHistoryConverter)
	{
		this.orderHistoryConverter = orderHistoryConverter;
	}

	/**
	 * @return the bolOrderFacade
	 */
	public BolOrderFacade getBolOrderFacade()
	{
		return bolOrderFacade;
	}

	/**
	 * @param bolOrderFacade
	 *           the bolOrderFacade to set
	 */
	public void setBolOrderFacade(final BolOrderFacade bolOrderFacade)
	{
		this.bolOrderFacade = bolOrderFacade;
	}



	@Override
	public Item getItemFromOrder(final String orderCode, final String itemNumber)
	{
		final ItemList itemList = getBolOrderFacade().getSavedOrder(orderCode).getItemList();
		final Optional<Item> optionalItem = itemList//
				.stream()//
				.filter(item -> item.getNumberInt() == Integer.valueOf(itemNumber))//
				.findAny();
		if (!optionalItem.isPresent())
		{
			throw new IllegalStateException("Cannot find order item: " + orderCode + "/" + itemNumber);
		}
		final Item item = optionalItem.get();

		return item;
	}





}
