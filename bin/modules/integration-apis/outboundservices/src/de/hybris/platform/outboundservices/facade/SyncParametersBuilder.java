/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.service.DestinationSearchService;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * A builder for creating {@link SyncParameters} instances.
 */
public class SyncParametersBuilder
{
	private static IntegrationObjectService ioService;
	private static DestinationSearchService destinationSearchService;

	private Object payloadObject;
	private String ioCode;
	private String destId;
	private OutboundSource source;
	private ConsumedDestinationModel destinationModel;
	private IntegrationObjectModel integrationObjectModel;
	private EventType eventType;
	private String integrationKey;
	private String changeId;

	SyncParametersBuilder()
	{
		// non-instantiable through constructor, use the factory method in SyncParameters
	}

	public static SyncParametersBuilder from(final SyncParameters params)
	{
		return new SyncParametersBuilder().withPayloadObject(params.getPayloadObject())
		                                  .withSource(params.getSource())
		                                  .withDestination(params.getDestination())
		                                  .withIntegrationObject(params.getIntegrationObject())
		                                  .withEventType(params.getEventType())
		                                  .withIntegrationKey(params.getIntegrationKey());
	}

	/**
	 * @deprecated use {@link #withPayloadObject(Object)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public SyncParametersBuilder withItem(final ItemModel item)
	{
		return withPayloadObject(item);
	}

	public SyncParametersBuilder withPayloadObject(final Object payloadObject)
	{
		this.payloadObject = payloadObject;
		return this;
	}

	/**
	 * @deprecated use {@link #withDestination(ConsumedDestinationModel)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public SyncParametersBuilder withDestinationId(final String destId)
	{
		this.destId = destId;
		return this;
	}

	public SyncParametersBuilder withSource(final OutboundSource src)
	{
		this.source = src;
		return this;
	}

	public SyncParametersBuilder withDestination(final ConsumedDestinationModel destinationModel)
	{
		this.destinationModel = destinationModel;
		return this;
	}

	public SyncParametersBuilder withIntegrationObject(final IntegrationObjectModel integrationObjectModel)
	{
		this.integrationObjectModel = integrationObjectModel;
		return this;
	}

	public SyncParametersBuilder withEventType(final EventType eventType)
	{
		this.eventType = eventType;
		return this;
	}

	public SyncParametersBuilder withIntegrationKey(final String keyValue)
	{
		integrationKey = keyValue;
		return this;
	}

	public SyncParametersBuilder withChangeId(final String id)
	{
		changeId = id;
		return this;
	}

	/**
	 * @deprecated use {@link #withIntegrationObject(IntegrationObjectModel)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public SyncParametersBuilder withIntegrationObjectCode(final String ioCode)
	{
		this.ioCode = ioCode;
		return this;
	}

	/**
	 * Builds the SyncParameters from the values provided to the builder.
	 *
	 * @return the SyncParameters with the provided values.
	 */
	public SyncParameters build()
	{
		return new SyncParameters(
				payloadObject,
				source,
				getDestination(),
				getIOModel(),
				eventType,
				integrationKey,
				getChangeId());
	}

	private ConsumedDestinationModel getDestination()
	{
		Preconditions.checkArgument(destinationModel != null || StringUtils.isNotBlank(destId),
				"Either ConsumedDestinationModel, or destinationId must be provided.");

		return destinationModel == null
				? getDestinationSearchService().findDestination(destId)
				: destinationModel;
	}

	private IntegrationObjectModel getIOModel()
	{
		Preconditions.checkArgument(integrationObjectModel != null || StringUtils.isNotBlank(ioCode),
				"Either IntegrationObjectModel, or integrationObjectCode must be provided.");

		return integrationObjectModel == null
				? getIOService().findIntegrationObject(ioCode)
				: integrationObjectModel;
	}

	private String getChangeId()
	{
		return changeId != null
				? changeId
				: UUID.randomUUID().toString();
	}

	private static synchronized IntegrationObjectService getIOService()
	{
		if (ioService == null)
		{
			ioService = ApplicationBeans.getBean("integrationObjectService", IntegrationObjectService.class);
		}
		return ioService;
	}

	private static synchronized DestinationSearchService getDestinationSearchService()
	{
		if (destinationSearchService == null)
		{
			destinationSearchService =
					ApplicationBeans.getBean("destinationSearchService", DestinationSearchService.class);
		}
		return destinationSearchService;
	}
}
