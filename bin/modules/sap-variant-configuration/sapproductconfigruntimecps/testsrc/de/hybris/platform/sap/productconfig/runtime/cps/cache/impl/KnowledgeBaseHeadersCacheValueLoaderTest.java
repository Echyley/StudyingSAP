/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.sap.productconfig.runtime.cps.ProductConfigurationPassportService;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.scripting.engine.internal.cache.impl.SimpleScriptCacheKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;

import rx.Observable;


/**
 * Unit test for {@link KnowledgeBaseHeadersCacheValueLoader}
 */
@UnitTest
public class KnowledgeBaseHeadersCacheValueLoaderTest
{
	private static final String CPS_SERVICE_TENANT = "cps service tenant";
	private static final String CPS_SERVICE_URL = "cps service url";
	private static final String TENANT_ID = "tenantId";
	private static final String PRODUCT = "product";
	private static final String SAP_PASSPORT = "pass";
	private KnowledgeBaseHeadersCacheValueLoader classUnderTest;
	@Mock
	private ProductConfigurationCacheKey paramCacheKey;
	private List<CPSMasterDataKBHeaderInfo> kbHeaders;
	@Mock
	private KbDeterminationClient client;

	@Mock
	private ApiRegistryClientService apiRegistryClientService;

	@Mock
	private ProductConfigurationPassportService productConfigurationPassportService;

	private final Map<String, String> keyMap = new HashMap();

	@Before
	public void setup() throws CredentialException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new KnowledgeBaseHeadersCacheValueLoader();
		classUnderTest.setClient(client);
		Mockito.when(apiRegistryClientService.lookupClient(KbDeterminationClient.class)).thenReturn(client);

		classUnderTest.setApiRegistryClientService(apiRegistryClientService);
		classUnderTest.setProductConfigurationPassportService(productConfigurationPassportService);
		Mockito.when(productConfigurationPassportService.generate(KnowledgeBaseHeadersCacheValueLoader.PASSPORT_GET_KB))
				.thenReturn(SAP_PASSPORT);
		kbHeaders = new ArrayList<>();
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_PRODUCT, PRODUCT);
		Mockito.when(paramCacheKey.getKeys()).thenReturn(keyMap);
	}

	@Test
	public void testPassportService()
	{
		assertEquals(productConfigurationPassportService, classUnderTest.getProductConfigurationPassportService());
	}

	@Test
	public void testLoadNotNull()
	{
		final Observable<List<CPSMasterDataKBHeaderInfo>> kbObservable = Observable.from(Arrays.asList(kbHeaders));
		Mockito.when(client.getKnowledgebases(SAP_PASSPORT, PRODUCT)).thenReturn(kbObservable);
		final List<CPSMasterDataKBHeaderInfo> result = classUnderTest.load(paramCacheKey);
		Mockito.verify(client).getKnowledgebases(SAP_PASSPORT, PRODUCT);
		assertNotNull(result);
		assertEquals(kbHeaders, result);
	}

	@Test(expected = CacheValueLoadException.class)
	public void testInvalidCacheKeyClass()
	{
		final CacheKey paramCacheKey = new SimpleScriptCacheKey("protocol", "path", TENANT_ID);
		classUnderTest.load(paramCacheKey);
	}

	@Test
	public void testGetClientCreated()
	{
		classUnderTest.setClient(null);
		final KbDeterminationClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test
	public void testGetClientExisting()
	{
		final KbDeterminationClientBase result = classUnderTest.getClient();
		assertEquals(client, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClientNotFound() throws CredentialException
	{
		classUnderTest.setClient(null);
		Mockito.when(apiRegistryClientService.lookupClient(KbDeterminationClient.class)).thenThrow(CredentialException.class);
		classUnderTest.getClient();

	}

	@Test
	public void testGetKbHeadersFromService()
	{
		final Observable<List<CPSMasterDataKBHeaderInfo>> kbObservable = Observable.from(Arrays.asList(kbHeaders));
		Mockito.when(client.getKnowledgebases(SAP_PASSPORT, PRODUCT)).thenReturn(kbObservable);
		classUnderTest.getKbHeadersFromService(PRODUCT);
		Mockito.verify(client).getKnowledgebases(SAP_PASSPORT, PRODUCT);
	}

	@Test
	public void testGetKbHeadersFromServiceException()
	{
		final HttpException httpEx = new HttpException(Integer.valueOf(666), "Evil exception");
		Mockito.when(client.getKnowledgebases(SAP_PASSPORT, PRODUCT)).thenThrow(httpEx);
		try
		{
			classUnderTest.getKbHeadersFromService(PRODUCT);
		}
		catch (final CacheValueLoadException ex)
		{
			assertEquals(httpEx, ex.getCause());
		}
	}
}
