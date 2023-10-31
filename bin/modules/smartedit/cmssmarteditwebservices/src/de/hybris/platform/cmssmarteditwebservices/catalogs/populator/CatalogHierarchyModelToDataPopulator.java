/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CatalogHierarchyData;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmssmarteditwebservices.catalogs.ContentCatalogService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

public class CatalogHierarchyModelToDataPopulator implements Populator<CatalogModel, CatalogHierarchyData>
{
    private LocalizedPopulator localizedPopulator;
    private ContentCatalogService contentCatalogService;

    @Override
    public void populate(final CatalogModel source, final CatalogHierarchyData target) throws ConversionException
    {

        final Map<String, String> nameMap = Optional.ofNullable(target.getCatalogName()).orElseGet(() -> getNewNameMap(target));
        getLocalizedPopulator().populate( //
                (locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
                (locale) -> source.getName(locale));

        target.setCatalogId(source.getId());
        target.setSites(getContentCatalogService().getSites((ContentCatalogModel) source));
    }

    protected Map<String, String> getNewNameMap(final CatalogHierarchyData target)
    {
        target.setCatalogName(new LinkedHashMap<>());
        return target.getCatalogName();
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

   public ContentCatalogService getContentCatalogService()
   {
      return contentCatalogService;
   }

   public void setContentCatalogService(ContentCatalogService contentCatalogService)
   {
      this.contentCatalogService = contentCatalogService;
   }
}
