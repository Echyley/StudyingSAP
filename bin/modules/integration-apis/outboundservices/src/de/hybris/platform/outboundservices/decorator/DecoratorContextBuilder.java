/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.enums.HttpMethod;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.impl.NullIntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.service.IntegrationObjectDescriptorService;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.event.EventType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A builder for creating {@link DecoratorContext} instances.
 */
public class DecoratorContextBuilder
{
	private Object payloadObject;
	private IntegrationObjectDescriptor integrationObject;
	private ConsumedDestinationModel destinationModel;
	private OutboundSource outboundSource;
	private EventType eventType;
	private HttpMethod httpMethod;
	private String integrationKey;
	private Collection<String> errors;

	protected DecoratorContextBuilder()
	{
	}

	/**
	 * @deprecated since 2211.FP1. Will be removed. Use {@link #withPayloadObject(Object)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public DecoratorContextBuilder withItemModel(final ItemModel itemModel)
	{
		return withPayloadObject(itemModel);
	}

	/**
	 * Specifies an arbitrary payload object to be placed into the sync context to be created.
	 *
	 * @param payload arbitrary payload that will be used for describing data being sent out.
	 * @return a builder with the payload specified.
	 */
	public DecoratorContextBuilder withPayloadObject(Object payload)
	{
		payloadObject = payload;
		return this;
	}

	/**
	 * Specifies an integration object to be placed into the sync context to be created.
	 *
	 * @param io an integration object that will be used for describing data being sent out.
	 * @return a builder with the integration object specified.
	 */
	public DecoratorContextBuilder withIntegrationObject(final IntegrationObjectDescriptor io)
	{
		integrationObject = io;
		return this;
	}

	/**
	 * @deprecated since 2011. Not used anymore and will be removed. Use {@link #withIntegrationObject(IntegrationObjectDescriptor)}
	 * instead. To convert from the integration object code to {@code IntegrationObjectDescriptor}, use
	 * {@link IntegrationObjectDescriptorService#retrieve(String)} method. This is what this method does:
	 * <code>
	 * <pre>
	 *
	 *     final var ioService = ApplicationBeans.getBean(
	 *             "integrationObjectDescriptorService", IntegrationObjectDescriptorService.class);
	 *     final IntegrationObjectDescriptor ioDescriptor = ioService.retrieve(code);
	 *     return ioDescriptor == null
	 *             ? withIntegrationObject(new NullIntegrationObjectDescriptor(code))
	 *             : withIntegrationObject(ioDescriptor);
	 * </pre>
	 * </code>
	 */
	@Deprecated(since = "2011.0", forRemoval = true)
	public DecoratorContextBuilder withIntegrationObjectCode(final String code)
	{
		final var ioService = ApplicationBeans.getBean(
				"integrationObjectDescriptorService", IntegrationObjectDescriptorService.class);
		final IntegrationObjectDescriptor ioDescriptor = ioService.retrieve(code);
		return ioDescriptor == null
				? withIntegrationObject(new NullIntegrationObjectDescriptor(code))
				: withIntegrationObject(ioDescriptor);
	}

	public DecoratorContextBuilder withDestinationModel(final ConsumedDestinationModel destinationModel)
	{
		this.destinationModel = destinationModel;
		return this;
	}

	/**
	 * Specifies source module sending data outbound
	 *
	 * @param source a source module that initiated the outbound data exchange. If {@code null}, then default value of
	 *               {@link OutboundSource#UNKNOWN} is used.
	 * @return a builder with the source specified.
	 */
	public DecoratorContextBuilder withOutboundSource(final OutboundSource source)
	{
		outboundSource = source;
		return this;
	}

	public DecoratorContextBuilder withHttpMethod(final HttpMethod httpMethod)
	{
		this.httpMethod = httpMethod;
		return this;
	}

	public DecoratorContextBuilder withIntegrationKey(final String integrationKey)
	{
		this.integrationKey = integrationKey;
		return this;
	}

	public DecoratorContextBuilder withErrors(final Collection<String> errors)
	{
		this.errors = errors == null ? Collections.emptyList() : List.copyOf(errors);
		return this;
	}

	public DecoratorContextBuilder withEventType(final EventType eventType)
	{
		this.eventType = eventType;
		return this;
	}

	public DecoratorContext build()
	{
		return new DecoratorContext(integrationObject, destinationModel, payloadObject, integrationKey, outboundSource,
				httpMethod, eventType, errors);
	}
}
