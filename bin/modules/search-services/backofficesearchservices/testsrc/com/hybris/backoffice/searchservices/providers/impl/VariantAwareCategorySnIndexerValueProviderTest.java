/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito;

import com.hybris.backoffice.searchservices.providers.impl.ProductAttributeSnIndexerValueProvider.ProductData;


@RunWith(MockitoJUnitRunner.class)
public class VariantAwareCategorySnIndexerValueProviderTest
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
	private VariantAwareCategorySnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	@Mock
	private ProductModel item;

	@Mock
	private Object value;

	@Mock
	private Map<String, Set<ProductModel>> productsMap;

	private static final String FIELD_1_ID = "field1";


	@Test
	public void shouldGetSupportedQualifierClasses() throws SnIndexerException
	{
		assertThat(provider.getSupportedQualifierClasses()).containsExactly(Locale.class);
	}

	@Test
	public void shouldGetNullFieldValue() throws SnIndexerException
	{
		//give
		Set<ProductModel> products = new HashSet<>();
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, data);
		assertThat(fieldValue).isEqualTo(null);
	}

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
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(product.getSupercategories()).thenReturn(categorys);
		when(snExpressionEvaluator.evaluate(any(), any())).thenReturn(value);

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, data);
		assertThat(fieldValue).isEqualTo(value);
	}

	@Test
	public void shouldGetLocalizedFieldValue() throws SnIndexerException, SnException
	{
		//give
		List<CategoryModel> categorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		categorys.add(category);
		Set<ProductModel> products = new HashSet<>();
		ProductModel product = mock(ProductModel.class);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		SnQualifier qualifier1 = mock(SnQualifier.class);
		SnQualifier qualifier2 = mock(SnQualifier.class);
		List<SnQualifier> qualifiers = List.of(qualifier1, qualifier2);
		products.add(product);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isLocalized()).thenReturn(true);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		when(qualifier1.getAs(any())).thenReturn(Locale.ENGLISH);
		when(qualifier2.getAs(any())).thenReturn(Locale.GERMAN);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(product.getSupercategories()).thenReturn(categorys);
		when(snExpressionEvaluator.evaluate(any(), any(), eq(List.of(Locale.ENGLISH, Locale.GERMAN)))).thenReturn(value);

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, data);
		assertThat(fieldValue).isEqualTo(value);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenGetFieldValue() throws SnException
	{
		//give
		List<CategoryModel> categorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		categorys.add(category);
		Set<ProductModel> products = new HashSet<>();
		ProductModel product = mock(ProductModel.class);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		SnQualifier qualifier1 = mock(SnQualifier.class);
		SnQualifier qualifier2 = mock(SnQualifier.class);
		List<SnQualifier> qualifiers = List.of(qualifier1, qualifier2);
		products.add(product);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isLocalized()).thenReturn(true);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		when(qualifier1.getAs(any())).thenReturn(Locale.ENGLISH);
		when(qualifier2.getAs(any())).thenReturn(Locale.GERMAN);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(product.getSupercategories()).thenReturn(categorys);
		given(snExpressionEvaluator.evaluate(any(), any(), eq(List.of(Locale.ENGLISH, Locale.GERMAN))))
				.willThrow(new SnException());

		//then
		provider.getFieldValue(indexerContext, fieldWrapper, source, data);
	}


	@Test
	public void shouldCollectDirectCategories()
	{
		//give
		List<CategoryModel> categorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		categorys.add(category);
		Set<ProductModel> products = new HashSet();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(product.getSupercategories()).thenReturn(categorys);

		//then
		assertThat(provider.collectDirectCategories(products).contains(category)).isEqualTo(true);
	}

	@Test
	public void shouldCollectAllCategories()
	{
		//give
		List<CategoryModel> categorys = new ArrayList<>();
		List<CategoryModel> superCategorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		CategoryModel superCategory = mock(CategoryModel.class);
		categorys.add(category);
		superCategorys.add(superCategory);
		Set<ProductModel> products = new HashSet();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(product.getSupercategories()).thenReturn(categorys);
		when(category.getAllSupercategories()).thenReturn(superCategorys);

		//then
		assertThat(provider.collectAllCategories(products).contains(category)).isEqualTo(true);
		assertThat(provider.collectAllCategories(products).contains(superCategory)).isEqualTo(true);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenCollectAllCategories() throws SnIndexerException
	{
		//give
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM, "exceptionParameters");
		List<CategoryModel> categorys = new ArrayList<>();
		List<CategoryModel> superCategorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		CategoryModel superCategory = mock(CategoryModel.class);
		categorys.add(category);
		superCategorys.add(superCategory);
		Set<ProductModel> products = new HashSet();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		Mockito.lenient().when(product.getSupercategories()).thenReturn(categorys);
		Mockito.lenient().when(category.getAllSupercategories()).thenReturn(superCategorys);

		//then
		provider.collectCategories(fieldWrapper, data);
	}

	@Test
	public void shouldCollectCategoriesWithDirectCategories() throws SnIndexerException
	{
		//give
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		List<CategoryModel> categorys = new ArrayList<>();
		List<CategoryModel> superCategorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		CategoryModel superCategory = mock(CategoryModel.class);
		categorys.add(category);
		superCategorys.add(superCategory);
		Set<ProductModel> products = new HashSet();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(product.getSupercategories()).thenReturn(categorys);
		Mockito.lenient().when(category.getAllSupercategories()).thenReturn(superCategorys);

		//then
		assertThat(provider.collectCategories(fieldWrapper, data).size()).isEqualTo(1);
		assertThat(provider.collectCategories(fieldWrapper, data).contains(category)).isEqualTo(true);
	}

	@Test
	public void shouldCollectCategoriesWithAllCategories() throws SnIndexerException
	{
		//give
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_ALLCATEGORIES);
		List<CategoryModel> categorys = new ArrayList<>();
		List<CategoryModel> superCategorys = new ArrayList<>();
		CategoryModel category = mock(CategoryModel.class);
		CategoryModel superCategory = mock(CategoryModel.class);
		categorys.add(category);
		superCategorys.add(superCategory);
		Set<ProductModel> products = new HashSet();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(product.getSupercategories()).thenReturn(categorys);
		when(category.getAllSupercategories()).thenReturn(superCategorys);

		//then
		assertThat(provider.collectCategories(fieldWrapper, data).size()).isEqualTo(2);
		assertThat(provider.collectCategories(fieldWrapper, data).contains(category)).isEqualTo(true);
		assertThat(provider.collectAllCategories(products).contains(superCategory)).isEqualTo(true);
	}


	@Test
	public void shouldResolveCategorySelector()
	{
		//give
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_PARAM,
				VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.resolveCategorySelector(fieldWrapper))
				.isEqualTo(VariantAwareCategorySnIndexerValueProvider.CATEGORY_SELECTOR_VALUE_CATEGORIES);
	}
}
