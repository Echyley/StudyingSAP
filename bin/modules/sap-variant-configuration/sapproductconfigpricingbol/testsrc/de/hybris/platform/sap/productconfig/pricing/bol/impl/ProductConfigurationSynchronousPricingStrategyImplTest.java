/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.pricing.bol.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationSynchronousPricingStrategyImplTest
{
	private static final String CONFIG_ID = "configId";
	private static final String CART_ENTRY_PK = "1";
	private static final BigDecimal PRICE_VALUE = BigDecimal.valueOf(2000);
	private static final String EUR = "EUR";
	private static final String PRODUCT_CODE = "product code";
	private ProductConfigurationSynchronousPricingStrategyImpl classUnderTest;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private SapPricingEnablementService sapPricingEnablementService;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy cartEntryLinkStrategy;
	@Mock
	private ConfigurationCopyStrategy configCopyStrategy;
	@Mock
	private PricingService pricingService;
	@Mock
	private ProductConfigurationService configurationService;
	@Mock
	private CommonI18NService i18NService;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;
	@Mock
	private CartEntryModel cartEntry;
	@Mock
	private CartModel cart;
	@Mock
	private ModelService modelService;
	@Mock
	private CurrencyModel currentCurrency;
	@Mock
	private ProductModel productModel;

	private final ConfigModel configModel = new ConfigModelImpl();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigurationSynchronousPricingStrategyImpl();
		classUnderTest.setConfigurationService(configurationService);
		classUnderTest.setPricingService(pricingService);
		classUnderTest.setCommerceCartService(commerceCartService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setConfigurationAbstractOrderEntryLinkStrategy(cartEntryLinkStrategy);
		classUnderTest.setConfigCopyStrategy(configCopyStrategy);
		classUnderTest.setI18NService(i18NService);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		classUnderTest.setSapPricingEnablementService(sapPricingEnablementService);

		when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.TRUE);
		when(cartEntry.getOrder()).thenReturn(cart);
		when(cartEntry.getPk()).thenReturn(PK.fromLong(1));
		when(cartEntry.getBasePrice()).thenReturn(Double.valueOf(1000));
		when(cartEntry.getProduct()).thenReturn(productModel);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(pricingService.isActive()).thenReturn(Boolean.FALSE);
		final PriceModel currentTotal = new PriceModelImpl();
		currentTotal.setPriceValue(PRICE_VALUE);
		currentTotal.setCurrency(EUR);
		configModel.setCurrentTotalPrice(currentTotal);
		when(configurationService.retrieveConfigurationOverview(Mockito.any())).thenReturn(configModel);
		when(i18NService.getCurrentCurrency()).thenReturn(currentCurrency);
		when(currentCurrency.getIsocode()).thenReturn(EUR);
		when(cartEntryLinkStrategy.getConfigIdForCartEntry(CART_ENTRY_PK)).thenReturn(CONFIG_ID);
	}

	@Test
	public void testUpdateCartEntryPrices() throws CalculationException
	{
		final CommerceCartParameter parameters = new CommerceCartParameter();
		final boolean result = classUnderTest.updateCartEntryPrices(null, true, parameters);
		assertTrue(result);
		verify(commerceCartService).recalculateCart(parameters);
	}

	@Test
	public void testUpdateCartEntryPricesNoParameters() throws CalculationException
	{
		final boolean result = classUnderTest.updateCartEntryPrices(cartEntry, true, null);
		assertTrue(result);
		verify(commerceCartService).recalculateCart(Mockito.any(CommerceCartParameter.class));
		verify(cartEntryLinkStrategy).getConfigIdForCartEntry(CART_ENTRY_PK);
	}

	@Test
	public void testUpdateCartEntryPricesNoCalculation() throws CalculationException
	{
		final CommerceCartParameter parameters = new CommerceCartParameter();
		final boolean result = classUnderTest.updateCartEntryPrices(null, false, parameters);
		assertFalse(result);
		verify(commerceCartService, Mockito.times(0)).recalculateCart(parameters);
	}

	@Test
	public void testUpdateCartEntryPricesException() throws CalculationException
	{
		final CommerceCartParameter parameters = new CommerceCartParameter();
		doThrow(new CalculationException("unit test")).when(commerceCartService).recalculateCart(parameters);
		final boolean result = classUnderTest.updateCartEntryPrices(null, true, parameters);
		assertFalse(result);
	}

	@Test
	public void testUpdateCartEntryPricesSynchronousPricingInactive() throws CalculationException
	{
		when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.FALSE);
		final CommerceCartParameter parameters = new CommerceCartParameter();
		final boolean result = classUnderTest.updateCartEntryPrices(cartEntry, true, parameters);
		assertTrue(result);
		verify(commerceCartService).calculateCart(parameters);
	}

	@Test
	public void testIsPricingErrorPresentInCartSynchronousPricingActive()
	{
		assertFalse(classUnderTest.isCartPricingErrorPresent(null));
	}

	@Test
	public void testIsPricingErrorPresentInCartSynchronousPricingInactive()
	{
		configModel.setPricingError(true);
		when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.FALSE);
		assertTrue(classUnderTest.isCartPricingErrorPresent(configModel));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCalculateBasePriceForConfigurationSynchronousPricingActive()
	{
		classUnderTest.calculateBasePriceForConfiguration(cartEntry);
	}

	@Test
	public void testCalculateBasePriceForConfigurationSynchronousPricingInActive()
	{
		Mockito.when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.FALSE);
		final PriceValue result = classUnderTest.calculateBasePriceForConfiguration(cartEntry);
		assertNotNull(result);
		assertEquals(PRICE_VALUE.doubleValue(), result.getValue(), 0.001);
		assertEquals(EUR, result.getCurrencyIso());
	}

}
