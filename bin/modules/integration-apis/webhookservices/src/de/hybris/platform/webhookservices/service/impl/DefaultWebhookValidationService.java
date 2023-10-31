/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.event.impl.DefaultEventType;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.facade.impl.RemoteSystemClient;
import de.hybris.platform.webhookservices.event.ItemCreatedEvent;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.event.ItemSavedEvent;
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigurationValidationException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.CloudEventHeadersService;
import de.hybris.platform.webhookservices.service.WebhookValidationService;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import rx.Observable;


/**
 * Default implementation of WebhookValidationService
 */
public class DefaultWebhookValidationService implements WebhookValidationService
{
	private static final Logger LOG = Log.getLogger(DefaultWebhookValidationService.class);
	private static final String UPDATED_EVENT_TYPE = "Updated";
	private static final String CREATED_EVENT_TYPE = "Created";
	private static final String DELETED_EVENT_TYPE = "Deleted";
	private static final String UNKNOWN_EVENT_TYPE = "Unknown";
	private static final Map<String, String> EVENT_TYPES = Map.of(
			ItemUpdatedEvent.class.getCanonicalName(), UPDATED_EVENT_TYPE,
			ItemCreatedEvent.class.getCanonicalName(), CREATED_EVENT_TYPE,
			ItemSavedEvent.class.getCanonicalName(), UPDATED_EVENT_TYPE,
			ItemDeletedEvent.class.getCanonicalName(), DELETED_EVENT_TYPE);
	private static final CloudEventHeadersService DEFAULT_EVENT_HEADER_SERVICE = new DefaultCloudEventHeadersService(
			new DefaultCloudEventConfigurationService());
	private static final List<HttpStatus> HTTP_STATUS_ERROR_CODES_LIST = List.of(
			HttpStatus.BAD_REQUEST,
			HttpStatus.UNAUTHORIZED,
			HttpStatus.FORBIDDEN,
			HttpStatus.NOT_FOUND,
			HttpStatus.METHOD_NOT_ALLOWED,
			HttpStatus.INTERNAL_SERVER_ERROR,
			HttpStatus.SERVICE_UNAVAILABLE);

	private final RemoteSystemClient remoteSystemClient;
	private final OutboundServiceFacade outboundServiceFacade;
	private final CloudEventHeadersService cloudEventHeadersService;

	/**
	 * Instantiates the {@link DefaultWebhookValidationService}
	 *
	 * @param integrationRestTemplateFactory Factory class to create REST clients for webhook integrations.
	 * @param outboundServiceFacade          OutboundServiceFacade builds the payload and to integrate with restful endpoint
	 * @param cloudEventHeadersService       to generate CloudEvent headers
	 * @deprecated Use the {@link #DefaultWebhookValidationService(RemoteSystemClient, OutboundServiceFacade, CloudEventHeadersService)}
	 * constructor instead.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public DefaultWebhookValidationService(@NotNull final IntegrationRestTemplateFactory integrationRestTemplateFactory,
	                                       @NotNull final OutboundServiceFacade outboundServiceFacade,
	                                       @NotNull final CloudEventHeadersService cloudEventHeadersService)
	{
		this(getContextRemoteSystemClient(), outboundServiceFacade, cloudEventHeadersService);
	}

	/**
	 * Instantiates the {@link DefaultWebhookValidationService}
	 *
	 * @param integrationRestTemplateFactory Factory class to create REST clients for webhook integrations.
	 * @param outboundServiceFacade          OutboundServiceFacade builds the payload and to integrate with restful endpoint
	 * @deprecated Use the {@link #DefaultWebhookValidationService(RemoteSystemClient, OutboundServiceFacade, CloudEventHeadersService)}
	 * constructor instead.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public DefaultWebhookValidationService(@NotNull final IntegrationRestTemplateFactory integrationRestTemplateFactory,
	                                       @NotNull final OutboundServiceFacade outboundServiceFacade)
	{
		this(getContextRemoteSystemClient(), outboundServiceFacade, DEFAULT_EVENT_HEADER_SERVICE);
	}

	/**
	 * Instantiates the {@link DefaultWebhookValidationService}
	 *
	 * @param remoteClient             Implementation of the remote system client
	 * @param outboundServiceFacade    OutboundServiceFacade builds the payload and to integrate with restful endpoint
	 * @param cloudEventHeadersService to generate CloudEvent headers
	 */
	public DefaultWebhookValidationService(@NotNull final RemoteSystemClient remoteClient,
	                                       @NotNull final OutboundServiceFacade outboundServiceFacade,
	                                       @NotNull final CloudEventHeadersService cloudEventHeadersService)
	{
		Preconditions.checkArgument(remoteClient != null, "RemoteSystemClient cannot be null");
		Preconditions.checkArgument(outboundServiceFacade != null, "OutboundServiceFacade cannot be null");
		Preconditions.checkArgument(cloudEventHeadersService != null, "CloudEventHeadersService cannot be null");

		this.remoteSystemClient = remoteClient;
		this.outboundServiceFacade = outboundServiceFacade;
		this.cloudEventHeadersService = cloudEventHeadersService;
	}

	@Override
	public void pingWebhookDestination(@NotNull final WebhookConfigurationModel webhookConfiguration,
	                                   @NotNull final String webhookPayload) throws WebhookConfigurationValidationException
	{
		final Map<String, Object> jsonPayload = parsePayload(webhookPayload);

		sendMockedPayload(webhookConfiguration, jsonPayload);
	}

	@Override
	public Observable<ResponseEntity<Map>> pingWebhookDestination(@NotNull final WebhookConfigurationModel webhookConfig,
	                                                              @NotNull final ItemModel item)
	{
		return sendItemToWebhook(webhookConfig, item);
	}

	private static RemoteSystemClient getContextRemoteSystemClient()
	{
		return ApplicationBeans.getBean("remoteSystemClient", RemoteSystemClient.class);
	}

	private Map<String, Object> parsePayload(final String payload)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(payload), "webhookPayload cannot be blank");

		final ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> jsonPayload = null;
		try
		{
			jsonPayload = objectMapper.readValue(payload, new TypeReference<>()
			{
			});
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Error while parsing payload {}.", payload, e);
		}
		return jsonPayload;
	}

	private void sendMockedPayload(final WebhookConfigurationModel webhookConfig, final Map<String, Object> jsonPayload)
			throws WebhookConfigurationValidationException
	{
		Preconditions.checkArgument(webhookConfig != null, "webhookConfiguration cannot be null");
		Preconditions.checkArgument(webhookConfig.getDestination() != null, "consumedDestination cannot be null");

		final HttpEntity<Map<String, Object>> request = generateHttpEntityWithHeaders(webhookConfig, jsonPayload);

		try
		{
			remoteSystemClient.post(webhookConfig.getDestination(), request);
		}
		catch (final HttpStatusCodeException e)
		{
			if (HTTP_STATUS_ERROR_CODES_LIST.contains(e.getStatusCode()))
			{
				throw new WebhookConfigurationValidationException(e.getMessage(), e);
			}
		}
		catch (final ResourceAccessException e)
		{
			if (e.getCause() instanceof SocketTimeoutException)
			{
				throw new WebhookConfigurationValidationException(e.getMessage(), e.getCause());
			}
			else
			{
				throw new WebhookConfigurationValidationException(e.getMessage(), e);
			}
		}
		catch (final RuntimeException e)
		{
			final String destinationId = webhookConfig.getDestination().getId();
			final String errorMessage = String.format("Request failed to destination [{%s}] with error [{%s}]", destinationId,
					e.getMessage());
			LOG.error(errorMessage);
			throw new WebhookConfigurationValidationException(e.getMessage(), e);
		}
	}

	private Observable<ResponseEntity<Map>> sendItemToWebhook(final WebhookConfigurationModel webhookConfig, final ItemModel item)
	{
		final SyncParameters params = SyncParameters.syncParametersBuilder()
		                                            .withPayloadObject(item)
		                                            .withSource(OutboundSource.WEBHOOKSERVICES)
		                                            .withIntegrationObject(webhookConfig.getIntegrationObject())
		                                            .withDestination(webhookConfig.getDestination())
		                                            .withEventType(getEventType(webhookConfig))
		                                            .build();
		return outboundServiceFacade.send(params);
	}

	private HttpEntity<Map<String, Object>> generateHttpEntityWithHeaders(final WebhookConfigurationModel webhookConfig,
	                                                                      final Map<String, Object> jsonPayload)
	{
		final HttpHeaders headers = generateHttpHeaders(webhookConfig.getIntegrationObject(), getIntegrationKey(jsonPayload),
				getEventType(webhookConfig));
		return new HttpEntity<>(jsonPayload, headers);
	}

	private HttpHeaders generateHttpHeaders(final IntegrationObjectModel integrationObject,
	                                        final String integrationKeyValue,
	                                        final EventType eventType)
	{
		Preconditions.checkArgument(integrationObject != null, "integrationObject cannot be null");

		final HttpHeaders headers = cloudEventHeadersService
				.generateCloudEventHeaders(integrationObject.getCode(), integrationKeyValue, eventType, null);

		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

	private String getIntegrationKey(final Map<String, Object> payload)
	{
		final Object integrationKey = payload.get(INTEGRATION_KEY_PROPERTY_NAME);
		return integrationKey != null ? integrationKey.toString() : "";
	}

	private DefaultEventType getEventType(final WebhookConfigurationModel webhookConfig)
	{
		return new DefaultEventType(getWebhookConfigEventType(webhookConfig));
	}

	private String getWebhookConfigEventType(final WebhookConfigurationModel webhookConfig)
	{
		return EVENT_TYPES.getOrDefault(webhookConfig.getEventType(), UNKNOWN_EVENT_TYPE);
	}
}
