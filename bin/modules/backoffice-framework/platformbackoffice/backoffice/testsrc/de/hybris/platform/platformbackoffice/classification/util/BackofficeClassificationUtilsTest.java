/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;
import de.hybris.platform.platformbackoffice.classification.ClassificationPropertyAccessor;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.hybris.cockpitng.util.Range;


/**
 * Test class to verify feature qualifier generation
 */
public class BackofficeClassificationUtilsTest
{
	final ClassificationPropertyAccessor classificationPropertyAccessor = new ClassificationPropertyAccessor();
	private final String catalogId = "SampleClassification";
	private final String versionId = "1.0";
	private final String classificationClass = "cpu";
	private final String attribute = "speed";

	@Test
	public void testIsFeatureQualifier() throws Exception
	{
		String qualifierEncoded = "ClassificationFeatureEncoded_U2FtcGxlQ2xhc3NpZmljYX+pb24vMS4wL2NwdS5zcGlZA_$_$$_$$";

		Assert.assertFalse(String.format("Escaped qualifier shouldn't match %s", qualifierEncoded),
				qualifierEncoded.matches(BackofficeClassificationUtils.FEATURE_QUALIFIER_REGEX));

		Assert.assertTrue(String.format("Proper feature qualifier %s isn't recognized by util", qualifierEncoded),
				BackofficeClassificationUtils.isFeatureQualifier(qualifierEncoded));

		qualifierEncoded = qualifierEncoded.concat("B%UG");
		Assert.assertFalse(String.format("Wrong feature qualifier %s is recognized as proper by util", qualifierEncoded),
				BackofficeClassificationUtils.isFeatureQualifier(qualifierEncoded));
	}

	@Test
	public void testGetFeatureQualifierEncoded()
	{
		String qualifierEncoded = BackofficeClassificationUtils.getFeatureQualifierEncoded(catalogId, versionId,
				classificationClass, attribute);

		qualifierEncoded = BackofficeClassificationUtils.unescapeBase64(qualifierEncoded);
		Assert.assertTrue(
				String.format("Encoded qualifier %s doesn't match %s", qualifierEncoded,
						BackofficeClassificationUtils.FEATURE_QUALIFIER_REGEX),
				qualifierEncoded.matches(BackofficeClassificationUtils.FEATURE_QUALIFIER_REGEX));

		qualifierEncoded = qualifierEncoded.concat("BU%G");
		Assert.assertFalse(
				String.format("Qualifier %s shouldn't match %s", qualifierEncoded,
						BackofficeClassificationUtils.FEATURE_QUALIFIER_REGEX),
				qualifierEncoded.matches(BackofficeClassificationUtils.FEATURE_QUALIFIER_REGEX));

		qualifierEncoded = BackofficeClassificationUtils
				.getFeatureQualifierEncoded("SampleClassification/1.0/electronics.dimensions");

		Assert.assertTrue("Encoded qualifier isn't properly encoded",
				BackofficeClassificationUtils.isFeatureQualifier(qualifierEncoded));

	}

	@Test
	public void testEscapeAndUnescapeBase64()
	{
		final String featureQualifier = "ClassificationFeatureEncoded_U2FtcGxlQ2xhc3NpZmljYXRpb24vMS4wL2NwdS5zcGlZA/==";
		final String escapedQualifier = BackofficeClassificationUtils.escapeBase64(featureQualifier);
		final String unescapedQualifier = BackofficeClassificationUtils.unescapeBase64(escapedQualifier);

		Assert.assertNotEquals("Feature qualifier is not escaped", featureQualifier, escapedQualifier);
		Assert.assertFalse(escapedQualifier.contains("/"));
		Assert.assertFalse(escapedQualifier.contains("\\+"));
		Assert.assertFalse(escapedQualifier.contains("="));
		Assert.assertEquals("Feature qualifier unescaped doesn't equal original one", featureQualifier, unescapedQualifier);
	}

	@Test
	public void testSpringParser()
	{
		final String qualifierEncoded = BackofficeClassificationUtils
				.encodeBase64("ClassificationFeatureEncoded_U2FtcGxlQ2xhc3NpZmljYXRpb24vMS4wL2NwdS5zcGVlZA_/+///++==");
		final String qualifierGenerated = BackofficeClassificationUtils.getFeatureQualifierEncoded(catalogId, versionId,
				classificationClass, attribute);
		final String qualifierUnicornGenerated = BackofficeClassificationUtils.getFeatureQualifierEncoded(
				"'+ I'll hack you, definitely! +'", "who' + ' broke' + , the?:app + '", "4096^4096", "hack' + ' two' + , alert + '");

		final Map<String, String> root = new HashMap<>();
		root.put(qualifierEncoded, catalogId);
		root.put(qualifierGenerated, versionId);
		root.put(qualifierUnicornGenerated, classificationClass);

		final ExpressionParser parser = new SpelExpressionParser();
		final EvaluationContext context = new StandardEvaluationContext(root);

		String result = parser.parseExpression(String.format("[%s]", qualifierEncoded)).getValue(context, String.class);
		Assert.assertEquals(result, catalogId);

		result = parser.parseExpression(String.format("[%s]", qualifierGenerated)).getValue(context, String.class);
		Assert.assertEquals(result, versionId);

		result = parser.parseExpression(String.format("[%s]", qualifierUnicornGenerated)).getValue(context, String.class);
		Assert.assertEquals(result, classificationClass);
	}

	@Test
	public void testDecodeFeatureQualifier() throws UnsupportedEncodingException
	{
		final String catalogId = "SampleClassification";
		final String versionId = "1.0";
		final String classificationClass = "cpuon";
		final String attribute = "on";

		final String encodedQualifier = BackofficeClassificationUtils.getFeatureQualifierEncoded(catalogId, versionId,
				classificationClass, attribute);

		final String decodedQualifier = decode(encodedQualifier);
		final String originalQualifier = String.format("%s/%s/%s.%s", catalogId, versionId, classificationClass, attribute);
		Assert.assertEquals("Decoded qualifier doesn't match original qualifier", originalQualifier, decodedQualifier);
	}

	private String decode(final String featureQualifier) throws UnsupportedEncodingException
	{
		String qualifier = StringUtils.remove(featureQualifier, BackofficeClassificationUtils.CLASSIFICATION_FEATURE_PREFIX);
		qualifier = BackofficeClassificationUtils.unescapeBase64(qualifier);
		return new String(Base64.getDecoder().decode(qualifier), "UTF-8");
	}

	@Test
	public void testNullSafeAddMultiValues()
	{
		final Feature feature = mock(Feature.class);
		final ClassificationInfo info = mock(ClassificationInfo.class);
		when(info.isLocalized()).thenReturn(false);
		when(info.isMultiValue()).thenReturn(true);
		when(info.getValue()).thenReturn(null);

		BackofficeClassificationUtils.updateFeatureWithClassificationInfo(feature, info);
	}

	@Test
	public void testLocalizedRangeIsNotClearedIfNewValueIsNotProvided()
	{
		// WARNING: if only the to value is set it will be shifted to from value! same behaviour as in the hmc
		final LocalizedFeature feature = mock(LocalizedFeature.class);
		final ClassificationInfo info = mock(ClassificationInfo.class);
		when(info.isLocalized()).thenReturn(true);
		when(info.isMultiValue()).thenReturn(false);
		when(info.hasRange()).thenReturn(true);
		when(info.getValue()).thenReturn(new HashMap<>());

		BackofficeClassificationUtils.updateFeatureWithClassificationInfo(feature, info);

		verify(feature, never()).removeAllValues(any(Locale.class));
	}

	@Test
	public void testRangeIsAddedEvenOnlyStartProvided()
	{
		final LocalizedFeature feature = mock(LocalizedFeature.class);
		final ClassificationInfo info = mock(ClassificationInfo.class);
		when(info.isLocalized()).thenReturn(true);
		when(info.isMultiValue()).thenReturn(false);
		when(info.hasRange()).thenReturn(true);

		final Map<Locale, Range<FeatureValue>> rangeMap = new HashMap<>();
		final FeatureValue start = mock(FeatureValue.class);
		rangeMap.put(Locale.ENGLISH, new Range<>(start, null));
		when(info.getValue()).thenReturn(rangeMap);

		BackofficeClassificationUtils.updateFeatureWithClassificationInfo(feature, info);

		verify(feature).removeAllValues(Locale.ENGLISH);
		verify(feature).addValue(start, Locale.ENGLISH);
		verify(feature).addValue(any(FeatureValue.class), same(Locale.ENGLISH));
	}

	@Test
	public void testRangeIsAddedEvenOnlyEndProvided()
	{
		final LocalizedFeature feature = mock(LocalizedFeature.class);
		final ClassificationInfo info = mock(ClassificationInfo.class);
		when(info.isLocalized()).thenReturn(true);
		when(info.isMultiValue()).thenReturn(false);
		when(info.hasRange()).thenReturn(true);

		final Map<Locale, Range<FeatureValue>> rangeMap = new HashMap<>();
		final FeatureValue end = mock(FeatureValue.class);
		rangeMap.put(Locale.ENGLISH, new Range<>(null, end));
		when(info.getValue()).thenReturn(rangeMap);

		BackofficeClassificationUtils.updateFeatureWithClassificationInfo(feature, info);

		verify(feature).removeAllValues(Locale.ENGLISH);
		verify(feature).addValue(any(FeatureValue.class), same(Locale.ENGLISH));
		verify(feature).addValue(end, Locale.ENGLISH);
	}

	@Test
	public void shouldAddRangeEvenOneValueIsProvided()
	{
		final Feature feature = mock(Feature.class);
		final ClassificationInfo info = mock(ClassificationInfo.class);
		when(info.isLocalized()).thenReturn(false);
		when(info.isMultiValue()).thenReturn(false);
		when(info.hasRange()).thenReturn(true);

		final FeatureValue end = mock(FeatureValue.class);
		final Range<FeatureValue> range = new Range<>(null, end);
		when(info.getValue()).thenReturn(range);

		BackofficeClassificationUtils.updateFeatureWithClassificationInfo(feature, info);

		final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
		verify(feature).setValues(argumentCaptor.capture());
		assertThat(argumentCaptor.getAllValues()).hasSize(1);
		assertThat(argumentCaptor.getAllValues().get(0)).hasSize(1);
	}


	@Test
	public void testWithPropertyAccessor() throws AccessException
	{
		boolean canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("aa", "bb", "cc", "dd"));
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a", "b", "c", "d"));
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("", "b@#$ZX ssaewe b", "cc", "dd"));
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a.a", "b.b", "c.c", "d.d"));
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a.a.a", "b.b.b", "c.c.c", "d.d.d"));
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a/a/a", "b/b/b", "c/c/c", "d/d/d"));
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a/a.a", "b/b.b", "c/c.c", "d/d.d"));
		Assertions.assertThat(canRead).isTrue();

		final String code = prepare("{a}/{b}/{c}.{d}", "{a}/$$$$/{c}.{d}", "{a}/{b}/{c}.{d}", "{a}/{b}/{c}.{d}");
		canRead = classificationPropertyAccessor.canRead(null, new Object(), code);
		Assertions.assertThat(canRead).isTrue();

		canRead = classificationPropertyAccessor.canRead(null, new Object(), prepare("a{aa", "b}b", "ccc}/", "{ddd"));
		Assertions.assertThat(canRead).isTrue();

	}

	private String prepare(final String systemId, final String systemVersion, final String classificationClass,
			final String attribute)
	{
		final ClassAttributeAssignmentModel attributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		final ClassificationSystemModel classificationSystemModel = mock(ClassificationSystemModel.class);

		when(attributeAssignmentModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		when(classificationSystemVersionModel.getCatalog()).thenReturn(classificationSystemModel);
		when(classificationSystemModel.getId()).thenReturn(systemId);
		when(classificationSystemVersionModel.getVersion()).thenReturn(systemVersion);

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		when(classificationClassModel.getCode()).thenReturn(classificationClass);

		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		when(classificationAttributeModel.getCode()).thenReturn(attribute);
		when(attributeAssignmentModel.getClassificationClass()).thenReturn(classificationClassModel);
		when(attributeAssignmentModel.getClassificationAttribute()).thenReturn(classificationAttributeModel);

		final Feature feature = new UnlocalizedFeature(attributeAssignmentModel);
		return BackofficeClassificationUtils.getFeatureQualifierEncoded(feature.getClassAttributeAssignment());
	}
}
