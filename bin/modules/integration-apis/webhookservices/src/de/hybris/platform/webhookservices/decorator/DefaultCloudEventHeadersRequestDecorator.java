/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.decorator;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.SAP_PASSPORT_HEADER_NAME;

import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.webhookservices.service.CloudEventHeadersService;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Preconditions;

/**
 * CloudEvent headers implementation of {@link OutboundRequestDecorator}
 */
public class DefaultCloudEventHeadersRequestDecorator implements OutboundRequestDecorator
{
	private final CloudEventHeadersService cloudEventHeadersService;

	/**
	 * Constructor to create DefaultCloudEventHeadersRequestDecorator
	 *
	 * @param cloudEventHeadersService to generate CloudEvent headers
	 */
	public DefaultCloudEventHeadersRequestDecorator(@NotNull final CloudEventHeadersService cloudEventHeadersService)
	{
		Preconditions.checkArgument(cloudEventHeadersService != null, "cloudEventHeadersService cannot be null");
		this.cloudEventHeadersService = cloudEventHeadersService;
	}

	@Override
	public boolean isApplicable(final DecoratorContext decoratorContext)
	{
		return OutboundSource.WEBHOOKSERVICES.equals(decoratorContext.getSource());
	}

	@Override
	public HttpEntity<Map<String, Object>> decorate(@NotNull final HttpHeaders httpHeaders,
	                                                @NotNull final Map<String, Object> payload,
	                                                @NotNull final DecoratorContext decoratorContext,
	                                                @NotNull final DecoratorExecution execution)
	{
		final HttpHeaders cloudEventHeaders = cloudEventHeadersService.generateCloudEventHeaders(decoratorContext.getIntegrationObjectCode(),
				getIntegrationKey(payload), decoratorContext.getEventType() ,httpHeaders.getFirst(SAP_PASSPORT_HEADER_NAME));

		httpHeaders.addAll(cloudEventHeaders);

		return execution.createHttpEntity(httpHeaders, payload, decoratorContext);
	}

	private String getIntegrationKey(@NotNull final Map<String, Object> payload)
	{
		final Object integrationKey = payload.get(INTEGRATION_KEY_PROPERTY_NAME);
		return integrationKey != null ? integrationKey.toString() : "";
	}
}
