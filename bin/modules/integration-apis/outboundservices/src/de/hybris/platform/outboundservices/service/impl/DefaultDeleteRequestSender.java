/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service.impl;

import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.decorator.RequestDecoratorService;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.service.DeleteRequestSender;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import com.google.common.base.Preconditions;

/**
 * Default implementation of the {@code DeleteRequestSender} that uses RestTemplates for communication with the remote system.
 */
public class DefaultDeleteRequestSender implements DeleteRequestSender
{
	private static final String INTEGRATION_KEY_PARAM_NAME = "key";
	private static final String RESOURCE_ID_PART = "('{" + INTEGRATION_KEY_PARAM_NAME + "}')";
	private final IntegrationRestTemplateFactory restTemplateFactory;
	private final RequestDecoratorService requestDecoratorService;


	/**
	 * Instantiates this sender
	 *
	 * @param factory rest template factory to be used for {@link org.springframework.web.client.RestTemplate} creation.
	 *                The {@code RestTemplate}s will be used to communicate to the remote system.
	 */
	public DefaultDeleteRequestSender(@NotNull final IntegrationRestTemplateFactory factory,
	                                  @NotNull final RequestDecoratorService decoratorService)
	{
		Preconditions.checkArgument(factory != null, "IntegrationRestTemplateFactory cannot be null");
		Preconditions.checkArgument(decoratorService != null, "RequestDecoratorService cannot be null");
		restTemplateFactory = factory;
		requestDecoratorService = decoratorService;
	}

	@Override
	public void send(final SyncParameters params)
	{
		final HttpEntity<Map<String, Object>> httpEntity = requestDecoratorService.createHttpEntity(params);
		final var restTemplate = restTemplateFactory.create(params.getDestination());

		restTemplate.exchange(urlForDelete(params), HttpMethod.DELETE, httpEntity, Object.class, asParamMap(params));
	}

	private String urlForDelete(final SyncParameters params)
	{
		return params.getDestination().getUrl() + RESOURCE_ID_PART;
	}

	private Map<String, String> asParamMap(final SyncParameters params)
	{
		return Map.of(INTEGRATION_KEY_PARAM_NAME, params.getIntegrationKey());
	}
}
