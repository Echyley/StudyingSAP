/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.rest.client;

import com.hybris.datahub.client.ClientConfiguration;
import com.hybris.datahub.core.facades.ItemImportResult;
import com.hybris.datahub.core.services.impl.DataHubFacade;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.retry.support.RetryTemplate;

import com.google.common.base.Preconditions;


/**
 * A REST client to communicate to the Data Hub. Brings data from the Data Hub into a Core system and responds to Data
 * Hub with the results of the data import
 */
public class ImpexDataImportClient implements DataHubFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImpexDataImportClient.class);
	private Optional<ClientConfiguration> clientConfig;
	private RetryTemplate retryTemplate;

	@Override
	public InputStream readData(final String url, final Map<String, ?> headers)
	{
		Preconditions.checkArgument(url != null, "url must be provided");

		LOGGER.info("Requesting data from resource {}", url);

		return retryTemplate.execute(context -> readDataWithRetry(url, headers));
	}

	private InputStream readDataWithRetry(final String url, final Map<String, ?> headers)
	{
		final Invocation.Builder exportRequest = createRequest(url, headers);

		try
		{
			final Response response = exportRequest.get();
			LOGGER.debug("Response status from {}: {}", url, response.getStatus());
			if (response.getStatus() == Response.Status.OK.getStatusCode())
			{
				return response.readEntity(InputStream.class);
			}
			throw new IllegalStateException(response.getStatusInfo().getReasonPhrase() + " response from " + url);
		}
		catch (final Exception e)
		{
			throw new IllegalStateException("Failed to communicate to " + url, e);
		}
	}

	@Override
	public void returnImportResult(final String url, final ItemImportResult itemImportResult)
	{
		Preconditions.checkArgument(url != null, "url must be provided");

		LOGGER.info("Returning {} to {}", itemImportResult, url);

		retryTemplate.execute(
				context -> returnImportResultWithRetry(url, itemImportResult));
	}

	private Object returnImportResultWithRetry(final String url, final ItemImportResult itemImportResult)
	{
		final Invocation.Builder exportRequest = createRequest(url);

		try
		{
			final Response response = exportRequest.accept(MediaType.APPLICATION_XML_TYPE)
			                                       .put(Entity.entity(itemImportResult, MediaType.APPLICATION_XML_TYPE));
			LOGGER.info("Response status from {}: {}", url, response.getStatus());
			if (response.getStatus() != Response.Status.OK.getStatusCode())
			{
				throw new IllegalStateException(response.getStatusInfo().getReasonPhrase() + " response from " + url);
			}
			// I don't like this, but retryTemplate.execute wants a method with a return
			return null;
		}
		catch (final Exception e)
		{
			throw new IllegalStateException("Failed to communicate to " + url, e);
		}
	}

	private Invocation.Builder createRequest(final String url, final Map<String, ?> headers)
	{
		final Invocation.Builder exportRequestBuilder = createRequest(url);
		addHeaders(exportRequestBuilder, headers);
		return exportRequestBuilder;
	}

	private Invocation.Builder createRequest(final String url)
	{
		final WebTarget exportTarget = createTarget(url);
		return exportTarget.request();
	}

	private WebTarget createTarget(final String url)
	{
		if (clientConfig.isPresent())
		{
			LOGGER.trace("Creating Client with configuration.");
			final Client client = JerseyClientBuilder.createClient(clientConfig.get().toJerseyClientConfig());
			return client.target(url);
		}
		LOGGER.trace("Creating Client without configuration.");
		final Client client = JerseyClientBuilder.createClient();
		return client.target(url);
	}

	private static void addHeaders(final Invocation.Builder invocationBuilder, final Map<String, ?> headers)
	{
		LOGGER.debug("Headers:");
		if (headers != null)
		{
			for (final Map.Entry<String, ?> entry : headers.entrySet())
			{
				invocationBuilder.header(entry.getKey(), entry.getValue());
				LOGGER.debug("{}={}", entry.getKey(), entry.getValue());
			}
		}
	}

	public void setClientConfig(final ClientConfiguration clientConfig)
	{
		this.clientConfig = Optional.ofNullable(clientConfig);
	}

	@Required
	public void setRetryTemplate(final RetryTemplate retryTemplate)
	{
		this.retryTemplate = retryTemplate;
	}
}
