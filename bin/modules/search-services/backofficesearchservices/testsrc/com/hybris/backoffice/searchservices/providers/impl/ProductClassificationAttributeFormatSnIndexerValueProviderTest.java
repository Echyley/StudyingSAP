/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.core.service.SnSessionService;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.searchservices.daos.SnClassificationAttributeAssignmentModelDao;
import com.hybris.backoffice.searchservices.providers.impl.ProductClassificationAttributeFormatSnIndexerValueProvider.ProductClassificationData;



@RunWith(MockitoJUnitRunner.class)
public class ProductClassificationAttributeFormatSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@InjectMocks
	private ProductClassificationAttributeFormatSnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	@Mock
	private ItemModel item;

	@Mock
	private ClassificationSystemService classificationSystemService;

	@Mock
	private ClassificationService classificationService;

	@Mock
	private SnSessionService snSessionService;

	@Mock
	private SnClassificationAttributeAssignmentModelDao snClassificationAttributeAssignmentModelDao;

	static final String FIELD_1_ID = "field1";

	static final PK PRODUCT_PK = PK.fromLong(1);

	static final String LOWER_CASE_FORMAT = "lowerCaseFormat";
	static final String EXPRESSION = "expression";

	static final String CLASSIFICATON_SYTEM_ID = "ElectronicsClassification";
	static final String CLASSIFICATON_SYTEM_VERSION = "ElectronicsClassification";

	static final String CLASSIFICATON_CLASS_1_CODE = "622";
	static final String CLASSIFICATON_ATTRIBUTE_1_CODE = "Size, 1147";
	static final String CLASSIFICATON_ATTRIBUTE_1 = CLASSIFICATON_SYTEM_ID + "/" + CLASSIFICATON_SYTEM_VERSION + "/"
			+ CLASSIFICATON_CLASS_1_CODE + "." + CLASSIFICATON_ATTRIBUTE_1_CODE;

	static final String CLASSIFICATON_CLASS_2_CODE = "631";
	static final String CLASSIFICATON_ATTRIBUTE_2_CODE = "Resolution, 80";
	static final String CLASSIFICATON_ATTRIBUTE_2 = CLASSIFICATON_SYTEM_ID + "/" + CLASSIFICATON_SYTEM_VERSION + "/"
			+ CLASSIFICATON_CLASS_2_CODE + "." + CLASSIFICATON_ATTRIBUTE_2_CODE;

	@Test
	public void shouldGetSupportedQualifierClasses() throws SnIndexerException
	{
		assertThat(provider.getSupportedQualifierClasses()).containsExactly(Locale.class);
	}

	@Test
	public void shouldGetNullFieldValue() throws SnIndexerException
	{
		//give
		ProductModel productSource = mock(ProductModel.class);
		ProductClassificationData data = mock(ProductClassificationData.class);
		Set<ProductModel> products = new HashSet<>();
		Map<String, Set<ProductModel>> productsMap = new HashMap<>();
		productsMap.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM_DEFAULT_VALUE, products);
		when(data.getProducts()).thenReturn(productsMap);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				EXPRESSION);
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);


		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, productSource, data)).isEqualTo(null);
	}

	@Test
	public void shouldGetFieldValue() throws SnIndexerException
	{
		//give
		ProductModel productSource = mock(ProductModel.class);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		ProductClassificationData data = mock(ProductClassificationData.class);
		Set<ProductModel> products = new HashSet<>();
		ProductModel product = mock(ProductModel.class);
		when(fieldWrapper.isLocalized()).thenReturn(false);
		products.add(product);
		Map<String, Set<ProductModel>> productsMap = new HashMap<>();
		productsMap.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM_DEFAULT_VALUE, products);
		when(data.getProducts()).thenReturn(productsMap);
		Map<String, ClassAttributeAssignmentModel> classAttributeAssignmentModelMap = new HashMap<>();
		classAttributeAssignmentModelMap.put(EXPRESSION, classAttributeAssignment);
		when(data.getClassAttributeAssignments()).thenReturn(classAttributeAssignmentModelMap);
		when(product.getPk()).thenReturn(PRODUCT_PK);
		Map<PK, FeatureList> featureListMap = new HashMap<>();
		when(data.getFeatures()).thenReturn(featureListMap);
		FeatureList featureList = mock(FeatureList.class);
		featureListMap.put(PRODUCT_PK, featureList);
		Feature feature = mock(Feature.class);
		when(featureList.getFeatureByAssignment(classAttributeAssignment)).thenReturn(feature);
		List<FeatureValue> featureValues = new ArrayList<>();
		when(feature.getValues()).thenReturn(featureValues);
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				EXPRESSION);
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(false);


		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, productSource, data)).isEqualTo(stringValue);
	}


	@Test
	public void shouldGetLocalizedFieldValue() throws SnIndexerException
	{
		//give
		ProductModel productSource = mock(ProductModel.class);
		Locale locale = Locale.forLanguageTag("en");
		when(fieldWrapper.isLocalized()).thenReturn(true);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		ProductClassificationData data = mock(ProductClassificationData.class);
		Set<ProductModel> products = new HashSet<>();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		Map<String, Set<ProductModel>> productsMap = new HashMap<>();
		productsMap.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM_DEFAULT_VALUE, products);
		when(data.getProducts()).thenReturn(productsMap);
		Map<String, ClassAttributeAssignmentModel> classAttributeAssignmentModelMap = new HashMap<>();
		classAttributeAssignmentModelMap.put(EXPRESSION, classAttributeAssignment);
		when(data.getClassAttributeAssignments()).thenReturn(classAttributeAssignmentModelMap);
		when(product.getPk()).thenReturn(PRODUCT_PK);
		Map<PK, FeatureList> featureListMap = new HashMap<>();
		FeatureList featureList = mock(FeatureList.class);
		featureListMap.put(PRODUCT_PK, featureList);
		when(data.getFeatures()).thenReturn(featureListMap);
		Feature feature = mock(Feature.class);
		when(featureList.getFeatureByAssignment(classAttributeAssignment)).thenReturn(feature);
		List<FeatureValue> featureValues = new ArrayList<>();
		when(feature.getValues()).thenReturn(featureValues);
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);
		List<SnQualifier> qualifiers = new ArrayList<>();
		SnQualifier qualifier = mock(SnQualifier.class);
		when(qualifier.getAs(any())).thenReturn(locale);
		qualifiers.add(qualifier);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				EXPRESSION);
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(false);


		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, productSource, data) instanceof Map).isEqualTo(true);
		final Map fieldValueMap = (Map) provider.getFieldValue(indexerContext, fieldWrapper, productSource, data);
		assertThat(fieldValueMap.containsKey(locale)).isEqualTo(true);
		assertThat(fieldValueMap.get(locale)).isEqualTo(stringValue);
	}

	@Test
	public void shouldCollectLocalizedValues() throws SnIndexerException
	{
		//give
		List<Locale> locales = new ArrayList<>();
		Locale locale = Locale.forLanguageTag("en");
		locales.add(locale);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		ProductClassificationData data = mock(ProductClassificationData.class);
		List<ProductModel> products = new ArrayList<>();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(product.getPk()).thenReturn(PRODUCT_PK);
		Map<PK, FeatureList> featureListMap = new HashMap<>();
		FeatureList featureList = mock(FeatureList.class);
		featureListMap.put(PRODUCT_PK, featureList);
		when(data.getFeatures()).thenReturn(featureListMap);
		Feature feature = mock(Feature.class);
		when(featureList.getFeatureByAssignment(classAttributeAssignment)).thenReturn(feature);
		List<FeatureValue> featureValues = new ArrayList<>();
		when(feature.getValues()).thenReturn(featureValues);
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(false);


		//then
		assertThat(provider.collectLocalizedValues(fieldWrapper, products, classAttributeAssignment, data, locales) instanceof Map)
				.isEqualTo(true);
		final Map fieldValueMap = (Map) provider.collectLocalizedValues(fieldWrapper, products, classAttributeAssignment, data,
				locales);
		assertThat(fieldValueMap.containsKey(locale)).isEqualTo(true);
		assertThat(fieldValueMap.get(locale)).isEqualTo(stringValue);
	}

	@Test
	public void shouldAddFeatureValuesWithLocalizedFeature()
	{
		//give
		LocalizedFeature feature = mock(LocalizedFeature.class);
		List<Locale> locales = new ArrayList<>();
		Locale locale = Locale.forLanguageTag("en");
		locales.add(locale);
		Map<Locale, List<Object>> localizedValues = new HashMap<>();
		List<Object> values = new ArrayList<>();
		localizedValues.put(locale, values);
		List<FeatureValue> featureValues = new ArrayList<>();
		when(feature.getValues(locale)).thenReturn(featureValues);
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);


		//then
		provider.addLocalizedFeatureValues(localizedValues, feature, locales);
		assertThat(localizedValues.get(locale).size()).isEqualTo(1);
		assertThat(localizedValues.get(locale).get(0)).isEqualTo(stringValue);
	}

	@Test
	public void shouldAddFeatureValuesWithoutLocalizedFeature()
	{
		//give
		Feature feature = mock(Feature.class);
		List<Locale> locales = new ArrayList<>();
		Locale locale = Locale.forLanguageTag("en");
		locales.add(locale);
		Map<Locale, List<Object>> localizedValues = new HashMap<>();
		List<Object> values = new ArrayList<>();
		localizedValues.put(locale, values);
		List<FeatureValue> featureValues = new ArrayList<>();
		when(feature.getValues()).thenReturn(featureValues);
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);


		//then
		provider.addLocalizedFeatureValues(localizedValues, feature, locales);
		assertThat(localizedValues.get(locale).size()).isEqualTo(1);
		assertThat(localizedValues.get(locale).get(0)).isEqualTo(stringValue);
	}

	@Test
	public void shouldNotCleanLocalizedValues()
	{
		//give
		Map<Locale, List<Object>> localizedValues = new HashMap<>();
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		Mockito.lenient().when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.cleanLocalizedValues(fieldWrapper, localizedValues)).isEqualTo(null);
	}


	@Test
	public void shouldCleanMultiLocalizedValues()
	{
		//give
		Map<Locale, List<Object>> localizedValues = new HashMap<>();
		List<Object> values = new ArrayList<>();
		Locale locale = Locale.forLanguageTag("en");
		localizedValues.put(locale, values);
		String stringValue = "ABC";
		values.add(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(true);

		//then
		assertThat(provider.cleanLocalizedValues(fieldWrapper, localizedValues) instanceof Map).isEqualTo(true);
		final Map fieldValueMap = (Map) provider.cleanLocalizedValues(fieldWrapper, localizedValues);
		assertThat(fieldValueMap.containsKey(locale)).isEqualTo(true);
		assertThat(fieldValueMap.get(locale)).isEqualTo(values);
	}


	@Test
	public void shouldCleanLocalizedValues()
	{
		//give
		Map<Locale, List<Object>> localizedValues = new HashMap<>();
		List<Object> values = new ArrayList<>();
		Locale locale = Locale.forLanguageTag("en");
		localizedValues.put(locale, values);
		String stringValue = "ABC";
		values.add(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(false);

		//then
		assertThat(provider.cleanLocalizedValues(fieldWrapper, localizedValues) instanceof Map).isEqualTo(true);
		final Map fieldValueMap = (Map) provider.cleanLocalizedValues(fieldWrapper, localizedValues);
		assertThat(fieldValueMap.containsKey(locale)).isEqualTo(true);
		assertThat(fieldValueMap.get(locale)).isEqualTo(stringValue);
	}

	@Test
	public void shouldCollectValues()
	{
		//give
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		ProductClassificationData data = mock(ProductClassificationData.class);
		List<ProductModel> products = new ArrayList<>();
		ProductModel product = mock(ProductModel.class);
		products.add(product);
		when(product.getPk()).thenReturn(PRODUCT_PK);
		Map<PK, FeatureList> featureListMap = new HashMap<>();
		when(data.getFeatures()).thenReturn(featureListMap);
		FeatureList featureList = mock(FeatureList.class);
		featureListMap.put(PRODUCT_PK, featureList);
		Feature feature = mock(Feature.class);
		when(featureList.getFeatureByAssignment(classAttributeAssignment)).thenReturn(feature);
		List<FeatureValue> featureValues = new ArrayList<>();
		when(feature.getValues()).thenReturn(featureValues);
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(false);


		//then
		assertThat(provider.collectValues(fieldWrapper, products, classAttributeAssignment, data)).isEqualTo(stringValue);
	}

	@Test
	public void shouldAddFeatureValues()
	{
		//give
		List<Object> values = new ArrayList<>();
		List<FeatureValue> featureValues = new ArrayList<>();
		String stringValue = "ABC";
		FeatureValue featureValue = mock(FeatureValue.class);
		featureValues.add(featureValue);
		when(featureValue.getValue()).thenReturn(stringValue);


		//then
		provider.addFeatureValues(values, featureValues);
		assertThat(values.size()).isEqualTo(1);
		assertThat(values.get(0)).isEqualTo(stringValue);
	}

	@Test
	public void shouldAddEmptyFeatureValues()
	{
		//give
		List<Object> values = new ArrayList<>();
		List<FeatureValue> featureValues = new ArrayList<>();

		//then
		provider.addFeatureValues(values, featureValues);
		assertThat(values.size()).isEqualTo(0);
	}

	@Test
	public void shouldNotCleanValues()
	{
		//give
		List<Object> values = new ArrayList<>();
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.cleanValues(fieldWrapper, values)).isEqualTo(null);
	}


	@Test
	public void shouldCleanMultiValues()
	{
		//give
		List<Object> values = new ArrayList<>();
		String stringValue = "ABC";
		values.add(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(true);

		//then
		assertThat(provider.cleanValues(fieldWrapper, values)).isEqualTo(values);
	}


	@Test
	public void shouldCleanValues()
	{
		//give
		List<Object> values = new ArrayList<>();
		String stringValue = "ABC";
		values.add(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		when(fieldWrapper.isMultiValued()).thenReturn(false);

		//then
		assertThat(provider.cleanValues(fieldWrapper, values)).isEqualTo(stringValue);
	}

	@Test
	public void shouldNotFormatValues()
	{
		//give
		String stringValue = "ABC";
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);


		//then
		assertThat(provider.formatValues(fieldWrapper, stringValue)).isEqualTo(stringValue);
	}

	@Test
	public void shouldFormatValues()
	{
		//give
		String stringValue = "ABC";
		String formatStringValue = "abc";
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM, LOWER_CASE_FORMAT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.formatValues(fieldWrapper, stringValue)).isEqualTo(formatStringValue);
	}


	@Test
	public void shouldFormatToLowerCaseWithCollection()
	{
		//give
		String stringValue = "ABC";
		String formatStringValue = "abc";
		List<Object> stringList = new ArrayList<>();
		stringList.add(stringValue);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM, LOWER_CASE_FORMAT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);


		//then
		assertThat(provider.formatValues(fieldWrapper, stringList) instanceof List).isEqualTo(true);
		final List foramtValuesList = (List) provider.formatValues(fieldWrapper, stringList);
		assertThat(foramtValuesList.size()).isEqualTo(1);
		assertThat(foramtValuesList.get(0)).isEqualTo(formatStringValue);
	}

	@Test
	public void shouldFormatToLowerCaseWithOtherType()
	{
		//give
		ProductModel product = mock(ProductModel.class);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM, LOWER_CASE_FORMAT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.formatValues(fieldWrapper, product)).isEqualTo(product);
	}


	@Test
	public void shouldLoadData() throws SnIndexerException
	{
		//give
		ProductModel product = mock(ProductModel.class);
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_PARAM,
				AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		Map<String, Object> attributes = new HashMap<>();
		Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = new HashMap<>();
		attributes.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY,
				classAttributeAssignments);
		when(indexerContext.getAttributes()).thenReturn(attributes);

		provider.setClassificationService(classificationService);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		FeatureList featureList = mock(FeatureList.class);
		classAttributeAssignments.put("test", classAttributeAssignment);
		when(classificationService.getFeatures(eq(product), any())).thenReturn(featureList);
		when(product.getPk()).thenReturn(PRODUCT_PK);

		//then
		final ProductClassificationData productClassificationData = provider.loadData(indexerContext, fieldWrappers, product);
		final Map<String, Set<ProductModel>> productsValues = productClassificationData.getProducts();
		assertThat(productsValues.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT).size()).isEqualTo(1);
		assertThat(productsValues.get(AbstractProductSnIndexerValueProvider.PRODUCT_SELECTOR_VALUE_CURRENT).contains(product))
				.isEqualTo(true);
		final Map<String, ClassAttributeAssignmentModel> classAttributeAssignmentsValuesMap = productClassificationData
				.getClassAttributeAssignments();
		assertThat(classAttributeAssignmentsValuesMap).isEqualTo(classAttributeAssignments);
		final Map<PK, FeatureList> featuresValuesMap = productClassificationData.getFeatures();
		assertThat(featuresValuesMap.containsKey(PRODUCT_PK)).isEqualTo(true);
		assertThat(featuresValuesMap.get(PRODUCT_PK)).isEqualTo(featureList);
	}


	@Test
	public void shouldCollectClassAttributeAssignments() throws SnIndexerException
	{
		//give
		Map<String, Object> attributes = new HashMap<>();
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = new HashMap<>();
		attributes.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY,
				classAttributeAssignments);
		when(indexerContext.getAttributes()).thenReturn(attributes);


		//then
		final Map<String, ClassAttributeAssignmentModel> classAttributeAssignmentsValue = provider
				.collectClassAttributeAssignments(indexerContext, fieldWrappers);
		assertThat(classAttributeAssignmentsValue).isEqualTo(classAttributeAssignments);
	}

	@Test
	public void shouldCollectClassAttributeAssignmentsWithNull() throws SnIndexerException
	{
		//give
		Map<String, Object> attributes = new HashMap<>();
		when(indexerContext.getAttributes()).thenReturn(attributes);
		provider.setSnSessionService(snSessionService);
		provider.setClassificationSystemService(classificationSystemService);
		provider.setSnClassificationAttributeAssignmentModelDao(snClassificationAttributeAssignmentModelDao);
		doNothing().when(snSessionService).initializeSession();
		doNothing().when(snSessionService).disableSearchRestrictions();
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				CLASSIFICATON_ATTRIBUTE_1);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ClassificationSystemVersionModel classSystemVersion = mock(ClassificationSystemVersionModel.class);
		ClassificationClassModel classClass = mock(ClassificationClassModel.class);
		ClassificationAttributeModel classAttribute = mock(ClassificationAttributeModel.class);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		when(classificationSystemService.getSystemVersion(CLASSIFICATON_SYTEM_ID, CLASSIFICATON_SYTEM_VERSION))
				.thenReturn(classSystemVersion);
		when(classificationSystemService.getClassForCode(classSystemVersion, CLASSIFICATON_CLASS_1_CODE)).thenReturn(classClass);
		when(classificationSystemService.getAttributeForCode(classSystemVersion, CLASSIFICATON_ATTRIBUTE_1_CODE))
				.thenReturn(classAttribute);
		when(snClassificationAttributeAssignmentModelDao.findClassAttributeAssignmentByClassAndAttribute(classClass,
				classAttribute)).thenReturn(Optional.of(classAttributeAssignment));


		//then
		final Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = provider
				.collectClassAttributeAssignments(indexerContext, fieldWrappers);
		assertThat(classAttributeAssignments.containsKey(CLASSIFICATON_ATTRIBUTE_1)).isEqualTo(true);
		assertThat(classAttributeAssignments.get(CLASSIFICATON_ATTRIBUTE_1)).isEqualTo(classAttributeAssignment);
		assertThat(attributes
				.containsKey(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY))
						.isEqualTo(true);
		assertThat(
				attributes.get(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY))
						.isEqualTo(classAttributeAssignments);
	}


	@Test
	public void shouldDoCollectClassAttributeAssignments() throws SnIndexerException
	{
		//give
		provider.setSnSessionService(snSessionService);
		provider.setClassificationSystemService(classificationSystemService);
		provider.setSnClassificationAttributeAssignmentModelDao(snClassificationAttributeAssignmentModelDao);
		doNothing().when(snSessionService).initializeSession();
		doNothing().when(snSessionService).disableSearchRestrictions();
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				CLASSIFICATON_ATTRIBUTE_1);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ClassificationSystemVersionModel classSystemVersion = mock(ClassificationSystemVersionModel.class);
		ClassificationClassModel classClass = mock(ClassificationClassModel.class);
		ClassificationAttributeModel classAttribute = mock(ClassificationAttributeModel.class);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		when(classificationSystemService.getSystemVersion(CLASSIFICATON_SYTEM_ID, CLASSIFICATON_SYTEM_VERSION))
				.thenReturn(classSystemVersion);
		when(classificationSystemService.getClassForCode(classSystemVersion, CLASSIFICATON_CLASS_1_CODE)).thenReturn(classClass);
		when(classificationSystemService.getAttributeForCode(classSystemVersion, CLASSIFICATON_ATTRIBUTE_1_CODE))
				.thenReturn(classAttribute);
		when(snClassificationAttributeAssignmentModelDao.findClassAttributeAssignmentByClassAndAttribute(classClass,
				classAttribute)).thenReturn(Optional.of(classAttributeAssignment));


		//then
		final Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = provider
				.doCollectClassAttributeAssignments(fieldWrappers);
		assertThat(classAttributeAssignments.containsKey(CLASSIFICATON_ATTRIBUTE_1)).isEqualTo(true);
		assertThat(classAttributeAssignments.get(CLASSIFICATON_ATTRIBUTE_1)).isEqualTo(classAttributeAssignment);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenDoCollectClassAttributeAssignments() throws SnIndexerException
	{
		//give
		provider.setSnSessionService(snSessionService);
		provider.setClassificationSystemService(classificationSystemService);
		provider.setSnClassificationAttributeAssignmentModelDao(snClassificationAttributeAssignmentModelDao);
		doNothing().when(snSessionService).initializeSession();
		doNothing().when(snSessionService).disableSearchRestrictions();
		List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();
		fieldWrappers.add(fieldWrapper);
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				"exceptionParameters");
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);
		ClassificationSystemVersionModel classSystemVersion = mock(ClassificationSystemVersionModel.class);
		ClassificationClassModel classClass = mock(ClassificationClassModel.class);
		ClassificationAttributeModel classAttribute = mock(ClassificationAttributeModel.class);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		Mockito.lenient().when(classificationSystemService.getSystemVersion(CLASSIFICATON_SYTEM_ID, CLASSIFICATON_SYTEM_VERSION))
				.thenReturn(classSystemVersion);
		Mockito.lenient().when(classificationSystemService.getClassForCode(classSystemVersion, CLASSIFICATON_CLASS_1_CODE)).thenReturn(classClass);
		Mockito.lenient().when(classificationSystemService.getAttributeForCode(classSystemVersion, CLASSIFICATON_ATTRIBUTE_1_CODE))
				.thenReturn(classAttribute);
		Mockito.lenient().when(snClassificationAttributeAssignmentModelDao.findClassAttributeAssignmentByClassAndAttribute(classClass,
				classAttribute)).thenReturn(Optional.of(classAttributeAssignment));


		//then
		provider.doCollectClassAttributeAssignments(fieldWrappers);
	}

	@Test
	public void shouldCollectFeatures()
	{
		//give
		provider.setClassificationService(classificationService);
		List<ProductModel> products = new ArrayList<>();
		List<ClassAttributeAssignmentModel> classAttributeAssignments = new ArrayList<>();
		ProductModel product = mock(ProductModel.class);
		ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		FeatureList featureList = mock(FeatureList.class);
		products.add(product);
		classAttributeAssignments.add(classAttributeAssignment);
		when(classificationService.getFeatures(eq(product), any())).thenReturn(featureList);
		when(product.getPk()).thenReturn(PRODUCT_PK);

		//then
		final Map<PK, FeatureList> features = provider.collectFeatures(products, classAttributeAssignments);
		assertThat(features.containsKey(PRODUCT_PK)).isEqualTo(true);
		assertThat(features.get(PRODUCT_PK)).isEqualTo(featureList);
	}

	@Test
	public void shouldResolveClassificationAttribute()
	{
		//give
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.resolveClassificationAttribute(fieldWrapper))
				.isEqualTo(ProductClassificationAttributeFormatSnIndexerValueProvider.CLASSIFICATION_ATTRIBUTE_PARAM_DEFAULT_VALUE);
	}

	@Test
	public void shouldResolveFormat()
	{
		//give
		Map<String, String> valueProviderParameters = new HashMap<>();
		valueProviderParameters.put(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM,
				ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
		when(fieldWrapper.getValueProviderParameters()).thenReturn(valueProviderParameters);
		Mockito.lenient().when(fieldWrapper.getField()).thenReturn(snField);
		Mockito.lenient().when(snField.getId()).thenReturn(FIELD_1_ID);

		//then
		assertThat(provider.resolveFormat(fieldWrapper))
				.isEqualTo(ProductClassificationAttributeFormatSnIndexerValueProvider.FORMAT_PARAM_DEFAULT_VALUE);
	}

}
