/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.event.listeners;

import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.EXPORTING_OVERRIDDEN_PROP;
import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.EXPORTING_PROP;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Event listener for the {@link AfterInitializationEndEvent} which enables Event Exporting if it was disabled during
 * initialization process
 */
public class ApiregistryAfterInitEndEventListener extends AbstractEventListener<AfterInitializationEndEvent>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiregistryAfterInitEndEventListener.class);

	private ConfigurationService configurationService;

	@Override
	protected void onEvent(final AfterInitializationEndEvent afterInitializationEndEvent)
	{
		if (getConfigurationService().getConfiguration().getBoolean(EXPORTING_OVERRIDDEN_PROP, false))
		{
			LOGGER.info("Returning apiregistryservices.events.exporting to true, it was overridden due to initialization process");
			getConfigurationService().getConfiguration().setProperty(EXPORTING_PROP, "true");
			getConfigurationService().getConfiguration().clearProperty(EXPORTING_OVERRIDDEN_PROP);
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
