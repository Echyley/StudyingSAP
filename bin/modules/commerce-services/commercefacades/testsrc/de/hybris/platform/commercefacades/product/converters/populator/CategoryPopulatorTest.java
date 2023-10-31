/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryPopulatorTest
{
	private static final String CATALOG_ID = "catId";
	private static final String CATEGORY_CODE = "catCode";
	private static final String CATEGORY_NAME = "catName";
	private static final String CATEGORY_URL = "catURL";
	//	private static final String SUPERCATEGORY_CODE = "supcatCode";

	private final CategoryPopulator categoryPopulator = new CategoryPopulator();
	private AbstractPopulatingConverter<CategoryModel, CategoryData> categoryConverter;

	@Mock
	private UrlResolver<CategoryModel> categoryModelUrlResolver;

	@Before
	public void setUp()
	{
		categoryPopulator.setCategoryModelUrlResolver(categoryModelUrlResolver);
		//		categoryPopulator.setSuperCategoriesLevel(1);

		categoryConverter = new ConverterFactory<CategoryModel, CategoryData, CategoryPopulator>().create(CategoryData.class,
				categoryPopulator);
	}

	@Test
	public void testConvert()
	{
		final CategoryModel source = mock(CategoryModel.class, withSettings().lenient());
		final CategoryModel supercategory = mock(CategoryModel.class, withSettings().lenient());
		final CategoryModel superClassificationClass = mock(ClassificationClassModel.class, withSettings().lenient());
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class, withSettings().lenient());
		final CatalogModel catalogModel = mock(CatalogModel.class, withSettings().lenient());
		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class, withSettings().lenient());
		final ClassificationSystemModel systemModel = mock(ClassificationSystemModel.class, withSettings().lenient());

		//		final List<CategoryModel> supercategories = new ArrayList<CategoryModel>();
		//		supercategories.add(superClassificationClass);
		//		supercategories.add(supercategory);

		given(systemModel.getId()).willReturn(CATALOG_ID);
		given(systemVersionModel.getCatalog()).willReturn(systemModel);
		given(catalogModel.getId()).willReturn(CATALOG_ID);
		given(catalogVersionModel.getCatalog()).willReturn(catalogModel);
		given(source.getCatalogVersion()).willReturn(catalogVersionModel);
		given(supercategory.getCatalogVersion()).willReturn(catalogVersionModel);
		given(superClassificationClass.getCatalogVersion()).willReturn(systemVersionModel);
		//		given(source.getSupercategories()).willReturn(supercategories);
		given(categoryModelUrlResolver.resolve(any(CategoryModel.class))).willReturn(CATEGORY_URL);
		given(source.getCode()).willReturn(CATEGORY_CODE);
		given(source.getName()).willReturn(CATEGORY_NAME);
		//		given(supercategory.getCode()).willReturn(SUPERCATEGORY_CODE);

		final CategoryData result = categoryConverter.convert(source);

		Assert.assertEquals(CATEGORY_CODE, result.getCode());
		Assert.assertEquals(CATEGORY_NAME, result.getName());
		//		Assert.assertEquals(1, result.getSupercategories().size());
		//		Assert.assertEquals(SUPERCATEGORY_CODE, result.getSupercategories().iterator().next().getCode());
	}
}
