/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationyprofile.mapper.CxConsumptionLayerProfileMapper;
import de.hybris.platform.personalizationyprofile.yaas.Profile;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;


public abstract class AbstractBaseMapper implements CxConsumptionLayerProfileMapper
{
	private String prefix;
	private String separator;
	private ConfigurationService configurationService;

	@Override
	public void populate(final Profile source, final MappingData target)
	{
		if (isSourceValid(source) && isTargetValid(target) && isEnabled())
		{
			populateAfterValidation(source, target);
		}
	}

	protected boolean isEnabled()
	{
		return getConfigurationService().getConfiguration().getBoolean(getEnabledProperty(), true);
	}

	protected boolean isSourceValid(final Profile source)
	{
		return source != null;
	}

	protected boolean isTargetValid(final MappingData target)
	{
		return target != null && target.getSegments() != null;
	}

	protected abstract void populateAfterValidation(final Profile source, final MappingData target);

	protected String getSegmentCode(final String baseName)
	{
		if (StringUtils.isBlank(getPrefix()))
		{
			return baseName;
		}
		else
		{
			return StringUtils.join(getPrefix(), StringUtils.defaultIfEmpty(separator, StringUtils.SPACE), baseName);

		}
	}

	protected abstract Set<String> getDefaultRequiredFields();

	protected abstract String getEnabledProperty();

	protected String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

	@Override
	public Set<String> getRequiredFields()
	{
		return isEnabled() ? getDefaultRequiredFields() : Collections.emptySet();
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected String getSeparator()
	{
		return separator;
	}

	public void setSeparator(final String separator)
	{
		this.separator = separator;
	}
}
