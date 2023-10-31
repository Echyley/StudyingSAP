/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.impl;

import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmssmarteditwebservices.catalogs.ContentCatalogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


public class DefaultContentCatalogService implements ContentCatalogService
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public List<SiteData> getSites(final ContentCatalogModel contentCatalog)
	{
		return contentCatalog.getCmsSites().stream() //
				.map(cmsSite -> {
					SiteData site = new SiteData();
					site.setUid(cmsSite.getUid());

					final Map<String, String> nameMap = Optional.ofNullable(site.getName()).orElseGet(HashMap::new);
					getLocalizedPopulator().populate(
							((locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value)),
							(locale) -> cmsSite.getName(locale));
					site.setName(nameMap);

					return site;
				}).collect(Collectors.toList());
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}
}
