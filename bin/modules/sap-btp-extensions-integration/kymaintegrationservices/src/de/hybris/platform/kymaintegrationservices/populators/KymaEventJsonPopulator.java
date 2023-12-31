/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.kymaintegrationservices.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.kymaintegrationservices.dto.JsonPublishRequestData;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;

import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Kyma json specific event populator
 */
public class KymaEventJsonPopulator implements Populator<PublishRequestData, JsonPublishRequestData>
{
	private ObjectMapper jacksonObjectMapper;

	@Override
	public void populate(final PublishRequestData publishRequestData, final JsonPublishRequestData jsonPublishRequestData)
	{
		jsonPublishRequestData.setEventTypeVersion(publishRequestData.getEventTypeVersion());
		jsonPublishRequestData.setEventType(publishRequestData.getEventType());
		jsonPublishRequestData.setEventTime(publishRequestData.getEventTime());
		jsonPublishRequestData.setEventId(publishRequestData.getEventId());
		jsonPublishRequestData.setData(getJacksonObjectMapper().valueToTree(publishRequestData.getData()));
	}

	protected ObjectMapper getJacksonObjectMapper()
	{
		return jacksonObjectMapper;
	}

	@Required
	public void setJacksonObjectMapper(final ObjectMapper jacksonObjectMapper)
	{
		this.jacksonObjectMapper = jacksonObjectMapper;
	}
}
