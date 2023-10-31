/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.invoice.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for {@link InvoiceFacadeImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class InvoiceFacadeImplTest
{
	private static final String INVALID = "INVALID";
	private static final String ORDER_CODE = "order123";
	private static final String INVOICE_ID = "123";
	private static final int NUMBER_OF_RESULTS = 2;
	private static final Integer PAGESIZE = 20;
	private static final Double PRICE = Double.valueOf(10.50);

	@InjectMocks
	private InvoiceFacadeImpl invoiceFacadeImpl;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Mock
	private InvoiceStrategy invoiceStrategy;

	@Mock
	private Map<ExternalSystemId, InvoiceStrategy> handlers;

	@Mock
	private Date referenceDate;

	private BaseStoreModel baseStoreModel;
	private CustomerModel customerModel;
	private OrderModel orderModel;
	private SearchPageData<SAPInvoiceData> searchPageDataInput;



	@Before
	public void setUp()
	{
        baseStoreModel = new BaseStoreModel();
        baseStoreModel.setSapInvoiceEnabled(true);
        customerModel = new CustomerModel();
        orderModel = new OrderModel();
		  orderModel.setCode(ORDER_CODE);
        searchPageDataInput = new SearchPageData<>();
		  final PaginationData paginationData = new PaginationData();
		  paginationData.setCurrentPage(0);
		  paginationData.setPageSize(PAGESIZE);
		  final SortData sort = new SortData();
		  sort.setCode("invoiceId");
		  sort.setAsc(true);
		  searchPageDataInput.setPagination(paginationData);
		  searchPageDataInput.setSorts(Collections.singletonList(sort));

    }

	 @Test
	 public void testGetInvoices()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);

		 final SAPInvoiceData sapInvoiceData1 = new SAPInvoiceData();
		 sapInvoiceData1.setInvoiceId(INVOICE_ID);
		 final SAPInvoiceData sapInvoiceData2 = new SAPInvoiceData();
		 sapInvoiceData2.setInvoiceId(INVOICE_ID);

		 final List<SAPInvoiceData> invoices = new ArrayList<>(Arrays.asList(sapInvoiceData1, sapInvoiceData2));

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(invoices);
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 verify(userService).getCurrentUser();
		 verify(customerAccountService).getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		 verify(invoiceStrategy).getInvoices(orderModel);
		 assertEquals(NUMBER_OF_RESULTS, actual.getResults().size());
		 assertEquals(sapInvoiceData1, actual.getResults().get(0));
	 }

	 @Test
	 public void testGetInvoicesBaseStoreDiabled()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.FALSE);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(Boolean.FALSE);
		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 assertEquals(0, actual.getResults().size());
		 assertEquals(0, actual.getPagination().getTotalNumberOfResults());
	 }


	 @Test
	 public void testGetInvoicesWithPriceSortParam()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 final SortData sort = new SortData();
		 sort.setCode("netAmount");
		 sort.setAsc(false);
		 searchPageDataInput.setSorts(Collections.singletonList(sort));
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);
		 final PriceData priceData = new PriceData();
		 priceData.setCurrencyIso(INVOICE_ID);
		 priceData.setValue(BigDecimal.valueOf(PRICE));
		 final SAPInvoiceData sapInvoiceData1 = new SAPInvoiceData();
		 sapInvoiceData1.setInvoiceId(INVOICE_ID);
		 sapInvoiceData1.setNetAmount(priceData);
		 final SAPInvoiceData sapInvoiceData2 = new SAPInvoiceData();
		 sapInvoiceData2.setInvoiceId(INVOICE_ID);
		 sapInvoiceData2.setNetAmount(priceData);

		 final List<SAPInvoiceData> invoices = new ArrayList<>(Arrays.asList(sapInvoiceData1, sapInvoiceData2));

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(invoices);
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 verify(userService).getCurrentUser();
		 verify(customerAccountService).getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		 verify(invoiceStrategy).getInvoices(orderModel);
		 assertEquals(NUMBER_OF_RESULTS, actual.getResults().size());
		 assertEquals(sapInvoiceData1, actual.getResults().get(0));

	 }



	 @Test(expected = IllegalArgumentException.class)
	 public void testGetInvoicesWithInvalidPageSize()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 final PaginationData paginationData = new PaginationData();
		 paginationData.setPageSize(PAGESIZE);
		 paginationData.setCurrentPage(-1);
		 final SortData sort = new SortData();
		 sort.setCode("invoiceDate");
		 sort.setAsc(false);
		 searchPageDataInput.setPagination(paginationData);
		 searchPageDataInput.setSorts(Collections.singletonList(sort));
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);
		 final SAPInvoiceData sapInvoiceData1 = new SAPInvoiceData();
		 sapInvoiceData1.setInvoiceId(INVOICE_ID);
		 sapInvoiceData1.setInvoiceDate(referenceDate);
		 final SAPInvoiceData sapInvoiceData2 = new SAPInvoiceData();
		 sapInvoiceData2.setInvoiceId(INVOICE_ID);
		 sapInvoiceData2.setInvoiceDate(referenceDate);

		 final List<SAPInvoiceData> invoices = new ArrayList<>(Arrays.asList(sapInvoiceData1, sapInvoiceData2));

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(invoices);
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

	 }

	 @Test(expected = UnknownIdentifierException.class)
	 public void testGetInvoicesForNonExistingOrder()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.FALSE);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(Boolean.TRUE);
		 invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");


	 }

	 @Test(expected = UnknownIdentifierException.class)
	 public void testGetInvoicesWithModelNotFound()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.FALSE);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(Boolean.TRUE);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel))
				 .thenThrow(ModelNotFoundException.class);

		 invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");


	 }

	 @Test
	 public void testGetInvoicesInvalidSortingParam()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 final SortData sort = new SortData();
		 sort.setCode(INVALID);
		 sort.setAsc(false);
		 searchPageDataInput.setSorts(Collections.singletonList(sort));
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);
		 final SAPInvoiceData sapInvoiceData1 = new SAPInvoiceData();
		 sapInvoiceData1.setInvoiceId(INVOICE_ID);
		 sapInvoiceData1.setInvoiceDate(referenceDate);
		 final SAPInvoiceData sapInvoiceData2 = new SAPInvoiceData();
		 sapInvoiceData2.setInvoiceId(INVOICE_ID);
		 sapInvoiceData2.setInvoiceDate(referenceDate);

		 final List<SAPInvoiceData> invoices = new ArrayList<>(Arrays.asList(sapInvoiceData1, sapInvoiceData2));

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(invoices);
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 verify(userService).getCurrentUser();
		 verify(customerAccountService).getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		 verify(invoiceStrategy).getInvoices(orderModel);
		 assertEquals(NUMBER_OF_RESULTS, actual.getResults().size());
		 assertEquals(sapInvoiceData1, actual.getResults().get(0));

	 }

	 @Test
	 public void testGetInvoicesInvalidSortFiled()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 final SortData sort = new SortData();
		 sort.setCode("netAmount");
		 sort.setAsc(false);
		 searchPageDataInput.setSorts(Collections.singletonList(sort));
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);
		 final SAPInvoiceData sapInvoiceData1 = new SAPInvoiceData();
		 sapInvoiceData1.setInvoiceId(INVOICE_ID);
		 sapInvoiceData1.setInvoiceDate(referenceDate);
		 final SAPInvoiceData sapInvoiceData2 = new SAPInvoiceData();
		 sapInvoiceData2.setInvoiceId(INVOICE_ID);
		 sapInvoiceData2.setInvoiceDate(referenceDate);

		 final List<SAPInvoiceData> invoices = new ArrayList<>(Arrays.asList(sapInvoiceData1, sapInvoiceData2));

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(invoices);
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 verify(userService).getCurrentUser();
		 verify(customerAccountService).getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		 verify(invoiceStrategy).getInvoices(orderModel);
		 assertEquals(NUMBER_OF_RESULTS, actual.getResults().size());
		 assertEquals(sapInvoiceData1, actual.getResults().get(0));

	 }

	 @Test
	 public void testGetInvoicesWithEmptyResult()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(Collections.emptyList());
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 verify(userService).getCurrentUser();
		 verify(customerAccountService).getOrderForCode(customerModel, ORDER_CODE, baseStoreModel);
		 verify(invoiceStrategy).getInvoices(orderModel);
		 assertEquals(0, actual.getResults().size());
		 assertEquals(0, actual.getPagination().getTotalNumberOfResults());
	 }

	 @Test
	 public void testGetInvoicesWithEmptyResultAnonymousCheckout()
	 {
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(Boolean.TRUE);
		 when(customerAccountService.getOrderDetailsForGUID(ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 invoiceStrategy = mock(InvoiceStrategy.class);

		 when(invoiceStrategy.getInvoices(orderModel)).thenReturn(Collections.emptyList());
		 when(handlers.values()).thenReturn(Collections.singletonList(invoiceStrategy));

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 verify(customerAccountService).getOrderDetailsForGUID(ORDER_CODE, baseStoreModel);
		 verify(invoiceStrategy).getInvoices(orderModel);
		 assertEquals(0, actual.getResults().size());
		 assertEquals(0, actual.getPagination().getTotalNumberOfResults());
	 }

	 @Test
	 public void testGetInvoicesWithException()
	 {
		 baseStoreModel = new BaseStoreModel();
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED))
				 .thenThrow(AttributeNotSupportedException.class);

		 final SearchPageData<SAPInvoiceData> actual = invoiceFacadeImpl.getInvoices(ORDER_CODE, searchPageDataInput, "");

		 verify(baseStoreService).getCurrentBaseStore();
		 assertEquals(0, actual.getResults().size());
		 assertEquals(0, actual.getPagination().getTotalNumberOfResults());
	 }

	 @Test
	 public void testGetInvoiceBinaryWithException()
	 {
		 final BaseStoreModel mockBaseStore = new BaseStoreModel();
		 mockBaseStore.setSapInvoiceEnabled(Boolean.FALSE);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStore);
		 when(modelService.getAttributeValue(mockBaseStore, BaseStoreModel.SAPINVOICEENABLED))
				 .thenThrow(AttributeNotSupportedException.class);

		 final byte[] result = invoiceFacadeImpl.getInvoiceBinary(ORDER_CODE, INVOICE_ID, "externalSystemId");

		 assertEquals(0, result.length);
	 }

	 @Test
	 public void testGetInvoiceBinaryUnknownExternalSystemId()
	 {
		 final BaseStoreModel mockBaseStore = new BaseStoreModel();
		 mockBaseStore.setSapInvoiceEnabled(true);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStore);
		 when(modelService.getAttributeValue(mockBaseStore, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, mockBaseStore)).thenReturn(orderModel);

		 final byte[] result = invoiceFacadeImpl.getInvoiceBinary(ORDER_CODE, INVOICE_ID, "unknownExternalSystemId");

		 assertEquals(0, result.length);
	 }

	 @Test
	 public void testGetInvoiceBinaryExternalSystemIdNotFound()
	 {
		 final BaseStoreModel mockBaseStore = new BaseStoreModel();
		 mockBaseStore.setSapInvoiceEnabled(true);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStore);
		 when(modelService.getAttributeValue(mockBaseStore, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, mockBaseStore)).thenReturn(orderModel);

		 final ExternalSystemId mockExternalSystemId = ExternalSystemId.S4SALES;
		 final InvoiceStrategy mockInvoiceStrategy = new InvoiceStrategy()
		 {
			 @Override
			 public byte[] getInvoiceBinary(final OrderModel orderModel, final String invoiceId)
			 {
				 return new byte[]
				 { 1, 2, 3 };
			 }

			 @Override
			 public List<SAPInvoiceData> getInvoices(final OrderModel orderModel)
			 {
				 return Collections.emptyList();
			 }
		 };
		 invoiceFacadeImpl.registerHandler(mockExternalSystemId, mockInvoiceStrategy);
		 final byte[] result = invoiceFacadeImpl.getInvoiceBinary(ORDER_CODE, INVOICE_ID, mockExternalSystemId.toString());
		 invoiceFacadeImpl.removeHandler(mockExternalSystemId);
		 assertEquals(0, result.length);
	 }

	 @Test
	 public void testGetInvoiceBinaryExternalSystemIdFound()
	 {

		 final String externalSystemId = "S4HANA";
		 baseStoreModel = new BaseStoreModel();
		 baseStoreModel.setSapInvoiceEnabled(Boolean.TRUE);
		 customerModel = new CustomerModel();
		 orderModel = new OrderModel();
		 orderModel.setCode(ORDER_CODE);
		 orderModel.setUser(customerModel);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		 when(modelService.getAttributeValue(baseStoreModel, BaseStoreModel.SAPINVOICEENABLED)).thenReturn(true);
		 when(userService.getCurrentUser()).thenReturn(customerModel);
		 when(customerAccountService.getOrderForCode(customerModel, ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		 when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);

		 invoiceStrategy = mock(InvoiceStrategy.class);
		 final byte[] expectedInvoiceBinary = new byte[0];

		 when(invoiceStrategy.getInvoiceBinary(orderModel, INVOICE_ID)).thenReturn(expectedInvoiceBinary);

		 final ExternalSystemId expectedExternalSystemId = ExternalSystemId.valueOf(externalSystemId);

		 final Map<ExternalSystemId, InvoiceStrategy> expectedHandlers = new HashMap<>();
		 expectedHandlers.put(expectedExternalSystemId, invoiceStrategy);

		 invoiceFacadeImpl.setHandlers(expectedHandlers);

		 final byte[] actualInvoiceBinary = invoiceFacadeImpl.getInvoiceBinary(ORDER_CODE, INVOICE_ID, externalSystemId);

		 assertEquals(expectedInvoiceBinary, actualInvoiceBinary);
	 }

 }
