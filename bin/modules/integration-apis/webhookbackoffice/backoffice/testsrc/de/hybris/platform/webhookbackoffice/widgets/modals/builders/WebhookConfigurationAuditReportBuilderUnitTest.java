/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.widgets.modals.builders;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WebhookConfigurationAuditReportBuilderUnitTest
{
	@InjectMocks
	private WebhookConfigurationAuditReportBuilder auditReportBuilder = new WebhookConfigurationAuditReportBuilder();

	@Test
	public void testGetDownloadFileName()
	{
		final IntegrationObjectModel io = integrationObject("testIO");
		final ConsumedDestinationModel cd = consumedDestination("testConsumedDestination");
		final String eventType = "de.hybris.platform.webhookservices.event.ItemSavedEvent";
		final WebhookConfigurationModel itemModel = webhookConfiguration(io, cd, eventType);
		auditReportBuilder.setSelectedModel(itemModel);

		assertEquals("testIO-testConsumedDestination-ItemSavedEvent_webhook", auditReportBuilder.getDownloadFileName());
	}

	private static IntegrationObjectModel integrationObject(final String code)
	{
		final var model = mock(IntegrationObjectModel.class);
		lenient().when(model.getCode()).thenReturn(code);
		return model;
	}

	private static ConsumedDestinationModel consumedDestination(final String id)
	{
		final var model = mock(ConsumedDestinationModel.class);
		lenient().when(model.getId()).thenReturn(id);
		return model;
	}

	private static WebhookConfigurationModel webhookConfiguration(final IntegrationObjectModel io,
	                                                              final ConsumedDestinationModel cd, final String eventType)
	{
		final var model = mock(WebhookConfigurationModel.class);
		lenient().when(model.getIntegrationObject()).thenReturn(io);
		lenient().when(model.getDestination()).thenReturn(cd);
		lenient().when(model.getEventType()).thenReturn(eventType);
		return model;
	}
}
