/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link KnowledgeBaseHeadersCacheAccessServiceImpl}
 */
@UnitTest
public class KnowledgeBaseHeadersCacheAccessServiceImplTest
{
	private static final String PRODUCT = "product";
	private KnowledgeBaseHeadersCacheAccessServiceImpl classUnderTest;

	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<CPSMasterDataKBHeaderInfo>> cache;
	@Mock
	private CacheValueLoader<List<CPSMasterDataKBHeaderInfo>> loader;
	@Mock
	private CPSCacheKeyGenerator keyGenerator;

	private List<CPSMasterDataKBHeaderInfo> kbHeaderList;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new KnowledgeBaseHeadersCacheAccessServiceImpl();
		classUnderTest.setCache(cache);
		classUnderTest.setLoader(loader);
		classUnderTest.setKeyGenerator(keyGenerator);

		kbHeaderList = new ArrayList<>();

		Mockito.when(cache.getWithLoader(Mockito.any(), Mockito.any())).thenReturn(kbHeaderList);
	}

	@Test
	public void testGetKBHeaders()
	{
		final List<CPSMasterDataKBHeaderInfo> result = classUnderTest.getKnowledgeBases(PRODUCT);
		Mockito.verify(cache).getWithLoader(Mockito.any(), Mockito.any());
		assertNotNull(result);
		assertEquals(kbHeaderList, result);
	}
}
