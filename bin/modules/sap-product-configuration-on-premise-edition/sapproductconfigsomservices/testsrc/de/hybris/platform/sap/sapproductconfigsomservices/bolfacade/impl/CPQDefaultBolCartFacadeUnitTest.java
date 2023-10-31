/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CPQDefaultBolCartFacadeUnitTest
{
	private static final String NEW_HANDLE = "new handle";

	private static final String HANDLE = "handle";

	private static final String NEW_PRODUCT_ID = "new product id";

	private static final String PRODUCT_CODE = "product code";

	@InjectMocks
	private CPQDefaultBolCartFacade classUnderTest;

	@Mock
	private GenericFactory genericFactory;

	@Mock
	private Basket cart;
	@Mock
	private CPQItem item;
	@Mock
	private CPQItem newItem;

	private final ConfigModel configModel = new ConfigModelImpl();

	@Before
	public void setup()
	{
		when(genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART)).thenReturn(cart);
		when(cart.getItem(any())).thenReturn(item);
		when(cart.isInitialized()).thenReturn(true);
		when(cart.createItem()).thenReturn(newItem);
		configModel.setRootInstance(new InstanceModelImpl());
		configModel.getRootInstance().setName(PRODUCT_CODE);
		when(item.getProductId()).thenReturn(PRODUCT_CODE);
		when(item.getHandle()).thenReturn(HANDLE);
		when(newItem.getHandle()).thenReturn(NEW_HANDLE);
		when(item.getQuantity()).thenReturn(BigDecimal.ONE);
	}

	@Test
	public void testHandleUpdateReplaceVariant()
	{
		configModel.getRootInstance().setName(NEW_PRODUCT_ID);
		final String result = classUnderTest.handleUpdate(configModel, item);
		assertNotNull(result);
		assertEquals(NEW_HANDLE, result);
		verify(item).setProductId("");
		verify(newItem).setConfigurable(true);
		verify(newItem).setProductConfiguration(configModel);
	}

	@Test
	public void testHandleUpdateConfigurableProduct()
	{
		final String result = classUnderTest.handleUpdate(configModel, item);
		assertNotNull(result);
		assertEquals(HANDLE, result);
		verify(item).setProductConfigurationDirty(true);
		verify(item).setProductConfiguration(configModel);
	}

	@Test
	public void testUpdateConfigurationInCart()
	{
		final String result = classUnderTest.updateConfigurationInCart("key", configModel);
		assertNotNull(result);
		assertEquals(HANDLE, result);
		verify(item).setProductConfigurationDirty(true);
		verify(item).setProductConfiguration(configModel);
	}

	@Test
	public void testPerformVariantReplacementQuantity()
	{
		when(item.getQuantity()).thenReturn(BigDecimal.TEN);
		classUnderTest.performVariantReplacement(configModel, item);
		verify(newItem).setQuantity(BigDecimal.TEN);
	}

	@Test
	public void testAddConfigurationToCart()
	{
		final String result = classUnderTest.addConfigurationToCart(configModel);
		assertNotNull(result);
		assertEquals(NEW_HANDLE, result);
	}

	@Test
	public void testSetConfigModelForItemNull()
	{
		classUnderTest.setConfigModelForItem(null, item);
		assertNull(item.getProductConfiguration());
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testgetProductIdFromConfigModelNoRootInstance()
	{
		classUnderTest.getProductIdFromConfigModel(new ConfigModelImpl());
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateConfigurationInCartNoItem()
	{
		when(cart.getItem(any())).thenReturn(null);
		classUnderTest.updateConfigurationInCart(HANDLE, configModel);
	}

	@Test
	public void testAddItemsToCart()
	{
		final List<Item> items = new ArrayList<Item>();
		items.add(item);
		items.add(item);
		classUnderTest.addItemsToCart(items);
		verify(cart, times(2)).createItem();
	}

}
