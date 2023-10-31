/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.editors;

import de.hybris.platform.apiregistryservices.dao.impl.DefaultDestinationTargetDao;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;

import javax.annotation.Resource;

import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.editor.defaultreferenceeditor.DefaultReferenceEditor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.util.notifications.NotificationService;

import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.WEBHOOKSERVICES_DESTINATIONTARGET_ID;


/**
 * Reference editor for {@link DestinationTargetModel} when creating webhooks.
 */
public class WebhookDestinationTargetEditor extends DefaultReferenceEditor<DestinationTargetModel>
{
	@Resource
	private NotificationService notificationService;
	@Resource
	private DefaultDestinationTargetDao destinationTargetDao;

	/**
	 * Extends the behavior of the {@link EditorListener} with {@link WebhookDestinationTargetEditor}.
	 *
	 * @param parent   parent component.
	 * @param context  the editor context.
	 * @param listener the editor listener.
	 */
	@Override
	public void render(final Component parent, final EditorContext<DestinationTargetModel> context,
			final EditorListener<DestinationTargetModel> listener)
	{
		super.render(parent, context, listener);

		final EditorListener<DestinationTargetModel> editorListener = this.getEditorListener();
		this.setEditorListener(new WebhookDestinationTargetEditorListener(editorListener, notificationService));
	}

	/**
	 * Sets the initial value of the editor to the "webhookServices" destination target.
	 *
	 * @param context the editor context.
	 */
	@Override
	protected void setInitialValue(final EditorContext<DestinationTargetModel> context)
	{
		super.setInitialValue(context);

		final DestinationTargetModel webhookServicesDestinationTarget = findWebhookServicesDestinationTarget();
		this.getEditorLayout().onAddSelectedObject(webhookServicesDestinationTarget, true);
		this.getEditorListener().onValueChanged(webhookServicesDestinationTarget);
	}

	private DestinationTargetModel findWebhookServicesDestinationTarget()
	{
		return destinationTargetDao.findDestinationTargetById(WEBHOOKSERVICES_DESTINATIONTARGET_ID);
	}
}
