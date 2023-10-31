/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.classification.util.BackofficeClassificationUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hybris.cockpitng.core.model.WidgetModel;


@UnitTest
public class ClassificationPropertyAccessorUnitTest
{

	public static final Logger LOG = LoggerFactory.getLogger(ClassificationPropertyAcessorIntegrationTest.class);

	private static final String PRODUCT_CODE = "HW2120-0341";
	private static final String MODIFIED_FEATURES_MODEL_PARAM = "modifiedProductFeatures";
	private static final String MODIFIED_FEATURES_MODEL_PARAM_PREFIX = "modifiedProductFeatures.pk";
	private final ClassificationPropertyAccessor classificationPropertyAccessor = new ClassificationPropertyAccessor();
	private ProductModel productModel;
	private WidgetModel widgetModel;
	private EvaluationContext evaluationContext;
	private ClassificationService classificationService;

	@Before
	public void setUp() throws Exception
	{
		evaluationContext = Mockito.mock(EvaluationContext.class);
		widgetModel = Mockito.mock(WidgetModel.class);

		Mockito.when(widgetModel.getValue(MODIFIED_FEATURES_MODEL_PARAM, Map.class)).thenReturn(null);

		final TypedValue rootObject = new TypedValue(widgetModel);
		Mockito.when(evaluationContext.getRootObject()).thenReturn(rootObject);

		classificationService = Mockito.mock(ClassificationService.class);
		classificationPropertyAccessor.setClassificationService(classificationService);

		productModel = spy(new ProductModel());
		productModel.setCode(PRODUCT_CODE);

	}

	@Test
	public void testClassificationAttrributeCorrect()
	{
		Assertions.assertThat(
				classificationPropertyAccessor.isClassificationAttributeCorrect(BackofficeClassificationUtils
						.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed"))).isTrue();
		Assertions.assertThat(
				classificationPropertyAccessor.isClassificationAttributeCorrect("/{SampleClassification}/{1.0}/{cpu}.{fsbSpeed}"))
				.isFalse();
	}

	@Test
	public void testCanReadSuccess() throws AccessException
	{
		Assertions.assertThat(
				classificationPropertyAccessor.canRead(evaluationContext, productModel,
						BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed")))
				.isTrue();

	}

	@Test
	public void testCanReadMultidotSuccess() throws AccessException
	{
		Assertions.assertThat(
				classificationPropertyAccessor.canRead(evaluationContext, productModel, BackofficeClassificationUtils
						.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed.frequency"))).isTrue();

	}

	@Test
	public void testCanWriteSuccess() throws AccessException
	{
		Assertions.assertThat(
				classificationPropertyAccessor.canRead(evaluationContext, productModel,
						BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed")))
				.isTrue();

	}

	@Test
	public void testCanWriteMultidotSuccess() throws AccessException
	{
		Assertions.assertThat(
				classificationPropertyAccessor.canWrite(evaluationContext, productModel, BackofficeClassificationUtils
						.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed.frequency"))).isTrue();
	}


	@Test
	public void testSimpleReadFsbSpeedForProduct() throws AccessException
	{
		// given
		final String qualifier = BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu",
				"fsbSpeed");
		final double parameter = 10.0;
		final FeatureValue featureValue = new FeatureValue(Double.valueOf(parameter));

		final Feature feature = prepareFeature("SampleClassification", "1.0", "cpu", "fsbSpeed");
		Mockito.when(feature.getCode()).thenReturn(qualifier);
		Mockito.when(feature.getValue()).thenReturn(featureValue);

		Mockito.when(feature.getClassAttributeAssignment().getMultiValued()).thenReturn(Boolean.FALSE);


		final List<Feature> features = Lists.newArrayList(feature);
		final FeatureList featureList = new FeatureList(features);

		// when
		Mockito.when(classificationService.getFeatures(productModel)).thenReturn(featureList);

		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel,
				BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed"));
		// then
		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(parameter));

	}

	@Test
	public void testSimpleWriteFsbSpeedForProduct() throws AccessException
	{
		// given
		final String qualifier = BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu",
				"fsbSpeed");
		final double parameter = 666.0;
		final FeatureValue featureValue = new FeatureValue(Double.valueOf(parameter));
		final Feature feature = prepareFeature("SampleClassification", "1.0", "cpu", "fsbSpeed");
		Mockito.when(feature.getValue()).thenReturn(featureValue);

		final List<Feature> features = Lists.newArrayList(feature);
		final FeatureList featureList = new FeatureList(features);

		final Map<String, Feature> modifiedFeature = Maps.newHashMap();
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeature);

		Mockito.when(classificationService.getFeatures(productModel)).thenReturn(featureList);

		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), featureValue);

		// when
		classificationPropertyAccessor.write(evaluationContext, productModel, qualifier, classificationInfo);
		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifier);

		// then
		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(parameter));
		verify(productModel).setModifiedtime(any(Date.class));
	}

	private String createFeatureMapForProductKey(final ProductModel productModel)
	{
		return MODIFIED_FEATURES_MODEL_PARAM_PREFIX + productModel.getPk();
	}

	private Feature prepareFeature(final String systemId, final String systemVersion, final String classificationClass,
			final String attribute)
	{
		final ClassAttributeAssignmentModel attributeAssignmentModel = Mockito.mock(ClassAttributeAssignmentModel.class);
		final ClassificationSystemVersionModel classificationSystemVersionModel = Mockito
				.mock(ClassificationSystemVersionModel.class);
		final ClassificationSystemModel classificationSystemModel = Mockito.mock(ClassificationSystemModel.class);

		Mockito.when(attributeAssignmentModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		Mockito.when(classificationSystemVersionModel.getCatalog()).thenReturn(classificationSystemModel);
		Mockito.when(classificationSystemModel.getId()).thenReturn(systemId);
		Mockito.when(classificationSystemVersionModel.getVersion()).thenReturn(systemVersion);

		final ClassificationClassModel classificationClassModel = Mockito.mock(ClassificationClassModel.class);
		Mockito.when(classificationClassModel.getCode()).thenReturn(classificationClass);

		final ClassificationAttributeModel classificationAttributeModel = Mockito.mock(ClassificationAttributeModel.class);
		Mockito.when(classificationAttributeModel.getCode()).thenReturn(attribute);
		Mockito.when(attributeAssignmentModel.getClassificationClass()).thenReturn(classificationClassModel);
		Mockito.when(attributeAssignmentModel.getClassificationAttribute()).thenReturn(classificationAttributeModel);

		final Feature feature = Mockito.mock(Feature.class);
		Mockito.when(feature.getClassAttributeAssignment()).thenReturn(attributeAssignmentModel);
		return feature;
	}


}
