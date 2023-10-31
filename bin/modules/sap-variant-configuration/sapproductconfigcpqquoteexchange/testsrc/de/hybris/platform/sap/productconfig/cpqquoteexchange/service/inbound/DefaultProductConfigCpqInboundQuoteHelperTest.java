/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpqquoteexchange.service.inbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;


@UnitTest
public class DefaultProductConfigCpqInboundQuoteHelperTest
{
	private static final String CONFIG_ID = "c123";
	private static final String NEW_CONFIG_ID = "c456";
	private static final String P_CODE = "p_code";

	private static final String ROLLEDUP_LIST_PRICE = "125.00";
	private static final String ROLLEDUP_EXTENDED_LIST_PRICE = "250.00";
	private static final String ROLLEDUP_EXTENDED_AMOUNT = "225.00";
	private static final String ROLLEDUP_DISCOUNT_AMOUNT = "25.00";
	private static final String ROLLEDUP_DISCOUNT_PERCENT = "10.000000";

	private static final String QUOTE_ENTRY_DISCOUNT_CODE = "DM01";
	private static final String CURRENCY_ISO_CODE = "USD";
	private static final String DISCOUNT_STRING_ABSOLUTE_ALLOWED = "<<DV<DM01#25.0#true#25.0#USD#false>VD>>";
	private static final String DISCOUNT_STRING_ABSOLUTE_NOT_ALLOWED = "<<DV<DM01#10.0#false#25.0#USD#false>VD>>";

	@InjectMocks
	private DefaultProductConfigCpqInboundQuoteHelper classUnderTest;

	private final QuoteModel quoteModel = new QuoteModel();
	private final QuoteEntryModel quoteEntry = new QuoteEntryModel();
	private final ProductConfigurationModel productConfigurationModel = new ProductConfigurationModel();
	private final ProductConfigurationModel newProductConfigurationModel = new ProductConfigurationModel();
	private final ProductModel productModel = new ProductModel();
	private final BaseSiteModel baseSiteModel = new BaseSiteModel();
	private final UserModel userModel = new UserModel();

	@Mock
	private ConfigurationDeepCopyHandler copyHandler;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ModelService modelService;
	@Mock
	private ProductConfigurationPersistenceService persistenceService;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		quoteEntry.setCpqExternalConfigurationId(CONFIG_ID);
		quoteEntry.setProduct(productModel);
		quoteEntry.setQuantity(1L);
		quoteEntry.setOrder(quoteModel);
		productModel.setCode(P_CODE);
		quoteModel.setSite(baseSiteModel);
		quoteModel.setUser(userModel);
		final List<QuoteEntryModel> quoteEntryList = new ArrayList<>();
		quoteEntryList.add(quoteEntry);
		quoteModel.setCpqQuoteEntries(quoteEntryList);

		given(copyHandler.deepCopyConfiguration(anyString(), anyString(), nullable(String.class), anyBoolean(),
				any(ProductConfigurationRelatedObjectType.class))).willReturn(NEW_CONFIG_ID);
		given(persistenceService.getByConfigId(NEW_CONFIG_ID)).willReturn(newProductConfigurationModel);
		newProductConfigurationModel.setConfigurationId(NEW_CONFIG_ID);
		given(modelService.create(CPQOrderEntryProductInfoModel.class)).willReturn(new CPQOrderEntryProductInfoModel());
	}

	@Test
	public void testProcessInboundQuoteReturnsSameQuoteEntry()
	{
		assertSame(quoteModel, classUnderTest.processInboundQuote(quoteModel));
	}

	@Test
	public void testProcessInboundQuoteEntryWithoutConfig()
	{
		quoteEntry.setCpqExternalConfigurationId(null);
		classUnderTest.processInboundQuoteEntry(quoteEntry);
		assertNull(quoteEntry.getProductConfiguration());
		verifyZeroInteractions(copyHandler);
	}

	@Test
	public void testProcessInboundQuoteEntryWithConfig()
	{
		classUnderTest.processInboundQuoteEntry(quoteEntry);
		assertEquals(NEW_CONFIG_ID, quoteEntry.getProductConfiguration().getConfigurationId());
		assertSame(userModel,  quoteEntry.getProductConfiguration().getUser());
		verify(copyHandler).deepCopyConfiguration(CONFIG_ID, P_CODE, null, true, ProductConfigurationRelatedObjectType.QUOTE_ENTRY);
	}


	@Test
	public void testProcessInboundQuote()
	{
		classUnderTest.processInboundQuote(quoteModel);
		final QuoteEntryModel quoteEntry = quoteModel.getCpqQuoteEntries().get(0);
		assertEquals(NEW_CONFIG_ID, quoteEntry.getProductConfiguration().getConfigurationId());
	}

	@Test
	public void testProcessInboundQuoteSetsBaseSite()
	{
		classUnderTest.processInboundQuoteEntry(quoteEntry);
		verify(baseSiteService).setCurrentBaseSite(same(baseSiteModel), anyBoolean());
	}

	@Test
	public void testCreateBasicConfigurationInfo() {
		classUnderTest.createBasicConfigurationInfo(quoteEntry);
		assertFalse(CollectionUtils.isEmpty(quoteEntry.getProductInfos()));
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, quoteEntry.getProductInfos().get(0).getConfiguratorType());
		assertSame(quoteEntry, quoteEntry.getProductInfos().get(0).getOrderEntry());
	}

	@Test
	public void testEnsureBaseSiteIsAvailableAlreadySet()
	{
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		classUnderTest.ensureBaseSiteIsAvailable(quoteModel);
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
	}

	@Test
	public void testEnsureBaseSiteIsAvailableNotSet()
	{
		classUnderTest.ensureBaseSiteIsAvailable(quoteModel);
		verify(baseSiteService).setCurrentBaseSite(same(baseSiteModel), anyBoolean());
	}

	@Test
	public void testProcessMainEntryPrices()
	{
		preparePricingDetails();
		preparePricingSettings(Boolean.TRUE);
		classUnderTest.processMainEntryPrices(quoteEntry);
		assertEquals(Double.valueOf(ROLLEDUP_LIST_PRICE), quoteEntry.getBasePrice());
		assertEquals(Double.valueOf(ROLLEDUP_EXTENDED_AMOUNT), quoteEntry.getTotalPrice());
		assertEquals(Double.valueOf(ROLLEDUP_DISCOUNT_AMOUNT), quoteEntry.getCpqEntryDiscount());
		assertEquals(ROLLEDUP_DISCOUNT_PERCENT, quoteEntry.getDiscountPercent());
	}

	@Test
	public void testProcessMainEntryDiscountAbsoluteAllowed()
	{
		preparePricingSettings(Boolean.TRUE);
		classUnderTest.processMainEntryDiscount(new BigDecimal(ROLLEDUP_DISCOUNT_AMOUNT), new BigDecimal(ROLLEDUP_DISCOUNT_PERCENT),
				quoteEntry);
		assertEquals(DISCOUNT_STRING_ABSOLUTE_ALLOWED, quoteEntry.getCpqEntryDiscountInternal());
	}

	@Test
	public void testProcessMainEntryDiscountAbsoluteNotAllowed()
	{
		preparePricingSettings(Boolean.FALSE);
		classUnderTest.processMainEntryDiscount(new BigDecimal(ROLLEDUP_DISCOUNT_AMOUNT), new BigDecimal(ROLLEDUP_DISCOUNT_PERCENT),
				quoteEntry);
		assertEquals(DISCOUNT_STRING_ABSOLUTE_NOT_ALLOWED, quoteEntry.getCpqEntryDiscountInternal());
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
		quoteEntry.setCpqPricingDetails(pricingDetails);
	}

	protected void preparePricingSettings(final Boolean absoluteDiscountEnabled)
	{
		final BaseStoreModel baseStore = new BaseStoreModel();
		quoteModel.setStore(baseStore);
		final SAPConfigurationModel sapConfiguration = new SAPConfigurationModel();
		baseStore.setSAPConfiguration(sapConfiguration);
		sapConfiguration.setCpqAbsoluteDiscountEnabled(absoluteDiscountEnabled);
		sapConfiguration.setCpqQuoteEntryDiscountConditionCode(QUOTE_ENTRY_DISCOUNT_CODE);
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(CURRENCY_ISO_CODE);
		quoteModel.setCurrency(currencyModel);
	}

}
