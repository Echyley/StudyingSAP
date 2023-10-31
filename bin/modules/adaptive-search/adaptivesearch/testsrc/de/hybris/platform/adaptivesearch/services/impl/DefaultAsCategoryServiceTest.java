/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.daos.AsCategoryDao;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsCategoryServiceTest
{
	private static final String CATEGORY_1_CODE = "cat1";
	private static final String CATEGORY_2_CODE = "cat2";
	private static final String CATEGORY_3_CODE = "cat3";
	private static final String CATEGORY_4_CODE = "cat4";

	@Mock
	private ModelService modelService;

	@Mock
	private SessionService sessionService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private AsCategoryDao asCategoryDao;

	private DefaultAsCategoryService asCategoryService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		asCategoryService = new DefaultAsCategoryService();
		asCategoryService.setModelService(modelService);
		asCategoryService.setSessionService(sessionService);
		asCategoryService.setCategoryService(categoryService);
		asCategoryService.setAsCategoryDao(asCategoryDao);
	}

	@Test
	public void setCurrentCategoryPath()
	{
		// given
		final PK pk1 = PK.fromLong(1);
		final CategoryModel category1 = mock(CategoryModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(category1.getPk()).thenReturn(pk1);
		when(modelService.get(pk1)).thenReturn(category1);

		when(category2.getPk()).thenReturn(pk2);
		when(modelService.get(pk2)).thenReturn(category2);

		// when
		asCategoryService.setCurrentCategoryPath(Arrays.asList(category1, category2));

		// then
		verify(sessionService).setAttribute(DefaultAsCategoryService.CURRENT_CATEGORY_PATH, Arrays.asList(pk1, pk2));
	}

	@Test
	public void getEmptyCurrentCategoryPath()
	{
		// when
		final Optional<List<CategoryModel>> categoryPathResult = asCategoryService.getCurrentCategoryPath();

		// then
		assertFalse(categoryPathResult.isPresent());
	}

	@Test
	public void getCurrentCategoryPath()
	{
		// given
		final PK pk1 = PK.fromLong(1);
		final CategoryModel category1 = mock(CategoryModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(category1.getPk()).thenReturn(pk1);
		when(modelService.get(pk1)).thenReturn(category1);

		when(category2.getPk()).thenReturn(pk2);
		when(modelService.get(pk2)).thenReturn(category2);

		when(sessionService.getAttribute(DefaultAsCategoryService.CURRENT_CATEGORY_PATH)).thenReturn(Arrays.asList(pk1, pk2));

		//when
		final Optional<List<CategoryModel>> categoryPathResult = asCategoryService.getCurrentCategoryPath();

		// then
		assertTrue(categoryPathResult.isPresent());

		final List<CategoryModel> categoryPath = categoryPathResult.get();
		assertEquals(2, categoryPath.size());
		assertSame(category1, categoryPath.get(0));
		assertSame(category2, categoryPath.get(1));
	}

	@Test
	public void clearCurrentCategoryPath()
	{
		// when
		asCategoryService.clearCurrentCategoryPath();

		// then
		verify(sessionService).removeAttribute(DefaultAsCategoryService.CURRENT_CATEGORY_PATH);
	}

	@Test
	public void buildEmptyCategoryPathEmptyCategoryCodes()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(), List.of(catalogVersion), false);

		// then
		assertThat(categoryPath).isNotNull().isEmpty();
	}

	@Test
	public void buildEmptyCategoryPathCategoryNotFoundForCategoryCode()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of());

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE),
				List.of(catalogVersion), false);

		// then
		assertThat(categoryPath).isNotNull().isEmpty();
	}

	@Test
	public void buildCategoryPathWithSingleCategory()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		final PK pk = PK.fromLong(1);
		final CategoryModel category = mock(CategoryModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of(category));
		when(category.getPk()).thenReturn(pk);
		when(category.getCatalogVersion()).thenReturn(catalogVersion);

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE),
				List.of(catalogVersion), false);

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category);
	}

	@Test
	public void buildCategoryPathWithMultipleCategories()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		final PK pk1 = PK.fromLong(1);
		final CategoryModel category1 = mock(CategoryModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of(category1));
		when(category1.getPk()).thenReturn(pk1);
		when(category1.getCatalogVersion()).thenReturn(catalogVersion);

		when(categoryService.getCategoriesForCode(CATEGORY_2_CODE)).thenReturn(List.of(category2));
		when(category2.getPk()).thenReturn(pk2);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_1_CODE, CATEGORY_2_CODE),
				List.of(catalogVersion), false);

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category1, category2);
	}

	@Test
	public void buildNonRecursiveCategoryPath()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		final PK pk1 = PK.fromLong(1);
		final CategoryModel category1 = mock(CategoryModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of(category1));
		when(category1.getPk()).thenReturn(pk1);
		when(category1.getCatalogVersion()).thenReturn(catalogVersion);

		when(categoryService.getCategoriesForCode(CATEGORY_2_CODE)).thenReturn(List.of(category2));
		when(category2.getPk()).thenReturn(pk2);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getSupercategories()).thenReturn(List.of(category1));

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_2_CODE),
				List.of(catalogVersion), false);

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category2);
	}

	@Test
	public void buildRecursiveCategoryPath()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		final PK pk1 = PK.fromLong(1);
		final CategoryModel category1 = mock(CategoryModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of(category1));
		when(category1.getPk()).thenReturn(pk1);
		when(category1.getCatalogVersion()).thenReturn(catalogVersion);

		when(categoryService.getCategoriesForCode(CATEGORY_2_CODE)).thenReturn(List.of(category2));
		when(category2.getPk()).thenReturn(pk2);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getSupercategories()).thenReturn(List.of(category1));

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_2_CODE),
				List.of(catalogVersion), true);

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category1, category2);
	}

	@Test
	public void buildRecursiveCategoryPathWithMultiplePaths()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		final PK pk1 = PK.fromLong(1);
		final CategoryModel category1 = mock(CategoryModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		final PK pk3 = PK.fromLong(3);
		final CategoryModel category3 = mock(CategoryModel.class);

		final PK pk4 = PK.fromLong(4);
		final CategoryModel category4 = mock(CategoryModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of(category1));
		when(category1.getPk()).thenReturn(pk1);
		when(category1.getCatalogVersion()).thenReturn(catalogVersion);

		when(categoryService.getCategoriesForCode(CATEGORY_2_CODE)).thenReturn(List.of(category2));
		when(category2.getPk()).thenReturn(pk2);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getSupercategories()).thenReturn(List.of(category1, category3, category4));

		when(categoryService.getCategoriesForCode(CATEGORY_3_CODE)).thenReturn(List.of(category3));
		when(category3.getPk()).thenReturn(pk3);
		when(category3.getCatalogVersion()).thenReturn(catalogVersion);
		when(category3.getSupercategories()).thenReturn(List.of(category1));

		when(categoryService.getCategoriesForCode(CATEGORY_4_CODE)).thenReturn(List.of(category4));
		when(category4.getPk()).thenReturn(pk4);
		when(category4.getCatalogVersion()).thenReturn(catalogVersion);

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_2_CODE),
				List.of(catalogVersion), true);

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category1, category3, category4, category2);
	}

	@Test
	public void buildRecursiveCategoryPathParentIsNotSupported()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		final PK pk1 = PK.fromLong(1);
		final ClassificationClassModel category1 = mock(ClassificationClassModel.class);

		final PK pk2 = PK.fromLong(2);
		final CategoryModel category2 = mock(CategoryModel.class);

		when(categoryService.getCategoriesForCode(CATEGORY_1_CODE)).thenReturn(List.of(category1));
		when(category1.getPk()).thenReturn(pk1);

		when(categoryService.getCategoriesForCode(CATEGORY_2_CODE)).thenReturn(List.of(category2));
		when(category2.getPk()).thenReturn(pk2);
		when(category2.getCatalogVersion()).thenReturn(catalogVersion);
		when(category2.getSupercategories()).thenReturn(List.of(category1));

		// when
		final List<CategoryModel> categoryPath = asCategoryService.buildCategoryPath(List.of(CATEGORY_2_CODE),
				List.of(catalogVersion), true);

		// then
		assertThat(categoryPath).isNotEmpty().containsExactly(category2);
	}

	@Test
	public void getAllCategoriesForCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		// when
		asCategoryService.getAllCategoriesForCatalogVersion(catalogVersion);

		// then
		verify(asCategoryDao).findCategoriesByCatalogVersion(catalogVersion);
	}

	@Test
	public void getAllCategoryRelationsForCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		// when
		asCategoryService.getAllCategoryRelationsForCatalogVersion(catalogVersion);

		// then
		verify(asCategoryDao).findCategoryRelationsByCatalogVersion(catalogVersion);
	}
}
