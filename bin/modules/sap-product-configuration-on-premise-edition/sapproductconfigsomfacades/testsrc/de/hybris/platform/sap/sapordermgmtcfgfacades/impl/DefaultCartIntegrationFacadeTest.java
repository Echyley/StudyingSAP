/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationExpertModeFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationMessageMapper;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapproductconfigsomservices.cart.CPQCartService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCartIntegrationFacadeTest
{
	private static final String BASE_PRODUCT_CODE = "base product code";
	@InjectMocks
	private DefaultCartIntegrationFacade classUnderTest;
	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private CPQCartService cartService;
	@Mock
	private ProductService productService;
	@Mock
	private BackendAvailabilityService backendAvailabilityService;
	@Mock
	private ConfigurationCartIntegrationFacade configurationCartIntegrationFacade;
	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private CartRestorationFacade cartRestorationFacade;
	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;
	@Mock
	private ConfigurationData configurationData;
	@Mock
	private ProductModel productModel;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;

	@Mock
	private Item businessItem;

	private final ConfigurationData draftConfigurationData = new ConfigurationData();
	private final ConfigModel defaultConfigurationModel = new ConfigModelImpl();
	@Mock
	private ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy;
	private final InstanceModel rootInstance = new InstanceModelImpl();
	@Mock
	private UniqueUIKeyGenerator uiKeyGenerator;
	@Mock
	private SolvableConflictPopulator conflictsPopulator;
	@Mock
	private PricingService pricingService;
	@Mock
	private AnalyticsService analyticsService;
	@Mock
	private ConfigPricing configPricing;
	@Mock
	private ConfigurationMessageMapper messagesMapper;
	@Mock
	private ConfigurationExpertModeFacade configurationExpertModeFacade;
	@Mock
	private ConfigurationCopyStrategy configCopyStrategy;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel currentBaseStore;
	@Mock
	private SAPConfigurationModel sapConfiguration;

	private static final String ITEM_KEY = "123";
	private static final String PRODUCT_CODE = "PRODUCT";
	private static final String CONFIG_ID = "configId";
	private static final String DRAFT_CONFIG_ID = "draft configId";
	private static final String DEFAULT_CONFIG_ID = "default configId";
	private static final String EXTERNAL_CONFIGURATION = "external configuration";
	private static final String DEFAULT_EXTERNAL_CONFIGURATION = "default external configuration";
	private static final String UNKNOWN = "Unknown";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
		when(currentBaseStore.getSAPConfiguration()).thenReturn(sapConfiguration);
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(true);

		draftConfigurationData.setConfigId(DRAFT_CONFIG_ID);
		draftConfigurationData.setKbKey(new KBKeyData());
		draftConfigurationData.getKbKey().setProductCode(PRODUCT_CODE);

		defaultConfigurationModel.setId(DEFAULT_CONFIG_ID);
		defaultConfigurationModel.setRootInstance(rootInstance);
		defaultConfigurationModel.setKbKey(new KBKeyImpl(PRODUCT_CODE));

		when(cartService.getItemByKey(ITEM_KEY)).thenReturn(businessItem);
		when(businessItem.getProductId()).thenReturn(PRODUCT_CODE);
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ITEM_KEY)).thenReturn(CONFIG_ID);
		when(productConfigurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(EXTERNAL_CONFIGURATION);
		when(productConfigurationService.retrieveExternalConfiguration(DEFAULT_CONFIG_ID))
				.thenReturn(DEFAULT_EXTERNAL_CONFIGURATION);
		when(configurationCartIntegrationFacade.draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(CONFIG_ID), Mockito.eq(true), Mockito.eq(EXTERNAL_CONFIGURATION))).thenReturn(draftConfigurationData);
		when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(productModel);
		when(configurationVariantUtil.isCPQVariantProduct(productModel)).thenReturn(false);
		when(productConfigurationService.createDefaultConfiguration(Mockito.any())).thenReturn(defaultConfigurationModel);
		when(configurationExpertModeFacade.isExpertModeActive()).thenReturn(false);
	}

	/**
	 * Expect that the asynchronous facade is called
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartNoBackend() throws CommerceCartModificationException
	{
		given(Boolean.valueOf(backendAvailabilityService.isBackendDown())).willReturn(Boolean.TRUE);
		given(configurationCartIntegrationFacade.addConfigurationToCart(configurationData)).willReturn(ITEM_KEY);
		final String key = classUnderTest.addConfigurationToCart(configurationData);

		assertEquals(ITEM_KEY, key);
	}


	/**
	 * Expect that both configuration integration facades are called
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartBackendAvailable() throws CommerceCartModificationException
	{
		given(configurationData.getConfigId()).willReturn(DEFAULT_CONFIG_ID);
		given(cartService.addConfigurationToCart(defaultConfigurationModel)).willReturn(ITEM_KEY);
		when(productConfigurationService.retrieveConfigurationOverview(DEFAULT_CONFIG_ID)).thenReturn(defaultConfigurationModel);
		final String key = classUnderTest.addConfigurationToCart(configurationData);

		assertEquals(ITEM_KEY, key);
	}

	@Test
	public void testAddToCartRemoveLinkToProduct() throws CommerceCartModificationException
	{
		given(configurationData.getConfigId()).willReturn(DEFAULT_CONFIG_ID);
		given(cartService.addConfigurationToCart(defaultConfigurationModel)).willReturn(ITEM_KEY);
		when(productConfigurationService.retrieveConfigurationOverview(DEFAULT_CONFIG_ID)).thenReturn(defaultConfigurationModel);
		final String key = classUnderTest.addConfigurationToCart(configurationData);
		verify(configurationProductLinkStrategy).removeConfigIdForProduct(PRODUCT_CODE);
		assertEquals(ITEM_KEY, key);
	}


	/**
	 * Expect that an update and and add is done but no removeConfigIdForProduct
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testAddToCartUpdateConfigurationAndBackendAvailable() throws CommerceCartModificationException
	{
		given(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId("123")).willReturn("PK");
		given(configurationData.getConfigId()).willReturn("123");
		given(Boolean.valueOf(cartService.isItemAvailable("PK"))).willReturn(Boolean.TRUE);
		given(cartService.updateConfigurationInCart(eq("PK"), any())).willReturn("PK");
		final String key = classUnderTest.addConfigurationToCart(configurationData);
		assertEquals("PK", key);
		verify(configurationProductLinkStrategy, times(0)).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testProductLinkStrategy()
	{
		assertEquals(configurationProductLinkStrategy, classUnderTest.getProductLinkStrategy());
		classUnderTest.setProductLinkStrategy(null);
		assertNull(classUnderTest.getProductLinkStrategy());
	}

	@Test
	public void testRemoveConfigurationLink()
	{
		classUnderTest.removeConfigurationLink(PRODUCT_CODE);
		verify(configurationProductLinkStrategy).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test(expected = IllegalStateException.class)
	public void testAddProductConfigurationToCart() throws CommerceCartModificationException
	{
		classUnderTest.addProductConfigurationToCart(PRODUCT_CODE, 1L, CONFIG_ID);
	}

	@Test
	public void testAddConfigurationToCartNoSOM() throws CommerceCartModificationException
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(false);
		classUnderTest.addConfigurationToCart(configurationData);
		verify(configurationCartIntegrationFacade).addConfigurationToCart(configurationData);
	}

	@Test
	public void testConfigureCartItemUsingOldApiForBackwardCompatibility()
	{

		final ConfigurationData result = classUnderTest.configureCartItem(ITEM_KEY);

		assertNotNull(result);
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy).getConfigIdForCartEntry(ITEM_KEY);
		Mockito.verify(configurationCartIntegrationFacade).draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(CONFIG_ID), Mockito.eq(true), Mockito.eq(EXTERNAL_CONFIGURATION));
	}

	@Test
	public void testConfigureCartItem()
	{
		final ConfigurationData result = classUnderTest.configureCartItem(ITEM_KEY);

		assertNotNull(result);
		Mockito.verify(configurationProductLinkStrategy, times(0)).setConfigIdForProduct(PRODUCT_CODE, DRAFT_CONFIG_ID);
		Mockito.verify(configurationCartIntegrationFacade).draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(CONFIG_ID), Mockito.eq(true), Mockito.eq(EXTERNAL_CONFIGURATION));
	}

	@Test
	public void testConfigureCartItemDefaultConfig()
	{
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(ITEM_KEY)).thenReturn(null);
		Mockito
				.when(configurationCartIntegrationFacade.draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
						Mockito.eq(DEFAULT_CONFIG_ID), Mockito.eq(false), Mockito.eq(DEFAULT_EXTERNAL_CONFIGURATION)))
				.thenReturn(draftConfigurationData);
		final ConfigurationData result = classUnderTest.configureCartItem(ITEM_KEY);

		assertNotNull(result);
		Mockito.verify(configurationProductLinkStrategy, times(0)).setConfigIdForProduct(PRODUCT_CODE, DRAFT_CONFIG_ID);
		Mockito.verify(configurationCartIntegrationFacade).draftConfig(Mockito.eq(ITEM_KEY), Mockito.any(KBKeyData.class),
				Mockito.eq(DEFAULT_CONFIG_ID), Mockito.eq(false), Mockito.eq(DEFAULT_EXTERNAL_CONFIGURATION));
	}

	@Test
	public void testConfigureCartItemOnExistingDraft()
	{
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(ITEM_KEY))
				.thenReturn(DEFAULT_CONFIG_ID);
		Mockito.when(productConfigurationService.retrieveConfigurationOverview(DEFAULT_CONFIG_ID))
				.thenReturn(defaultConfigurationModel);
		final ConfigurationData configurationDataFromDraft = classUnderTest.configureCartItemOnExistingDraft(ITEM_KEY);
		assertNotNull(configurationDataFromDraft);
		assertEquals(DEFAULT_CONFIG_ID, configurationDataFromDraft.getConfigId());
	}

	@Test(expected = IllegalStateException.class)
	public void testConfigureCartItemOnExistingDraftNoDraft()
	{
		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(ITEM_KEY)).thenReturn(null);
		classUnderTest.configureCartItemOnExistingDraft(ITEM_KEY);
	}

	@Test
	public void testConfigureCartItemOnExistingDraftNoEntry()
	{
		assertNull(classUnderTest.configureCartItemOnExistingDraft(UNKNOWN));
	}

	@Test
	public void testIsItemInCartByKeyNoSOM()
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(false);
		classUnderTest.isItemInCartByKey(ITEM_KEY);
		verify(configurationCartIntegrationFacade).isItemInCartByKey(ITEM_KEY);
	}

	@Test
	public void testIsItemInCartByKeyBackendDown()
	{
		when(backendAvailabilityService.isBackendDown()).thenReturn(true);
		classUnderTest.isItemInCartByKey(ITEM_KEY);
		verify(configurationCartIntegrationFacade).isItemInCartByKey(ITEM_KEY);
	}

	@Test
	public void testGetKbKey()
	{
		final KBKey result = classUnderTest.getKBKey(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result.getProductCode());
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		assertEquals(format.format(new KBKeyImpl(PRODUCT_CODE).getDate()), format.format(result.getDate()));
	}

	@Test
	public void testResetConfiguration()
	{
		classUnderTest.resetConfiguration(CONFIG_ID);
		//method exists and does nothing in SOM case
		verifyNoInteractions(configurationCartIntegrationFacade);
	}

	@Test
	public void testResetConfigurationNoSOM()
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(false);
		classUnderTest.resetConfiguration(CONFIG_ID);
		verify(configurationCartIntegrationFacade).resetConfiguration(CONFIG_ID);
	}

	@Test
	public void testGetSessionAccessService()
	{
		final SessionAccessService result = classUnderTest.getSessionAccessService();
		assertEquals(sessionAccessService, result);

	}

	@Test
	public void testRestoreConfiguration()
	{
		final KBKeyData kbKey = new KBKeyData();
		assertNull(classUnderTest.restoreConfiguration(kbKey, ITEM_KEY));
	}

	@Test
	public void testRestoreConfigurationNoSOM()
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(false);
		final KBKeyData kbKey = new KBKeyData();
		assertNull(classUnderTest.restoreConfiguration(kbKey, ITEM_KEY));
		verify(configurationCartIntegrationFacade).restoreConfiguration(kbKey, ITEM_KEY);
	}

	@Test
	public void testISapOrderMgmtEnabledNoSAPConfiguration()
	{
		when(currentBaseStore.getSAPConfiguration()).thenReturn(null);
		assertFalse(classUnderTest.isSapOrderMgmtEnabled());
	}

	@Test
	public void testConfigureCartItemNull()
	{
		when(cartService.getItemByKey(ITEM_KEY)).thenReturn(null);
		assertNull(classUnderTest.configureCartItem(ITEM_KEY));
	}

	@Test
	public void testConfigureCartItemNoSOM()
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(false);
		classUnderTest.configureCartItem(ITEM_KEY);
		verify(configurationCartIntegrationFacade).configureCartItem(ITEM_KEY);
	}

	@Test
	public void testUpdateKBKeyForVariants()
	{
		when(configurationVariantUtil.isCPQVariantProduct(productModel)).thenReturn(true);
		when(configurationVariantUtil.getBaseProductCode(productModel)).thenReturn(BASE_PRODUCT_CODE);
		assertEquals(PRODUCT_CODE, draftConfigurationData.getKbKey().getProductCode());
		classUnderTest.updateKBKeyForVariants(draftConfigurationData);
		assertEquals(BASE_PRODUCT_CODE, draftConfigurationData.getKbKey().getProductCode());
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateProductConfigurationInCart()
	{
		classUnderTest.updateProductConfigurationInCart(PRODUCT_CODE, CONFIG_ID);
	}


	@Test
	public void testConfigureCartItemOnExistingDraftNoSOM()
	{
		when(sapConfiguration.isSapordermgmt_enabled()).thenReturn(false);
		classUnderTest.configureCartItemOnExistingDraft(ITEM_KEY);
		verify(configurationCartIntegrationFacade).configureCartItemOnExistingDraft(ITEM_KEY);
	}

}
