/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmsfacades.data.CatalogHierarchyData;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmssmarteditwebservices.catalogs.ContentCatalogService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.lang.reflect.Array;
import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EntryToCatalogDataPopulatorTest
{
	private static final String UUID = "uuid";

	@InjectMocks
	private EntryToCatalogDataPopulator populator;
	@Mock
	private Populator<CatalogModel, CatalogData> catalogModelToDataPopulator;
	@Mock
	private Converter<CatalogVersionModel, CatalogVersionData> catalogVersionModelToDataConverter;
	@Mock
	private Comparator<CatalogVersionData> catalogVersionDataComparator;
	@Mock
	private Populator<CatalogModel, CatalogHierarchyData> catalogModelToHierarchyDataPopulator;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Mock
	private CatalogModel catalogModel;
	@Mock
	private ContentCatalogModel contentCatalogModel;
	@Mock
	private ContentCatalogModel parentContentCatalogModel;
	@Mock
	private ContentCatalogModel superParentContentCatalogModel;
	@Mock
	private CatalogVersionModel catalogVersionStaged;
	@Mock
	private CatalogVersionModel catalogVersionOnline;
	@Mock
	private CatalogVersionModel parentCatalogVersionOnline;
	@Mock
	private CatalogVersionModel superParentCatalogVersionOnline;
	@Mock
	private ContentCatalogService contentCatalogService;

	private CatalogData catalogData;
	private Map.Entry<CatalogModel, Set<CatalogVersionModel>> entry;

	private ItemData itemData;
	private ItemData parentItemData;
	private ItemData superParentItemData;
	private CatalogVersionData catalogVersionData;

	private CatalogHierarchyData catalogHierarchyData;

	protected void setUpWithCatalogVersions(final CatalogModel catalogModel, final CatalogVersionModel... catalogVersionModels)
	{
		final Map<CatalogModel, Set<CatalogVersionModel>> catalogsAndVersions = new HashMap<>();
		catalogsAndVersions.put(catalogModel, Sets.newHashSet(catalogVersionModels));

		entry = catalogsAndVersions.entrySet().iterator().next();
		catalogData = new CatalogData();
		catalogVersionData = new CatalogVersionData();

		when(catalogVersionModelToDataConverter.convert(catalogVersionStaged)).thenReturn(catalogVersionData);
		when(catalogVersionModelToDataConverter.convert(catalogVersionOnline)).thenReturn(catalogVersionData);

		itemData = new ItemData();
		itemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(catalogVersionStaged)).thenReturn(Optional.of(itemData));
		when(uniqueItemIdentifierService.getItemData(catalogVersionOnline)).thenReturn(Optional.of(itemData));
	}

	protected void setUpParentCatalog()
	{
		when(contentCatalogModel.getSuperCatalog()).thenReturn(parentContentCatalogModel);
		when(parentContentCatalogModel.getCatalogVersions()).thenReturn(Sets.newHashSet(parentCatalogVersionOnline));
		when(catalogVersionModelToDataConverter.convert(parentCatalogVersionOnline)).thenReturn(catalogVersionData);

		parentItemData = new ItemData();
		parentItemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(parentCatalogVersionOnline)).thenReturn(Optional.of(parentItemData));

		when(parentContentCatalogModel.getSuperCatalog()).thenReturn(superParentContentCatalogModel);
		when(superParentContentCatalogModel.getCatalogVersions()).thenReturn(Sets.newHashSet(superParentCatalogVersionOnline));
		when(catalogVersionModelToDataConverter.convert(superParentCatalogVersionOnline)).thenReturn(catalogVersionData);

		superParentItemData = new ItemData();
		superParentItemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(superParentCatalogVersionOnline)).thenReturn(Optional.of(superParentItemData));
	}

	protected void setUpCycleParentCatalog()
	{
		when(contentCatalogModel.getSuperCatalog()).thenReturn(parentContentCatalogModel);
		when(parentContentCatalogModel.getCatalogVersions()).thenReturn(Sets.newHashSet(parentCatalogVersionOnline));
		when(catalogVersionModelToDataConverter.convert(parentCatalogVersionOnline)).thenReturn(catalogVersionData);

		parentItemData = new ItemData();
		parentItemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(parentCatalogVersionOnline)).thenReturn(Optional.of(parentItemData));

		when(parentContentCatalogModel.getSuperCatalog()).thenReturn(superParentContentCatalogModel);
		when(superParentContentCatalogModel.getCatalogVersions()).thenReturn(Sets.newHashSet(superParentCatalogVersionOnline));
		when(catalogVersionModelToDataConverter.convert(superParentCatalogVersionOnline)).thenReturn(catalogVersionData);

		when(superParentContentCatalogModel.getSuperCatalog()).thenReturn(parentContentCatalogModel);

		superParentItemData = new ItemData();
		superParentItemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(superParentCatalogVersionOnline)).thenReturn(Optional.of(superParentItemData));
	}

	@Test
	public void shouldPopulateCatalogDataAndCatalogVersions()
	{
		setUpWithCatalogVersions(catalogModel, catalogVersionStaged);

		populator.populate(entry, catalogData);

		verify(catalogModelToDataPopulator).populate(catalogModel, catalogData);
		verify(catalogVersionModelToDataConverter).convert(catalogVersionStaged);
	}

	@Test
	public void shouldPopulateCatalogDataAndSortCatalogVersions()
	{
		setUpWithCatalogVersions(catalogModel, catalogVersionStaged, catalogVersionOnline);

		populator.populate(entry, catalogData);

		verify(catalogModelToDataPopulator).populate(catalogModel, catalogData);
		verify(catalogVersionModelToDataConverter).convert(catalogVersionStaged);
		verify(catalogVersionModelToDataConverter).convert(catalogVersionOnline);
		verify(catalogVersionDataComparator).compare(any(), any());
	}

	@Test
	public void shouldReturnEmptyParentsForContentCatalogWithNoParent()
	{
		setUpWithCatalogVersions(catalogModel, catalogVersionStaged, catalogVersionOnline);

		populator.populate(entry, catalogData);

		verify(catalogModelToHierarchyDataPopulator, never()).populate(catalogModel, catalogHierarchyData);
	}

	@Test
	public void shouldReturnParentsContentCatalogWithParent()
	{
		setUpWithCatalogVersions(contentCatalogModel, catalogVersionStaged, catalogVersionOnline);
		setUpParentCatalog();
		when(contentCatalogService.getSites(contentCatalogModel)).thenReturn(Arrays.asList());

		populator.populate(entry, catalogData);

		verify(catalogModelToHierarchyDataPopulator, times(2)).populate(any(), any());
		verify(catalogVersionModelToDataConverter).convert(parentCatalogVersionOnline);
		verify(catalogVersionModelToDataConverter).convert(superParentCatalogVersionOnline);
		verify(catalogVersionDataComparator).compare(any(), any());
	}

	@Test
	public void shouldStopWhenParentsHasCycle() {
		setUpWithCatalogVersions(contentCatalogModel, catalogVersionStaged, catalogVersionOnline);
		setUpCycleParentCatalog();
		when(contentCatalogService.getSites(contentCatalogModel)).thenReturn(Arrays.asList());

		populator.populate(entry, catalogData);

		verify(catalogModelToHierarchyDataPopulator, times(2)).populate(any(), any());
		verify(catalogVersionModelToDataConverter).convert(parentCatalogVersionOnline);
		verify(catalogVersionModelToDataConverter).convert(superParentCatalogVersionOnline);
		verify(catalogVersionDataComparator).compare(any(), any());
	}

	@Test(expected = ConversionException.class)
	public void shouldFailWithConversionException()
	{
		setUpWithCatalogVersions(catalogModel, catalogVersionStaged);
		when(catalogVersionModelToDataConverter.convert(catalogVersionStaged))
				.thenThrow(new ConversionException("Error occured during conversion"));

		populator.populate(entry, catalogData);
	}

}
