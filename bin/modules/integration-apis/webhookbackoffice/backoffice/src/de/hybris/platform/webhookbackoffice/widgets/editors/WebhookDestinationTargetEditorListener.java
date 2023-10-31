/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.editors;

import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;

import org.zkoss.lang.Strings;
import org.zkoss.util.resource.Labels;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.util.notifications.NotificationService;

import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.NOTIFICATION_TYPE;
import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.WEBHOOKSERVICES_DESTINATIONTARGET_ID;


/**
 * Extended listener behavior for {@link WebhookDestinationTargetEditor}.
 */
public class WebhookDestinationTargetEditorListener implements EditorListener<DestinationTargetModel>
{
	private static final String LABEL_KEY = "webhookbackoffice.destinationTargetEditor.notWebhookServicesWarning";

	private final EditorListener<DestinationTargetModel> editorListener;
	private final NotificationService notificationService;

	/**
	 * Constructs {@link WebhookDestinationTargetEditorListener}.
	 *
	 * @param editorListener      the original listener from {@link WebhookDestinationTargetEditor}.
	 * @param notificationService the notification service.
	 */
	public WebhookDestinationTargetEditorListener(final EditorListener<DestinationTargetModel> editorListener,
			final NotificationService notificationService)
	{
		this.editorListener = editorListener;
		this.notificationService = notificationService;
	}

	/**
	 * Notifies user if destination target selected is not the default "webhookServices" destination target.
	 *
	 * @param destinationTargetModel the selected {@link DestinationTargetModel}.
	 */
	@Override
	public void onValueChanged(final DestinationTargetModel destinationTargetModel)
	{
		editorListener.onValueChanged(destinationTargetModel);

		if (destinationTargetModel != null && !WEBHOOKSERVICES_DESTINATIONTARGET_ID.equals(destinationTargetModel.getId()))
		{
			notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.WARNING, Labels.getLabel(LABEL_KEY));
		}
	}

	@Override
	public void onEditorEvent(final String s)
	{
		editorListener.onEditorEvent(s);
	}

	@Override
	public void sendSocketOutput(final String s, final Object o)
	{
		editorListener.sendSocketOutput(s, o);
	}
}
