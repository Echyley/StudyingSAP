/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultOrderIntegrationFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderIntegrationFacadeTest
{
	private static final String CONFIG_ID = "config123";
	private static final String ORDER_CODE = "cart123";
	private static final Integer ENTRY_NUMBER = Integer.valueOf(3);
	private static final String P_CODE = "product123";
	@Mock
	private AbstractOrderIntegrationService abstractOrderIntegrationService;
	@Mock
	private OrderService orderService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CustomerAccountService customerAccountService;

	@InjectMocks
	private DefaultOrderIntegrationFacade classUnderTest;
	private final OrderModel orderModel = new OrderModel();
	private final OrderEntryModel orderEntry = new OrderEntryModel();
	private final BaseStoreModel baseStoreModel = new BaseStoreModel();

	@Before
	public void setUp()
	{
		orderModel.setEntries(new ArrayList<AbstractOrderEntryModel>());
		orderModel.getEntries().add(orderEntry);
		orderEntry.setEntryNumber(ENTRY_NUMBER);
		orderEntry.setProduct(new ProductModel());
		orderEntry.getProduct().setCode(P_CODE);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		when(orderService.getEntryForNumber(orderModel, ENTRY_NUMBER.intValue())).thenReturn(orderEntry);
		when(customerAccountService.getOrderForCode(ORDER_CODE, baseStoreModel)).thenReturn(orderModel);
		when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(orderEntry)).thenReturn(CONFIG_ID);
	}

	@Test
	public void testGetConfigurationId()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigurationId(ORDER_CODE, ENTRY_NUMBER.intValue()).getConfigId());
	}

	@Test
	public void testGetProductCode() throws CommerceSaveCartException
	{
		assertEquals(P_CODE, classUnderTest.getProductCode(ORDER_CODE, ENTRY_NUMBER.intValue()));
	}
}
