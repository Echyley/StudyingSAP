/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.modals.controllers;

import static de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants.NOTIFICATION_TYPE;

import de.hybris.platform.apiregistryservices.utils.EventExportUtils;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.collectionbrowser.CollectionBrowserController;

public class WebhookConfigCollectionBrowserController extends CollectionBrowserController
{
	@WireVariable
	private transient NotificationService notificationService;

	@SocketEvent(socketId = "eventExportFlagCheck")
	public void loadCreateWebhookConfigurationModal(final String message)
	{
		if (!EventExportUtils.isEventExportActive())
		{
			notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("webhookbackoffice.webhookConfigurationCollectionBrowser.eventExportNotEnabled"));
		}
	}
}
