/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.pci.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.pci.PCICharonFacade;
import de.hybris.platform.sap.productconfig.runtime.pci.PCIRequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.pci.client.PCIClient;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;

import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Default implementation of {@link PCICharonFacade}. Traces REST input
 */
public class PCICharonFacadeImpl implements PCICharonFacade
{
	private static final Logger LOG = Logger.getLogger(PCICharonFacadeImpl.class);

	private PCIClient clientSetExternally;
	private final Scheduler scheduler = Schedulers.io();
	private ObjectMapper objectMapper;
	private ApiRegistryClientService apiRegistryClientService;
	private PCIRequestErrorHandler pciRequestErrorHandler;
	private final PCITimer timer = new PCITimer();



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


	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}


	@Override
	public AnalyticsDocument createAnalyticsDocument(final AnalyticsDocument analyticsDocumentInput)
	{
		if (LOG.isDebugEnabled())
		{
			traceJsonRequestBody("Input for createAnalyticsDocument REST call: ", analyticsDocumentInput);
		}

		AnalyticsDocument analyticsDocumentOutput;
		try
		{
			timer.start("createAnalyticsDocument");
			analyticsDocumentOutput = getClient().createAnalyticsDocument(analyticsDocumentInput).subscribeOn(getScheduler())
					.toBlocking().first();
			timer.stop();
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output of createAnalyticsDocument REST call: ", analyticsDocumentOutput);
			}
		}
		catch (final HttpException ex)
		{
			timer.stop();
			analyticsDocumentOutput = getPciRequestErrorHandler().processCreateAnalyticsDocumentHttpError(ex,
					analyticsDocumentInput);
		}
		catch (final RuntimeException ex)
		{
			timer.stop();
			analyticsDocumentOutput = getPciRequestErrorHandler().processCreateAnalyticsDocumentRuntimeException(ex,
					analyticsDocumentInput);
		}
		return analyticsDocumentOutput;
	}

	protected void traceJsonRequestBody(final String prefix, final AnalyticsDocument analyticsDocumentInput)
	{
		try
		{
			LOG.debug(prefix + getObjectMapper().writeValueAsString(analyticsDocumentInput));
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Could not trace " + prefix, e);
		}
	}

	protected PCIClient getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			try
			{
				return getApiRegistryClientService().lookupClient(PCIClient.class);
			}
			catch (final CredentialException e)
			{
				throw new IllegalStateException("Client could not be retrieved through apiregistry", e);
			}
		}
	}

	protected void setClient(final PCIClient client)
	{
		this.clientSetExternally = client;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}


	protected PCIRequestErrorHandler getPciRequestErrorHandler()
	{
		return pciRequestErrorHandler;
	}


	/**
	 * @param pciRequestErrorHandler
	 *           is called in error case
	 */
	@Required
	public void setPciRequestErrorHandler(final PCIRequestErrorHandler pciRequestErrorHandler)
	{
		this.pciRequestErrorHandler = pciRequestErrorHandler;
	}



}
