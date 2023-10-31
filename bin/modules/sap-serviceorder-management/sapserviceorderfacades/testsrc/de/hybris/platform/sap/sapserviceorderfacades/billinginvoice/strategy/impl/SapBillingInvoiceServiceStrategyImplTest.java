/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.billinginvoice.strategy.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderType ;

import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.sap.sapserviceorder.billlinginvoice.service.SapServiceOrderBillingInvoiceService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;


/**
 *
 */
@UnitTest
public class SapBillingInvoiceServiceStrategyImplTest
{

	@InjectMocks
	SapBillingInvoiceServiceStrategyImpl sapBillingInvoiceServiceStrategyImpl;

	@Mock
	private SapServiceOrderBillingInvoiceService sapServiceOrderBillingInvoiceService;

	@Mock
	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	@Mock
	SAPPlantLogSysOrgModel sapPlantLogSysOrg;

	@Mock
	SAPLogicalSystemModel sAPLogicalSystemModel;

	@Mock
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	@Mock
	private Converter<JsonObject, ExternalSystemBillingDocumentData> externalSystemBillingDocumentConverter;

	@Mock
	private PriceDataFactory priceFactory;
	
	@Mock
	private ConfigurationService configurationService;
	
	@Mock
	private Configuration configuration;
	

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sapBillingInvoiceServiceStrategyImpl.setPriceFactory(priceFactory);
		sapBillingInvoiceServiceStrategyImpl.setConfigurationService(configurationService);
	}

	@Test
	public void getBillingDocuments()
	{

		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("123");

		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setName("baseStore", Locale.ENGLISH);
		orderModel.setStore(baseStore);

		final JsonArray billingDocumentArray = new JsonArray();

		final JsonParser parser = new JsonParser();
		final JsonElement jsonElement = parser.parse(
				"{\"BillingDocument\":\"bill123\",\"CreationDate\":\"/Date(1562803200000)/\",\"TransactionCurrency\":\"USD\",\"NetAmount\":\"100.00\",\"TotalGrossAmount\":\"100.00\"}");
		billingDocumentArray.add(jsonElement);

		final List<ExternalSystemBillingDocumentData> listBillingDocumentArray = new ArrayList<>();
		final ExternalSystemBillingDocumentData externalSystemBillingDocumentData = new ExternalSystemBillingDocumentData();
		externalSystemBillingDocumentData.setBillingDocumentId("bill123");

		listBillingDocumentArray.add(externalSystemBillingDocumentData);

		final Map<String, Object> mockParametersMap = new HashMap<>();
		mockParametersMap.put("billingDocumentArray", billingDocumentArray);


		final SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode("12345");
		sapOrder.setSapOrderType(SAPOrderType.SERVICE);
		sapOrder.setServiceOrderId("so12345");
		sapOrder.setOrder(orderModel);
		final Set<ConsignmentModel> consignments = new HashSet<>();
		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setCode("con123");

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("wh123");

		consignment.setWarehouse(warehouse);

		consignments.add(consignment);

		sapOrder.setConsignments(consignments);
		
		final String targetSuffixUrl = "/sap/id?$filter=DocumentReferenceID eq '{serviceOrderCode}'";

		Mockito.doReturn(configuration).when(configurationService).getConfiguration();

		Mockito.doReturn(targetSuffixUrl).when(configuration).getString(Mockito.any());
		
		given(sapBillingInvoiceUtils.s4DateStringToDate("/Date(1562803200000)/")).willReturn(new Date());

		given(externalSystemBillingDocumentConverter.convert(jsonElement.getAsJsonObject()))
				.willReturn(externalSystemBillingDocumentData);

		given(sapServiceOrderBillingInvoiceService.callS4forBillingDocuments(Mockito.any(), Mockito.any())).willReturn(mockParametersMap);

		final List<ExternalSystemBillingDocumentData> billinDocArray = sapBillingInvoiceServiceStrategyImpl
				.getBillingDocuments(sapOrder);

		Assert.assertEquals(listBillingDocumentArray.get(0).getBillingDocumentId(), billinDocArray.get(0).getBillingDocumentId());


	}

}
