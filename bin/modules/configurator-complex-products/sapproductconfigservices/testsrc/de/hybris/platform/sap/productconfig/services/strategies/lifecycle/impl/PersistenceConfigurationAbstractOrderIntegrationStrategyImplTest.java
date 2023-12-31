/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ExternalConfigurationAccess;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationReleaseProductLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link PersistenceConfigurationAbstractOrderIntegrationStrategyImpl}
 */
@UnitTest
public class PersistenceConfigurationAbstractOrderIntegrationStrategyImplTest
{
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "productCode";
	private static final PK CART_ENTRY_PK = PK.fromLong(1);
	private static final String EXT_CONFIG = "<xml/>";
	private static final String BASE_PRODUCT_CODE = "base";
	private static final String KB_NAME = "kbName";
	private static final String KB_VERSION = "kbVersion";
	private static final String KB_LOGSYS = "kbLogsys";

	private PersistenceConfigurationAbstractOrderIntegrationStrategyImpl classUnderTest;
	private final ProductConfigurationModel productConfigModel = new ProductConfigurationModel();

	@Mock
	private AbstractOrderEntryModel mockOrderEntry;
	@Mock
	private final AbstractOrderEntryModel mockOrderEntryWithPk = new AbstractOrderEntryModel();
	@Mock
	private ProductConfigurationPersistenceService mockPersistenceService;
	@Mock
	private ModelService mockModelService;
	@Mock
	private SessionAccessService mockSessionAccessServiceMock;
	@Mock
	private ProductConfigurationService mockConfigurationService;
	@Mock
	private ConfigModel mockConfigModel;
	@Mock
	private VariantProductModel mockProductModel;
	@Mock
	private ProductConfigurationModel mockProductConfigDraftModel;
	@Mock
	private CommerceCartParameter mockCommerceCartParameter;
	@Mock
	private ConfigurationVariantUtil mockConfigurationVariantUtil;
	@Mock
	private ProductService mockProductService;
	@Mock
	private ProductModel mockBaseProduct;
	@Mock
	private ConfigurationLifecycleStrategy mockConfigurationLifecycleStrategy;
	@Mock
	private ExternalConfigurationAccess mockExternalConfigurationAccess;
	@Mock
	private ConfigurationReleaseProductLinkStrategy mockConfigurationReleaseProductLinkStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PersistenceConfigurationAbstractOrderIntegrationStrategyImpl();
		classUnderTest.setModelService(mockModelService);
		classUnderTest.setPersistenceService(mockPersistenceService);
		classUnderTest.setSessionAccessService(mockSessionAccessServiceMock);
		classUnderTest.setConfigurationService(mockConfigurationService);
		classUnderTest.setConfigurationVariantUtil(mockConfigurationVariantUtil);
		classUnderTest.setConfigurationLifecycleStrategy(mockConfigurationLifecycleStrategy);
		classUnderTest.setExternalConfigurationAccess(mockExternalConfigurationAccess);
		classUnderTest.setConfigurationReleaseProductLinkStrategy(mockConfigurationReleaseProductLinkStrategy);

		productConfigModel.setConfigurationId(CONFIG_ID);
		productConfigModel.setKbName(KB_NAME);
		productConfigModel.setKbVersion(KB_VERSION);
		productConfigModel.setKbLogsys(KB_LOGSYS);
		productConfigModel.setOwner(mockOrderEntry);

		given(mockPersistenceService.getByConfigId(CONFIG_ID)).willReturn(productConfigModel);
		given(mockPersistenceService.getOrderEntryByConfigId(CONFIG_ID, false)).willReturn(mockOrderEntryWithPk);
		given(mockOrderEntryWithPk.getPk()).willReturn(PK.fromLong(1));
		given(mockConfigurationService.retrieveConfigurationOverview(CONFIG_ID)).willReturn(mockConfigModel);
		given(mockConfigurationService.createDefaultConfiguration(Mockito.any())).willReturn(mockConfigModel);
		given(mockConfigurationService.createConfigurationForVariant(any(), any())).willReturn(mockConfigModel);
		given(mockConfigurationService.isKbVersionValid(Mockito.any(KBKeyImpl.class))).willReturn(true);
		given(mockConfigurationService.retrieveExternalConfiguration(CONFIG_ID)).willReturn(EXT_CONFIG);
		given(mockOrderEntry.getProduct()).willReturn(mockProductModel);
		given(mockOrderEntry.getProductConfiguration()).willReturn(productConfigModel);
		given(mockOrderEntry.getProductConfigurationDraft()).willReturn(mockProductConfigDraftModel);
		given(mockOrderEntry.getPk()).willReturn(CART_ENTRY_PK);
		given(mockProductConfigDraftModel.getConfigurationId()).willReturn(CONFIG_ID);
		given(mockProductModel.getCode()).willReturn(PRODUCT_CODE);
		given(mockProductModel.getBaseProduct()).willReturn(mockBaseProduct);
		given(mockProductService.getProductForCode(PRODUCT_CODE)).willReturn(mockProductModel);
		given(mockBaseProduct.getCode()).willReturn(BASE_PRODUCT_CODE);
		given(mockConfigurationVariantUtil.getBaseProductCode(mockProductModel)).willReturn(BASE_PRODUCT_CODE);
	}

	@Test
	public void testConfigurationReleaseProductLinkStrategy()
	{
		assertEquals(mockConfigurationReleaseProductLinkStrategy, classUnderTest.getConfigurationReleaseProductLinkStrategy());
	}

	@Test
	public void testExternalConfigurationAccess()
	{
		assertEquals(mockExternalConfigurationAccess, classUnderTest.getExternalConfigurationAccess());
	}

	@Test
	public void testVariantUtil()
	{
		assertEquals(mockConfigurationVariantUtil, classUnderTest.getConfigurationVariantUtil());
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntry()
	{
		assertEquals(mockConfigModel, classUnderTest.getConfigurationForAbstractOrderEntry(mockOrderEntry));
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntryForOneTimeAccess()
	{
		assertEquals(mockConfigModel, classUnderTest.getConfigurationForAbstractOrderEntryForOneTimeAccess(mockOrderEntry));
	}

	@Test
	public void testGetConfigurationForAbstractOrderEntryNoConfiguration()
	{
		mockOrderEntry.setProductConfiguration(null);
		assertNotNull(classUnderTest.getConfigurationForAbstractOrderEntry(mockOrderEntry));
	}

	@Test
	public void testCreateDefaultConfiguration()
	{
		assertNotNull(classUnderTest.createDefaultConfiguration(mockOrderEntry));
		verify(mockConfigurationService).createDefaultConfiguration(any());
	}

	@Test
	public void testCreateDefaultConfigurationIsVariant()
	{
		given(mockConfigurationVariantUtil.isCPQVariantProduct(mockProductModel)).willReturn(true);
		assertNotNull(classUnderTest.createDefaultConfiguration(mockOrderEntry));
		verify(mockConfigurationService).createConfigurationForVariant(any(), any());
	}

	@Test
	public void testIsKbVersionForEntryExisting()
	{
		final boolean result = classUnderTest.isKbVersionForEntryExisting(mockOrderEntry);
		final ArgumentCaptor<KBKeyImpl> argument = ArgumentCaptor.forClass(KBKeyImpl.class);
		verify(mockConfigurationService).isKbVersionValid(argument.capture());
		assertEquals(KB_NAME, argument.getValue().getKbName());
		assertEquals(PRODUCT_CODE, argument.getValue().getProductCode());
		assertTrue(result);
	}

	@Test
	public void testIsKbVersionForEntryExistingChanagebaleVariant()
	{
		given(mockConfigurationVariantUtil.isCPQChangeableVariantProduct(mockProductModel)).willReturn(true);
		final boolean result = classUnderTest.isKbVersionForEntryExisting(mockOrderEntry);
		final ArgumentCaptor<KBKeyImpl> argument = ArgumentCaptor.forClass(KBKeyImpl.class);
		verify(mockConfigurationService).isKbVersionValid(argument.capture());
		assertEquals(KB_NAME, argument.getValue().getKbName());
		assertEquals(BASE_PRODUCT_CODE, argument.getValue().getProductCode());
		assertTrue(result);
	}

	@Test
	public void testIsKbVersionForEntryExistingNotExisting()
	{
		given(mockConfigurationService.isKbVersionValid(Mockito.any(KBKeyImpl.class))).willReturn(false);
		assertFalse(classUnderTest.isKbVersionForEntryExisting(mockOrderEntry));
	}

	@Test(expected = NullPointerException.class)
	public void testIsKbVersionForEntryExistingNoConfig()
	{
		given(mockOrderEntry.getProductConfiguration()).willReturn(null);
		given(mockOrderEntry.getProductConfigurationDraft()).willReturn(null);
		classUnderTest.isKbVersionForEntryExisting(mockOrderEntry);
	}

	@Test
	public void testFinalizeCartEntry()
	{
		classUnderTest.finalizeCartEntry(mockOrderEntry);
		verify(mockConfigurationReleaseProductLinkStrategy).releaseCartEntryProductRelation(mockOrderEntry);
		verify(mockOrderEntry).setProductConfigurationDraft(null);
		verify(mockOrderEntry, never()).setProductConfiguration(null);
	}

	@Test
	public void testGetExternalConfigurationForAbstractOrderEntry()
	{
		assertEquals(EXT_CONFIG, classUnderTest.getExternalConfigurationForAbstractOrderEntry(mockOrderEntry));
	}

	@Test
	public void testInvalidateCartEntryConfiguration()
	{
		classUnderTest.invalidateCartEntryConfiguration(mockOrderEntry);
		verify(mockOrderEntry, atLeastOnce()).setProductConfiguration(null);
	}

	@Test
	public void testUpdateOrderEntryOnUpdate()
	{
		classUnderTest.updateAbstractOrderEntryOnUpdate(CONFIG_ID, mockOrderEntry);
		verify(mockExternalConfigurationAccess, never()).setExternalConfiguration(EXT_CONFIG, mockOrderEntry);
	}

	@Test
	public void testUpdateOrderEntryOnLink()
	{
		classUnderTest.updateAbstractOrderEntryOnLink(mockCommerceCartParameter, mockOrderEntry);
		verify(mockExternalConfigurationAccess, never()).setExternalConfiguration(EXT_CONFIG, mockOrderEntry);
	}

	@Test
	public void testPrepareForOrderReplication()
	{
		classUnderTest.prepareForOrderReplication(mockOrderEntry);
		verify(mockExternalConfigurationAccess, atLeastOnce()).setExternalConfiguration(EXT_CONFIG, mockOrderEntry);
	}

	@Test(expected = NullPointerException.class)
	public void testPrepareForOrderReplicationNoConfig()
	{
		when(mockOrderEntry.getProductConfiguration()).thenReturn(null);
		when(mockOrderEntry.getProductConfigurationDraft()).thenReturn(null);
		classUnderTest.prepareForOrderReplication(mockOrderEntry);
	}

	@Test
	public void testConfigurationLifecycleStrategy()
	{
		assertEquals(mockConfigurationLifecycleStrategy, classUnderTest.getConfigurationLifecycleStrategy());
	}

	@Test
	public void testReleaseDraft()
	{
		classUnderTest.releaseDraft(this.mockOrderEntry);
		verify(mockConfigurationLifecycleStrategy).releaseSession(CONFIG_ID);
	}

	@Test
	public void testReleaseDraftNoDraft()
	{
		given(mockOrderEntry.getProductConfigurationDraft()).willReturn(null);
		classUnderTest.releaseDraft(this.mockOrderEntry);
		verify(mockConfigurationLifecycleStrategy, times(0)).releaseSession(CONFIG_ID);
	}

	@Test
	public void testIsRuntimeConfigForEntryExistingTrue()
	{
		assertTrue(classUnderTest.isRuntimeConfigForEntryExisting(mockOrderEntry));
	}

	@Test
	public void testIsRuntimeConfigForEntryExistingFalse()
	{
		given(mockOrderEntry.getProductConfiguration()).willReturn(null);
		assertFalse(classUnderTest.isRuntimeConfigForEntryExisting(mockOrderEntry));
	}


	@Test
	public void testRefreshCartEntryConfiguration()
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setRelatedObjectType(ProductConfigurationRelatedObjectType.CART_ENTRY);
		given(mockConfigurationService.retrieveExternalConfiguration(CONFIG_ID)).willReturn(EXT_CONFIG);
		given(mockConfigurationService.createConfigurationFromExternal(Mockito.any(KBKeyImpl.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(ConfigurationRetrievalOptions.class))).willReturn(mockConfigModel);
		final ConfigModel retrievedConfigModel = classUnderTest.refreshCartEntryConfiguration(mockOrderEntry);
		verify(mockConfigurationLifecycleStrategy).releaseSession(CONFIG_ID);
		assertEquals(mockConfigModel, retrievedConfigModel);
	}
}

