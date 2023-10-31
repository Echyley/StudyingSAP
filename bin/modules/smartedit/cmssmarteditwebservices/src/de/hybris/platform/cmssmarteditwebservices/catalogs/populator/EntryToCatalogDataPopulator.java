/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import com.google.common.collect.Lists;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmsfacades.data.CatalogHierarchyData;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmssmarteditwebservices.catalogs.ContentCatalogService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@code java.util.Map.Entry<CatalogModel, Set<CatalogVersionModel>} object to a {@link CatalogData} dto
 */
public class EntryToCatalogDataPopulator implements Populator<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData>
{
	private Populator<CatalogModel, CatalogData> catalogModelToDataPopulator;
	private Populator<CatalogModel, CatalogHierarchyData> catalogModelToHierarchyDataPopulator;
	private Converter<CatalogVersionModel, CatalogVersionData> catalogVersionDataConverter;
	private Comparator<CatalogVersionData> catalogVersionDataComparator; 
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private ContentCatalogService contentCatalogService;

	@Override
	public void populate(final Entry<CatalogModel, Set<CatalogVersionModel>> source, final CatalogData target)
			throws ConversionException
	{
		// populate the basic fields (id, name)
		getCatalogModelToDataPopulator().populate(source.getKey(), target);

		// populate the catalog versions
		if (!source.getValue().isEmpty())
		{
			final List<CatalogVersionData> versions = source.getValue().stream() //
					.map(this::convertCatalogVersionModelToData) //
					.sorted(getCatalogVersionDataComparator()) //
					.collect(Collectors.toList());
			target.setVersions(versions);
		}

		// populate parent hierarchy for content catalogs
		if(ContentCatalogModel.class.isInstance(source.getKey()))
		{
			target.setParents(Lists.reverse(getParentsForCatalog((ContentCatalogModel) source.getKey())));
		}

		//For content catalog, should populate it's site
		if(ContentCatalogModel.class.isInstance(source.getKey()))
		{
			target.setSites(getContentCatalogService().getSites((ContentCatalogModel) source.getKey()));
		}
	}

	/**
	 * Returns the catalog hierarchy data for the provided content catalog.
	 *
	 * @param contentCatalog
	 * 			the content catalog model for which the catalog hierarchy needs to be retrieved.
	 * @return the catalog hierarchy data for the provided content catalog.
	 */
	protected List<CatalogHierarchyData> getParentsForCatalog(ContentCatalogModel contentCatalog)
	{
		return getParentCatalogs(contentCatalog).entrySet().stream()
				.map(entry -> {
					CatalogHierarchyData catalog = new CatalogHierarchyData();
					getCatalogModelToHierarchyDataPopulator().populate(entry.getKey(), catalog);
					if (!entry.getValue().isEmpty())
					{
						final List<CatalogVersionData> versions = entry.getValue().stream() //
								.map(this::convertCatalogVersionModelToData) //
								.sorted(getCatalogVersionDataComparator()) //
								.collect(Collectors.toList());
						catalog.setVersions(versions);
					}
					return catalog;
				}).collect(Collectors.toList());
	}

	/**
	 * Returns all parent content catalogs and catalog versions for the provided catalog.
	 *
	 * @param contentCatalog
	 * 			the content catalog model for which the parents need to be retrieved.
	 * @return all parent content catalogs and catalog versions for the provided catalog.
	 */
	protected Map<CatalogModel, Set<CatalogVersionModel>> getParentCatalogs(ContentCatalogModel contentCatalog)
	{
		Map<CatalogModel, Set<CatalogVersionModel>> parents = new LinkedHashMap<>();

		ContentCatalogModel parent = contentCatalog.getSuperCatalog();
		while (Objects.nonNull(parent) && !parents.containsKey(parent))
		{
			Set<CatalogVersionModel> versions = parent.getCatalogVersions();
			parents.put(parent, versions);
			parent = parent.getSuperCatalog();
		}

		return parents;
	}

	/**
	 * Converts {@code CatalogVersionModel} into {@code CatalogVersionData} dto.
	 *
	 * @param catalogVersionModel
	 *           the catalog version to be converted
	 * @return a catalog version dto
	 */
	protected CatalogVersionData convertCatalogVersionModelToData(final CatalogVersionModel catalogVersionModel)
	{
		final CatalogVersionData catalogVersionData = getCatalogVersionDataConverter().convert(catalogVersionModel);
		if (null != catalogVersionData) {
			catalogVersionData.setUuid(getUniqueItemIdentifierService().getItemData(catalogVersionModel)
					.orElseThrow(
							() -> new UnknownIdentifierException("Cannot generate uuid for component in EntryToCatalogDataPopulator"))
					.getItemId());
		}
		return catalogVersionData;
	}

	protected Populator<CatalogModel, CatalogData> getCatalogModelToDataPopulator()
	{
		return catalogModelToDataPopulator;
	}

	@Required
	public void setCatalogModelToDataPopulator(final Populator<CatalogModel, CatalogData> catalogModelToDataPopulator)
	{
		this.catalogModelToDataPopulator = catalogModelToDataPopulator;
	}

	protected Converter<CatalogVersionModel, CatalogVersionData> getCatalogVersionDataConverter()
	{
		return catalogVersionDataConverter;
	}

	@Required
	public void setCatalogVersionDataConverter(
			final Converter<CatalogVersionModel, CatalogVersionData> catalogVersionDataConverter)
	{
		this.catalogVersionDataConverter = catalogVersionDataConverter;
	}

	protected Comparator<CatalogVersionData> getCatalogVersionDataComparator()
	{
		return catalogVersionDataComparator;
	}

	@Required
	public void setCatalogVersionDataComparator(final Comparator<CatalogVersionData> catalogVersionDataComparator)
	{
		this.catalogVersionDataComparator = catalogVersionDataComparator;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected Populator<CatalogModel, CatalogHierarchyData> getCatalogModelToHierarchyDataPopulator() {
		return catalogModelToHierarchyDataPopulator;
	}

	@Required
	public void setCatalogModelToHierarchyDataPopulator(Populator<CatalogModel, CatalogHierarchyData> catalogModelToHierarchyDataPopulator) {
		this.catalogModelToHierarchyDataPopulator = catalogModelToHierarchyDataPopulator;
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
