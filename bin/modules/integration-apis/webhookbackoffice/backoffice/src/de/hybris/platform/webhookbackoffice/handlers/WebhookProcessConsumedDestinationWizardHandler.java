/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.handlers;

import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.NOTIFICATION_TYPE_LINK;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.OBJECTS_UPDATED_EVENT;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.OBJECTS_UPDATED_EVENT_TYPE;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webhookservices.event.ItemSavedEvent;
import de.hybris.platform.webhookservices.exceptions.DestinationTargetNoSupportedEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigInvalidChannelException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventConfigException;

import java.util.Map;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;

/**
 * A wizard handler for the dynamic creation of Consumed Destinations in the process of making Webhooks. Applies the specific
 * limitations of creating a Consumed Destination suitable for a Webhook.
 */
public class WebhookProcessConsumedDestinationWizardHandler implements FlowActionHandler
{
	static final String SUPPORTED_EVENT = ItemSavedEvent.class.getCanonicalName();

	private final ModelService modelService;
	private final CockpitEventQueue cockpitEventQueue;
	private final NotificationService notificationService;

	public WebhookProcessConsumedDestinationWizardHandler(final ModelService modelService,
	                                                      final CockpitEventQueue cockpitEventQueue,
	                                                      final NotificationService notificationService)
	{
		this.modelService = modelService;
		this.cockpitEventQueue = cockpitEventQueue;
		this.notificationService = notificationService;
	}

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter flowActionHandlerAdapter,
	                    final Map<String, String> map)
	{
		final ConsumedDestinationModel consumedDestination = flowActionHandlerAdapter.getWidgetInstanceManager()
		                                                                             .getModel()
		                                                                             .getValue("newConsumedDestination",
				                                                                             ConsumedDestinationModel.class);
		if (!isWebhookChannel(consumedDestination))
		{
			throw new IllegalArgumentException(new WebhookConfigInvalidChannelException(null));
		}
		if (!isEventNotEmpty(consumedDestination))
		{
			throw new IllegalArgumentException(new WebhookConfigNoEventConfigException(null));
		}
		if (!isSupportedEventExisting(consumedDestination))
		{
			throw new IllegalArgumentException(new DestinationTargetNoSupportedEventConfigException());
		}

		modelService.save(consumedDestination);
		publishEvent(consumedDestination);
		notificationService.notifyUser(flowActionHandlerAdapter.getWidgetInstanceManager(), NOTIFICATION_TYPE_LINK, Level.SUCCESS,
				consumedDestination);
		flowActionHandlerAdapter.done();
	}

	boolean isWebhookChannel(final ConsumedDestinationModel destination)
	{
		return destination != null && destination.getDestinationTarget() != null
				&& DestinationChannel.WEBHOOKSERVICES == destination.getDestinationTarget().getDestinationChannel();
	}

	boolean isEventNotEmpty(final ConsumedDestinationModel destination)
	{
		return destination != null && destination.getDestinationTarget() != null
				&& destination.getDestinationTarget().getEventConfigurations() != null
				&& !destination.getDestinationTarget().getEventConfigurations().isEmpty();
	}

	boolean isSupportedEventExisting(final ConsumedDestinationModel destination)
	{
		return destination != null && destination.getDestinationTarget() != null
				&& destination.getDestinationTarget().getEventConfigurations() != null
				&& destination.getDestinationTarget()
				              .getEventConfigurations()
				              .stream()
				              .anyMatch(config -> SUPPORTED_EVENT.equals(config.getEventClass()));
	}

	private void publishEvent(final ConsumedDestinationModel destinationTarget)
	{
		final DefaultCockpitEvent cockpitEvent = new DefaultCockpitEvent(OBJECTS_UPDATED_EVENT, destinationTarget, null);
		cockpitEvent.getContext().put(OBJECTS_UPDATED_EVENT_TYPE, true);
		cockpitEventQueue.publishEvent(cockpitEvent);
	}

}
