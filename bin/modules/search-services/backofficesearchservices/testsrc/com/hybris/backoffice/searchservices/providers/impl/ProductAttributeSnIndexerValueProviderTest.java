/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

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

import com.hybris.backoffice.searchservices.providers.impl.ProductAttributeSnIndexerValueProvider.ProductData;


@RunWith(MockitoJUnitRunner.class)
public class ProductAttributeSnIndexerValueProviderTest
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
	private ProductAttributeSnIndexerValueProvider provider;

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
	private static final String EXPRESSION = "expression";


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
		valueProviderParameters.put(ProductAttributeSnIndexerValueProvider.EXPRESSION_PARAM, EXPRESSION);
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
		Set<ProductModel> products = new HashSet<>();
		products.add(item);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductAttributeSnIndexerValueProvider.EXPRESSION_PARAM, EXPRESSION);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_1_ID);
		when(data.getProducts()).thenReturn(productsMap);
		when(productsMap.get(any())).thenReturn(products);
		when(snExpressionEvaluator.evaluate(products, EXPRESSION)).thenReturn(value);

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, data);
		assertThat(fieldValue).isEqualTo(value);
	}

	@Test
	public void shouldGetLocalizedFieldValue() throws SnIndexerException, SnException
	{
		//give
		Set<ProductModel> products = new HashSet<>();
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductAttributeSnIndexerValueProvider.EXPRESSION_PARAM, EXPRESSION);
		SnQualifier qualifier1 = mock(SnQualifier.class);
		SnQualifier qualifier2 = mock(SnQualifier.class);
		List<SnQualifier> qualifiers = List.of(qualifier1, qualifier2);
		products.add(item);
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
		when(snExpressionEvaluator.evaluate(products, EXPRESSION, List.of(Locale.ENGLISH, Locale.GERMAN))).thenReturn(value);

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, data);
		assertThat(fieldValue).isEqualTo(value);
	}


	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenGetFieldValue() throws SnIndexerException, SnException
	{
		//give
		Set<ProductModel> products = new HashSet<>();
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductAttributeSnIndexerValueProvider.EXPRESSION_PARAM, EXPRESSION);
		SnQualifier qualifier1 = mock(SnQualifier.class);
		SnQualifier qualifier2 = mock(SnQualifier.class);
		List<SnQualifier> qualifiers = List.of(qualifier1, qualifier2);
		products.add(item);
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
		given(snExpressionEvaluator.evaluate(products, EXPRESSION, List.of(Locale.ENGLISH, Locale.GERMAN)))
				.willThrow(new SnException());

		//then
		provider.getFieldValue(indexerContext, fieldWrapper, source, data);
	}

	@Test
	public void shouldLoadData() throws SnIndexerException
	{
		//give
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT);
		List<SnIndexerFieldWrapper> fieldWrappers = List.of(fieldWrapper);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.loadData(indexerContext, fieldWrappers, source).getProducts()
				.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT).contains(source)).isEqualTo(true);
	}


	@Test
	public void shouldResolveExpression()
	{
		//give
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductAttributeSnIndexerValueProvider.EXPRESSION_PARAM, EXPRESSION);
		provider.setSnExpressionEvaluator(snExpressionEvaluator);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		when(fieldWrapper.getField()).thenReturn(snField);
		when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.resolveExpression(fieldWrapper)).isEqualTo(EXPRESSION);
	}
}
