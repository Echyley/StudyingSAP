/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.kymaintegrationservices.event.impl;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.enums.EventMappingType;
import de.hybris.platform.apiregistryservices.event.impl.EventServiceActivator;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.kymaintegrationservices.event.KymaEventFilterService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.google.common.base.Preconditions;


/**
 * Kyma-specific channel activator.
 * Only difference with default - type conversion.
 */
public class KymaEventServiceActivator extends EventServiceActivator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KymaEventServiceActivator.class);
	private Converter<EventSourceData, PublishRequestData> kymaEventConverter;
	private KymaEventFilterService kymaEventFilterService;

	@Override
	public void handle(final Message<?> message)
	{
		final Object payload = message.getPayload();

		if (payload instanceof PublishRequestData)
		{
			LOGGER.debug("Handle publish request data for the kyma event [{}].", ((PublishRequestData) payload).getEventId());
			super.handle(message);
		}
		else if (payload instanceof EventSourceData)
		{
			LOGGER.debug("Handle event source data for the kyma event [{}].", ((EventSourceData) payload).getEventConfig().getEventClass());
			handleEventSourceData(message, (EventSourceData) payload);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	private void handleEventSourceData(final Message<?> message, final EventSourceData eventSourceData)
	{
		if (filterKymaEvent(eventSourceData))
		{
			final EventConfigurationModel eventConfig = eventSourceData.getEventConfig();
			final Converter<EventSourceData, PublishRequestData> converter = getConverter(eventConfig);
			Preconditions.checkNotNull(converter, String.format("No converter found for the Kyma event [%s].", eventConfig.getEventClass()));

			final PublishRequestData requestData = converter.convert(eventSourceData);
			Preconditions.checkNotNull(requestData, String.format("Publish request data for the Kyma event [%s] cannot be NULL!", eventConfig.getEventClass()));

			super.handle(new GenericMessage<>(requestData, message.getHeaders()));
		}
	}

	private boolean filterKymaEvent(final EventSourceData eventSourceData)
	{
		return kymaEventFilterService.filterKymaEvent(eventSourceData);
	}
	
	protected Converter<EventSourceData, PublishRequestData> getConverter(final EventConfigurationModel eventConfiguration)
	{
		if (EventMappingType.GENERIC.equals(eventConfiguration.getMappingType()) || EventMappingType.PROCESS
				.equals(eventConfiguration.getMappingType()))
		{
			return kymaEventConverter;
		}
		else
		{
			return Registry.getApplicationContext().getBean(eventConfiguration.getConverterBean(), Converter.class);
		}
	}

	public void setKymaEventConverter(final Converter<EventSourceData, PublishRequestData> kymaEventConverter)
	{
		this.kymaEventConverter = kymaEventConverter;
	}

	public void setKymaEventFilterService(final KymaEventFilterService kymaEventFilterService)
	{
		this.kymaEventFilterService = kymaEventFilterService;
	}

}
