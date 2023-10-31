/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.spi.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.searchservices.model.AbstractSnSearchProviderConfigurationModel;
import de.hybris.platform.searchservices.spi.data.AbstractSnSearchProviderConfiguration;
import de.hybris.platform.searchservices.util.ModelUtils;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link AbstractSnSearchProviderConfiguration} from {@link AbstractSnSearchProviderConfigurationModel}.
 */
public class SnSearchProviderConfigurationPopulator
		implements Populator<AbstractSnSearchProviderConfigurationModel, AbstractSnSearchProviderConfiguration>
{
	private I18NService i18NService;

	@Override
	public void populate(final AbstractSnSearchProviderConfigurationModel source,
			final AbstractSnSearchProviderConfiguration target)
	{
		final Set<Locale> supportedLocales = i18NService.getSupportedLocales();

		target.setId(source.getId());
		target.setName(ModelUtils.extractLocalizedValue(source, AbstractSnSearchProviderConfigurationModel.NAME, supportedLocales));

		final List<String> listeners = source.getListeners();
		if (CollectionUtils.isNotEmpty(listeners))
		{
			target.setListeners(new ArrayList<>(listeners));
		}
	}

	public I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}
}
