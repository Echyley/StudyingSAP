/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.catalog.impl;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MarketplaceCategorySourceTest
{
	private static final String TEST_ROOT_CAT_CODE = "TestRootCat";
	private static final String TEST_CATS_QUALIFIER = "supercategories";


	private MarketplaceCategorySource marketplaceCategorySource;
	@Mock
	private ModelService modelService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;

	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private CatalogVersionModel catalogVersion;
	// |-rootCategory (Brands)
	// |----classificationClass
	// |----category1
	// |-------category2
	// |----category3
	@Mock
	private CategoryModel category1;
	@Mock
	private CategoryModel category2;
	@Mock
	private CategoryModel category3;
	@Mock
	private CategoryModel rootCategory;
	@Mock
	private ClassificationClassModel classificationClass;

	private boolean includeClassificationClasses;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	protected void configure()
	{
		marketplaceCategorySource = new MarketplaceCategorySource();

		marketplaceCategorySource.setModelService(modelService);
		marketplaceCategorySource.setCategoriesQualifier(TEST_CATS_QUALIFIER);
		marketplaceCategorySource.setIncludeClassificationClasses(includeClassificationClasses);
		marketplaceCategorySource.setRootCategory(TEST_ROOT_CAT_CODE);
		marketplaceCategorySource.setCategoryService(categoryService);

		Mockito.lenient().when(rootCategory.getSupercategories()).thenReturn(null);
		Mockito.lenient().when(rootCategory.getAllSupercategories()).thenReturn(null);
		final List<CategoryModel> superCats = new ArrayList<CategoryModel>();
		superCats.add(rootCategory);
		superCats.add(classificationClass);
		given(category1.getSupercategories()).willReturn(superCats);
		Mockito.lenient().when(category1.getAllSupercategories()).thenReturn(superCats);
		given(category2.getSupercategories()).willReturn(singletonList(category1));
		Mockito.lenient().when(category2.getAllSupercategories()).thenReturn(singletonList(category1));
		Mockito.lenient().when(category3.getSupercategories()).thenReturn(singletonList(rootCategory));
		Mockito.lenient().when(category3.getAllSupercategories()).thenReturn(singletonList(rootCategory));

		given(model.getBaseProduct()).willReturn(baseProduct);
		given(modelService.getAttributeValue(any(), eq(TEST_CATS_QUALIFIER)))
				.willReturn(singletonList(category2));

		given(category2.getCatalogVersion()).willReturn(catalogVersion);
		given(categoryService.getCategoryForCode(catalogVersion, TEST_ROOT_CAT_CODE)).willReturn(rootCategory);

	}


	@Test
	public void testGetCategories()
	{
		//Test ProductModel
		Set<CategoryModel> superCategories = new HashSet<>();
		superCategories.add(category2);
		given(model.getSupercategories()).willReturn(superCategories);

		Collection<CategoryModel> result = marketplaceCategorySource.getCategoriesForConfigAndProperty(indexConfig,
				indexedProperty, model);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(category1));
		Assert.assertTrue(result.contains(category2));

		//Test VariantProductModel
		final ProductModel model2 = Mockito.spy(new ProductModel());
		given(model2.getSupercategories()).willReturn(superCategories);

		result = marketplaceCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model2);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(category1));
		Assert.assertTrue(result.contains(category2));
	}

}
