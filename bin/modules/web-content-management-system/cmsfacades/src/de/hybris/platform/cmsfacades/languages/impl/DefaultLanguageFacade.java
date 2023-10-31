/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.languages.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link LanguageFacade}.
 */
public class DefaultLanguageFacade implements LanguageFacade
{
	private StoreSessionFacade storeSessionFacade;
	private BaseSiteService baseSiteService;

	@Override
	public List<LanguageData> getLanguages()
	{
		final Collection<LanguageData> allLanguages = getStoreSessionFacade().getAllLanguages();
		final String defaultIsocode = getDefaultLanguageIsocode();

		if (Objects.isNull(defaultIsocode))
		{
			return new ArrayList<>(allLanguages);
		}
		else
		{
			final Predicate<LanguageData> isDefaultLanguage = lang -> defaultIsocode.equalsIgnoreCase(lang.getIsocode());

			final List<LanguageData> languageList = allLanguages.stream() //
					.filter(isDefaultLanguage.negate()) //
					.collect(Collectors.toList());

			allLanguages.stream() //
					.filter(isDefaultLanguage).findFirst() //
					.ifPresent((final LanguageData languageData) -> {
						languageData.setRequired(true);
						languageList.add(0, languageData);
					});

			return languageList;
		}
	}

	/**
	 * Find the default language for the base site in the active session. If none was defined on the site level, this
	 * will fallback to the default language defined on the base store level.
	 *
	 * @return the isocode of the default language defined for a given site; can be {@code NULL} when no default language
	 *         is defined on both the base site and the base store level.
	 */
	protected String getDefaultLanguageIsocode()
	{
		final BaseSiteModel site = getBaseSiteService().getCurrentBaseSite();
		String defaultIsocode = null;

		if (Objects.nonNull(site) && Objects.nonNull(site.getDefaultLanguage()))
		{
			defaultIsocode = site.getDefaultLanguage().getIsocode();
		}

		if (Objects.isNull(defaultIsocode) && Objects.nonNull(getStoreSessionFacade().getDefaultLanguage()))
		{
			defaultIsocode = getStoreSessionFacade().getDefaultLanguage().getIsocode();
		}

		return defaultIsocode;
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}
