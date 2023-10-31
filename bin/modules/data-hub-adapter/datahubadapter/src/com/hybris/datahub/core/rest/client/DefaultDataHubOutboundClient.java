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
import com.hybris.datahub.core.dto.ResultData;
import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.rest.filter.FollowRedirectFilter;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.retry.support.RetryTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Implementation of a REST client for communication with the Data Hub
 */
public class DefaultDataHubOutboundClient implements DataHubOutboundClient
{
	private static final String COMMUNICATION_ERROR_MSG = "Unable to communicate with the Data Hub server";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Client client;
	private String dataHubUrl;
	private RetryTemplate retryTemplate;

	/**
	 * Instantiates this client.
	 */
	public DefaultDataHubOutboundClient()
	{
		this(JerseyClientBuilder.createClient());
	}

	protected DefaultDataHubOutboundClient(final Client cl)
	{
		client = cl;
		client.register(FollowRedirectFilter.class);
	}

	public DefaultDataHubOutboundClient(final ClientConfiguration cfg)
	{
		this(JerseyClientBuilder.createClient(cfg.toJerseyClientConfig()));
	}

	@Override
	public ResultData exportData(final String[] csvContent, final String dataFeed, final String rawItemType)
			throws DataHubCommunicationException
	{
		final WebTarget exportTarget = createTarget("/data-feeds/" + dataFeed + "/items/" + rawItemType);
		try
		{
			return retryTemplate.execute(retryContext -> exportDataInternal(exportTarget, csvContent));
		}
		catch (final Exception ex)
		{
			throw new DataHubCommunicationException(COMMUNICATION_ERROR_MSG, ex);
		}
	}

	private static ResultData exportDataInternal(final WebTarget exportTarget, final String[] csvContent) throws DataHubOutboundException
	{
		final Response response = exportTarget.request(MediaType.APPLICATION_XML)
				.post(Entity.entity(new ByteArrayInputStream(StringUtils.join(csvContent, System.lineSeparator()).getBytes()), MediaType.APPLICATION_OCTET_STREAM_TYPE));
		return ResponseHandler.handle(response, OutboundOperationType.EXPORT);
	}

	@Override
	public ResultData deleteItem(final String poolName, final String canonicalItemType, final Map<String, String> keyFields)
			throws DataHubCommunicationException
	{
		final WebTarget poolTarget = poolTarget(poolName, canonicalItemType, keyFields);
		try
		{
			return retryTemplate.execute(retryContext -> deleteItemAndHandleResponse(poolTarget));
		}
		catch (final Exception ex)
		{
			throw new DataHubCommunicationException(COMMUNICATION_ERROR_MSG, ex);
		}
	}

	private static ResultData deleteItemAndHandleResponse(final WebTarget poolTarget) throws DataHubOutboundException
	{
		final Response deleteResponse = sendDeleteFromPoolRequest(poolTarget);
		return ResponseHandler.handle(deleteResponse, OutboundOperationType.DELETE);
	}

	@Override
	public ResultData deleteByFeed(final String feedName, final String rawItemType) throws DataHubCommunicationException
	{
		return deleteByFeed(feedName, rawItemType, null);
	}

	@Override
	public ResultData deleteByFeed(final String feedName, final String rawItemType, final Map<String, Object> keyFields)
			throws DataHubCommunicationException
	{
		final WebTarget feedTarget = feedTarget(feedName, rawItemType, keyFields);
		try
		{
			return retryTemplate.execute(context -> deleteByFeedInternal(feedTarget));
		}
		catch (Exception ex)
		{
			throw new DataHubCommunicationException(COMMUNICATION_ERROR_MSG, ex);
		}
	}

	private static ResultData deleteByFeedInternal(final WebTarget feedTarget) throws DataHubOutboundException
	{
		final Response deleteResponse = sendDeleteFromFeedRequest(feedTarget);
		return ResponseHandler.handle(deleteResponse, OutboundOperationType.DELETE);
	}

	private static Response sendDeleteFromPoolRequest(final WebTarget poolTarget)
	{
		return poolTarget.request().delete();
	}

	private WebTarget poolTarget(final String pool, final String type, final Map<String, String> keys)
	{
		final WebTarget res = createTarget("/pools/" + pool + "/items/" + type);
		return addParam(res, "keyFields", keys);
	}

	private static Response sendDeleteFromFeedRequest(final WebTarget feedTarget) throws DataHubCommunicationException
	{
		try
		{
			return feedTarget.request().delete();
		}
		catch (final Exception ex)
		{
			throw new DataHubCommunicationException(COMMUNICATION_ERROR_MSG, ex);
		}
	}

	private WebTarget feedTarget(final String feedName, final String rawType, final Map<String, Object> attributes)
	{
		final WebTarget req = createTarget("/data-feeds/" + feedName + "/types/" + rawType);
		return addParam(req, "rawFields", attributes);
	}

	private WebTarget createTarget(final String url)
	{
		return client.target(dataHubUrl + url);
	}

	private static WebTarget addParam(final WebTarget res, final String paramName, final Map<String, ?> attributes)
	{
		if (MapUtils.isNotEmpty(attributes))
		{
			try
			{
				final String json = MAPPER.writeValueAsString(attributes);
				return res.queryParam(paramName, URLEncoder.encode(json, "UTF-8"));
			}
			catch (final Exception e)
			{
				throw new IllegalArgumentException("Could not convert key fields map into a JSON query string", e);
			}
		}
		return res;
	}

	@Required
	public void setRetryTemplate(final RetryTemplate retryTemplate)
	{
		this.retryTemplate = retryTemplate;
	}

	/**
	 * Specifies DataHub location.
	 *
	 * @param dataHubUrl URL for the DataHub server to exchange data with.
	 */
	@Required
	public void setDataHubUrl(final String dataHubUrl)
	{
		this.dataHubUrl = dataHubUrl;
	}

	private enum OutboundOperationType
	{
		EXPORT("Export", "exporting"), DELETE("Delete", "deleting");

		private final String name;
		private final String presentTense;

		private OutboundOperationType(final String enumName, final String presentTense)
		{
			this.name = enumName;
			this.presentTense = presentTense;
		}
	}

	private static class ResponseHandler
	{
		private static final int OK = 200;
		private static final int BAD_REQUEST = 400;
		private static final int NOT_FOUND = 404;
		private static final int SERVER_ERROR = 500;

		private ResponseHandler()
		{
			// nothing
		}

		private static ResultData handle(final Response response, final OutboundOperationType operationType)
				throws DataHubOutboundException
		{
			final int responseStatus = response.getStatus();
			if (responseStatus == OK)
			{
				return response.readEntity(ResultData.class);
			}

			final String message = response.readEntity(String.class);
			switch (responseStatus)
			{
				case BAD_REQUEST:
					throw new DataHubOutboundException(operationType.name + " operation was unsuccessful : " + message);
				case NOT_FOUND:
				case SERVER_ERROR:
					throw new DataHubCommunicationException("Communication failure with the Data Hub server: " + message);
				default:
					throw new IllegalStateException("Exception occurred while " + operationType.presentTense
							+ " item from the Data Hub. The status received was " + responseStatus + "; the message was '"
							+ message + "'");
			}
		}
	}
}