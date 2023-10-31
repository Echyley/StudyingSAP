/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_LANG_ISO_CODE;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeMatchingService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Strings;

import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

/**
 * Default implementation of <code>ComponentTypeMatchingService</code>. This is a simple POJO implementation.
 */
public class DefaultComponentTypeMatchingService implements ComponentTypeMatchingService {

	private CommonI18NService commonI18NService;

	/**
	 * Constructor of default implementation of <code>ComponentTypeMatchingService</code>.
	 * @param commonI18NService
	 * 			The Service for commonI18N to handle I18N.
	 */
	public DefaultComponentTypeMatchingService (CommonI18NService commonI18NService) {
		super();
		this.commonI18NService = commonI18NService;
	}

	@Override
	public ComponentTypeData mapNameForComponentType(final ComponentTypeData componentTypeData, String langIsoCode) {

		if (langIsoCode == null  || langIsoCode.isEmpty()) {
			langIsoCode = DEFAULT_LANG_ISO_CODE;
		}

		final Map<String, String> nameList = componentTypeData.getNameWithLocale();
		final String currentName = nameList.get(langIsoCode);

		if (currentName == null || currentName.isEmpty()) {
			String nameWithFallBack = getComponentTypeDataWithFallbackName(langIsoCode, nameList);
			componentTypeData.setName(nameWithFallBack);
		} else {
			componentTypeData.setName(currentName);
		}
		return componentTypeData;
	}

	private String getComponentTypeDataWithFallbackName (String langIsoCode, Map<String, String> nameList) {
		String nameWithDefaultLangIsoCode = nameList.get(DEFAULT_LANG_ISO_CODE);
		try {
			final LanguageModel language = this.commonI18NService.getLanguage(langIsoCode);
			final List<LanguageModel> fallbackLanguages = language.getFallbackLanguages();

			String nameWithFallBack = getNameForFallbackLanguages(fallbackLanguages, nameList);
			return Strings.isNullOrEmpty(nameWithFallBack)? nameWithDefaultLangIsoCode : nameWithFallBack;
		} catch (Exception e) {
			return nameWithDefaultLangIsoCode;
		}
	}

	private String getNameForFallbackLanguages (final List<LanguageModel> fallbackLanguages, Map<String, String> nameList) {
		if (CollectionUtils.isNotEmpty(fallbackLanguages))
		{
			// As current langIsoCode can set many fallback languages.
			// When name with current langIsoCode is empty or null, find first current langIsoCode fallback language name.
			// If still can not find the fallback languages name, return directly.
			for (final LanguageModel fallbackLanguage : fallbackLanguages)
			{
				final String fallBackLangIsoCode = fallbackLanguage.getIsocode();
				if (fallBackLangIsoCode != null && !Strings.isNullOrEmpty(nameList.get(fallBackLangIsoCode)))
				{
					return nameList.get(fallBackLangIsoCode);
				}
			}
		}
		return null;
	}

	@Override
	public boolean isTypeMatchingCriteria(final ComponentTypeData componentTypeData, final Set<String> typeRestrictionsForPage, final String mask) {
		if (typeRestrictionsForPage.contains(componentTypeData.getCode()))
		{
			final String maskCompared = getValueOrDefault(mask).toLowerCase(Locale.getDefault());
			final String code = getValueOrDefault(componentTypeData.getCode()).toLowerCase();
			final String name = getValueOrDefault(componentTypeData.getName()).toLowerCase();

			return Strings.isNullOrEmpty(mask) || code.indexOf(maskCompared) > -1 || name.indexOf(maskCompared) > -1;
		}

		return false;
	}

	@Override
	public String getValueOrDefault(final String value)
	{
		return (Strings.isNullOrEmpty(value)) ? "" : value;
	}
}
