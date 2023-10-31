/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.widgets.modals.builders;

import de.hybris.platform.integrationbackoffice.widgets.modals.builders.AbstractAuditReportBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modals.builders.ItemModelNotSelectedForReportException;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

import java.util.Map;

public class WebhookConfigurationAuditReportBuilder extends AbstractAuditReportBuilder
{
	private static final String FILENAME_TEMPLATE = "%s-%s-%s_webhook";

	@Override
	public void traversePayload(final Map map)
	{
		// map here is a payload. To increase readability of audit report, some operations could be done here, such as converting
		// list to map, renaming attributes, etc.
		// WebhookConfigurationAuditReportBuilder doesn't need to adjust payloads from AuditViewService.
	}

	@Override
	public String getDownloadFileName()
	{
		if(this.getSelectedModel() == null)
		{
			throw new ItemModelNotSelectedForReportException(WebhookConfigurationModel._TYPECODE);
		}

		final WebhookConfigurationModel webhookConfig = (WebhookConfigurationModel) this.getSelectedModel();
		final String integrationObjectCode = webhookConfig.getIntegrationObject().getCode();
		final String consumedDestinationCode = webhookConfig.getDestination().getId();
		String eventType = webhookConfig.getEventType();

		// For now there is only one eventType: de.hybris.platform.webhookservices.event.ItemSavedEvent
		// EventType is supposed to be full path.
		if(eventType.lastIndexOf('.') > -1)
		{
			eventType = eventType.substring(eventType.lastIndexOf('.') + 1);
		}

		return String.format(FILENAME_TEMPLATE, integrationObjectCode, consumedDestinationCode, eventType);
	}
}
