/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.handlers;

import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.NOTIFICATION_TYPE_LINK;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.OBJECTS_UPDATED_EVENT;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.OBJECTS_UPDATED_EVENT_TYPE;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.enums.EventMappingType;
import de.hybris.platform.apiregistryservices.enums.EventPriority;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigInvalidChannelException;

import java.util.List;
import java.util.Map;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;

/**
 * A wizard handler for the dynamic creation of Destination Targets in the process of making Webhooks. Applies the specific
 * limitations of creating a Destination Target suitable for a Webhook.
 */
public class WebhookProcessDestinationTargetWizardHandler implements FlowActionHandler
{
	private final ModelService modelService;
	private final CockpitEventQueue cockpitEventQueue;
	private final NotificationService notificationService;

	public WebhookProcessDestinationTargetWizardHandler(final ModelService modelService,
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
		final WidgetInstanceManager widgetInstanceManager = flowActionHandlerAdapter.getWidgetInstanceManager();
		final DestinationTargetModel destinationTarget = widgetInstanceManager.getModel()
		                                                                      .getValue("newDestinationTarget",
				                                                                      DestinationTargetModel.class);

		if (isValidDestinationTarget(destinationTarget))
		{
			final EventConfigurationModel eventConfiguration = createEventConfigurationWithDestinationTarget(destinationTarget);
			destinationTarget.setEventConfigurations(List.of(eventConfiguration));
			modelService.save(destinationTarget);

			final DefaultCockpitEvent cockpitEvent = new DefaultCockpitEvent(OBJECTS_UPDATED_EVENT,
					List.of(eventConfiguration, destinationTarget), null);
			cockpitEvent.getContext().put(OBJECTS_UPDATED_EVENT_TYPE, true);
			cockpitEventQueue.publishEvent(cockpitEvent);

			notificationService.notifyUser(widgetInstanceManager, NOTIFICATION_TYPE_LINK, Level.SUCCESS, eventConfiguration);
			notificationService.notifyUser(widgetInstanceManager, NOTIFICATION_TYPE_LINK, Level.SUCCESS, destinationTarget);
			flowActionHandlerAdapter.done();
		}
		else
		{
			throw new IllegalArgumentException(new WebhookConfigInvalidChannelException(null));
		}

	}

	boolean isValidDestinationTarget(final DestinationTargetModel destinationTarget)
	{
		return destinationTarget != null && DestinationChannel.WEBHOOKSERVICES == destinationTarget.getDestinationChannel();
	}

	private EventConfigurationModel createEventConfigurationWithDestinationTarget(final DestinationTargetModel destinationTarget)
	{
		final EventConfigurationModel eventConfiguration = modelService.create(EventConfigurationModel.class);
		eventConfiguration.setEventClass("de.hybris.platform.webhookservices.event.ItemSavedEvent");
		eventConfiguration.setVersion(1);
		eventConfiguration.setExportFlag(true);
		eventConfiguration.setExportName("webhookservices.ItemSavedEvent");
		eventConfiguration.setExtensionName("webhookservices");
		eventConfiguration.setMappingType(EventMappingType.GENERIC);
		eventConfiguration.setPriority(EventPriority.CRITICAL);
		eventConfiguration.setDestinationTarget(destinationTarget);
		return eventConfiguration;
	}
}
