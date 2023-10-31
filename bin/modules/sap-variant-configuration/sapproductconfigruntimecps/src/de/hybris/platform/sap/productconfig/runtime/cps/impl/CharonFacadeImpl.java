/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.ProductConfigurationPassportService;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.KnowledgebaseKeyComparator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValueInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CPSResponseAttributeStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CPSConfigurationParentReferenceStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.catalina.util.URLEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Default implementation of {@link CharonFacade}. This bean has prototype scope
 */
public class CharonFacadeImpl implements CharonFacade
{
	private static final Logger LOG = Logger.getLogger(CharonFacadeImpl.class);
	private static final String AUTO_CLEANUP_PROP_KEY = "sapproductconfigruntimecps.autocleanup";
	protected static final String ETAG_NOT_FOUND = "No eTag found for config ";
	protected static final String AUTO_CLEAN_UP_FALSE = "false";
	public static final String PASSPORT_CREATE_CONFIG = "CREATE_CONFIGURATION";
	public static final String PASSPORT_CREATE_FROM_EXT_CONFIG = "CREATE_CONFIGURATION_FROM_EXT";
	public static final String PASSPORT_UPDATE_CONFIG = "UPDATE_CONFIGURATION";
	public static final String PASSPORT_GET_CONFIG = "GET_CONFIGURATION";
	public static final String PASSPORT_GET_EXT_CONFIG = "GET_EXT_CONFIGURATION";
	public static final String PASSPORT_DELETE_CONFIG = "DELETE_CONFIGURATION";
	public static final String GET_CONFIG_SELECT_ALL = "subItems,characteristicGroups,variantConditions,characteristics,possibleValues,values";
	public static final String GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES = "subItems,characteristicGroups,variantConditions,characteristics,values";

	private ConfigurationClientBase clientSetExternally = null;
	private CPSResponseAttributeStrategy responseStrategy;
	private RequestErrorHandler requestErrorHandler;
	private ObjectMapper objectMapper;
	private final Scheduler scheduler = Schedulers.io();
	private CPSContextSupplier contextSupplier;
	private final CPSTimer timer = new CPSTimer();
	private CPSCache cache;
	private I18NService i18NService;
	private CPSConfigurationParentReferenceStrategy configurationParentReferenceStrategy;
	private CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy;
	private KnowledgebaseKeyComparator kbKeyComparator;
	private ApiRegistryClientService apiRegistryClientService;
	private ProductConfigurationPassportService passportService;
	private boolean readDomainValuesOnDemand;
	private UniqueKeyGenerator keyGenerator;


	/**
	 * @return the passportService
	 */
	protected ProductConfigurationPassportService getPassportService()
	{
		return passportService;
	}

	protected ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	/**
	 * @return the commerceExternalConfigurationStrategy
	 */
	protected CommerceExternalConfigurationStrategy getCommerceExternalConfigurationStrategy()
	{
		return commerceExternalConfigurationStrategy;
	}

	@Override
	public CPSConfiguration createDefaultConfiguration(final KBKey kbKey)
	{
		try
		{
			final CPSCreateConfigInput cloudEngineConfigurationRequest = assembleCreateDefaultConfigurationRequest(kbKey);

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (create default configuration): ", cloudEngineConfigurationRequest);
			}
			timer.start("createDefaultConfiguration");
			final String select = isReadDomainValuesOnDemand() ? GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES
					: GET_CONFIG_SELECT_ALL;
			final Observable<RawResponse<CPSConfiguration>> rawResponse = getClient().createDefaultConfiguration(
					cloudEngineConfigurationRequest, getI18NService().getCurrentLocale().toLanguageTag(),
					getPassportService().generate(PASSPORT_CREATE_CONFIG), getAutoCleanUpFlag(), select);
			final CPSConfiguration cpsConfig = retrieveConfigurationAndSaveResponseAttributes(rawResponse);
			timer.stop();
			getConfigurationParentReferenceStrategy().addParentReferences(cpsConfig);
			return cpsConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreateDefaultConfigurationError(ex);
		}
	}

	protected CPSCreateConfigInput assembleCreateDefaultConfigurationRequest(final KBKey kbKey)
	{
		final CPSCreateConfigInput cloudEngineConfigurationRequest = new CPSCreateConfigInput();
		cloudEngineConfigurationRequest.setProductKey(kbKey.getProductCode());

		final List<CPSContextInfo> context = getContextSupplier().retrieveContext(kbKey.getProductCode());
		cloudEngineConfigurationRequest.setContext(context);

		return cloudEngineConfigurationRequest;
	}

	@Override
	public String getExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		try
		{
			final CPSExternalConfiguration externalConfigStructured = placeGetExternalConfigurationRequest(configId);

			final CPSCommerceExternalConfiguration commerceExternalConfiguration = getCommerceExternalConfigurationStrategy()
					.createCommerceFormatFromCPSRepresentation(externalConfigStructured);

			final String extConfig = getObjectMapper().writeValueAsString(commerceExternalConfiguration);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Output for REST call (get ext configuration): " + extConfig);
			}
			return extConfig;

		}
		catch (final JsonProcessingException e)
		{
			throw new IllegalStateException("External configuration from client cannot be parsed to string", e);
		}
	}

	protected CPSExternalConfiguration placeGetExternalConfigurationRequest(final String configId)
			throws ConfigurationEngineException
	{
		try
		{
			final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);
			timer.start("getExternalConfiguration/" + configId);
			CPSExternalConfiguration extConfig = null;
			if (cookiesAsString != null)
			{
				extConfig = getClient().getExternalConfiguration(configId, cookiesAsString.get(0), cookiesAsString.get(1),
						getPassportService().generate(PASSPORT_GET_EXT_CONFIG)).subscribeOn(getScheduler()).toBlocking().first();
			}
			else
			{
				extConfig = getClient().getExternalConfiguration(configId, getPassportService().generate(PASSPORT_GET_EXT_CONFIG))
						.subscribeOn(getScheduler()).toBlocking().first();
			}
			timer.stop();
			return extConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetExternalConfigurationError(ex, configId);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex, configId);
			return null;
		}
	}

	@Override
	public CPSConfiguration createConfigurationFromExternal(final String externalConfiguration, final String contextProduct)
	{
		final CPSExternalConfiguration externalConfigStructured = convertFromStringToStructured(externalConfiguration);
		return createConfigurationFromExternal(externalConfigStructured, contextProduct);
	}

	@Override
	public CPSConfiguration createConfigurationFromExternal(final CPSExternalConfiguration externalConfigStructured,
			final String contextProduct)
	{
		final String productCodeForContext;
		if (StringUtils.isNotEmpty(contextProduct))
		{
			productCodeForContext = contextProduct;
		}
		else
		{
			productCodeForContext = externalConfigStructured.getRootItem().getObjectKey().getId();
		}

		final List<CPSContextInfo> context = getContextSupplier().retrieveContext(productCodeForContext);
		externalConfigStructured.setContext(context);

		try
		{
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (create form external configuration): ", externalConfigStructured);
			}
			timer.start("createConfigurationFromExternal");
			final String select = isReadDomainValuesOnDemand() ? GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES
					: GET_CONFIG_SELECT_ALL;
			final Observable<RawResponse<CPSConfiguration>> rawResponse = getClient().createRuntimeConfigurationFromExternal(
					getPassportService().generate(PASSPORT_CREATE_FROM_EXT_CONFIG), externalConfigStructured, getAutoCleanUpFlag(),
					select);
			final CPSConfiguration cpsConfig = retrieveConfigurationAndSaveResponseAttributes(rawResponse);
			timer.stop();
			getConfigurationParentReferenceStrategy().addParentReferences(cpsConfig);
			return cpsConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreateRuntimeConfigurationFromExternalError(ex);
		}
	}

	protected String getAutoCleanUpFlag()
	{
		String autoCleanUp = AUTO_CLEAN_UP_FALSE;
		if (Registry.hasCurrentTenant())
		{
			autoCleanUp = Config.getString(AUTO_CLEANUP_PROP_KEY, AUTO_CLEAN_UP_FALSE);
		}
		return autoCleanUp;
	}

	protected CPSExternalConfiguration convertFromStringToStructured(final String externalConfiguration)
	{
		final CPSCommerceExternalConfiguration externalConfigStructuredCommerceFormat;
		try
		{
			externalConfigStructuredCommerceFormat = getObjectMapper().readValue(externalConfiguration,
					CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing from JSON failed", e);

		}
		return getCommerceExternalConfigurationStrategy()
				.extractCPSFormatFromCommerceRepresentation(externalConfigStructuredCommerceFormat);
	}

	@Override
	public void releaseSession(final String configId, final String version)
	{
		final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);

		if (version == null)
		{
			throw new IllegalStateException(ETAG_NOT_FOUND + configId);
		}
		try
		{
			timer.start("releaseSession/" + configId);
			if (cookiesAsString != null)
			{
				getClient().deleteConfiguration(configId, cookiesAsString.get(0), cookiesAsString.get(1), version,
						getPassportService().generate(PASSPORT_DELETE_CONFIG)).subscribeOn(getScheduler()).toBlocking().first();
			}
			else
			{
				getClient().deleteConfiguration(configId, version, getPassportService().generate(PASSPORT_DELETE_CONFIG))
						.subscribeOn(getScheduler()).toBlocking().first();
			}
			timer.stop();
		}
		catch (final HttpException ex)
		{
			getRequestErrorHandler().processDeleteConfigurationError(ex);
		}
		finally
		{
			getCache().removeCookies(configId);
			getCache().removeConfiguration(configId);
		}
	}

	@Override
	public String updateConfiguration(final CPSConfiguration configuration) throws ConfigurationEngineException
	{
		final CPSItem rootItem = configuration.getRootItem();
		if (rootItem == null)
		{
			throw new IllegalStateException("Root item not available");
		}

		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();
		final MutableBoolean updateWasDone = new MutableBoolean(false);
		final String initialVersion = configuration.getETag();
		return updateCPSCharacteristic(updateWasDone, rootItem, cfgId, initialVersion, itemId);
	}

	protected String updateCPSCharacteristic(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String currentVersion, final String itemId) throws ConfigurationEngineException
	{

		String updatedVersion = handleUpdateOwnCharacteristics(updateWasDone, item, cfgId, currentVersion, itemId);
		updatedVersion = handleUpdateSubItems(updateWasDone, item, cfgId, updatedVersion);
		return updatedVersion;
	}

	protected String handleUpdateSubItems(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String currentVersion) throws ConfigurationEngineException
	{
		final List<CPSItem> subItems = item.getSubItems();
		String updatedVersion = currentVersion;
		if (CollectionUtils.isNotEmpty(subItems))
		{
			for (final CPSItem subItem : subItems)
			{
				updatedVersion = updateCPSCharacteristic(updateWasDone, subItem, cfgId, updatedVersion, subItem.getId());
			}
		}
		return updatedVersion;
	}

	protected String handleUpdateOwnCharacteristics(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String currentVersion, final String itemId) throws ConfigurationEngineException
	{
		final List<CPSCharacteristic> characteristics = item.getCharacteristics();

		String updatedVersion = currentVersion;
		for (final CPSCharacteristic characteristic : characteristics)
		{
			final CPSCharacteristicInput characteristicInput = createCharacteristicInput(characteristic);
			//multiple updates: We cannot always prevent this as in some environments, the above layers need to send multiple updates
			//(e.g. if unconstrained cstics are involved).
			//Still we raise a log warning as this can cause undesired conflict situations
			if (updateWasDone.isTrue())
			{
				LOG.warn("Multiple updates detected in one request, characteristic involved: " + characteristic.getId());
			}
			updatedVersion = updateConfiguration(cfgId, updatedVersion, itemId, characteristic.getId(), characteristicInput);
			updateWasDone.setValue(true);
		}
		return updatedVersion;
	}


	protected CPSCharacteristicInput createCharacteristicInput(final CPSCharacteristic characteristic)
	{
		final CPSCharacteristicInput characteristicInput = new CPSCharacteristicInput();
		characteristicInput.setValues(new ArrayList<>());
		for (final CPSValue value : characteristic.getValues())
		{
			final CPSValueInput valueInput = new CPSValueInput();
			valueInput.setValue(value.getValue());
			valueInput.setSelected(value.isSelected());
			characteristicInput.getValues().add(valueInput);
		}
		return characteristicInput;
	}

	protected String updateConfiguration(final String cfgId, final String version, final String itemId, final String csticId,
			final CPSCharacteristicInput changes) throws ConfigurationEngineException
	{
		final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(cfgId);
		try
		{
			final String csticIdEncoded = encode(csticId);
			if (version == null)
			{
				throw new IllegalStateException(ETAG_NOT_FOUND + cfgId);
			}

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (update configuration): ", changes);
				if (cookiesAsString != null)
				{
					LOG.debug("Cookies as input " + cookiesAsString);
				}
				else
				{
					LOG.debug("No cookies as input ");
				}
				LOG.debug("ETag as input: " + version);
			}

			timer.start("updateConfiguration/" + cfgId);
			Observable<RawResponse> rawResponse = null;
			if (cookiesAsString != null)
			{
				rawResponse = getClient().updateConfiguration(changes, cfgId, itemId, csticIdEncoded, cookiesAsString.get(0),
						cookiesAsString.get(1), version, getPassportService().generate(PASSPORT_UPDATE_CONFIG));
			}
			else
			{
				rawResponse = getClient().updateConfiguration(changes, cfgId, itemId, csticIdEncoded, version,
						getPassportService().generate(PASSPORT_UPDATE_CONFIG));
			}
			timer.stop();
			final RawResponse response = rawResponse.subscribeOn(getScheduler()).toBlocking().first();
			return getResponseStrategy().retrieveETagAndSaveResponseAttributes(response, cfgId);
		}
		catch (final HttpException e)
		{
			getRequestErrorHandler().processUpdateConfigurationError(e, cfgId);
		}
		catch (final RuntimeException e)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(e, cfgId);
		}
		finally
		{
			getCache().removeConfiguration(cfgId);
		}
		return null;
	}

	protected String encode(final String requestParam)
	{
		if (requestParam == null)
		{
			return null;
		}
		final URLEncoder encoder = new URLEncoder();

		return encoder.encode(requestParam, StandardCharsets.UTF_8);
	}

	@Override
	public CPSConfiguration getConfiguration(final String configId) throws ConfigurationEngineException
	{
		return retrieveConfiguration(configId);
	}

	protected CPSConfiguration retrieveConfiguration(final String configId) throws ConfigurationEngineException
	{
		final CPSConfiguration cachedConfiguration = getCache().getConfiguration(configId);
		if (cachedConfiguration != null)
		{
			return cachedConfiguration;
		}
		return retrieveConfigurationFromClient(configId);
	}

	protected CPSConfiguration retrieveConfigurationFromClient(final String configId) throws ConfigurationEngineException
	{
		try
		{
			final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);
			timer.start("getConfiguration/" + configId);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("GET reequested for config id " + configId);
				LOG.debug("Cookies as input for request: " + cookiesAsString);
			}
			final String select = isReadDomainValuesOnDemand() ? GET_CONFIG_SELECT_ALL_WITHOUT_POSSIBLE_VALUES
					: GET_CONFIG_SELECT_ALL;
			Observable<RawResponse<CPSConfiguration>> rawResponse = null;
			if (CollectionUtils.isNotEmpty(cookiesAsString))
			{
				rawResponse = getClient().getConfiguration(configId, getI18NService().getCurrentLocale().toLanguageTag(),
						cookiesAsString.get(0), cookiesAsString.get(1), getPassportService().generate(PASSPORT_GET_CONFIG), select);
			}
			else
			{
				rawResponse = getClient().getConfiguration(configId, getI18NService().getCurrentLocale().toLanguageTag(),
						getPassportService().generate(PASSPORT_GET_CONFIG), select);
			}
			final CPSConfiguration config = retrieveConfigurationAndSaveResponseAttributes(rawResponse);

			timer.stop();
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output for REST call (get configuration): ", config);
			}
			getConfigurationParentReferenceStrategy().addParentReferences(config);
			return config;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetConfigurationError(ex, configId);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex, configId);
			return null;
		}
	}

	protected KnowledgebaseKeyComparator getKbKeyComparator()
	{
		return kbKeyComparator;
	}

	@Required
	public void setKbKeyComparator(final KnowledgebaseKeyComparator kbKeyComparator)
	{
		this.kbKeyComparator = kbKeyComparator;
	}


	/**
	 * @param commerceExternalConfigurationStrategy
	 */
	public void setCommerceExternalConfigurationStrategy(
			final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy)
	{
		this.commerceExternalConfigurationStrategy = commerceExternalConfigurationStrategy;

	}

	protected RequestErrorHandler getRequestErrorHandler()
	{
		return requestErrorHandler;
	}

	/**
	 * @param requestErrorHandler
	 *           For wrapping the http errors we receive from the REST service call
	 */
	@Required
	public void setRequestErrorHandler(final RequestErrorHandler requestErrorHandler)
	{
		this.requestErrorHandler = requestErrorHandler;
	}

	protected ConfigurationClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			try
			{
				return getApiRegistryClientService().lookupClient(ConfigurationClient.class);
			}
			catch (final CredentialException cause)
			{
				throw new IllegalStateException("Client could not be retrieved through apiregistry", cause);
			}
		}
	}

	/**
	 * Sets charon client from outside. Only used in test environments
	 *
	 * @param newClient
	 *           Charon client representing REST calls for product configuration.
	 */
	public void setClient(final ConfigurationClientBase newClient)
	{
		clientSetExternally = newClient;
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected void setObjectMapper(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}

	/**
	 * @return the contextSupplier
	 */
	protected CPSContextSupplier getContextSupplier()
	{
		return contextSupplier;
	}

	/**
	 * @param contextSupplier
	 *           the contextSupplier to set
	 */
	public void setContextSupplier(final CPSContextSupplier contextSupplier)
	{
		this.contextSupplier = contextSupplier;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected CPSCache getCache()
	{
		return cache;
	}

	@Required
	public void setCache(final CPSCache cache)
	{
		this.cache = cache;
	}

	protected void traceJsonRequestBody(final String prefix, final Object obj)
	{
		try
		{
			LOG.debug(prefix + getObjectMapper().writeValueAsString(obj));
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Could not trace " + prefix, e);
		}
	}

	protected CPSConfiguration retrieveConfigurationAndSaveResponseAttributes(
			final Observable<RawResponse<CPSConfiguration>> rawResponse)
	{
		//Stateful calls are preferred (otherwise CPS needs to load its data from DB),
		//and this is facilitated via cookies. These cookies can be extracted from the RawResponse.
		final RawResponse<CPSConfiguration> response = rawResponse.subscribeOn(getScheduler()).toBlocking().first();
		final CPSConfiguration responseValue = response.content().subscribeOn(getScheduler()).toBlocking().first();
		final String configId = responseValue.getId();
		final String eTag = getResponseStrategy().retrieveETagAndSaveResponseAttributes(response, configId);
		responseValue.setETag(eTag);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Retrieved configuration with id: " + configId);
			traceJsonRequestBody("Output for REST call (create default/from external configuration): ", responseValue);
		}
		getCache().setConfiguration(responseValue.getId(), responseValue);

		//check for an outdated knowledgebase build number
		getKbKeyComparator().retrieveKnowledgebaseBuildSyncStatus(responseValue);

		return responseValue;
	}

	protected CPSItem retrieveItemAndSaveResponseAttributes(final Observable<RawResponse<CPSItem>> rawResponse,
			final String configId)
	{
		//Stateful calls are preferred (otherwise CPS needs to load its data from DB),
		//and this is facilitated via cookies. These cookies can be extracted from the RawResponse.
		final RawResponse<CPSItem> response = rawResponse.subscribeOn(getScheduler()).toBlocking().first();
		final CPSItem responseValue = response.content().subscribeOn(getScheduler()).toBlocking().first();
		final String itemId = responseValue.getId();
		getResponseStrategy().extractAndSaveCookies(response, configId);


		if (LOG.isDebugEnabled())
		{
			LOG.debug("Retrieved item with id: " + itemId);
			traceJsonRequestBody("Output for REST call (get item): ", responseValue);
		}

		return responseValue;
	}

	protected CPSResponseAttributeStrategy getResponseStrategy()
	{
		return responseStrategy;
	}

	@Required
	public void setResponseStrategy(final CPSResponseAttributeStrategy responseStrategy)
	{
		this.responseStrategy = responseStrategy;
	}

	protected CPSConfigurationParentReferenceStrategy getConfigurationParentReferenceStrategy()
	{
		return configurationParentReferenceStrategy;
	}

	@Required
	public void setConfigurationParentReferenceStrategy(
			final CPSConfigurationParentReferenceStrategy configurationParentReferenceStrategy)
	{
		this.configurationParentReferenceStrategy = configurationParentReferenceStrategy;
	}

	@Required
	public void setApiRegistryClientService(final ApiRegistryClientService apiRegistryClientService)
	{
		this.apiRegistryClientService = apiRegistryClientService;

	}

	@Required
	public void setPassportService(final ProductConfigurationPassportService sapPassportService)
	{
		this.passportService = sapPassportService;

	}

	@Override
	public boolean isReadDomainValuesOnDemand()
	{
		return readDomainValuesOnDemand;
	}


	public void setReadDomainValuesOnDemand(final boolean readDomainValuesOnDemand)
	{
		this.readDomainValuesOnDemand = readDomainValuesOnDemand;
	}

	@Override
	public CPSItem getItemWithGroupDetails(final String configId, final String instanceId, final List<String> groupNames)
			throws ConfigurationEngineException
	{
		Preconditions.checkNotNull(instanceId, "Here we expect that a instance ID is provided");
		Preconditions.checkNotNull(groupNames, "Here we expect that a group ID List is provided");

		final List<String> groupsWithCPSIds = groupNames.stream().map(this::handleGeneralGroupId).collect(Collectors.toList());

		final var groupNamesForRequestAsString = compileGroupNamesForRequest(groupsWithCPSIds);

		try
		{
			final List<String> cookiesAsString = getResponseStrategy().getCookiesAsString(configId);
			timer.start("getGroup");

			final var filter = String.format("characteristicGroup IN %s", groupNamesForRequestAsString);

			Observable<RawResponse<CPSItem>> rawResponse = null;
			if (CollectionUtils.isNotEmpty(cookiesAsString))
			{
				rawResponse = getClient().getGroup(configId, instanceId, filter, getI18NService().getCurrentLocale().toLanguageTag(),
						cookiesAsString.get(0), cookiesAsString.get(1), getPassportService().generate(PASSPORT_GET_CONFIG));
			}
			else
			{
				rawResponse = getClient().getGroup(configId, instanceId, filter, getI18NService().getCurrentLocale().toLanguageTag(),
						getPassportService().generate(PASSPORT_GET_CONFIG));
			}
			final var cpsItem = retrieveItemAndSaveResponseAttributes(rawResponse, configId);

			timer.stop();

			getConfigurationParentReferenceStrategy().addParentReferences(cpsItem);

			return cpsItem;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetItemWithGroupDetailsError(ex, configId, instanceId, groupsWithCPSIds);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex, configId);
			return null;
		}
	}

	protected String compileGroupNamesForRequest(final List<String> groupNames)
	{
		Preconditions.checkNotNull(groupNames, "We expect a non-null list of group names");
		final var groupNamesForRequest = new StringBuilder("[");

		//avoid sending duplicate groups -> use set
		groupNames.stream().collect(Collectors.toSet()).forEach(name -> groupNamesForRequest.append("'").append(name).append("',"));

		//in case we have at least one group: Remove last delimiter
		if (!CollectionUtils.isEmpty(groupNames))
		{
			groupNamesForRequest.deleteCharAt(groupNamesForRequest.length() - 1);
		}
		return groupNamesForRequest.append("]").toString();
	}

	protected String handleGeneralGroupId(final String groupId)
	{
		return groupId.equals(InstanceModel.GENERAL_GROUP_NAME)
				? SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID.toLowerCase(Locale.US)
				: groupId;
	}

	public void setKeyGenerator(final UniqueKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	protected UniqueKeyGenerator getKeyGenerator()
	{
		return this.keyGenerator;
	}

}
