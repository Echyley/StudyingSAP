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
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.impl.CPSTimer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;

import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Queries the CPS kb determination service to fill the CPS KB Header Info cache.
 */
public class KnowledgeBaseHeadersCacheValueLoader implements CacheValueLoader<List<CPSMasterDataKBHeaderInfo>>
{
	private KbDeterminationClientBase clientSetExternally = null;
	private ApiRegistryClientService apiRegistryClientService;
	private final CPSTimer timer = new CPSTimer();
	private final Scheduler scheduler = Schedulers.io();
	private ObjectMapper objectMapper;
	private ProductConfigurationPassportService productConfigurationPassportService;
	private static final Logger LOG = Logger.getLogger(KnowledgeBaseHeadersCacheValueLoader.class);
	public static final String PASSPORT_GET_KB = "GET_KB";

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
	public List<CPSMasterDataKBHeaderInfo> load(final CacheKey paramCacheKey)
	{
		if (!(paramCacheKey instanceof ProductConfigurationCacheKey))
		{
			throw new CacheValueLoadException("CacheKey is not instance of ProductConfigurationCacheKey");
		}
		final ProductConfigurationCacheKey key = (ProductConfigurationCacheKey) paramCacheKey;

		return getKbHeadersFromService(key.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_PRODUCT));
	}

	protected List<CPSMasterDataKBHeaderInfo> getKbHeadersFromService(final String product)
	{
		try
		{
			LOG.info("Getting KB header data for product: " + product);
			timer.start("getKnowledgebaseForProduct/" + product);
			final List<CPSMasterDataKBHeaderInfo> masterData = getClient()
					.getKnowledgebases(getProductConfigurationPassportService().generate(PASSPORT_GET_KB), product)
					.subscribeOn(getScheduler()).toBlocking().first();
			timer.stop();

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output for REST call (getKnowledgebaseForProduct): ", masterData);
			}

			return masterData;
		}
		catch (final HttpException ex)
		{
			throw new CacheValueLoadException("Could not get list of knowledge base headers from service", ex);
		}
	}

	protected KbDeterminationClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			try
			{
				return getApiRegistryClientService().lookupClient(KbDeterminationClient.class);
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
	public void setClient(final KbDeterminationClientBase newClient)
	{
		clientSetExternally = newClient;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
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

	public void setProductConfigurationPassportService(
			final ProductConfigurationPassportService productConfigurationPassportService)
	{
		this.productConfigurationPassportService = productConfigurationPassportService;

	}


	protected ProductConfigurationPassportService getProductConfigurationPassportService()
	{
		return productConfigurationPassportService;
	}

}
