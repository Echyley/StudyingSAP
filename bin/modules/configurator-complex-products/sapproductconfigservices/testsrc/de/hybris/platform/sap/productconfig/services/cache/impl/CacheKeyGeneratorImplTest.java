/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for {@link CacheKeyGeneratorImpl}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CacheKeyGeneratorImplTest
{
	private static final String USER_ID = "user id";
	private static final String CONFIG_ID = "config id";
	private static final String PRODUCT_CODE = "product code";
	private static final String CURRENCY_CODE = "currency code";


	@InjectMocks
	private CacheKeyGeneratorImpl classUnderTest;

	@Mock
	private ProductConfigurationUserIdProvider userIdProvider;

	@Mock
	private SessionService sessionService;

	@Mock
	private CommonI18NService i18NService;
	@Mock
	private CurrencyModel currencyModel;

	@Before
	public void setup()
	{
		when(userIdProvider.getCurrentUserId()).thenReturn(USER_ID);
		when(userIdProvider.isAnonymousUser()).thenReturn(false);
		when(i18NService.getCurrentCurrency()).thenReturn(currencyModel);
		when(currencyModel.getIsocode()).thenReturn(CURRENCY_CODE);
	}

	private void mockAnotherContext()
	{
		// if all works fine these stubbing's are never called, hence mark them as lenient
		// we could also reset these mocks, but that would create an NPE in error case,
		// while with stubbing's we get a nice error, that keys do not equal
		lenient().when(userIdProvider.getCurrentUserId()).thenReturn("s123");
		lenient().when(userIdProvider.isAnonymousUser()).thenReturn(true);
		lenient().when(i18NService.getCurrentCurrency()).thenReturn(currencyModel);
		lenient().when(currencyModel.getIsocode()).thenReturn("another code");
		Mockito.reset(sessionService);
	}

	@Test
	public void testSessionService()
	{
		assertEquals(sessionService, classUnderTest.getSessionService());
	}

	@Test
	public void testSessionBoundToConfiguration()
	{
		assertTrue(classUnderTest.isSessionBoundToConfiguration());
	}

	@Test
	public void testSessionBoundToConfigurationAttributeNoBoolean()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS)).thenReturn("Huhu");
		assertTrue(classUnderTest.isSessionBoundToConfiguration());
	}

	@Test
	public void testSessionBoundToConfigurationOCC()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS))
				.thenReturn(Boolean.valueOf(true));
		assertFalse(classUnderTest.isSessionBoundToConfiguration());
	}

	@Test
	public void testCreatePriceSummaryCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createPriceSummaryCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_PRICE_SUMMARY, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(3, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_USER_ID));
		assertEquals(CURRENCY_CODE, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CURRENCY));
	}

	@Test
	public void testCreateAnalyticsDataCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createAnalyticsDataCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_ANALYTICS_DATA, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(2, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_USER_ID));
	}

	@Test
	public void testCreateConfigCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createConfigCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_CONFIG, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(2, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_CONFIG_ID));
		assertEquals(USER_ID, result.getKeys().get(CacheKeyGeneratorImpl.KEY_USER_ID));
	}

	@Test
	public void testCreateClassificationSystemCPQAttributesCacheKey()
	{
		final ProductConfigurationCacheKey result = classUnderTest.createClassificationSystemCPQAttributesCacheKey(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(CacheKeyGeneratorImpl.TYPECODE_CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(1, result.getKeys().size());
		assertEquals(PRODUCT_CODE, result.getKeys().get(CacheKeyGeneratorImpl.KEY_PRODUCT_CODE));
	}

	@Test
	public void testGetTenantId()
	{
		final String result = classUnderTest.getTenantId();
		if (Registry.hasCurrentTenant())
		{
			assertEquals(Registry.getCurrentTenant().getTenantID(), result);
		}
		else
		{
			assertEquals(CacheKeyGeneratorImpl.NO_ACTIVE_TENANT, result);
		}
	}

	@Test
	public void testGetCurrencyIsoNull()
	{
		when(i18NService.getCurrentCurrency()).thenReturn(null);
		assertEquals("null", classUnderTest.getCurrencyIso());
	}

	@Test
	public void testPopulateCacheKeyContextComponents()
	{
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		assertEquals(5, map.size());
		assertEquals(USER_ID, map.get(CacheKeyGeneratorImpl.KEY_USER_ID));
		assertEquals("true", map.get(CacheKeyGeneratorImpl.KEY_IS_SESSION_BOUND_TO_CONFIG));
		assertEquals("false", map.get(CacheKeyGeneratorImpl.KEY_IS_ANONYMOUS));
		assertEquals(CURRENCY_CODE, map.get(CacheKeyGeneratorImpl.KEY_CURRENCY));
		assertEquals(classUnderTest.getTenantId(), map.get(CacheKeyGeneratorImpl.KEY_TENANT_ID));
	}

	@Test
	public void voidTestConfigKeyWithUserIdCanBeReconstructed()
	{
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		final ProductConfigurationCacheKey expected = classUnderTest.createConfigCacheKey(CONFIG_ID);
		mockAnotherContext(); // data should be taken from map,
		assertEquals(expected, classUnderTest.createConfigCacheKey(CONFIG_ID, map));
	}

	@Test
	public void voidTestConfigKeyWithoutUserIdCanBeReconstructed()
	{
		when(sessionService.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS))
				.thenReturn(Boolean.valueOf(true));
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		final ProductConfigurationCacheKey expected = classUnderTest.createConfigCacheKey(CONFIG_ID);
		mockAnotherContext(); // data should be taken from map,
		assertEquals(expected, classUnderTest.createConfigCacheKey(CONFIG_ID, map));
	}

	@Test
	public void voidTestConfigKeyWithoutSessionIdCanBeReconstructed()
	{
		when(userIdProvider.isAnonymousUser()).thenReturn(true);
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		final ProductConfigurationCacheKey expected = classUnderTest.createConfigCacheKey(CONFIG_ID);
		mockAnotherContext(); // data should be taken from map,
		assertEquals(expected, classUnderTest.createConfigCacheKey(CONFIG_ID, map));
	}

	@Test
	public void voidTestPricingKeyCanBeReconstructed()
	{
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		final ProductConfigurationCacheKey expected = classUnderTest.createPriceSummaryCacheKey(CONFIG_ID);
		mockAnotherContext(); // data should be taken from map,
		assertEquals(expected, classUnderTest.createPriceSummaryCacheKey(CONFIG_ID, map));
	}

	@Test
	public void voidTestAnalyticsKeyCanBeReconstructed()
	{
		final Map<String, String> map = new HashMap();
		classUnderTest.populateCacheKeyContextAttributes(map);
		final ProductConfigurationCacheKey expected = classUnderTest.createAnalyticsDataCacheKey(CONFIG_ID);
		mockAnotherContext(); // data should be taken from map,
		assertEquals(expected, classUnderTest.createAnalyticsDataCacheKey(CONFIG_ID, map));
	}

}
