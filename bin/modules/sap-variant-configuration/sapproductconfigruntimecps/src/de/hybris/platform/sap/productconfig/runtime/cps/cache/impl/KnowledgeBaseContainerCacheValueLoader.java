/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.sap.productconfig.runtime.cps.ProductConfigurationPassportService;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.impl.CPSTimer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;

import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Queries the CPS master data service to fill the CPS master data cache.
 */
public class KnowledgeBaseContainerCacheValueLoader implements CacheValueLoader<CPSMasterDataKnowledgeBaseContainer>
{
	private MasterDataClientBase clientSetExternally = null;
	private ApiRegistryClientService apiRegistryClientService;
	private final CPSTimer timer = new CPSTimer();
	private final Scheduler scheduler = Schedulers.io();
	private static final Logger LOG = Logger.getLogger(KnowledgeBaseContainerCacheValueLoader.class);
	public static final String PASSPORT_GET_KB = "GET_KB";

	private Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> knowledgeBaseConverter;
	private ObjectMapper objectMapper;
	private ProductConfigurationPassportService productConfigurationPassportService;

	/**
	 * @return the productConfigurationPassportService
	 */
	protected ProductConfigurationPassportService getProductConfigurationPassportService()
	{
		return productConfigurationPassportService;
	}

	protected ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	/**
	 * @param apiRegistryClientService
	 *           the apiRegistryClientService to set
	 */
	@Required
	public void setApiRegistryClientService(final ApiRegistryClientService apiRegistryClientService)
	{
		this.apiRegistryClientService = apiRegistryClientService;
	}

	@Override
	public CPSMasterDataKnowledgeBaseContainer load(final CacheKey paramCacheKey)
	{
		if (!(paramCacheKey instanceof ProductConfigurationCacheKey))
		{
			throw new CacheValueLoadException("CacheKey is not instance of ProductConfigurationCacheKey");
		}
		final ProductConfigurationCacheKey key = (ProductConfigurationCacheKey) paramCacheKey;

		return getKnowledgeBaseConverter().convert(getKbFromService(key.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_KB_ID),
				key.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_LANGUAGE)));
	}

	protected CPSMasterDataKnowledgeBase getKbFromService(final String kbId, final String lang)
	{
		try
		{
			LOG.info("Getting master data for KB with ID: " + kbId + ", language: " + lang);
			timer.start("getKnowledgebase/" + kbId);
			final CPSMasterDataKnowledgeBase masterData = getClient()
					.getKnowledgebase(kbId, lang, getProductConfigurationPassportService().generate(PASSPORT_GET_KB),
							SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION)
					.subscribeOn(getScheduler()).toBlocking().first();
			timer.stop();

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output for REST call (getKnowledgebase): ", masterData);
			}
			return masterData;
		}
		catch (final HttpException ex)
		{
			throw new CacheValueLoadException("Could not get knowledge base from service", ex);
		}
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

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}


	protected MasterDataClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			try
			{
				return getApiRegistryClientService().lookupClient(MasterDataClient.class);
			}
			catch (final CredentialException e)
			{
				throw new IllegalStateException("Client could not be retrieved through apiregistry", e);
			}
		}
	}

	/**
	 * Set Charon client from outside (only used for testing)
	 *
	 * @param newClient
	 */
	public void setClient(final MasterDataClientBase newClient)
	{
		clientSetExternally = newClient;
	}

	protected Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> getKnowledgeBaseConverter()
	{
		return knowledgeBaseConverter;
	}

	/**
	 * @param knowledgeBaseConverter
	 *           the knowledgeBaseConverter to set
	 */
	@Required
	public void setKnowledgeBaseConverter(
			final Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> knowledgeBaseConverter)
	{
		this.knowledgeBaseConverter = knowledgeBaseConverter;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}

	@Required
	public void setProductConfigurationPassportService(
			final ProductConfigurationPassportService productConfigurationPassportService)
	{
		this.productConfigurationPassportService = productConfigurationPassportService;

	}

}
