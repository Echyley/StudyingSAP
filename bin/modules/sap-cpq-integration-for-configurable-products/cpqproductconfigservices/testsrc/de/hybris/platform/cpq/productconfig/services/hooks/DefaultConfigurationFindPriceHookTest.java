/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.hooks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryDataContent;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultConfigurationFindPriceHook}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationFindPriceHookTest
{
	private static final String CURRENCY = "USD";
	private static final double DEFAULT_PRICE = 200.00;
	private static final double BASE_PRICE = 234.21;
	private static final double TOTAL_PRICE = 234.21;
	private static final double DELTA = 0.00001;
	private static final String CONFIG_ID = "abc876fd";

	@InjectMocks
	private DefaultConfigurationFindPriceHook classUnderTest;

	@Mock
	private List<PriceInformation> priceInformation;
	@Mock
	private AbstractOrderEntryModel cartEntry;
	@Mock
	private CloudCPQOrderEntryProductInfoModel productInfo;
	@Mock
	private AbstractOrderModel cart;
	@Mock
	private CurrencyModel currency;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private PriceValue BASE_PRICE_FROM_DEFAULT;

	private final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
	private final ConfigurationSummaryData configurationSummary = new ConfigurationSummaryData();
	private final ConfigurationSummaryDataContent configurationSummaryContent = new ConfigurationSummaryDataContent();

	@Before
	public void initialize() throws CalculationException
	{
		Mockito.when(cartEntry.getProductInfos()).thenReturn(productInfos);
		Mockito.when(cartEntry.getOrder()).thenReturn(cart);
		Mockito.when(productInfo.getConfiguratorType()).thenReturn(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		Mockito.when(productInfo.getConfigurationId()).thenReturn(CONFIG_ID);
		Mockito.when(cart.getCurrency()).thenReturn(currency);
		Mockito.when(currency.getIsocode()).thenReturn(CURRENCY);
		Mockito.when(configurationService.getConfigurationSummary(CONFIG_ID)).thenReturn(configurationSummary);
		productInfos.add(productInfo);
		configurationSummary.setConfiguration(configurationSummaryContent);
		configurationSummaryContent.setBasePrice(BigDecimal.valueOf(BASE_PRICE));
		configurationSummaryContent.setTotalPrice(BigDecimal.valueOf(TOTAL_PRICE));
	}

	@Test
	public void testIsApplicable()
	{
		assertTrue(classUnderTest.isApplicable(cartEntry));
	}

	@Test
	public void testIsApplicableNoProductInfo()
	{
		Mockito.when(cartEntry.getProductInfos()).thenReturn(null);
		assertFalse(classUnderTest.isApplicable(cartEntry));
	}

	@Test
	public void testIsApplicableEmptyList()
	{
		productInfos.clear();
		assertFalse(classUnderTest.isApplicable(cartEntry));
	}

	@Test
	public void testIsApplicableWrongConfiguratorType()
	{
		Mockito.when(productInfo.getConfiguratorType()).thenReturn(null);
		assertFalse(classUnderTest.isApplicable(cartEntry));
	}

	@Test
	public void testIsApplicableNoConfigIdAvailable()
	{
		//We might persist the configuration ID after the first calculation is done, therefore we need to be prepared for configuration IDs missing
		Mockito.when(productInfo.getConfigurationId()).thenReturn(null);
		assertFalse(classUnderTest.isApplicable(cartEntry));
	}

	@Test
	public void testIsApplicableMoreThanOneProductInfo()
	{
		productInfos.add(productInfo);
		assertFalse(classUnderTest.isApplicable(cartEntry));
	}

	@Test
	public void testFindBasePrice() throws CalculationException
	{
		final PriceValue basePrice = classUnderTest.findCustomBasePrice(cartEntry, null);
		assertNotNull(basePrice);
		assertEquals(CURRENCY, basePrice.getCurrencyIso());
		assertEquals(TOTAL_PRICE, basePrice.getValue(), DELTA);
	}

	@Test
	public void testFindBasePriceReturnDefaultWhenNull() throws CalculationException
	{
		configurationSummaryContent.setBasePrice(null);
		configurationSummaryContent.setTotalPrice(null);

		final PriceValue defaultPrice = new PriceValue(CURRENCY, DEFAULT_PRICE, false);
		final PriceValue basePrice = classUnderTest.findCustomBasePrice(cartEntry, defaultPrice );
		assertSame(defaultPrice, basePrice);
	}


	@Test(expected = IllegalStateException.class)
	public void testFindBasePriceFromProductInfoNoProductInfo() throws CalculationException
	{
		Mockito.when(cartEntry.getProductInfos()).thenReturn(null);
		classUnderTest.findBasePriceFromProductInfo(cartEntry,null);
	}

	@Test(expected = IllegalStateException.class)
	public void testFindBasePriceFromProductInfoEmptyList() throws CalculationException
	{
		productInfos.clear();
		classUnderTest.findBasePriceFromProductInfo(cartEntry, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testFindBasePriceFromProductInfoMoreThanOneProductInfo() throws CalculationException
	{
		productInfos.add(productInfo);
		classUnderTest.findBasePriceFromProductInfo(cartEntry, null);
	}

	@Test
	public void testConfigurationService()
	{
		assertEquals(configurationService, classUnderTest.getConfigurationService());
	}


}
