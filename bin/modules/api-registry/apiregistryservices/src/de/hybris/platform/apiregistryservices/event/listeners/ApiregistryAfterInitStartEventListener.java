/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.event.listeners;

import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.EXPORTING_OVERRIDDEN_PROP;
import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.EXPORTING_PROP;

import de.hybris.platform.apiregistryservices.utils.EventExportUtils;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Event listener for the {@link AfterInitializationStartEvent} which disables Event Exporting due to started
 * initialization process
 */
public class ApiregistryAfterInitStartEventListener extends AbstractEventListener<AfterInitializationStartEvent>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiregistryAfterInitStartEventListener.class);

	private ConfigurationService configurationService;

	@Override
	protected void onEvent(final AfterInitializationStartEvent afterInitializationStartEvent)
	{
		if (EventExportUtils.isEventExportActive())
		{
			LOGGER.info("Setting apiregistryservices.events.exporting to false due to initialization process");
			getConfigurationService().getConfiguration().setProperty(EXPORTING_PROP, "false");
			getConfigurationService().getConfiguration().addProperty(EXPORTING_OVERRIDDEN_PROP, "true");
		}
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
