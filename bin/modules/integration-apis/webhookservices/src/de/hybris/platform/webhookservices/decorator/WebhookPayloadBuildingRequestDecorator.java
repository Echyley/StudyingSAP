/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.decorator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.impl.DefaultPayloadBuildingRequestDecorator;
import de.hybris.platform.webhookservices.model.WebhookPayloadModel;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;


/**
 * A webhook payload decorator.
 */
public class WebhookPayloadBuildingRequestDecorator extends DefaultPayloadBuildingRequestDecorator
{
	@Override
	public HttpEntity<Map<String, Object>> decorate(final HttpHeaders httpHeaders, final Map<String, Object> payload,
	                                                final DecoratorContext context, final DecoratorExecution execution)
	{

		final Object payloadObject = context.getPayloadObject();

		if ((payloadObject instanceof ItemModel) && WebhookPayloadModel._TYPECODE.contentEquals(
				((ItemModel) payloadObject).getItemtype()))
		{
			final var webhookOutboundPayload = (WebhookPayloadModel) context.getPayloadObject();
			payload.putAll(webhookOutboundPayload.getData());
			return execution.createHttpEntity(httpHeaders, payload, context);
		}
		else
		{
			return super.decorate(httpHeaders, payload, context, execution);
		}
	}
}
