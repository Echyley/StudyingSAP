/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.search.utils.CategoryCatalogVersionMapper;
import com.hybris.backoffice.searchservices.providers.impl.ProductAttributeSnIndexerValueProvider.ProductData;


@RunWith(MockitoJUnitRunner.class)
public class CategoryCodeWithCatalogVersionMappingSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ProductModel source;

	@Mock
	private ProductData data;

	@InjectMocks
	private CategoryCodeWithCatalogVersionMappingSnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	@Mock
	private CategoryModel item;

	@Mock
	private Map<String, Set<ProductModel>> productsMap;

	@Mock
	private CategoryCatalogVersionMapper categoryCatalogVersionMapper;

	private static final String FIELD_1_ID = "field1";


	@Test
	public void shouldGetFieldValue() throws SnIndexerException, SnException
	{
		//give
		List<CategoryModel> categorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		categorys.add(category);
		Set<ProductModel> products = new HashSet<>();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		provider.setCategoryCatalogVersionMapper(categoryCatalogVersionMapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		String categoryCodeFieldValue = "categoryCodeFieldValue";
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(product.getSupercategories()).thenReturn(categorys);
		when(categoryCatalogVersionMapper.encode(category)).thenReturn(categoryCodeFieldValue);

		//then
		final Object categoryCodeFieldValueCollection = provider.getFieldValue(indexerContext, fieldWrapper, source, data);
		assertThat(categoryCodeFieldValueCollection instanceof List).isEqualTo(true);
		final List categoryCodeFieldValueList = (List) categoryCodeFieldValueCollection;
		assertThat(categoryCodeFieldValueList.size()).isEqualTo(1);
		assertThat(categoryCodeFieldValueList.get(0)).isEqualTo(categoryCodeFieldValue);
	}

}
