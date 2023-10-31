/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.invoice.impl;

import de.hybris.platform.commercefacades.invoice.InvoiceFacade;
import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.invoice.strategies.InvoiceStrategy;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.enums.ExternalSystemId;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;


/**
 * Default implementation of {@link InvoiceFacade}
 */
public class InvoiceFacadeImpl implements InvoiceFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(InvoiceFacadeImpl.class);
	private static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with guid [%s] not found for current user in current BaseStore";
	private Map<ExternalSystemId, InvoiceStrategy> handlers;
	private BaseStoreService baseStoreService;
	private UserService userService;
	private CustomerAccountService customerAccountService;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	private ModelService modelService;

	@Override
	public SearchPageData<SAPInvoiceData> getInvoices(final String orderCode,
			final SearchPageData<SAPInvoiceData> searchPageDataInput,
			final String addPaginationField)
	{
		final SearchPageData<SAPInvoiceData> invoicesList = new SearchPageData<>();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		if (!isSapInvoiceEnabled(baseStoreModel))
		{
			LOG.debug("SAP Invoice is disabled for [{}] basetore", baseStoreModel);
			return createEmptyInvoiceList(searchPageDataInput, invoicesList);

		}

		final OrderModel orderModel = getUserOrder(orderCode, baseStoreModel);

		final List<SAPInvoiceData> invoiceList = new ArrayList<>();
		final Collection<InvoiceStrategy> strategies = handlers.values();
		for (final InvoiceStrategy strategy : strategies)
		{
			invoiceList.addAll(strategy.getInvoices(orderModel));
		}

		if (invoiceList.isEmpty())
		{
			return createEmptyInvoiceList(searchPageDataInput, invoicesList);

		}

		if (searchPageDataInput.getSorts() != null && !searchPageDataInput.getSorts().isEmpty())
		{
			sortInvoices(searchPageDataInput.getSorts(), invoiceList);
			invoicesList.setSorts(searchPageDataInput.getSorts());
		}

		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(invoiceList.size());
		pagination.setCurrentPage(searchPageDataInput.getPagination().getCurrentPage());
		pagination.setPageSize(searchPageDataInput.getPagination().getPageSize());
		final int finalPage = (invoiceList.size() % pagination.getPageSize()) > 0 ? 1 : 0;
		pagination.setNumberOfPages(invoiceList.size() / pagination.getPageSize() + finalPage);
		final List<SAPInvoiceData> pagedInvoiceList = getPage(invoiceList, searchPageDataInput.getPagination().getCurrentPage(),
				searchPageDataInput.getPagination().getPageSize());

		invoicesList.setResults(pagedInvoiceList);
		invoicesList.setPagination(pagination);

		return invoicesList;
	}


	@Override
	public byte[] getInvoiceBinary(final String orderCode, final String invoiceId, final String externalSystemId)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		if (!isSapInvoiceEnabled(baseStoreModel))
		{
			LOG.debug("SAP Invoice is disabled for [{}] basetore", baseStoreModel);
			return new byte[0];
		}

		final OrderModel orderModel = getUserOrder(orderCode, baseStoreModel);

		final String trimmedExternalSystemId = StringUtils.trimToNull(externalSystemId);
		final InvoiceStrategy invoiceStrategy = handlers
				.get(trimmedExternalSystemId == null ? null : ExternalSystemId.valueOf(trimmedExternalSystemId));
		if (invoiceStrategy != null)
		{
			return invoiceStrategy.getInvoiceBinary(orderModel, invoiceId);
		}

		return new byte[0];
	}

	/**
	 * @param baseStoreModel
	 * @return
	 */
	private boolean isSapInvoiceEnabled(final BaseStoreModel baseStoreModel)
	{
		Object sapInvoiceEnabled = null;
		try
		{
			sapInvoiceEnabled = getModelService().getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED);
		}
		catch (final AttributeNotSupportedException e)
		{
			LOG.debug("SAP invoice feature is not enabled, please update the system", e);
			return false;
		}
		return (boolean) sapInvoiceEnabled;
	}


	protected OrderModel getUserOrder(final String orderCode, final BaseStoreModel baseStoreModel)
	{
		if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			return getCustomerAccountService().getOrderDetailsForGUID(orderCode, baseStoreModel);
		}
		else
		{
			try
			{
				final OrderModel orderModel = getCustomerAccountService().getOrderForCode(
						(CustomerModel) getUserService().getCurrentUser(), orderCode,
						baseStoreModel);
				if (orderModel == null)
				{
					throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, orderCode));
				}
				return orderModel;
			}
			catch (final ModelNotFoundException e)
			{
				throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, orderCode));
			}
		}
	}


	protected SearchPageData<SAPInvoiceData> createEmptyInvoiceList(final SearchPageData<SAPInvoiceData> searchPageDataInput,
			final SearchPageData<SAPInvoiceData> invoicesList)
	{
		invoicesList.setResults(Collections.emptyList());
		invoicesList.setPagination(createEmptyPagination());
		invoicesList.setSorts(searchPageDataInput.getSorts());

		return invoicesList;
	}


	/**
	 * @param sorts
	 * @param invoiceList
	 */
	protected void sortInvoices(final List<SortData> sorts, final List<SAPInvoiceData> invoiceList)
	{
		final SortData selectedSort = sorts.get(0);
		if (selectedSort != null)
		{
			sortList(invoiceList, selectedSort.getCode(), selectedSort.isAsc());
		}

	}

	protected void sortList(final List<SAPInvoiceData> invoiceList, final String attributeName, final boolean ascending)
	{
		Collections.sort(invoiceList, (final SAPInvoiceData invoice1, final SAPInvoiceData invoice2) -> {
			try
			{
				return sortObjects(attributeName, ascending, invoice1, invoice2);
			}
			catch (final IllegalStateException | IllegalArgumentException e)
			{
				throw new IllegalArgumentException("Invalid attribute name or type", e);
			}
		});
	}


	protected int sortObjects(final String attributeName, final boolean ascending, final SAPInvoiceData invoice1,
			final SAPInvoiceData invoice2)
	{
		final Field field = ReflectionUtils.findField(SAPInvoiceData.class, attributeName);
		if (field == null)
		{
			return 0;
		}
		ReflectionUtils.makeAccessible(field);

		final Object value1 = ReflectionUtils.getField(field, invoice1);
		final Object value2 = ReflectionUtils.getField(field, invoice2);
		if (value1 == null || value2 == null || value1.getClass() != value2.getClass())
		{
			return 0;
		}
		return switch (value1.getClass().getSimpleName())
		{
			case "String" -> compareStrings((String) value1, (String) value2, ascending);
			case "Date" -> compareDates((Date) value1, (Date) value2, ascending);
			case "Double" -> compareDoubles((Double) value1, (Double) value2, ascending);
			case "Integer" -> compareIntegers((Integer) value1, (Integer) value2, ascending);
			case "PriceData" -> comparePriceData((PriceData) value1, (PriceData) value2, ascending);
			default -> throw new IllegalArgumentException("Unsupported attribute type: " + value1.getClass());
		};
	}

	protected static int compareStrings(final String str1, final String str2, final boolean ascending)
	{
		final int result = str1.compareTo(str2);
		return ascending ? result : -result;
	}

	protected static int compareDates(final Date date1, final Date date2, final boolean ascending)
	{
		final int result = date1.compareTo(date2);
		return ascending ? result : -result;
	}

	protected static int compareDoubles(final Double num1, final Double num2, final boolean ascending)
	{
		final int result = Double.compare(num1, num2);
		return ascending ? result : -result;
	}

	protected static int compareIntegers(final Integer num1, final Integer num2, final boolean ascending)
	{
		final int result = Integer.compare(num1, num2);
		return ascending ? result : -result;
	}

	private int comparePriceData(final PriceData priceData1, final PriceData priceData2, final boolean ascending)
	{
		final BigDecimal value1 = priceData1.getValue();
		final BigDecimal value2 = priceData2.getValue();
		final int result = value1.compareTo(value2);
		return ascending ? result : -result;
	}


	protected PaginationData createEmptyPagination()
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(0);
		paginationData.setNumberOfPages(0);
		paginationData.setPageSize(1);
		paginationData.setTotalNumberOfResults(0);
		return paginationData;
	}





	protected <T> List<T> getPage(final List<T> sourceList, final int page, final int pageSize)
	{
		if (pageSize <= 0 || page < 0)
		{
			throw new IllegalArgumentException("invalid page size: " + pageSize);
		}

		final int fromIndex = page * pageSize;
		if (sourceList == null || sourceList.size() <= fromIndex)
		{
			return Collections.emptyList();
		}

		return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
	}


	/**
	 * @param handlers
	 *           the handlers to set
	 */
	public void setHandlers(final Map<ExternalSystemId, InvoiceStrategy> handlers)
	{
		this.handlers = handlers;
	}

	public void registerHandler(final ExternalSystemId externalSystemId, final InvoiceStrategy handler)
	{
		handlers.put(externalSystemId, handler);
	}

	public void removeHandler(final ExternalSystemId externalSystemId)
	{
		handlers.remove(externalSystemId);
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	public CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}


	public ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}



}
