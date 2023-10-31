/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.pricing.bol.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link ProductConfigurationSynchronousPricingAddToCartStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigurationSynchronousPricingAddToCartStrategyTest
{
	private static final String CONFIG_ID = "config id";
	@InjectMocks
	private ProductConfigurationSynchronousPricingAddToCartStrategy classUnderTest;
	private CommerceCartParameter params;
	private ProductModel product;
	private CartModel cart;
	@Mock
	private CartEntryModel cartEntry;
	private final CommerceCartModification commerceCartModification = new CommerceCartModification();

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private ProductService productService;

	@Mock
	private CartService cartService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private CommerceStockService commerceStockService;

	@Mock
	private ModelService modelService;

	@Mock
	private AddToCartValidator fallbackValidator;



	@Before
	public void setUp()
	{
		// super class still uses setter injection, while we use constructor injection
		// Mockito does not mix injection behaviors, so it will only auto-inject dependencies of our class via constructor
		// and we have to manually inject the super dependencies as below
		classUnderTest.setProductService(productService);
		classUnderTest.setCartService(cartService);
		classUnderTest.setBaseStoreService(baseStoreService);
		classUnderTest.setCommerceStockService(commerceStockService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setFallbackAddToCartValidator(fallbackValidator);
		createValidEntity();
		createCartEntry();
		Mockito.when(cartService.addNewEntry(Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.nullable(UnitModel.class),
				Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(cartEntry);

	}

	private void createCartEntry()
	{
		Mockito.when(cartEntry.getPk()).thenReturn(PK.fromLong(1));
	}

	private void createValidEntity()
	{
		params = new CommerceCartParameter();
		cart = new CartModel();
		params.setCart(cart);
		product = new ProductModel();
		params.setProduct(product);
		params.setQuantity(1);
		params.setConfigId(CONFIG_ID);

		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(true);
	}

	@Test
	public void testDoAddToCart() throws CommerceCartModificationException
	{
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy)
				.setConfigIdForCartEntry(cartEntry.getPk().toString(),CONFIG_ID);
	}

	@Test
	public void testDoAddToCartNoConfigurableProduct() throws CommerceCartModificationException
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(false);
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy, Mockito.times(0)).setConfigIdForCartEntry(Mockito.any(),
				Mockito.any());
	}

	@Test
	public void testDoAddToCartChangeableVariant() throws CommerceCartModificationException
	{
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy)
				.setConfigIdForCartEntry(cartEntry.getPk().toString(), CONFIG_ID);
	}

	@Test
	public void testDoAddToCartModelNotSaved() throws CommerceCartModificationException
	{
		Mockito.when(cartEntry.getPk()).thenReturn(null);
		assertNotNull(classUnderTest.doAddToCart(params));
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy, Mockito.times(0)).setConfigIdForCartEntry(Mockito.any(),
				Mockito.any());
	}
}
