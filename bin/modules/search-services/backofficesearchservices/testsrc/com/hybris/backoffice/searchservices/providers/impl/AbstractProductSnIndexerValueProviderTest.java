/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
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
public class AbstractProductSnIndexerValueProviderTest
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
	private TestAbstractProductSnIndexerValueProvider provider;

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

	protected static final Set<Class<?>> SUPPORTED_QUALIFIER_CLASSES = Set.of(Locale.class);
	private static final String FIELD_1_ID = "field1";

	@Test
	public void shouldCollectProductsWithCurrentProduct() throws SnIndexerException
	{
		//give
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ProductModel product = mock(ProductModel.class);

		//then
		final Map<String, Set<ProductModel>> productsResult = provider.collectProducts(fieldWrappers, product);
		assertThat(productsResult.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT).size()).isEqualTo(1);
		assertThat(productsResult.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT).contains(product))
				.isEqualTo(true);
	}

	@Test
	public void shouldCollectProductsWithCurrentParentProduct() throws SnIndexerException
	{
		//give
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT_PARENT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ProductModel product = mock(ProductModel.class);

		//then
		final Map<String, Set<ProductModel>> productsResult = provider.collectProducts(fieldWrappers, product);
		assertThat(productsResult.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT_PARENT).size())
				.isEqualTo(1);
		assertThat(
				productsResult.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT_PARENT).contains(product))
						.isEqualTo(true);
	}

	@Test
	public void shouldCollectProductsWithBaseProduct() throws SnIndexerException
	{
		//give
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_BASE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ProductModel product = mock(ProductModel.class);

		//then
		final Map<String, Set<ProductModel>> productsResult = provider.collectProducts(fieldWrappers, product);
		assertThat(productsResult.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_BASE).size()).isEqualTo(1);
		assertThat(productsResult.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_BASE).contains(product))
				.isEqualTo(true);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldThrowExceptionWhenCollectProducts() throws SnIndexerException
	{
		//give
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM, "exceptionParameters");
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ProductModel product = mock(ProductModel.class);

		//then
		provider.collectProducts(fieldWrappers, product);
	}

	@Test
	public void shouldCollectCurrentProduct() throws SnIndexerException
	{
		//give
		ProductModel product = mock(ProductModel.class);

		//then
		final Set<ProductModel> productsResult = provider.collectCurrentProduct(product);
		assertThat(productsResult.size()).isEqualTo(1);
		assertThat(productsResult.contains(product)).isEqualTo(true);
	}

	@Test
	public void shouldCollectCurrentParentProductsOfProduct() throws SnIndexerException
	{
		//give
		ProductModel product = mock(ProductModel.class);

		//then
		final Set<ProductModel> productsResult = provider.collectCurrentParentProducts(product);
		assertThat(productsResult.size()).isEqualTo(1);
		assertThat(productsResult.contains(product)).isEqualTo(true);
	}

	@Test
	public void shouldCollectCurrentParentProductsOfVariantProduct() throws SnIndexerException
	{
		//give
		VariantProductModel variantProduct = mock(VariantProductModel.class);
		ProductModel product = mock(ProductModel.class);
		when(variantProduct.getBaseProduct()).thenReturn(product);

		//then
		final Set<ProductModel> productsResult = provider.collectCurrentParentProducts(variantProduct);
		assertThat(productsResult.size()).isEqualTo(2);
		assertThat(productsResult.contains(product)).isEqualTo(true);
		assertThat(productsResult.contains(product)).isEqualTo(true);
	}


	@Test
	public void shouldCollectBaseProductOfProduct() throws SnIndexerException
	{
		//give
		ProductModel product = mock(ProductModel.class);

		//then
		final Set<ProductModel> productsResult = provider.collectBaseProduct(product);
		assertThat(productsResult.size()).isEqualTo(1);
		assertThat(productsResult.contains(product)).isEqualTo(true);
	}

	@Test
	public void shouldCollectBaseProductOfVariantProduct() throws SnIndexerException
	{
		//give
		VariantProductModel variantProduct = mock(VariantProductModel.class);
		ProductModel product = mock(ProductModel.class);
		when(variantProduct.getBaseProduct()).thenReturn(product);

		//then
		final Set<ProductModel> productsResult = provider.collectBaseProduct(variantProduct);
		assertThat(productsResult.size()).isEqualTo(1);
		assertThat(productsResult.contains(product)).isEqualTo(true);
	}

	@Test
	public void shouldMergeCurrentParentProducts() throws SnIndexerException
	{
		//give
		Set<ProductModel> currentProducts = new HashSet<>();
		Set<ProductModel> baseProducts = new HashSet<>();
		Set<ProductModel> currentParentProducts = new HashSet<>();
		ProductModel product1 = mock(ProductModel.class);
		ProductModel product2 = mock(ProductModel.class);
		ProductModel product3 = mock(ProductModel.class);
		currentProducts.add(product1);
		baseProducts.add(product2);
		currentParentProducts.add(product3);
		when(productsMap.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT_PARENT))
				.thenReturn(currentParentProducts);
		Mockito.lenient().when(productsMap.getOrDefault(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT, Collections.emptySet()))
				.thenReturn(currentProducts);
		Mockito.lenient().when(productsMap.getOrDefault(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_BASE, Collections.emptySet()))
				.thenReturn(baseProducts);

		//then
		assertThat(provider.mergeProducts(productsMap).size()).isEqualTo(1);
		assertThat(provider.mergeProducts(productsMap).contains(product3)).isEqualTo(true);
	}

	@Test
	public void shouldMergeCurrentAndBaseProducts() throws SnIndexerException
	{
		//give
		Set<ProductModel> currentProducts = new HashSet<>();
		Set<ProductModel> baseProducts = new HashSet<>();
		ProductModel product1 = mock(ProductModel.class);
		ProductModel product2 = mock(ProductModel.class);
		currentProducts.add(product1);
		baseProducts.add(product2);
		when(productsMap.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT_PARENT)).thenReturn(null);
		when(productsMap.getOrDefault(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT, Collections.emptySet()))
				.thenReturn(currentProducts);
		when(productsMap.getOrDefault(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_BASE, Collections.emptySet()))
				.thenReturn(baseProducts);

		//then
		assertThat(provider.mergeProducts(productsMap).size()).isEqualTo(2);
		assertThat(provider.mergeProducts(productsMap).contains(product1)).isEqualTo(true);
		assertThat(provider.mergeProducts(productsMap).contains(product2)).isEqualTo(true);
	}


	@Test
	public void shouldResolveProductSelector()
	{
		//give
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.resolveProductSelector(fieldWrapper))
				.isEqualTo(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT);
	}

	protected static class TestAbstractProductSnIndexerValueProvider extends AbstractProductSnIndexerValueProvider<ItemModel, Void>
	{
		@Override
		public Set<Class<?>> getSupportedQualifierClasses() throws SnIndexerException
		{
			return SUPPORTED_QUALIFIER_CLASSES;
		}

		@Override
		protected Object getFieldValue(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper,
				final ItemModel source, final Void data) throws SnIndexerException
		{
			return null;
		}

	}
}
