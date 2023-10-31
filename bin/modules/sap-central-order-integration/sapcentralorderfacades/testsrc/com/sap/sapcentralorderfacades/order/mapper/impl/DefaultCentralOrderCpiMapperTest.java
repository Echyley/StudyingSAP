/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper.impl;

import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCreditCardPayment;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderAddress;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderPriceComponent;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiPartnerRole;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.sapcentralorderfacades.order.populator.SapCpiOrderOutboundAdditionalAttributePopulator;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;



/**
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderCpiMapperTest
{


	private final SapCpiOrderConversionService sapCpiOrderConversionService = mock(SapCpiOrderConversionService.class);


	private final OrderModel orderModel = mock(OrderModel.class);

	private final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel = new SAPCpiOutboundOrderModel();


	private final SapCpiOrder sapCpiOrder = mock(SapCpiOrder.class);


	private final SapCpiOrderOutboundAdditionalAttributePopulator sapCpiOrderOutboundAdditionalAttributePopulator = mock(
			SapCpiOrderOutboundAdditionalAttributePopulator.class);


	private final SapCpiConfig sapCpiConfig = mock(SapCpiConfig.class);


	private final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);


	private final SAPConfigurationModel sapConfigModel = mock(SAPConfigurationModel.class);


	private final SapCpiTargetSystem sapCpiTargetSystem = mock(SapCpiTargetSystem.class);


	private final CoConfigurationService configurationService = mock(CoConfigurationService.class);


	private final DefaultCentralOrderCpiMapper defaultCentralOrderCpiMapper = new DefaultCentralOrderCpiMapper();

	String orderId = "orderId";
	String baseStoreUid = "baseStoreUid";
	String creationDate = "creationDate";
	String currencyIsoCode = "currencyIsoCode";
	String paymentMode = "paymentMode";
	String deliveryMode = "deliveryMode";
	String channel = "channel";
	String purchaseOrderNumber = "purchaseOrderNumber";
	String transactionType = "transactionType";
	String salesOrganization = "salesOrganization";
	String distributionChannel = "distributionChannel";
	String division = "division";
	String shippingCondition = "shippingCondition";
	String senderName = "senderName";


	List<SapCpiOrderItem> sapCpiOrderItems = mock(List.class);


	List<SapCpiPartnerRole> sapCpiPartnerRoles = mock(List.class);;


	List<SapCpiOrderAddress> sapCpiOrderAddress = mock(List.class);;


	List<SapCpiOrderPriceComponent> sapCpiOrderPriceComponent = mock(List.class);;


	List<SapCpiCreditCardPayment> sapCpiCreditCardComponent = mock(List.class);;

	@Before
	public void setUp()
	{
		defaultCentralOrderCpiMapper.setConfigurationService(configurationService);
		defaultCentralOrderCpiMapper.setSapCpiOrderConversionService(sapCpiOrderConversionService);
		defaultCentralOrderCpiMapper
				.setSapCpiOrderOutboundAdditionalAttributePopulator(sapCpiOrderOutboundAdditionalAttributePopulator);
		Mockito.lenient().when(sapCpiOrder.getOrderId()).thenReturn(orderId);
		Mockito.lenient().when(sapCpiOrder.getBaseStoreUid()).thenReturn(baseStoreUid);
		Mockito.lenient().when(sapCpiOrder.getCreationDate()).thenReturn(creationDate);
		Mockito.lenient().when(sapCpiOrder.getCurrencyIsoCode()).thenReturn(currencyIsoCode);
		Mockito.lenient().when(sapCpiOrder.getPaymentMode()).thenReturn(paymentMode);
		Mockito.lenient().when(sapCpiOrder.getDeliveryMode()).thenReturn(deliveryMode);
		Mockito.lenient().when(sapCpiOrder.getChannel()).thenReturn(channel);
		Mockito.lenient().when(sapCpiOrder.getPurchaseOrderNumber()).thenReturn(purchaseOrderNumber);
		Mockito.lenient().when(sapCpiOrder.getTransactionType()).thenReturn(transactionType);
		Mockito.lenient().when(sapCpiOrder.getSalesOrganization()).thenReturn(salesOrganization);
		Mockito.lenient().when(sapCpiOrder.getDistributionChannel()).thenReturn(distributionChannel);
		Mockito.lenient().when(sapCpiOrder.getDivision()).thenReturn(division);
		Mockito.lenient().when(sapCpiOrder.getShippingCondition()).thenReturn(shippingCondition);
		Mockito.lenient().when(sapCpiOrder.getSapCpiConfig()).thenReturn(sapCpiConfig);
		Mockito.lenient().when(orderModel.getStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigModel);

		Mockito.lenient().when(sapCpiOrder.getSapCpiOrderItems()).thenReturn(sapCpiOrderItems);
		Mockito.lenient().when(sapCpiOrder.getSapCpiPartnerRoles()).thenReturn(sapCpiPartnerRoles);
		Mockito.lenient().when(sapCpiOrder.getSapCpiOrderAddresses()).thenReturn(sapCpiOrderAddress);
		Mockito.lenient().when(sapCpiOrder.getSapCpiOrderPriceComponents()).thenReturn(sapCpiOrderPriceComponent);
		Mockito.lenient().when(sapCpiOrder.getSapCpiCreditCardPayments()).thenReturn(sapCpiCreditCardComponent);
		Mockito.lenient().when(sapCpiTargetSystem.getUrl()).thenReturn("URL");
		Mockito.lenient().when(sapCpiTargetSystem.getSenderName()).thenReturn(senderName);
		Mockito.lenient().when(sapCpiConfig.getSapCpiTargetSystem()).thenReturn(sapCpiTargetSystem);

		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(true);
	}

	@Test
	public void mapTest()
	{
		Mockito.lenient().when(sapCpiOrderConversionService.convertOrderToSapCpiOrder(orderModel)).thenReturn(sapCpiOrder);
		defaultCentralOrderCpiMapper.map(orderModel, sapCpiOutboundOrderModel);
		Mockito.verify(sapCpiOrderOutboundAdditionalAttributePopulator).addAdditionalAttributesToSapCpiOrder(orderModel,
				sapCpiOutboundOrderModel);

	}

	@Test
	public void mapOrderConfigInfoTestCentralOrderActive()
	{
		Mockito.lenient().when(sapConfigModel.getSapco_active()).thenReturn(true);
		final SAPCpiOutboundConfigModel sapCpiOutboundConfigModel = defaultCentralOrderCpiMapper.mapOrderConfigInfo(sapCpiConfig,
				orderModel);
		Assert.assertEquals(null, sapCpiOutboundConfigModel.getSenderName());
	}

	@Test
	public void mapOrderConfigInfoTestCentralOrderNotActive()
	{
		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(false);
		final SAPCpiOutboundConfigModel sapCpiOutboundConfigModel = defaultCentralOrderCpiMapper.mapOrderConfigInfo(sapCpiConfig,
				orderModel);
		Assert.assertEquals(senderName, sapCpiOutboundConfigModel.getSenderName());
	}

	@Test
	public void mapSapCpiOrderToSAPCpiOrderOutboundTest()
	{
		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(false);
		defaultCentralOrderCpiMapper.mapSapCpiOrderToSAPCpiOrderOutbound(sapCpiOrder, sapCpiOutboundOrderModel, orderModel);
		Assert.assertEquals(shippingCondition, sapCpiOutboundOrderModel.getShippingCondition());
		Assert.assertEquals(senderName, sapCpiOutboundOrderModel.getSapCpiConfig().getSenderName());
	}

}
