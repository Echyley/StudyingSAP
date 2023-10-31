/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderrcfacades.order.mapper.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderRCOutboundAdditionalAttributeMapperTest
{

	/**
	 *
	 */
	private static final String PRICE_PLAN_ID = "Price_Plan_Id";

	/**
	 *
	 */
	private static final String PRODUCT1 = "Product1";

	/**
	 *
	 */
	private static final String SUBSCRIPTION_PRODUCT = "Subscription_Product1";


	private final OrderModel orderModel = mock(OrderModel.class);


	private final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);


	private final ProductModel productModel = mock(ProductModel.class);


	private final CMSSiteService cmsSiteService = mock(CMSSiteService.class);


	private final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);


	private final CurrencyModel currencyModel = mock(CurrencyModel.class);


	private final CommonI18NService commonI18NService = mock(CommonI18NService.class);


	private final SubscriptionCommercePriceService commercePriceService = mock(SubscriptionCommercePriceService.class);


	private final SubscriptionPricePlanModel subscriptionPricePlanModel = mock(SubscriptionPricePlanModel.class);


	private final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);


	private final SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);


	private final CoConfigurationService configurationService = mock(CoConfigurationService.class);


	private final DefaultCentralOrderRCOutboundAdditionalAttributeMapper defaultCentralOrderRCOutboundAdditionalAttributeMapper = new DefaultCentralOrderRCOutboundAdditionalAttributeMapper();

	private SAPCpiOutboundOrderModel sapCpiOutboundOrderModel;

	private SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItemModel;


	@Before
	public void setUp()
	{
		defaultCentralOrderRCOutboundAdditionalAttributeMapper.setConfigurationService(configurationService);
		defaultCentralOrderRCOutboundAdditionalAttributeMapper.setCmsSiteService(cmsSiteService);
		defaultCentralOrderRCOutboundAdditionalAttributeMapper.setCommonI18NService(commonI18NService);
		defaultCentralOrderRCOutboundAdditionalAttributeMapper.setCommercePriceService(commercePriceService);


		sapCpiOutboundOrderModel = new SAPCpiOutboundOrderModel();
		sapCpiOutboundOrderItemModel = new SAPCpiOutboundOrderItemModel();

		final List<AbstractOrderEntryModel> abstractOrderEntryModels = new ArrayList();
		abstractOrderEntryModels.add(abstractOrderEntryModel);

		final Set<SAPCpiOutboundOrderItemModel> cpiOutboundOrderItemModels = new HashSet();
		sapCpiOutboundOrderItemModel.setProductCode(PRODUCT1);
		cpiOutboundOrderItemModels.add(sapCpiOutboundOrderItemModel);
		sapCpiOutboundOrderModel.setSapCpiOutboundOrderItems(cpiOutboundOrderItemModels);

		//Mocks
		Mockito.lenient().when(orderModel.getEntries()).thenReturn(abstractOrderEntryModels);
		Mockito.lenient().when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
		Mockito.lenient().when(productModel.getSubscriptionCode()).thenReturn(SUBSCRIPTION_PRODUCT);
		Mockito.lenient().when(productModel.getCode()).thenReturn(PRODUCT1);
		Mockito.lenient().when(orderModel.getSite()).thenReturn(cmsSiteModel);
		Mockito.lenient().when(orderModel.getCurrency()).thenReturn(currencyModel);

		Mockito.lenient().when(orderModel.getStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		Mockito.lenient().when(sapConfigurationModel.getSapco_active()).thenReturn(true);

		Mockito.lenient().when(commercePriceService.getSubscriptionPricePlanForProduct(any(ProductModel.class)))
				.thenReturn(subscriptionPricePlanModel);
		Mockito.lenient().when(subscriptionPricePlanModel.getPricePlanId()).thenReturn(PRICE_PLAN_ID);

		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(true);
	}

	@Test
	public void addAdditionalAttributesTest()
	{
		//before
		assertEquals(sapCpiOutboundOrderItemModel.getProductCode(), PRODUCT1);
		//Test
		defaultCentralOrderRCOutboundAdditionalAttributeMapper.mapAdditionalAttributes(orderModel, sapCpiOutboundOrderModel);
		//after
		assertEquals(sapCpiOutboundOrderItemModel.getProductCode(), SUBSCRIPTION_PRODUCT);
		assertEquals(sapCpiOutboundOrderItemModel.getPricePlanId(), PRICE_PLAN_ID);
		assertTrue(sapCpiOutboundOrderItemModel.getSubscriptionValidFrom() != null);
	}
}
