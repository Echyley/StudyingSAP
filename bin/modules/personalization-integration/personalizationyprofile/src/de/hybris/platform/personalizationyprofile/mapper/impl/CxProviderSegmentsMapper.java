/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationservices.data.BaseSegmentData;
import de.hybris.platform.personalizationyprofile.mapper.CxConsumptionLayerSegmentsMapper;
import de.hybris.platform.personalizationyprofile.yaas.Segment;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.springframework.beans.factory.annotation.Required;


public class CxProviderSegmentsMapper implements CxConsumptionLayerSegmentsMapper
{
	public static final String FIELD_CODE = "name";
	public static final String FIELD_DESCRITPION = "description";
	private static final String ENABLED_PROPERTY = "personalizationyprofile.mapper.providersegments.enabled";

	private ConfigurationService configurationService;

	@Override
	public void populate(final Segment source, final BaseSegmentData target)
	{
		if (source != null && target != null && isEnabled())
		{
			target.setCode(source.getName());
			target.setDescription(source.getDescription());
		}
	}

	protected boolean isEnabled()
	{
		return getConfigurationService().getConfiguration().getBoolean(ENABLED_PROPERTY, true);
	}


	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
