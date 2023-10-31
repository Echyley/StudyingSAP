/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.admin.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.admin.data.SnSynonymDictionary;
import de.hybris.platform.searchservices.admin.data.SnSynonymEntry;
import de.hybris.platform.searchservices.model.SnSynonymDictionaryModel;
import de.hybris.platform.searchservices.model.SnSynonymEntryModel;
import de.hybris.platform.searchservices.util.ModelUtils;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link SnSynonymDictionary} from {@link SnSynonymDictionaryModel}.
 */
public class SnSynonymDictionaryPopulator implements Populator<SnSynonymDictionaryModel, SnSynonymDictionary>
{
	private I18NService i18NService;
	private Converter<SnSynonymEntryModel, SnSynonymEntry> snSynonymEntryConverter;

	@Override
	public void populate(final SnSynonymDictionaryModel source, final SnSynonymDictionary target)
	{
		final Set<Locale> supportedLocales = i18NService.getSupportedLocales();

		target.setId(source.getId());
		target.setName(ModelUtils.extractLocalizedValue(source, SnSynonymDictionaryModel.NAME, supportedLocales));

		final List<LanguageModel> languages = source.getLanguages();
		if (CollectionUtils.isNotEmpty(languages))
		{
			target.setLanguageIds(languages.stream().map(LanguageModel::getIsocode).collect(Collectors.toList()));
		}

		final List<SnSynonymEntryModel> entries = source.getEntries();
		if (CollectionUtils.isNotEmpty(entries))
		{
			target.setEntries(getSnSynonymEntryConverter().convertAll(entries));
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

	public Converter<SnSynonymEntryModel, SnSynonymEntry> getSnSynonymEntryConverter()
	{
		return snSynonymEntryConverter;
	}

	@Required
	public void setSnSynonymEntryConverter(final Converter<SnSynonymEntryModel, SnSynonymEntry> snSynonymEntryConverter)
	{
		this.snSynonymEntryConverter = snSynonymEntryConverter;
	}
}
