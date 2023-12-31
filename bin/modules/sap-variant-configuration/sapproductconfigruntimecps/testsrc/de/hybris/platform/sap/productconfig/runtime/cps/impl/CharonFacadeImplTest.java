/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.ProductConfigurationPassportService;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.KnowledgebaseKeyComparator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.session.impl.CPSResponseAttributeStrategyImpl;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CPSConfigurationParentReferenceStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.NewCookie;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;

import rx.Observable;


/**
 * Unit test for {@link CharonFacadeImpl}
 */
@UnitTest
public class CharonFacadeImplTest
{
	protected CharonFacadeImpl classUnderTest = new CharonFacadeImpl();
	protected static final String PRODUCT_CODE = "ProductCode";
	protected static final String EXTERNAL_CONFIG_STRING = "external configuration string";
	protected static final Integer KB_ID = Integer.valueOf(1234);
	protected static final String COOKIE_NAME = "CookieName";
	protected static final String COOKIE_VALUE = "CookieValue";
	protected static final String COOKIE_AS_STRING = "CookieName=CookieValue";
	protected static final String CONFIG_ID = "99";
	protected static final String VERSION_SUCCESSOR = "successor to eTag";
	protected static final String VERSION = "eTag";
	protected static final String OLD_VERSION = "old eTag";
	protected static final String SUB_ITEM_CSTIC_VALUE = "SubItemCsticValue";
	protected static final String SUB_ITEM_CSTIC = "SubItemCstic";
	protected static final Locale LOCALE = new Locale("en-GB");
	protected static final String LANG = LOCALE.toLanguageTag();
	protected static final String ITEM_ID = "1";
	protected static final String GROUP_ID = "Group";
	protected static final String GROUP_ID_2 = "GROUP 2";
	protected static final String CSTIC_ID = "Cstic";
	protected static final String CSTIC_ID2 = "Cstic2";
	private static final String SAP_PASSPORT = "passport";
	private static final String COMPOSED_GROUP_ID = "1-$1-ROOT@Group";
	private static final String FILTER = "characteristicGroup IN ['Group']";
	protected final CPSConfiguration configuration = new CPSConfiguration();
	protected final CPSItem rootItem = new CPSItem();
	protected final CPSCharacteristicGroup group = new CPSCharacteristicGroup();
	protected final CPSCharacteristic characteristic = new CPSCharacteristic();
	protected final CPSCharacteristic characteristic2 = new CPSCharacteristic();
	protected final List<NewCookie> responseCookies = new ArrayList<NewCookie>();
	protected final CPSExternalConfiguration externalConfiguration = new CPSExternalConfiguration();
	protected final CPSCommerceExternalConfiguration externalConfigurationCommerceFormat = new CPSCommerceExternalConfiguration();

	private final UniqueKeyGenerator keyGenerator = new UniqueKeyGeneratorImpl();
	@Mock
	protected CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy;
	protected final Optional<String> optinalETag = Optional.of(VERSION);
	protected final Optional<String> optinalETagSuccessor = Optional.of(VERSION_SUCCESSOR);
	protected final List<String> cookieList = new ArrayList<>();

	protected CPSCharacteristicInput changes;
	protected Observable<String> emptyResponseObservable;
	protected Observable<RawResponse> eTagRawResponseObservable;
	protected Observable<RawResponse> eTagRawResponseObservableSuccessor;
	protected Observable<RawResponse<CPSConfiguration>> rawResponseObservable;
	protected Observable<RawResponse<CPSItem>> cpsItemObservable;

	@Mock
	protected RequestErrorHandler errorHandler;
	@Mock
	protected CPSResponseAttributeStrategyImpl cookieHandler;
	@Mock
	protected NewCookie cookie;
	@Mock
	protected ConfigurationClient client;

	@Mock
	protected I18NService i18NService;
	@Mock
	protected ObjectMapper objectMapperMock;
	@Mock
	protected RawResponse<CPSConfiguration> rawResponse;
	@Mock
	protected RawResponse<CPSItem> cpsItemRawResponse;
	@Mock
	protected RawResponse<CPSConfiguration> eTagRawResponse;
	@Mock
	protected RawResponse<CPSConfiguration> eTagRawResponseSuccessor;
	@Mock
	protected CPSContextSupplier contextSupplier;
	@Mock
	protected CPSCache cpsCache;
	@Mock
	protected CPSConfigurationParentReferenceStrategy configurationParentReferenceStrategy;
	@Mock
	protected RuntimeException runtimeExceptionWrappingTimeout;
	@Mock
	protected TimeoutException timeoutException;
	@Mock
	protected RuntimeException runtimeExceptionWrappingNPE;
	@Mock
	protected RawResponse<CPSConfiguration> configurationGetResponse;
	@Mock
	private KnowledgebaseKeyComparator kbKeyComparator;
	@Mock
	private ApiRegistryClientService apiRegistryClientService;
	@Mock
	private ProductConfigurationPassportService sapPassportService;

	private final ConfigModel configModel = new ConfigModelImpl();
	private final List<String> groupNames = Arrays.asList(GROUP_ID, GROUP_ID_2);




	@Before
	public void setup() throws ConfigurationEngineException, CredentialException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setKeyGenerator(keyGenerator);
		classUnderTest.setReadDomainValuesOnDemand(false);
		classUnderTest.setRequestErrorHandler(errorHandler);
		classUnderTest.setApiRegistryClientService(apiRegistryClientService);

		classUnderTest.setContextSupplier(contextSupplier);
		classUnderTest.setI18NService(i18NService);
		classUnderTest.setConfigurationParentReferenceStrategy(configurationParentReferenceStrategy);
		classUnderTest.setCommerceExternalConfigurationStrategy(commerceExternalConfigurationStrategy);
		classUnderTest.setKbKeyComparator(kbKeyComparator);
		classUnderTest.setPassportService(sapPassportService);
		Mockito.when(sapPassportService.generate(Mockito.anyString())).thenReturn(SAP_PASSPORT);
		Mockito.when(i18NService.getCurrentLocale()).thenReturn(LOCALE);

		Mockito.when(cookie.getName()).thenReturn(COOKIE_NAME);
		Mockito.when(cookie.getValue()).thenReturn(COOKIE_VALUE);
		Mockito.when(apiRegistryClientService.lookupClient(ConfigurationClient.class)).thenReturn(client);

		Mockito.when(runtimeExceptionWrappingTimeout.getCause()).thenReturn(timeoutException);

		cookieList.add(COOKIE_AS_STRING);
		cookieList.add(COOKIE_AS_STRING);

		Mockito.when(cpsCache.getCookies(Mockito.anyString())).thenReturn(cookieList);
		//2 cookies sufficient, we don't care that their content is the same
		responseCookies.add(cookie);
		responseCookies.add(cookie);
		classUnderTest.setResponseStrategy(cookieHandler);
		configuration.setId(CONFIG_ID);
		configuration.setETag(OLD_VERSION);
		classUnderTest.setCache(cpsCache);
		when(cookieHandler.getCookiesAsString(CONFIG_ID)).thenReturn(cookieList);
		changes = new CPSCharacteristicInput();
		changes.setValues(new ArrayList<>());

		configuration.setRootItem(rootItem);
		rootItem.setId(ITEM_ID);
		rootItem.setCharacteristicGroups(new ArrayList<>());
		rootItem.setSubItems(new ArrayList<>());
		rootItem.setCharacteristics(new ArrayList<CPSCharacteristic>());

		group.setId(GROUP_ID);
		addRuntimeCsticGroup(rootItem, group);
		characteristic.setId(CSTIC_ID);
		characteristic.setPossibleValues(new ArrayList<>());
		characteristic.setValues(new ArrayList<>());
		characteristic2.setId(CSTIC_ID2);
		characteristic2.setPossibleValues(new ArrayList<>());
		characteristic2.setValues(new ArrayList<>());
		addRuntimeCstic(rootItem, characteristic);

		externalConfiguration.setComplete(false);
		externalConfiguration.setConsistent(true);

		final CPSExternalObjectKey objKey = new CPSExternalObjectKey();
		objKey.setId("PRODUCTCODE");
		final CPSExternalItem extRootItem = new CPSExternalItem();
		extRootItem.setObjectKey(objKey);
		externalConfiguration.setRootItem(extRootItem);

		rawResponseObservable = Observable.from(Arrays.asList(rawResponse));
		cpsItemObservable = Observable.from(Arrays.asList(cpsItemRawResponse));
		when(rawResponse.content()).thenReturn(Observable.from(Arrays.asList(configuration)));
		when(cpsItemRawResponse.content()).thenReturn(Observable.from(Arrays.asList(rootItem)));
		emptyResponseObservable = Observable.from(Arrays.asList("Hello"));
		eTagRawResponseObservable = Observable.from(Arrays.asList(eTagRawResponse));
		eTagRawResponseObservableSuccessor = Observable.from(Arrays.asList(eTagRawResponseSuccessor));
		Mockito.when(client.updateConfiguration(changes, CONFIG_ID, ITEM_ID, CSTIC_ID, COOKIE_AS_STRING, COOKIE_AS_STRING,
				OLD_VERSION, SAP_PASSPORT)).thenReturn(eTagRawResponseObservable);
		Mockito.when(client.deleteConfiguration(CONFIG_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, OLD_VERSION, SAP_PASSPORT))
				.thenReturn(emptyResponseObservable);
		Mockito.when(client.deleteConfiguration(CONFIG_ID, OLD_VERSION, SAP_PASSPORT)).thenReturn(emptyResponseObservable);
		Mockito
				.when(client.createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
						CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL))
				.thenReturn(rawResponseObservable);
		Mockito.when(client.getGroup(CONFIG_ID, ITEM_ID, FILTER, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT))
				.thenReturn(cpsItemObservable);
		Mockito.when(client.getGroup(CONFIG_ID, ITEM_ID, FILTER, LANG, SAP_PASSPORT)).thenReturn(cpsItemObservable);
		final Observable<CPSConfiguration> obsCPSConfig = Observable.from(Arrays.asList(configuration));
		when(configurationGetResponse.content()).thenReturn(obsCPSConfig);
		when(configurationGetResponse.eTag()).thenReturn(this.optinalETag);
		when(commerceExternalConfigurationStrategy.createCommerceFormatFromCPSRepresentation(externalConfiguration))
				.thenReturn(externalConfigurationCommerceFormat);
		when(commerceExternalConfigurationStrategy.extractCPSFormatFromCommerceRepresentation(externalConfigurationCommerceFormat))
				.thenReturn(externalConfiguration);
		externalConfigurationCommerceFormat.setExternalConfiguration(externalConfiguration);
		when(cookieHandler.retrieveETagAndSaveResponseAttributes(eTagRawResponse, CONFIG_ID)).thenReturn(VERSION);
		when(cookieHandler.retrieveETagAndSaveResponseAttributes(eTagRawResponseSuccessor, CONFIG_ID))
				.thenReturn(VERSION_SUCCESSOR);
	}

	@Test
	public void testPassportService()
	{
		assertEquals(sapPassportService, classUnderTest.getPassportService());
	}

	@Test
	public void testApiRegistryClientService()
	{
		classUnderTest.setApiRegistryClientService(apiRegistryClientService);
		assertEquals(apiRegistryClientService, classUnderTest.getApiRegistryClientService());
	}

	@Test
	public void testAssembleCreateDefaultConfigurationRequest()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		final CPSCreateConfigInput result = classUnderTest.assembleCreateDefaultConfigurationRequest(kbKey);
		Mockito.verify(contextSupplier).retrieveContext(kbKey.getProductCode());
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result.getProductKey());
	}



	@Test
	public void testCreateDefaultErrorHandlerCalled()
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createDefaultConfiguration(any(), any(), any(), any(), any());
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		classUnderTest.createDefaultConfiguration(kbKey);
		verify(errorHandler).processCreateDefaultConfigurationError(ex);
	}

	@Test
	public void testCreateDefault()
	{
		classUnderTest.setClient(client);
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		when(client.createDefaultConfiguration(any(), any(), any(), any(), eq(CharonFacadeImpl.GET_CONFIG_SELECT_ALL)))
				.thenReturn(rawResponseObservable);
		final CPSConfiguration createDefaultConfiguration = classUnderTest.createDefaultConfiguration(kbKey);
		assertSame(configuration, createDefaultConfiguration);
		verify(cpsCache).setConfiguration(CONFIG_ID, configuration);
	}

	@Test
	public void testCreateDefaultWithoutDomainValues()
	{
		classUnderTest.setClient(client);
		classUnderTest.setReadDomainValuesOnDemand(true);
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		when(client.createDefaultConfiguration(any(), any(), any(), any(),
				eq(CharonFacadeImpl.GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES))).thenReturn(rawResponseObservable);
		final CPSConfiguration createDefaultConfiguration = classUnderTest.createDefaultConfiguration(kbKey);
		assertSame(configuration, createDefaultConfiguration);
		verify(cpsCache).setConfiguration(CONFIG_ID, configuration);
	}

	@Test
	public void testGetExternalErrorHandlerCalled() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).getExternalConfiguration(any(), any(), any(), any());
		classUnderTest.getExternalConfiguration(CONFIG_ID);
		verify(errorHandler).processGetExternalConfigurationError(ex, CONFIG_ID);
	}

	@Test
	public void testDeleteErrorHandlerCalled()
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).deleteConfiguration(any(), any(), any(), Mockito.any(), any());
		classUnderTest.releaseSession(CONFIG_ID, VERSION);
		Mockito.verify(errorHandler).processDeleteConfigurationError(ex);
	}

	@Test
	public void testCreateConfigurationFromExternalErrorHandlerCalled()
			throws JsonParseException, JsonMappingException, IOException
	{
		Mockito.when(objectMapperMock.readValue(EXTERNAL_CONFIG_STRING, CPSCommerceExternalConfiguration.class))
				.thenReturn(externalConfigurationCommerceFormat);
		classUnderTest.setObjectMapper(objectMapperMock);
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createRuntimeConfigurationFromExternal(any(), any(), any(),
				eq(CharonFacadeImpl.GET_CONFIG_SELECT_ALL));
		classUnderTest.createConfigurationFromExternal(EXTERNAL_CONFIG_STRING, "CONTEXT_PRODUCT");
		verify(errorHandler).processCreateRuntimeConfigurationFromExternalError(ex);
	}

	@Test
	public void testGetExternalConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final Observable<CPSExternalConfiguration> externalConfigObs = Observable.from(Arrays.asList(externalConfiguration));
		Mockito.when(client.getExternalConfiguration(CONFIG_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT))
				.thenReturn(externalConfigObs);
		final String result = classUnderTest.getExternalConfiguration(CONFIG_ID);
		checkExternalConfiguration(result);
	}

	protected void checkExternalConfiguration(final String result)
	{
		assertNotNull(result);
		assertTrue(result.contains("complete"));
		assertTrue(result.contains("consistent"));
		assertTrue(result.contains("rootItem"));
		assertFalse(result.contains("non existing field"));
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	public void testGetExternalConfigurationNoCookies() throws ConfigurationEngineException
	{
		ensureCookieHandlerHasNoCookies();
		classUnderTest.setClient(client);
		final Observable<CPSExternalConfiguration> externalConfigObs = Observable.from(Arrays.asList(externalConfiguration));
		Mockito.when(client.getExternalConfiguration(CONFIG_ID, SAP_PASSPORT)).thenReturn(externalConfigObs);
		final String result = classUnderTest.getExternalConfiguration(CONFIG_ID);
		checkExternalConfiguration(result);
	}

	@Test
	public void testGetExternalConfigurationTimeOut() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		Mockito.when(client.getExternalConfiguration(CONFIG_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT))
				.thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.getExternalConfiguration(CONFIG_ID);
		verify(errorHandler).processConfigurationRuntimeException(runtimeExceptionWrappingTimeout, CONFIG_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetExternalConfiguration_invalidResponse() throws JsonProcessingException, ConfigurationEngineException
	{
		final Observable<CPSExternalConfiguration> externalConfigObs = Observable.from(Arrays.asList(externalConfiguration));
		Mockito.when(client.getExternalConfiguration(CONFIG_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT))
				.thenReturn(externalConfigObs);
		Mockito.when(objectMapperMock.writeValueAsString(Mockito.any()))
				.thenThrow(new InvalidFormatException("message", externalConfiguration, CPSExternalConfiguration.class));
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		classUnderTest.getExternalConfiguration(CONFIG_ID);
	}

	@Test
	public void testCreateConfigurationFromExternal() throws JsonParseException, JsonMappingException, IOException
	{
		Mockito.when(objectMapperMock.readValue(EXTERNAL_CONFIG_STRING, CPSCommerceExternalConfiguration.class))
				.thenReturn(externalConfigurationCommerceFormat);
		Mockito
				.when(client.createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
						CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL))
				.thenReturn(rawResponseObservable);
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		final CPSConfiguration result = classUnderTest.createConfigurationFromExternal(EXTERNAL_CONFIG_STRING, "CONTEXT_PRODUCT");
		assertNotNull(result);
		assertEquals(configuration, result);
		Mockito.verify(objectMapperMock).readValue(EXTERNAL_CONFIG_STRING, CPSCommerceExternalConfiguration.class);
		Mockito.verify(client).createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
				CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL);
	}

	@Test
	public void testReleaseSession()
	{
		classUnderTest.setClient(client);
		classUnderTest.releaseSession(CONFIG_ID, OLD_VERSION);
		Mockito.verify(client).deleteConfiguration(CONFIG_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, OLD_VERSION, SAP_PASSPORT);
		Mockito.verify(cpsCache).removeCookies(CONFIG_ID);
		verify(cpsCache).removeConfiguration(CONFIG_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testReleaseSessionNoETag()
	{
		classUnderTest.releaseSession(CONFIG_ID, null);
	}

	@Test
	public void testReleaseSessionNoCookies()
	{
		ensureCookieHandlerHasNoCookies();
		classUnderTest.setResponseStrategy(cookieHandler);
		classUnderTest.setClient(client);
		classUnderTest.releaseSession(CONFIG_ID, OLD_VERSION);
		Mockito.verify(client).deleteConfiguration(CONFIG_ID, OLD_VERSION, SAP_PASSPORT);
	}

	@Test
	public void testReleaseSessionClientException()
	{
		final HttpException httpException = new HttpException(Integer.valueOf(404), "Not found");
		when(client.deleteConfiguration(CONFIG_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, OLD_VERSION, SAP_PASSPORT))
				.thenThrow(httpException);
		classUnderTest.releaseSession(CONFIG_ID, OLD_VERSION);
		verify(cpsCache).removeCookies(CONFIG_ID);
		verify(cpsCache).removeConfiguration(CONFIG_ID);
		verify(errorHandler).processDeleteConfigurationError(httpException);
	}

	@Test
	public void testExtConfigurationStrategy()
	{
		assertEquals(commerceExternalConfigurationStrategy, classUnderTest.getCommerceExternalConfigurationStrategy());
	}

	@Test
	public void testCreateConfigurationFromExternalTyped() throws JsonParseException, JsonMappingException, IOException
	{
		Mockito
				.when(client.createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
						CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL))
				.thenReturn(rawResponseObservable);
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		final CPSConfiguration result = classUnderTest.createConfigurationFromExternal(externalConfiguration, "CONTEXT_PRODUCT");
		assertNotNull(result);
		assertEquals(configuration, result);
		Mockito.verify(client).createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
				CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL);
		Mockito.verify(contextSupplier).retrieveContext("CONTEXT_PRODUCT");
	}

	@Test
	public void testCreateConfigurationFromExternalTypedWithoutDomainValues()
			throws JsonParseException, JsonMappingException, IOException
	{
		Mockito
				.when(client.createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
						CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES))
				.thenReturn(rawResponseObservable);
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		classUnderTest.setReadDomainValuesOnDemand(true);
		final CPSConfiguration result = classUnderTest.createConfigurationFromExternal(externalConfiguration, "CONTEXT_PRODUCT");
		assertNotNull(result);
		assertEquals(configuration, result);
		Mockito.verify(client).createRuntimeConfigurationFromExternal(SAP_PASSPORT, externalConfiguration,
				CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, CharonFacadeImpl.GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES);
		Mockito.verify(contextSupplier).retrieveContext("CONTEXT_PRODUCT");
	}

	@Test
	public void testGetAutoCleanUP()
	{
		assertEquals(CharonFacadeImpl.AUTO_CLEAN_UP_FALSE, classUnderTest.getAutoCleanUpFlag());
	}

	@Test
	public void testUpdateConfigurationSingleCstic() throws ConfigurationEngineException
	{
		classUnderTest.updateConfiguration(CONFIG_ID, OLD_VERSION, ITEM_ID, CSTIC_ID, changes);
		Mockito.verify(client).updateConfiguration(changes, CONFIG_ID, ITEM_ID, CSTIC_ID, COOKIE_AS_STRING, COOKIE_AS_STRING,
				OLD_VERSION, SAP_PASSPORT);
		verify(cpsCache).removeConfiguration(CONFIG_ID);
	}

	@Test
	public void testUpdateConfigurationSavesCookie() throws ConfigurationEngineException
	{
		mockClientCallWithCsticSuccessor(CONFIG_ID, OLD_VERSION, ITEM_ID, CSTIC_ID);
		when(eTagRawResponseSuccessor.getSetCookies()).thenReturn(responseCookies);
		classUnderTest.updateConfiguration(CONFIG_ID, OLD_VERSION, ITEM_ID, CSTIC_ID, changes);
		Mockito.verify(cookieHandler).retrieveETagAndSaveResponseAttributes(eTagRawResponseSuccessor, CONFIG_ID);
	}

	@Test
	public void testUpdateConfigurationNoETag() throws ConfigurationEngineException
	{
		classUnderTest.updateConfiguration(CONFIG_ID, null, ITEM_ID, CSTIC_ID, changes);
		verify(errorHandler).processConfigurationRuntimeException(any(IllegalStateException.class), eq(CONFIG_ID));
	}

	@Test
	public void testUpdateConfigurationExceptionStillConfigCacheCleared() throws ConfigurationEngineException
	{
		when(client.updateConfiguration(changes, CONFIG_ID, ITEM_ID, CSTIC_ID, COOKIE_AS_STRING, COOKIE_AS_STRING, OLD_VERSION,
				SAP_PASSPORT)).thenThrow(new IllegalStateException());
		classUnderTest.updateConfiguration(CONFIG_ID, OLD_VERSION, ITEM_ID, CSTIC_ID, changes);
		verify(cpsCache).removeConfiguration(CONFIG_ID);
		verify(errorHandler).processConfigurationRuntimeException(any(IllegalStateException.class), eq(CONFIG_ID));
	}

	@Test
	public void testUpdateConfigurationSingleCsticWrongNumberOFCookies() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		cookieList.remove(1);
		Mockito.when(cpsCache.getCookies(Mockito.anyString())).thenReturn(cookieList);
		classUnderTest.updateConfiguration(CONFIG_ID, VERSION, ITEM_ID, CSTIC_ID, changes);
		verify(errorHandler).processConfigurationRuntimeException(any(IndexOutOfBoundsException.class), eq(CONFIG_ID));
	}

	@Test
	public void testCreateCharacteristicInput()
	{
		final CPSValue value = new CPSValue();
		value.setValue("value");
		value.setSelected(true);
		characteristic.getValues().add(value);
		final CPSCharacteristicInput result = classUnderTest.createCharacteristicInput(characteristic);
		assertNotNull(result);
		assertNotNull(result.getValues());
		assertEquals(1, result.getValues().size());
	}

	@Test
	public void testUpdateConfiguration() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		mockClientCallWithCstic(cfgId, OLD_VERSION, itemId, CSTIC_ID);
		when(cookieHandler.retrieveETagAndSaveResponseAttributes(eTagRawResponse, cfgId)).thenReturn(VERSION);
		classUnderTest.setClient(client);
		assertEquals(VERSION, classUnderTest.updateConfiguration(configuration));
		verifyClientCallWithCstic(cfgId, OLD_VERSION, itemId, CSTIC_ID, true);
	}

	@Test
	public void testUpdateConfigurationWithoutCookies() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		ensureCookieHandlerHasNoCookies();
		mockClientCallWithCsticWoCookies(cfgId, itemId, CSTIC_ID);
		when(cookieHandler.retrieveETagAndSaveResponseAttributes(eTagRawResponse, cfgId)).thenReturn(VERSION);

		classUnderTest.setClient(client);
		assertEquals(VERSION, classUnderTest.updateConfiguration(configuration));
		verifyClientCallWithCsticWoCookies(cfgId, itemId, CSTIC_ID, true);
	}

	@Test
	public void testUpdateConfigurationTimeOutExceptionHappens() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		Mockito
				.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(CSTIC_ID),
						Mockito.eq(COOKIE_AS_STRING), Mockito.eq(COOKIE_AS_STRING), Mockito.eq(OLD_VERSION), Mockito.eq(SAP_PASSPORT)))
				.thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.setClient(client);
		classUnderTest.updateConfiguration(configuration);
		verify(errorHandler).processConfigurationRuntimeException(runtimeExceptionWrappingTimeout, CONFIG_ID);
	}

	@Test
	public void testUpdateConfigurationRuntimeExceptionHappens() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		Mockito
				.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(CSTIC_ID),
						Mockito.eq(COOKIE_AS_STRING), Mockito.eq(COOKIE_AS_STRING), Mockito.eq(OLD_VERSION), Mockito.anyString()))
				.thenThrow(runtimeExceptionWrappingNPE);
		classUnderTest.setClient(client);
		classUnderTest.updateConfiguration(configuration);
		verify(errorHandler).processConfigurationRuntimeException(runtimeExceptionWrappingNPE, CONFIG_ID);
	}

	@Test
	public void testUpdateConfigurationMultipleChanges() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		//add another changed cstic
		addRuntimeCstic(rootItem, characteristic2);
		mockClientCallWithCstic(cfgId, OLD_VERSION, itemId, CSTIC_ID);
		mockClientCallWithCsticSuccessor(cfgId, VERSION, itemId, CSTIC_ID2);
		when(cookieHandler.retrieveETagAndSaveResponseAttributes(eTagRawResponse, cfgId)).thenReturn(VERSION);
		when(cookieHandler.retrieveETagAndSaveResponseAttributes(eTagRawResponseSuccessor, cfgId)).thenReturn(VERSION_SUCCESSOR);
		classUnderTest.setClient(client);
		assertEquals(VERSION_SUCCESSOR, classUnderTest.updateConfiguration(configuration));
		verifyClientCallWithCstic(cfgId, OLD_VERSION, itemId, CSTIC_ID, true);
		verifyClientCallWithCstic(cfgId, VERSION, itemId, CSTIC_ID2, true);
	}

	@Test
	public void testUpdateConfigurationNoUpdatePerformed() throws ConfigurationEngineException
	{
		configuration.getRootItem().setCharacteristics(Collections.emptyList());
		assertEquals(OLD_VERSION, classUnderTest.updateConfiguration(configuration));
	}

	@Test
	public void testUpdateCPSCharacteristicForSinglelevel() throws ConfigurationEngineException
	{
		final MutableBoolean updateWasPerformed = new MutableBoolean(false);
		final CPSItem rootItem = configuration.getRootItem();
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();
		final String currentVersion = configuration.getETag();

		mockClientCallWithCstic(cfgId, OLD_VERSION, itemId, CSTIC_ID);
		classUnderTest.setClient(client);
		classUnderTest.updateCPSCharacteristic(updateWasPerformed, rootItem, cfgId, currentVersion, itemId);
		assertTrue(updateWasPerformed.isTrue());
		verifyClientCallWithCstic(cfgId, OLD_VERSION, itemId, CSTIC_ID, true);
	}

	@Test
	public void testUpdateCPSCharacteristicForMultilevel() throws ConfigurationEngineException
	{
		final List<CPSItem> subItems = new ArrayList<>();
		subItems.add(createCPSItem("4"));
		rootItem.setSubItems(subItems);

		final MutableBoolean updateWasPerformed = new MutableBoolean(false);
		final CPSItem subItem = configuration.getRootItem().getSubItems().get(0);
		final String cfgId = configuration.getId();
		final String currentVersion = configuration.getETag();
		final String subitemId = subItem.getId();
		mockClientCallWithCsticSuccessor(cfgId, VERSION, subitemId, SUB_ITEM_CSTIC);
		mockClientCallWithCstic(cfgId, OLD_VERSION, ITEM_ID, CSTIC_ID);
		classUnderTest.setClient(client);
		classUnderTest.updateCPSCharacteristic(updateWasPerformed, rootItem, cfgId, currentVersion, ITEM_ID);
		assertTrue(updateWasPerformed.isTrue());
		verify(client, times(2)).updateConfiguration(any(), eq(CONFIG_ID), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString());
		verifyClientCallWithCstic(cfgId, VERSION, subitemId, SUB_ITEM_CSTIC, true);
	}

	@Test
	public void testUpdateErrorHandlerCalled() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).updateConfiguration(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		classUnderTest.updateConfiguration(CONFIG_ID, OLD_VERSION, null, CSTIC_ID, new CPSCharacteristicInput());
		verify(errorHandler).processUpdateConfigurationError(ex, CONFIG_ID);
	}

	@Test
	public void testEncodeNull()
	{
		assertNull(classUnderTest.encode(null));
	}

	@Test
	public void testEncode()
	{
		assertEquals("%5BGEN%5D", classUnderTest.encode("[GEN]"));
	}

	@Test
	public void testGetConfiguration() throws ConfigurationEngineException
	{
		final Observable<RawResponse<CPSConfiguration>> configObs = Observable.from(Arrays.asList(configurationGetResponse));
		Mockito.when(client.getConfiguration(CONFIG_ID, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT,
				CharonFacadeImpl.GET_CONFIG_SELECT_ALL)).thenReturn(configObs);
		classUnderTest.setClient(client);
		assertEquals(configuration, classUnderTest.getConfiguration(CONFIG_ID));
	}

	@Test
	public void testGetConfigurationWithoutDomainValues() throws ConfigurationEngineException
	{
		final Observable<RawResponse<CPSConfiguration>> configObs = Observable.from(Arrays.asList(configurationGetResponse));
		Mockito.when(client.getConfiguration(CONFIG_ID, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT,
				CharonFacadeImpl.GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES)).thenReturn(configObs);
		classUnderTest.setClient(client);
		classUnderTest.setReadDomainValuesOnDemand(true);
		assertEquals(configuration, classUnderTest.getConfiguration(CONFIG_ID));
	}

	@Test
	public void testGetConfigurationCached() throws ConfigurationEngineException
	{
		when(cpsCache.getConfiguration(CONFIG_ID)).thenReturn(configuration);
		final CPSConfiguration result = classUnderTest.getConfiguration(CONFIG_ID);
		assertEquals(configuration, result);
		Mockito.verify(client, times(0)).getConfiguration(CONFIG_ID, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT,
				CharonFacadeImpl.GET_CONFIG_SELECT_ALL);
	}

	@Test
	public void testGetConfigurationWithoutCookies() throws ConfigurationEngineException
	{
		final Observable<RawResponse<CPSConfiguration>> configObs = Observable.from(Arrays.asList(configurationGetResponse));
		ensureCookieHandlerHasNoCookies();
		Mockito.when(client.getConfiguration(CONFIG_ID, LANG, SAP_PASSPORT, CharonFacadeImpl.GET_CONFIG_SELECT_ALL))
				.thenReturn(configObs);
		classUnderTest.setClient(client);
		assertEquals(configuration, classUnderTest.getConfiguration(CONFIG_ID));
		Mockito.verify(cookieHandler, Mockito.never()).setCookies(CONFIG_ID, responseCookies);
	}

	@Test
	public void testGetConfigurationWithoutCookiesCPSReturnsNewCookies() throws ConfigurationEngineException
	{
		when(configurationGetResponse.getSetCookies()).thenReturn(responseCookies);
		final Observable<RawResponse<CPSConfiguration>> configObs = Observable.from(Arrays.asList(configurationGetResponse));
		ensureCookieHandlerHasNoCookies();
		Mockito.when(client.getConfiguration(CONFIG_ID, LANG, SAP_PASSPORT, CharonFacadeImpl.GET_CONFIG_SELECT_ALL))
				.thenReturn(configObs);
		classUnderTest.setClient(client);
		classUnderTest.getConfiguration(CONFIG_ID);
		Mockito.verify(cookieHandler, Mockito.atLeastOnce()).retrieveETagAndSaveResponseAttributes(configurationGetResponse,
				CONFIG_ID);
	}

	@Test
	public void testGetConfigurationTimeOutExceptionHappens() throws ConfigurationEngineException
	{
		Mockito.when(client.getConfiguration(CONFIG_ID, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT,
				CharonFacadeImpl.GET_CONFIG_SELECT_ALL)).thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.setClient(client);
		classUnderTest.getConfiguration(CONFIG_ID);
		verify(errorHandler).processConfigurationRuntimeException(runtimeExceptionWrappingTimeout, CONFIG_ID);
	}

	@Test
	public void testGetErrorHandlerCalled() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).getConfiguration(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				eq(CharonFacadeImpl.GET_CONFIG_SELECT_ALL));
		classUnderTest.getConfiguration(CONFIG_ID);
		verify(errorHandler).processGetConfigurationError(ex, CONFIG_ID);
	}

	@Test
	public void testGetConfigurationKbBuildCheck() throws ConfigurationEngineException
	{
		final Observable<RawResponse<CPSConfiguration>> configObs = Observable.from(Arrays.asList(configurationGetResponse));
		Mockito.when(client.getConfiguration(CONFIG_ID, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT,
				CharonFacadeImpl.GET_CONFIG_SELECT_ALL)).thenReturn(configObs);
		classUnderTest.getConfiguration(CONFIG_ID);
		verify(kbKeyComparator, times(1)).retrieveKnowledgebaseBuildSyncStatus(configuration);
	}

	@Test
	public void testRetrieveConfigurationAndSaveResponseAttributesKbBuildCheck() throws ConfigurationEngineException
	{
		final Observable<RawResponse<CPSConfiguration>> configObs = Observable.from(Arrays.asList(configurationGetResponse));
		classUnderTest.retrieveConfigurationAndSaveResponseAttributes(configObs);
		verify(kbKeyComparator, times(1)).retrieveKnowledgebaseBuildSyncStatus(configuration);
	}

	protected void mockClientCallWithCstic(final String cfgId, final String currentVersion, final String itemId,
			final String csticIdentifier)
	{
		Mockito
				.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(csticIdentifier),
						Mockito.eq(COOKIE_AS_STRING), Mockito.eq(COOKIE_AS_STRING), Mockito.eq(currentVersion), Mockito.anyString()))
				.thenReturn(eTagRawResponseObservable);

	}

	protected void mockClientCallWithCsticSuccessor(final String cfgId, final String currentVersion, final String itemId,
			final String csticIdentifier)
	{
		Mockito.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(csticIdentifier),
				Mockito.eq(COOKIE_AS_STRING), Mockito.eq(COOKIE_AS_STRING), Mockito.eq(currentVersion), Mockito.eq(SAP_PASSPORT)))
				.thenReturn(eTagRawResponseObservableSuccessor);

	}

	protected void mockClientCallWithCsticWoCookies(final String cfgId, final String itemId, final String csticIdentifier)
	{

		Mockito.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(csticIdentifier),
				Mockito.eq(OLD_VERSION), Mockito.eq(SAP_PASSPORT))).thenReturn(eTagRawResponseObservable);

	}

	protected void verifyClientCallWithCstic(final String cfgId, final String updatedVersion, final String itemId,
			final String csticIdentifier, final boolean isExpected)
	{
		final VerificationMode mode = isExpected ? Mockito.atLeastOnce() : Mockito.never();
		Mockito.verify(client, mode).updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId),
				Mockito.eq(csticIdentifier), Mockito.eq(COOKIE_AS_STRING), Mockito.eq(COOKIE_AS_STRING), Mockito.eq(updatedVersion),
				Mockito.anyString());
	}

	protected void verifyClientCallWithCsticWoCookies(final String cfgId, final String itemId, final String csticIdentifier,
			final boolean isExpected)
	{
		final VerificationMode mode = isExpected ? Mockito.atLeastOnce() : Mockito.never();
		Mockito.verify(client, mode).updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId),
				Mockito.eq(csticIdentifier), Mockito.eq(OLD_VERSION), Mockito.eq(SAP_PASSPORT));
	}

	protected void addRuntimeCsticGroup(final CPSItem item, final CPSCharacteristicGroup characteristicGroup)
	{
		if (characteristicGroup == null)
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add null CharacteristicGroup to Item ").append(item.getId()).toString());
		}
		if (isRuntimeCsticGroupPresent(item, characteristicGroup.getId()))
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add CharacteristicGroup with already existing id ")
							.append(characteristicGroup.getId()).append(" to Item ").append(item.getId()).toString());
		}
		item.getCharacteristicGroups().add(characteristicGroup);
	}

	protected boolean isRuntimeCsticGroupPresent(final CPSItem item, final String id)
	{
		for (final CPSCharacteristicGroup group : item.getCharacteristicGroups())
		{
			if (group.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isRuntimeCsticPresent(final CPSItem item, final String id)
	{
		for (final CPSCharacteristic characteristic : item.getCharacteristics())
		{
			if (characteristic.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	protected void addRuntimeCstic(final CPSItem item, final CPSCharacteristic characteristic)
	{
		if (characteristic == null)
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add null Characteristic to Item ").append(item.getId()).toString());
		}
		if (isRuntimeCsticPresent(item, characteristic.getId()))
		{
			throw new IllegalArgumentException(new StringBuilder().append("tried to add Characteristic with already existing id ")
					.append(characteristic.getId()).append(" to Item ").append(item.getId()).toString());
		}
		item.getCharacteristics().add(characteristic);
	}

	protected void ensureCookieHandlerHasNoCookies()
	{
		Mockito.when(cookieHandler.getCookiesAsString(Mockito.anyString())).thenReturn(null);
	}

	protected CPSItem createCPSItem(final String itemId)
	{
		final CPSItem subItem = new CPSItem();
		subItem.setId(itemId);
		subItem.setSubItems(new ArrayList<>());
		final List<CPSCharacteristic> characteristics = new ArrayList<>();
		characteristics.add(createCPSCharacteristic());
		subItem.setCharacteristics(characteristics);
		subItem.setCharacteristicGroups(new ArrayList<>());
		subItem.getCharacteristicGroups().add(new CPSCharacteristicGroup());
		return subItem;
	}

	protected CPSCharacteristic createCPSCharacteristic()
	{
		final CPSCharacteristic characteristic = new CPSCharacteristic();
		characteristic.setId(SUB_ITEM_CSTIC);
		characteristic.setValues(createListOfCPSValues(characteristic));
		characteristic.setPossibleValues(createListOfPossibleValues());
		return characteristic;
	}

	protected List<CPSPossibleValue> createListOfPossibleValues()
	{
		final List<CPSPossibleValue> possibleValues = new ArrayList<>();
		possibleValues.add(new CPSPossibleValue());
		return possibleValues;
	}

	protected List<CPSValue> createListOfCPSValues(final CPSCharacteristic characteristic)
	{
		final List<CPSValue> values = new ArrayList<>();
		values.add(createCPSValue(characteristic, SUB_ITEM_CSTIC_VALUE));
		return values;
	}

	protected CPSValue createCPSValue(final CPSCharacteristic characteristic, final String valueName)
	{
		final CPSValue value = new CPSValue();
		value.setValue(valueName);
		return value;
	}

	@Test
	public void testCache()
	{
		classUnderTest.setCache(cpsCache);
		assertEquals(cpsCache, classUnderTest.getCache());
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final ConfigurationClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetClientNotFound() throws CredentialException
	{
		Mockito.when(apiRegistryClientService.lookupClient(ConfigurationClient.class)).thenThrow(CredentialException.class);

		classUnderTest.getClient();

	}

	@Test
	public void testGetObjectMapper()
	{
		classUnderTest.setObjectMapper(null);
		assertNotNull(classUnderTest.getObjectMapper());
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConfigurationRootItemNull() throws ConfigurationEngineException
	{
		classUnderTest.updateConfiguration(new CPSConfiguration());
	}

	@Test(expected = IllegalStateException.class)
	public void testConvertFromStringToStructured()
	{
		classUnderTest.convertFromStringToStructured("invalid external config");
	}

	@Test
	public void testGetItemWithGroupDetails() throws ConfigurationEngineException
	{
		final CPSItem item = classUnderTest.getItemWithGroupDetails(CONFIG_ID, ITEM_ID, Arrays.asList(GROUP_ID));
		assertEquals(rootItem, item);
	}

	@Test
	public void testGetItemWithGroupDetailsNoCookies() throws ConfigurationEngineException
	{
		final CPSItem rootItem2 = new CPSItem();
		when(cookieHandler.getCookiesAsString(CONFIG_ID)).thenReturn(new ArrayList<String>());
		when(cpsItemRawResponse.content()).thenReturn(Observable.from(Arrays.asList(rootItem2)));
		final CPSItem item = classUnderTest.getItemWithGroupDetails(CONFIG_ID, ITEM_ID, Arrays.asList(GROUP_ID));
		assertEquals(rootItem2, item);
	}

	@Test(expected = NullPointerException.class)
	public void testGetItemWithGroupDetailsItemNull() throws ConfigurationEngineException
	{
		classUnderTest.getItemWithGroupDetails(CONFIG_ID, null, Arrays.asList(GROUP_ID));
	}

	@Test(expected = NullPointerException.class)
	public void testGetItemWithGroupDetailsGropListNull() throws ConfigurationEngineException
	{
		classUnderTest.getItemWithGroupDetails(CONFIG_ID, ITEM_ID, null);
	}

	@Test
	public void testGetItemWithGroupDetailsTimeOut() throws ConfigurationEngineException
	{
		Mockito.when(client.getGroup(CONFIG_ID, ITEM_ID, FILTER, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT))
				.thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.getItemWithGroupDetails(CONFIG_ID, ITEM_ID, Arrays.asList(GROUP_ID));
		verify(errorHandler).processConfigurationRuntimeException(runtimeExceptionWrappingTimeout, CONFIG_ID);
	}

	@Test
	public void testGetItemWithGroupDetailsErrorHandlerCalled() throws ConfigurationEngineException
	{
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.when(client.getGroup(CONFIG_ID, ITEM_ID, FILTER, LANG, COOKIE_AS_STRING, COOKIE_AS_STRING, SAP_PASSPORT))
				.thenThrow(ex);
		classUnderTest.getItemWithGroupDetails(CONFIG_ID, ITEM_ID, Arrays.asList(GROUP_ID));
		verify(errorHandler).processGetItemWithGroupDetailsError(ex, CONFIG_ID, ITEM_ID, Arrays.asList(GROUP_ID));
	}

	@Test
	public void testKeyGenerator()
	{
		assertEquals(keyGenerator, classUnderTest.getKeyGenerator());
	}

	@Test
	public void testHandleGeneralGroupId()
	{
		assertEquals(GROUP_ID, classUnderTest.handleGeneralGroupId(GROUP_ID));
	}

	@Test
	public void testHandleGeneralGroupIdGeneral()
	{
		assertEquals("$general", classUnderTest.handleGeneralGroupId(InstanceModel.GENERAL_GROUP_NAME));
	}

	@Test
	public void testRetrieveItemAndSaveResponseAttributes() throws ConfigurationEngineException
	{
		when(cpsItemRawResponse.getSetCookies()).thenReturn(responseCookies);
		final CPSResponseAttributeStrategyImpl cpsResponseAttributeStrategyImpl = new CPSResponseAttributeStrategyImpl();
		cpsResponseAttributeStrategyImpl.setCpsCache(cpsCache);
		classUnderTest.setResponseStrategy(cpsResponseAttributeStrategyImpl);
		final Observable<RawResponse<CPSItem>> itemObs = Observable.from(Arrays.asList(cpsItemRawResponse));
		final CPSItem item = classUnderTest.retrieveItemAndSaveResponseAttributes(itemObs, CONFIG_ID);
		assertEquals(rootItem, item);
		verify(cpsCache).setCookies(CONFIG_ID, cookieList);
	}

	@Test
	public void testCompileGroupNamesForRequest()
	{
		final String groupNamesForRequest = classUnderTest.compileGroupNamesForRequest(groupNames);
		final String expectedResult = new StringBuilder("['").append(GROUP_ID).append("','").append(GROUP_ID_2).append("']")
				.toString();
		assertEquals(expectedResult, groupNamesForRequest);
	}

	@Test
	public void testCompileGroupNamesForRequestEmptyList()
	{
		assertEquals("[]", classUnderTest.compileGroupNamesForRequest(Collections.emptyList()));
	}

	@Test
	public void testCompileGroupNamesForRequestDuplicateEntries()
	{
		assertEquals("['" + GROUP_ID + "']", classUnderTest.compileGroupNamesForRequest(Arrays.asList(GROUP_ID, GROUP_ID)));
	}

	@Test(expected = NullPointerException.class)
	public void testCompileGroupNamesForRequestNullList()
	{
		classUnderTest.compileGroupNamesForRequest(null);
	}


}
