/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.billinginvoice.strategy.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilder;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMOutboundService;
import de.hybris.platform.saps4omservices.dto.BillingDocument;
import de.hybris.platform.saps4omservices.dto.SAPS4OMBillingData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMBillingResponseData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMBillingResponseDataResults;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class SapBillingInvoiceSalesOrderStrategyImplTest {
	
	@InjectMocks
	SapBillingInvoiceSalesOrderStrategyImpl sapBillingInvoiceSalesOrderStrategyImpl;
	
	@Mock
	SapS4OMOutboundService defaultSapS4OMOutboundService;
	
	@Mock
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;
	
	@Mock
	private PriceDataFactory priceFactory;
	
	@Mock
	private SapS4OMOrderFilterBuilder sapS4OMOrderFilterBuilder;
	
	@Mock
	private UserService userService;
	
	@Mock
	private UserModel userModel;
	
	@Mock
	private SAPOrderModel sapOrderModel;
	
	@Mock
	private OrderModel mockedOrderModel;
	
	@Mock
	private CustomerModel customer = mock(B2BCustomerModel.class);
	
	private SAPS4OMBillingData billingData;
	
	private  SAPOrderModel sapOrder;
	
	
	
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		sapBillingInvoiceSalesOrderStrategyImpl.setPriceFactory(priceFactory);
		sapBillingInvoiceSalesOrderStrategyImpl.setSapBillingInvoiceUtils(sapBillingInvoiceUtils);
		sapBillingInvoiceSalesOrderStrategyImpl.setSapS4OMOrderFilterBuilder(sapS4OMOrderFilterBuilder);
		sapBillingInvoiceSalesOrderStrategyImpl.setUserService(userService);
		SAPS4OMBillingResponseData billingDoc = new SAPS4OMBillingResponseData();
		BillingDocument toBillingDocument = new BillingDocument();
		toBillingDocument.setTotalNetAmount("18");
		toBillingDocument.setTaxAmount("0");
	    billingDoc.setNetAmount("18");	
	    billingDoc.setTaxAmount("0");
	    billingDoc.setBillingDocument("90000047");
	    billingDoc.setTransactionCurrency("USD");
	    billingDoc.setCreationDate("12/03/2022");
	    billingDoc.setToBillingDocument(toBillingDocument);
	    List<SAPS4OMBillingResponseData> responseData = new ArrayList<>();
	    responseData.add(billingDoc);
	    SAPS4OMBillingResponseDataResults res= new SAPS4OMBillingResponseDataResults() ;
	    res.setResults(responseData);
	    
	   billingData = new SAPS4OMBillingData();
		billingData.setResults(res);
	    
	    final OrderModel orderModel = new OrderModel();
		orderModel.setCode("123");

		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setName("baseStore", Locale.ENGLISH);
		orderModel.setStore(baseStore);
		sapOrder = new SAPOrderModel();
		sapOrder.setCode("12345");
		sapOrder.setSapOrderType(SAPOrderType.SALES_SYNCHRONOUS);
		sapOrder.setOrder(orderModel);
		final Set<ConsignmentModel> consignments = new HashSet<>();
		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setCode("con123");

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("wh123");

		consignment.setWarehouse(warehouse);

		consignments.add(consignment);

		sapOrder.setConsignments(consignments);
	    
	}
	
	@Test
	public void getBillingDocuments() {

		
		 Map<String,List<FilterData>> filterData = new HashMap<>();
		 
		when(sapS4OMOrderFilterBuilder.getBillingDetailFilters("123")).thenReturn(filterData);
		
		when(
				defaultSapS4OMOutboundService.fetchBillingDocumentsfromS4(anyString(), anyString(), anyMap(), anyString()))
				.thenReturn(billingData);
	
		when(priceFactory.create(PriceDataType.BUY, new BigDecimal(18), "USD")).thenReturn(new PriceData());
	
		final List<ExternalSystemBillingDocumentData> billinDocArray = sapBillingInvoiceSalesOrderStrategyImpl.getBillingDocuments(sapOrder);
        Assert.assertEquals("90000047", billinDocArray.get(0).getBillingDocumentId());
	
	}
	@Test
	public void getBillingDocumentsExpectedException() {
		ResourceNotFoundException e = new ResourceNotFoundException("Resource not found");
		when(
				defaultSapS4OMOutboundService.fetchBillingDocumentsfromS4(anyString(), anyString(), anyMap(), anyString()))
				.thenThrow(e);
		final List<ExternalSystemBillingDocumentData> billinDocArray = sapBillingInvoiceSalesOrderStrategyImpl.getBillingDocuments(sapOrder);
		Assert.assertTrue(billinDocArray.isEmpty());
	}
	
	@Test
	public void getPDFData() throws SapBillingInvoiceUserException {
		when(userService.getCurrentUser()).thenReturn(customer);
		when (sapOrderModel.getOrder()).thenReturn(mockedOrderModel);
		when(mockedOrderModel.getUser()).thenReturn(customer);
		PK pk = null;
		when(customer.getPk()).thenReturn(pk);
		when(sapOrderModel.getOrder()).thenReturn(mockedOrderModel);
		
		when(defaultSapS4OMOutboundService.fetchPDFData(anyString(), anyString(), anyMap(), anyString(), any())).thenReturn(Base64.decodeBase64("hello"));
		byte[] result = sapBillingInvoiceSalesOrderStrategyImpl.getPDFData(sapOrderModel, "90000047");
		Assert.assertNotNull(result);
 	}

}
