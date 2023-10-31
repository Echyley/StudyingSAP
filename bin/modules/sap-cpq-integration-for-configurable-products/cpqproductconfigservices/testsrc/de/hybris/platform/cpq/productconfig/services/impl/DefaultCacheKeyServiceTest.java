/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.cpq.productconfig.services.cache.DefaultCacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultCacheKeyService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCacheKeyServiceTest
{
	private static final String BASE_SITE = "current base site";
	private static final String CONFIG_ID = "configuration guid";

	@InjectMocks
	private DefaultCacheKeyService classUnderTest;

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BaseSiteModel baseSite;


	@Test
	public void testCreateAnalyticsDataCacheKey()
	{
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getUid()).thenReturn(BASE_SITE);
		final DefaultCacheKey result = classUnderTest.createAuthorizationDataCacheKey();
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(DefaultCacheKeyService.TYPECODE_AUTHORIZATION_DATA, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(1, result.getKeys().size());
		assertEquals(BASE_SITE, result.getKeys().get(DefaultCacheKeyService.KEY_BASE_SITE));
	}

	@Test
	public void testCreateAnalyticsDataCacheKeyWithExternalBaseSite()
	{
		final DefaultCacheKey result = classUnderTest.createAuthorizationDataCacheKey(BASE_SITE);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(DefaultCacheKeyService.TYPECODE_AUTHORIZATION_DATA, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(1, result.getKeys().size());
		assertEquals(BASE_SITE, result.getKeys().get(DefaultCacheKeyService.KEY_BASE_SITE));
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
			assertEquals(DefaultCacheKeyService.NO_ACTIVE_TENANT, result);
		}
	}

	@Test
	public void testCreateConfigurationSummaryCacheKey()
	{
		final DefaultCacheKey result = classUnderTest.createConfigurationSummaryCacheKey(CONFIG_ID);
		assertNotNull(result);
		assertEquals(CacheUnitValueType.SERIALIZABLE, result.getCacheValueType());
		assertEquals(DefaultCacheKeyService.TYPECODE_CONFIGURATION_SUMMARY, result.getTypeCode());
		assertNotNull(result.getTenantId());
		assertNotNull(result.getKeys());
		assertEquals(1, result.getKeys().size());
		assertEquals(CONFIG_ID, result.getKeys().get(DefaultCacheKeyService.KEY_CONFIG_ID));
	}
}
