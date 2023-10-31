/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.router;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.messaging.Message;

import java.util.Map;

public class EventSourceDataRouter
{
    private Map<String, String> eventRoutingMap;

    public String route(Message<EventSourceData> msg)
    {
        final EventConfigurationModel eventConfig = msg.getPayload().getEventConfig();
        if (eventConfig.getDestinationTarget() != null && eventConfig.getDestinationTarget().getDestinationChannel() != null)
        {
            return getEventRoutingMap().get(eventConfig.getDestinationTarget().getDestinationChannel().getCode());
        }
        else
        {
            final Object errorChannel = msg.getHeaders().getErrorChannel();
            return errorChannel != null ? errorChannel.toString() : StringUtils.EMPTY;
        }
    }

    protected Map<String, String> getEventRoutingMap()
    {
        return eventRoutingMap;
    }

    @Required
    public void setEventRoutingMap(Map<String, String> eventRoutingMap)
    {
        this.eventRoutingMap = eventRoutingMap;
    }
}
