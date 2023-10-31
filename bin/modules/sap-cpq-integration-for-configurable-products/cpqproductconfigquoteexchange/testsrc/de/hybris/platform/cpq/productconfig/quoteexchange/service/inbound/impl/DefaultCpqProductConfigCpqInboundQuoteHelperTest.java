/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.quoteexchange.service.inbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultAbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Unit test for {@link DefaultCpqProductConfigCpqInboundQuoteHelper}
 */
@UnitTest
public class DefaultCpqProductConfigCpqInboundQuoteHelperTest
{
	private DefaultCpqProductConfigCpqInboundQuoteHelper classUnderTest;
	private static final String CONFIG_ID = "configurationId";
	private static final String CPQ_CONFIG_ID = "cpqConfigId";

	private static final String ROLLEDUP_LIST_PRICE = "125.00";
	private static final String ROLLEDUP_EXTENDED_LIST_PRICE = "250.00";
	private static final String ROLLEDUP_EXTENDED_AMOUNT = "225.00";
	private static final String ROLLEDUP_DISCOUNT_AMOUNT = "25.00";
	private static final String ROLLEDUP_DISCOUNT_PERCENT = "10.000000";

	private static final String QUOTE_ENTRY_DISCOUNT_CODE = "DM01";
	private static final String CURRENCY_ISO_CODE = "USD";
	private static final String DISCOUNT_STRING_ABSOLUTE_ALLOWED = "<<DV<DM01#25.0#true#25.0#USD#false>VD>>";
	private static final String DISCOUNT_STRING_ABSOLUTE_NOT_ALLOWED = "<<DV<DM01#10.0#false#25.0#USD#false>VD>>";

	private AbstractOrderIntegrationService abstractOrderIntegrationService;
	private QuoteEntryModel inboundQuoteEntry;
	private QuoteModel quote;
	final CloudCPQOrderEntryProductInfoModel productInfo = new CloudCPQOrderEntryProductInfoModel();
	private final BaseSiteModel baseSite = new BaseSiteModel();
	@Mock
	private ConfigurationService mockConfigService;
	@Mock
	private ConfigurableChecker mockConfigurableChecker;
	@Mock
	private BaseSiteService mockBaseSiteService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		abstractOrderIntegrationService = new DefaultAbstractOrderIntegrationService(null);
		classUnderTest = new DefaultCpqProductConfigCpqInboundQuoteHelper(abstractOrderIntegrationService, mockConfigService,
				mockConfigurableChecker, mockBaseSiteService);
		inboundQuoteEntry = new QuoteEntryModel();
		quote = new QuoteModel();
		quote.setSite(baseSite);
		inboundQuoteEntry.setOrder(quote);
		inboundQuoteEntry.setCpqConfigurationId(CONFIG_ID);
		inboundQuoteEntry.setQuantity(1L);

		when(mockConfigService.cloneConfiguration(CONFIG_ID, true)).thenReturn(CPQ_CONFIG_ID);
		when(mockConfigurableChecker.isCloudCPQConfigurableProduct(any())).thenReturn(true);
		when(mockBaseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
	}

	@Test
	public void testProcessInboundQuoteEntryWithKnownCurrentBaseSite()
	{
		classUnderTest.processInboundQuoteEntry(inboundQuoteEntry);
		assertNotNull(inboundQuoteEntry);
		final String newConfigId = abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(inboundQuoteEntry);
		verify(mockBaseSiteService, times(0)).setCurrentBaseSite(anyString(), anyBoolean());
		assertEquals(CPQ_CONFIG_ID, newConfigId);
	}

	@Test
	public void testProcessInboundQuoteEntryWithUnknownCurrentBaseSite()
	{
		when(mockBaseSiteService.getCurrentBaseSite()).thenReturn(null);
		classUnderTest.processInboundQuoteEntry(inboundQuoteEntry);
		assertNotNull(inboundQuoteEntry);
		final String newConfigId = abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(inboundQuoteEntry);
		verify(mockBaseSiteService, times(2)).setCurrentBaseSite(nullable(BaseSiteModel.class), anyBoolean());
		assertEquals(CPQ_CONFIG_ID, newConfigId);
	}

	@Test
	public void testProcessInboundQuoteEntryForNoneCloudCPQConfigurator()
	{
		when(mockConfigurableChecker.isCloudCPQConfigurableProduct(any())).thenReturn(false);
		classUnderTest.processInboundQuoteEntry(inboundQuoteEntry);
		assertNotNull(inboundQuoteEntry);
		assertNull(inboundQuoteEntry.getProductInfos());
	}

	@Test
	public void testEnsureBaseSiteIsAvailableWithDefinedBaseSite()
	{
		final boolean result = classUnderTest.ensureBaseSiteIsAvailable(new QuoteModel());
		assertFalse(result);
	}

	@Test
	public void testEnsureBaseSiteIsAvailableWithNullBaseSite()
	{
		when(mockBaseSiteService.getCurrentBaseSite()).thenReturn(null);
		final QuoteModel quoteModel = new QuoteModel();
		quoteModel.setSite(new BaseSiteModel());
		final boolean result = classUnderTest.ensureBaseSiteIsAvailable(quoteModel);
		assertTrue(result);
	}

	@Test
	public void testResetBaseSite()
	{
		classUnderTest.resetBaseSite(true);
		verify(mockBaseSiteService, times(1)).setCurrentBaseSite(nullable(BaseSiteModel.class), anyBoolean());
	}

	@Test
	public void testDontResetBaseSite()
	{
		classUnderTest.resetBaseSite(false);
		assertNotNull(mockBaseSiteService.getCurrentBaseSite());
		assertEquals(baseSite, mockBaseSiteService.getCurrentBaseSite());
	}

	@Test
	public void testProcessMainEntryPrices()
	{
		preparePricingDetails();
		preparePricingSettings(Boolean.TRUE);
		classUnderTest.processMainEntryPrices(inboundQuoteEntry);
		assertEquals(Double.valueOf(ROLLEDUP_LIST_PRICE), inboundQuoteEntry.getBasePrice());
		assertEquals(Double.valueOf(ROLLEDUP_EXTENDED_AMOUNT), inboundQuoteEntry.getTotalPrice());
		assertEquals(Double.valueOf(ROLLEDUP_DISCOUNT_AMOUNT), inboundQuoteEntry.getCpqEntryDiscount());
		assertEquals(ROLLEDUP_DISCOUNT_PERCENT, inboundQuoteEntry.getDiscountPercent());
	}

	@Test
	public void testProcessMainEntryDiscountAbsoluteAllowed()
	{
		preparePricingSettings(Boolean.TRUE);
		classUnderTest.processMainEntryDiscount(new BigDecimal(ROLLEDUP_DISCOUNT_AMOUNT), new BigDecimal(ROLLEDUP_DISCOUNT_PERCENT),
				inboundQuoteEntry);
		assertEquals(DISCOUNT_STRING_ABSOLUTE_ALLOWED, inboundQuoteEntry.getCpqEntryDiscountInternal());
	}

	@Test
	public void testProcessMainEntryDiscountAbsoluteNotAllowed()
	{
		preparePricingSettings(Boolean.FALSE);
		classUnderTest.processMainEntryDiscount(new BigDecimal(ROLLEDUP_DISCOUNT_AMOUNT), new BigDecimal(ROLLEDUP_DISCOUNT_PERCENT),
				inboundQuoteEntry);
		assertEquals(DISCOUNT_STRING_ABSOLUTE_NOT_ALLOWED, inboundQuoteEntry.getCpqEntryDiscountInternal());
	}

	protected void preparePricingDetails()
	{
		final List<CpqPricingDetailModel> pricingDetails = new ArrayList<>();
		final CpqPricingDetailModel pricingDetailFixed = new CpqPricingDetailModel();
		pricingDetailFixed.setPricingType("Fixed");
		pricingDetailFixed.setRolledUpListPrice(ROLLEDUP_LIST_PRICE);
		pricingDetailFixed.setRolledUpExtendedListPrice(ROLLEDUP_EXTENDED_LIST_PRICE);
		pricingDetailFixed.setRolledUpExtendedAmount(ROLLEDUP_EXTENDED_AMOUNT);
		pricingDetailFixed.setRolledUpDiscountAmount(ROLLEDUP_DISCOUNT_AMOUNT);
		pricingDetails.add(pricingDetailFixed);
		final CpqPricingDetailModel pricingDetailAnotherType = new CpqPricingDetailModel();
		pricingDetailAnotherType.setPricingType("AnotherType");
		pricingDetails.add(pricingDetailAnotherType);
		inboundQuoteEntry.setCpqPricingDetails(pricingDetails);
	}

	protected void preparePricingSettings(final Boolean absoluteDiscountEnabled)
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		quote.setStore(baseStore);
		final SAPConfigurationModel sapConfiguration = new SAPConfigurationModel();
		baseStore.setSAPConfiguration(sapConfiguration);
		sapConfiguration.setCpqAbsoluteDiscountEnabled(absoluteDiscountEnabled);
		sapConfiguration.setCpqQuoteEntryDiscountConditionCode(QUOTE_ENTRY_DISCOUNT_CODE);
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(CURRENCY_ISO_CODE);
		quote.setCurrency(currencyModel);
	}

}
