/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.classification.util.BackofficeClassificationUtils;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.core.model.WidgetModel;


@IntegrationTest
public class ClassificationPropertyAcessorIntegrationTest extends ServicelayerTransactionalTest
{
	public static final Logger LOG = LoggerFactory.getLogger(ClassificationPropertyAcessorIntegrationTest.class);

	private static final String PRODUCT_CODE = "HW2120-0341";
	private static final String MODIFIED_FEATURES_MODEL_PARAM = "modifiedProductFeatures";
	private static final String MODIFIED_FEATURES_MODEL_PARAM_PREFIX = "modifiedProductFeatures.pk";
	private final ClassificationPropertyAccessor classificationPropertyAccessor = new ClassificationPropertyAccessor();
	@Resource
	private ClassificationService classificationService;
	@Resource
	private ProductService productService;
	private FeaturePeristanceHandler featurePeristanceHandler;
	private ProductModel productModel;
	private WidgetModel widgetModel;
	private EvaluationContext evaluationContext;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createHardwareCatalog();
		evaluationContext = Mockito.mock(EvaluationContext.class);
		widgetModel = Mockito.mock(WidgetModel.class);
		Mockito.when(widgetModel.getValue(MODIFIED_FEATURES_MODEL_PARAM, Map.class)).thenReturn(null);
		final TypedValue rootObject = new TypedValue(widgetModel);
		Mockito.when(evaluationContext.getRootObject()).thenReturn(rootObject);

		classificationPropertyAccessor.setClassificationService(classificationService);

		productModel = productService.getProductForCode(PRODUCT_CODE);

		featurePeristanceHandler = new FeaturePeristanceHandler();
		featurePeristanceHandler.setClassificationService(classificationService);

	}

	@Test
	public void testSimpleReadFsbSpeedForProduct() throws AccessException
	{
		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel,
				BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu", "fsbSpeed"));

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				Double.valueOf(1000.0));
	}

	@Test
	public void testSimpleWriteFsbSpeedForProduct() throws AccessException
	{
		final String qualifier = BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu",
				"fsbSpeed");

		final Double parameter = Double.valueOf(2000.0);
		final FeatureValue fValue = new FeatureValue(parameter);
		final FeatureList featureList = classificationService.getFeatures(productModel);
		final Feature feature = getFeatureByCode(featureList, qualifier);
		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), fValue);

		Mockito.reset(widgetModel);

		final Map<String, Feature> modifiedFeatures = Maps.newHashMap();

		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeatures);

		classificationPropertyAccessor.write(evaluationContext, productModel, qualifier, classificationInfo);

		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifier);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				parameter);

	}

	@Test
	public void testWriteAndPersistFeatureForProduct() throws AccessException
	{
		final String qualifier = BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0", "cpu",
				"fsbSpeed");

		final Double parameter = Double.valueOf(666.0);
		final FeatureValue fValue = new FeatureValue(parameter);
		final FeatureList featureList = classificationService.getFeatures(productModel);
		final Feature feature = getFeatureByCode(featureList, qualifier);
		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), fValue);

		Mockito.reset(widgetModel);

		final Map<String, Feature> modifiedFeatures = Maps.newHashMap();
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeatures);

		classificationPropertyAccessor.write(evaluationContext, productModel, qualifier, classificationInfo);

		featurePeristanceHandler.saveFeatures(productModel, modifiedFeatures);

		// fetch from database
		Mockito.reset(widgetModel);
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(null);

		final TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifier);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				parameter);

	}


	@Test
	public void testWriteAndPersistTwoFeaturesForProduct() throws AccessException
	{
		final String qualifierFsbSpeed = BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0",
				"cpu", "fsbSpeed");


		Mockito.reset(widgetModel);

		final Map<String, Feature> modifiedFeatures = Maps.newHashMap();
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(modifiedFeatures);

		final FeatureList featureList = classificationService.getFeatures(productModel);
		final Feature feature = getFeatureByCode(featureList, qualifierFsbSpeed);

		final Double firstFeatureParameter = Double.valueOf(666.0);
		final FeatureValue fValue = new FeatureValue(firstFeatureParameter);
		final ClassificationInfo classificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), fValue);

		classificationPropertyAccessor.write(evaluationContext, productModel, qualifierFsbSpeed, classificationInfo);

		final String qualifierClockSpeed = BackofficeClassificationUtils.getFeatureQualifierEncoded("SampleClassification", "1.0",
				"cpu", "socket");

		final Double secondFeatureParameter = Double.valueOf(777.0);
		final FeatureValue sValue = new FeatureValue(secondFeatureParameter);
		final ClassificationInfo secondClassificationInfo = new ClassificationInfo(feature.getClassAttributeAssignment(), sValue);
		classificationPropertyAccessor.write(evaluationContext, productModel, qualifierClockSpeed, secondClassificationInfo);

		featurePeristanceHandler.saveFeatures(productModel, modifiedFeatures);

		// fetch from database
		Mockito.reset(widgetModel);
		Mockito.when(widgetModel.getValue(createFeatureMapForProductKey(productModel), Map.class)).thenReturn(null);

		TypedValue typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifierFsbSpeed);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				firstFeatureParameter);

		typedValue = classificationPropertyAccessor.read(evaluationContext, productModel, qualifierClockSpeed);

		Assertions.assertThat(typedValue).isNotNull();
		Assertions.assertThat(typedValue.getValue()).isInstanceOf(ClassificationInfo.class);
		Assertions.assertThat(((ClassificationInfo) typedValue.getValue()).getValue()).isInstanceOf(FeatureValue.class);
		Assertions.assertThat(((FeatureValue) ((ClassificationInfo) typedValue.getValue()).getValue()).getValue()).isEqualTo(
				secondFeatureParameter);
	}

	private String createFeatureMapForProductKey(final ProductModel productModel)
	{
		return MODIFIED_FEATURES_MODEL_PARAM_PREFIX + productModel.getPk();
	}

	protected Feature getFeatureByCode(final FeatureList featureList, final String code)
	{
		Feature ret = null;
		for (final Feature feature : featureList.getFeatures())
		{
			if (StringUtils.equals(code,
					BackofficeClassificationUtils.getFeatureQualifierEncoded(feature.getClassAttributeAssignment())))
			{
				ret = feature;
				break;
			}
		}
		return ret;
	}


}
