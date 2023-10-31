/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.enums.HttpMethod;
import de.hybris.platform.integrationservices.jalo.IntegrationObject;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.event.impl.DefaultEventType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Context that is used to hold & transfer data pertaining to outbound requests.
 */
public class DecoratorContext
{
	private static final EventType DEFAULT_EVENT_TYPE = DefaultEventType.UNKNOWN;
	private static final OutboundSource DEFAULT_SOURCE = OutboundSource.UNKNOWN;
	private static final HttpMethod DEFAULT_HTTP_METHOD = HttpMethod.POST;

	private final IntegrationObjectDescriptor integrationObject;
	private final ConsumedDestinationModel destinationModel;
	private final EventType eventType;
	private final HttpMethod httpMethod;
	private final Collection<String> errors;
	private final Object payloadObject;
	private final OutboundSource outboundSource;
	private final String integrationKey;

	/**
	 * Instantiates a new context.
	 *
	 * @param io integration object that governs the {@code item} presentation in the external system
	 * @param destination destination where the {@code item} is being sent.
	 * @param payload payload being synchronized to an external system
	 * @param key integrationKey for the integration object
	 * @param source platform module that is performing the {@code item} synchronization. If value is not provided, i.e. it is
	 *                    {@code null}, then a default value of {@link OutboundSource#UNKNOWN} is used.
	 * @param method httpMethod represented as a string
	 * @param type event type {@link EventType} that stores the operation change value
	 * @param errors errors that will be included in the monitoring request if any are present
	 */
	DecoratorContext(@NotNull final IntegrationObjectDescriptor io,
	                 @NotNull final ConsumedDestinationModel destination,
	                 final Object payload,
					 final String key,
	                 final OutboundSource source,
	                 final HttpMethod method,
	                 final EventType type,
	                 final Collection<String> errors)
	{
		Preconditions.checkArgument(io != null, "IntegrationObjectDescriptor cannot be null");
		Preconditions.checkArgument(destination != null, "ConsumedDestinationModel cannot be null");
		validateParameters(method, payload, key);

		integrationObject = io;
		destinationModel = destination;
		payloadObject = payload;
		integrationKey = key;
		outboundSource = source == null ? DEFAULT_SOURCE : source;
		httpMethod = method == null ? DEFAULT_HTTP_METHOD : method;
		eventType = type == null ? DEFAULT_EVENT_TYPE : type;
		this.errors = errors == null ? new ArrayList<>() : new ArrayList<>(errors);
	}

	public static DecoratorContextBuilder decoratorContextBuilder()
	{
		return new DecoratorContextBuilder();
	}

	/**
	 * This will return the {@link ItemModel} as the payload object.
	 *
	 * @return the item model or {@code null} when the payload object is not of type {@link ItemModel}.
	 * @deprecated use {@link #getPayloadObject()} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	@Nullable
	public ItemModel getItemModel()
	{
		return getPayloadObject() instanceof ItemModel item ? item : null;
	}

	/**
	 * This will return the payload object according to the integration object specs. It can be traditional {@link ItemModel} or
	 * any POJO.
	 *
	 * @return the payload object.
	 */
	public Object getPayloadObject()
	{
		return payloadObject;
	}

	/**
	 * Retrieves the {@link IntegrationObject#getCode()} value.
	 *
	 * @return integration object code
	 */
	@NotNull
	public IntegrationObjectDescriptor getIntegrationObject()
	{
		return integrationObject;
	}

	/**
	 * A convenience/shortcut method for retrieving the context integration object code. Calling this method is same as
	 * {@code getIntegrationObject().getCode()}
	 *
	 * @return code of the context integration object.
	 */
	public String getIntegrationObjectCode()
	{
		return getIntegrationObject().getCode();
	}

	/**
	 * Retrieves integration object item type descriptor for the context integration object and item model.
	 *
	 * @return an {@code Optional} containing a {@code TypeDescriptor}, if the integration object contains at least one item
	 * for the context item model type. I.e. there is a integration object item model matching the {@code getItemModel().getItemtype()}.
	 * Otherwise, an {@code Optional.empty()} is returned.
	 */
	public Optional<TypeDescriptor> getIntegrationObjectItem()
	{
		return getIntegrationObject().getTypeDescriptor(getPayloadObject());
	}

	/**
	 * Retrieves destination, to which the item should be sent.
	 *
	 * @return consumed destination
	 */
	@NotNull
	public ConsumedDestinationModel getDestinationModel()
	{
		return destinationModel;
	}

	/**
	 * Retrieves a list of errors that indicate why this context is not valid
	 * or returns an empty list.
	 *
	 * @return a list of errors
	 */
	@NotNull
	public Collection<String> getErrors()
	{
		return new ArrayList<>(errors);
	}

	/**
	 * Retrieves a value indicating if this context is valid
	 * based off the presence of errors.
	 *
	 * @return {@code true} if this context has errors, else {@code false}
	 */
	@NotNull
	public boolean hasErrors()
	{
		return !errors.isEmpty();
	}

	/**
	 * Retrieves source of the item synchronization.
	 *
	 * @return a platform module that sends the item to external system.
	 */
	@NotNull
	public OutboundSource getSource()
	{
		return outboundSource;
	}

	/**
	 * Retrieves {@link EventType} of the value
	 *
	 * @return value of event type
	 */
	public EventType getEventType()
	{
		return eventType;
	}

	/**
	 * Retrieves httpMethod of the item synchronization.
	 *
	 * @return httpMethod for the delta being sent
	 */
	@NotNull
	public HttpMethod getHttpMethod()
	{
		return httpMethod;
	}

	public String getIntegrationKey()
	{
		return integrationKey;
	}

	private static void validateParameters(HttpMethod method, Object payload, String integrationKey) {
		if (method == HttpMethod.DELETE && StringUtils.isEmpty(integrationKey)) {
			throw new IllegalArgumentException("An integration key must be provided for the HttpMethod DELETE.");
		}
		else if (method == HttpMethod.POST && payload == null)
		{
			throw new IllegalArgumentException("A payload must be provided for the HttpMethod POST");
		}
	}
}
