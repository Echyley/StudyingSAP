/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.event.impl.DefaultEventType;

import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

/**
 * Item synchronization parameters.
 */
public class SyncParameters
{
	private static final OutboundSource DEFAULT_SOURCE = OutboundSource.UNKNOWN;
	private static final EventType DEFAULT_EVENT_TYPE = DefaultEventType.UNKNOWN;

	private final Object payloadObject;
	private final OutboundSource source;
	private final EventType eventType;
	private final String integrationKey;
	private final IntegrationObjectModel integrationObjectModel;
	private final ConsumedDestinationModel destinationModel;
	private final String changeId;

	/**
	 * @deprecated use {@link SyncParametersBuilder} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	protected SyncParameters(final ItemModel item,
	                         final String ioCode,
	                         final IntegrationObjectModel ioModel,
	                         final String destId,
	                         final ConsumedDestinationModel destModel,
	                         final OutboundSource source)
	{
		final SyncParameters validatedParams =
				syncParametersBuilder()
						.withPayloadObject(item)
						.withIntegrationObjectCode(ioCode)
						.withIntegrationObject(ioModel)
						.withDestinationId(destId)
						.withDestination(destModel)
						.withSource(source)
						.build();

		payloadObject = validatedParams.payloadObject;
		this.source = validatedParams.source;
		eventType = validatedParams.eventType;
		integrationKey = validatedParams.integrationKey;
		integrationObjectModel = validatedParams.integrationObjectModel;
		changeId = validatedParams.changeId;
		destinationModel = validatedParams.destinationModel;
	}

	SyncParameters(final Object payloadObject,
	               final OutboundSource source,
	               final ConsumedDestinationModel destination,
	               final IntegrationObjectModel ioModel,
	               final EventType eventType,
	               final String integrationKey,
	               final String changeId)
	{
		Preconditions.checkArgument(payloadObject != null || StringUtils.isNotBlank(integrationKey),
				"Either payloadObject, or integrationKey must be provided.");

		destinationModel = Objects.requireNonNull(destination, "ConsumedDestinationModel cannot be null.");
		integrationObjectModel = Objects.requireNonNull(ioModel, "IntegrationObjectModel cannot be null.");

		this.payloadObject = payloadObject;
		this.source = source == null ? DEFAULT_SOURCE : source;
		this.eventType = eventType == null ? DEFAULT_EVENT_TYPE : eventType;
		this.integrationKey = integrationKey;
		this.changeId = changeId;
	}

	/**
	 * This will return the {@link ItemModel} as the payload object.
	 *
	 * @return the item model, or {@code null} when the payload object is not of type {@link ItemModel}.
	 * @deprecated use {@link #getPayloadObject()} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	@Nullable
	public ItemModel getItem()
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
	 * Retrieves information about the integration object used for outbound communication.
	 *
	 * @return code of the integration object to be used.
	 */
	public String getIntegrationObjectCode()
	{
		return getIntegrationObject().getCode();
	}

	/**
	 * Retrieves information about the destination, to which data should be sent.
	 *
	 * @return ID of the {@link ConsumedDestinationModel} to send data to.
	 */
	public String getDestinationId()
	{
		return getDestination().getId();
	}

	/**
	 * Retrieves information about the module that initiated the data exchange with the destination.
	 *
	 * @return module that sends data to the remote system.
	 * @see #getDestination()
	 */
	public OutboundSource getSource()
	{
		return source;
	}

	/**
	 * Retrieves destination to which data should be sent.
	 *
	 * @return a destination representing a remote system receiving data.
	 */
	public ConsumedDestinationModel getDestination()
	{
		return destinationModel;
	}

	/**
	 * Retrieves integration object used for outbound communication.
	 *
	 * @return integration object to be used.
	 */
	public IntegrationObjectModel getIntegrationObject()
	{
		return integrationObjectModel;
	}

	/**
	 * Retrieves type of the event that triggered outbound synchronization.
	 *
	 * @return type of the event or {@link DefaultEventType#UNKNOWN}, if the synchronization is not triggered by event but is a
	 * scheduled batch synchronization.
	 */
	public EventType getEventType()
	{
		return eventType;
	}

	/**
	 * Retrieves integration key value.
	 *
	 * @return integration key of the item being synchronized.
	 */
	public String getIntegrationKey()
	{
		return integrationKey;
	}

	/**
	 * Retrieves the change id
	 *
	 * @return change id
	 */
	public String getChangeId()
	{
		return changeId;
	}

	/**
	 * Determines whether the given SyncParameters supports outbound batch requests based on the type of IntegrationObject modeling.
	 *
	 * @return {@code true}, if the integration object has no class types. {@code false}, otherwise.
	 */
	public boolean isBatchSupported()
	{
		return getIntegrationObject().getClasses().isEmpty();
	}

	/**
	 * Returns a builder for creation of {@code SyncParameters}.
	 *
	 * @return a builder to use for {@code SyncParameters} creation. Returned builder has empty state, that is no
	 * {@code SyncParameters} property is specified for it.
	 */
	public static SyncParametersBuilder syncParametersBuilder()
	{
		return new SyncParametersBuilder();
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final SyncParameters that = (SyncParameters) o;

		return new EqualsBuilder()
				.append(getPayloadObject(), that.getPayloadObject())
				.append(getIntegrationObjectCode(), that.getIntegrationObjectCode())
				.append(getDestinationId(), that.getDestinationId())
				.append(getSource(), that.getSource())
				.append(getEventType(), that.getEventType())
				.append(getIntegrationKey(), that.getIntegrationKey())
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(getPayloadObject())
				.append(getIntegrationObjectCode())
				.append(getDestinationId())
				.append(getSource())
				.append(getEventType())
				.append(getIntegrationKey())
				.append(getChangeId())
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return "SyncParameters{" +
				"payloadObject='" + getPayloadObject() +
				"', integrationKey='" + getIntegrationKey() +
				"', integrationObjectCode='" + getIntegrationObjectCode() +
				"', destinationId='" + getDestinationId() +
				"', source='" + getSource().getCode() +
				"', eventType='" + getEventType().getType() +
				"', changeId='" + getChangeId() +
				"'}";
	}
}
