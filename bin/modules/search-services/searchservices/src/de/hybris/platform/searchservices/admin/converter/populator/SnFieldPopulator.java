/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.admin.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.model.SnFieldModel;
import de.hybris.platform.searchservices.util.ModelUtils;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link SnField} from {@link SnFieldModel}.
 */
public class SnFieldPopulator implements Populator<SnFieldModel, SnField>
{
	private I18NService i18NService;

	@Override
	public void populate(final SnFieldModel source, final SnField target)
	{
		final Set<Locale> supportedLocales = i18NService.getSupportedLocales();

		target.setId(source.getId());
		target.setName(ModelUtils.extractLocalizedValue(source, SnFieldModel.NAME, supportedLocales));
		target.setFieldType(source.getFieldType());
		target.setValueProvider(source.getValueProvider());
		target.setValueProviderParameters(source.getValueProviderParameters());
		target.setRetrievable(source.getRetrievable());
		target.setSearchable(source.getSearchable());
		target.setSearchTolerance(source.getSearchTolerance());
		target.setLocalized(source.getLocalized());
		target.setQualifierTypeId(source.getQualifierTypeId());
		target.setMultiValued(source.getMultiValued());
		target.setUseForSuggesting(source.getUseForSuggesting());
		target.setUseForSpellchecking(source.getUseForSpellchecking());
		target.setWeight(source.getWeight());
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
