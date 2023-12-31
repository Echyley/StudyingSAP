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
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.scripting.engine.internal.cache.impl.SimpleScriptCacheKey;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;

import rx.Observable;


/**
 * Unit test for {@link KnowledgeBaseContainerCacheValueLoader}
 */
@UnitTest
public class KnowledgeBaseContainerCacheValueLoaderTest
{
	private static final String CPS_SERVICE_TENANT = "cps service tenant";
	private static final String CPS_SERVICE_URL = "cps service url";
	private static final String TENANT_ID = "tenantId";
	private static final String LANG = "lang";
	private static final String KB_ID = "kbId";
	private static final String SAP_PASSPORT = "passport";
	private KnowledgeBaseContainerCacheValueLoader classUnderTest;
	@Mock
	private ProductConfigurationCacheKey paramCacheKey;
	private CPSMasterDataKnowledgeBase kb;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	@Mock
	private MasterDataClient client;
	@Mock
	private Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> knowledgeBaseConverter;

	@Mock
	private ApiRegistryClientService apiRegistryClientService;

	@Mock
	private ProductConfigurationPassportService productConfigurationPassportService;

	private final Map<String, String> cacheKeys = new HashMap<>();

	@Before
	public void setup() throws CredentialException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new KnowledgeBaseContainerCacheValueLoader();
		classUnderTest.setClient(client);
		classUnderTest.setKnowledgeBaseConverter(knowledgeBaseConverter);
		classUnderTest.setProductConfigurationPassportService(productConfigurationPassportService);
		Mockito.when(apiRegistryClientService.lookupClient(MasterDataClient.class)).thenReturn(client);
		Mockito.when(productConfigurationPassportService.generate(KnowledgeBaseContainerCacheValueLoader.PASSPORT_GET_KB))
				.thenReturn(SAP_PASSPORT);

		classUnderTest.setApiRegistryClientService(apiRegistryClientService);
		kb = new CPSMasterDataKnowledgeBase();
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		cacheKeys.put(CPSCacheKeyGeneratorImpl.KEY_KB_ID, KB_ID);
		cacheKeys.put(CPSCacheKeyGeneratorImpl.KEY_LANGUAGE, LANG);
		Mockito.when(paramCacheKey.getKeys()).thenReturn(cacheKeys);
	}

	@Test
	public void testProductConfigurationPassportService()
	{
		assertEquals(productConfigurationPassportService, classUnderTest.getProductConfigurationPassportService());
	}

	@Test
	public void testLoadNotNull()
	{
		final Observable<CPSMasterDataKnowledgeBase> kbObservable = Observable.from(Arrays.asList(kb));
		Mockito.when(client.getKnowledgebase(KB_ID, LANG, SAP_PASSPORT,
				SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION)).thenReturn(kbObservable);
		Mockito.when(knowledgeBaseConverter.convert(kb)).thenReturn(kbContainer);
		final CPSMasterDataKnowledgeBaseContainer result = classUnderTest.load(paramCacheKey);
		Mockito.verify(client).getKnowledgebase(KB_ID, LANG, SAP_PASSPORT,
				SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION);
		Mockito.verify(knowledgeBaseConverter).convert(kb);
		assertNotNull(result);
		assertEquals(kbContainer, result);
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

		final MasterDataClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClientNotFound() throws CredentialException
	{
		classUnderTest.setClient(null);
		Mockito.when(apiRegistryClientService.lookupClient(MasterDataClient.class)).thenThrow(CredentialException.class);
		classUnderTest.getClient();

	}

	@Test
	public void testGetClientExisting()
	{
		final MasterDataClientBase result = classUnderTest.getClient();
		assertEquals(client, result);
	}

	@Test
	public void testGetKbFromService()
	{
		final Observable<CPSMasterDataKnowledgeBase> kbObservable = Observable.from(Arrays.asList(kb));
		Mockito.when(client.getKnowledgebase(KB_ID, LANG, SAP_PASSPORT,
				SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION)).thenReturn(kbObservable);
		classUnderTest.getKbFromService(KB_ID, LANG);
		Mockito.verify(client).getKnowledgebase(KB_ID, LANG, SAP_PASSPORT,
				SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION);
	}

	@Test
	public void testGetKbFromServiceException()
	{
		final HttpException httpEx = new HttpException(Integer.valueOf(666), "Evil exception");
		Mockito.when(client.getKnowledgebase(KB_ID, LANG, SAP_PASSPORT,
				SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION)).thenThrow(httpEx);
		try
		{
			classUnderTest.getKbFromService(KB_ID, LANG);
		}
		catch (final CacheValueLoadException ex)
		{
			assertEquals(httpEx, ex.getCause());
		}
	}
}
