/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.facades.impl;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;
import de.hybris.platform.platformbackoffice.services.BackofficeClassificationService;
import de.hybris.platform.platformbackoffice.widgets.compare.model.BackofficeComparisonResult;
import de.hybris.platform.platformbackoffice.widgets.compare.model.CatalogDescriptor;
import de.hybris.platform.platformbackoffice.widgets.compare.model.ClassificationDescriptor;
import de.hybris.platform.platformbackoffice.widgets.compare.model.ClassificationValuesContainer;
import de.hybris.platform.platformbackoffice.widgets.compare.model.FeatureDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.compare.impl.DefaultObjectAttributeComparator;
import com.hybris.cockpitng.compare.model.CompareAttributeDescriptor;
import com.hybris.cockpitng.compare.model.CompareLocalizedAttributeDescriptor;
import com.hybris.cockpitng.compare.model.ComparisonResult;
import com.hybris.cockpitng.compare.model.GroupDescriptor;
import com.hybris.cockpitng.compare.model.ObjectAttributesValueContainer;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.type.ObjectValueService;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationAwareBackofficeItemComparisonFacadeTest
{

	private static final String FEATURE_PREFIX = "feature_";

	private static final String ATTRIBUTE_LOCALIZED = "localizedAttribute";
	private static final String ATTRIBUTE_STRING = "stringAttribute";
	private static final String ATTRIBUTE_LIST = "listAttribute";
	private static final String ATTRIBUTE_SET = "setAttribute";
	private static final String GROUP_NAME = "BaseGroup";

	private static final String INITIAL_REFERENCE_LOCALIZED_VALUE = "englishValue";
	private static final String INITIAL_REFERENCE_STRING_VALUE = "stringValue";
	private static final List INITIAL_REFERENCE_LIST_VALUE = Arrays.asList("One", "Two", "Three");
	private static final Set INITIAL_REFERENCE_SET_ATTRIBUTE_VALUE = Sets.newSet("One", "Two", "Three");
	private static final String INITIAL_COMPARE_LOCALIZED_VALUE = "compareValue";
	private static final String INITIAL_COMPARE_STRING_VALUE = "compareString";
	private static final List INITIAL_COMPARE_LIST_VALUE = Arrays.asList("OneCompare", "TwoCompare", "ThreeCompare");
	private static final Set INITIAL_COMPARE_SET_ATTRIBUTE_VALUE = Sets.newSet("OneCompare", "TwoCompare", "ThreeCompare");

	private static final String FEATURE_VALUE_1 = "featureValue1";
	private static final String FEATURE_VALUE_2 = "featureValue2";

	private static final String FEATURE_NAME_1 = "featureName1";
	private static final String FEATURE_NAME_2 = "featureName2";

	private static final String NAME_CLASSIFICATION_1 = "classificationName1";
	private static final String CODE_CLASSIFICATION_1 = "classificationCode1";
	private static final String NAME_CLASSIFICATION_2 = "classificationName2";
	private static final String CODE_CLASSIFICATION_2 = "classificationCode2";
	private static final String CODE_CLASSIFICATION_ATTRIBUTE_1 = "classificationAttribute1";
	private static final String CODE_CLASSIFICATION_ATTRIBUTE_2 = "classificationAttribute2";

	private static final String CATALOG_ID = "id";
	private static final String CATALOG_NAME = "catalogName";
	private static final String CATALOG_VERSION = "catalogVersion";
	private static final String CLASSIFICATION_GROUP_NAME = "hmc.tab.attribute";
	private final PK id1 = PK.fromLong(1);
	private final PK id2 = PK.fromLong(2);
	private final Map<String, FeatureDescriptor> featureDescriptorsByCode = new HashMap<>();
	private final Map<TestObject, Map<String, Map<FeatureDescriptor, ClassificationInfo>>> featuresClassificationInfo = new HashMap<>();

	@InjectMocks
	@Spy
	private ClassificationAwareBackofficeItemComparisonFacade compareEngineService;
	@Mock
	private ObjectValueService objectValueService;
	@Mock
	private BackofficeClassificationService backofficeClassificationService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private PermissionFacade permissionFacade;
	private TestObject referenceObject;
	private List<TestObject> compareObjects;
	private WidgetInstanceManager widgetInstanceManager;

	@Before
	public void setUp()
	{
		final DefaultObjectAttributeComparator comparator = new DefaultObjectAttributeComparator();
		compareEngineService.setObjectAttributeComparator(comparator);
		compareEngineService.setClassificationGroupName(CLASSIFICATION_GROUP_NAME);
		widgetInstanceManager = mock(WidgetInstanceManager.class);

		referenceObject = initializeReferenceObject();
		compareObjects = initializeCompareObjects();
		when(permissionFacade.canReadInstanceProperty(any(), anyString())).thenReturn(true);

		final List<ClassificationDescriptor> classificationDescriptors = createClassificationDescriptors();
		when(backofficeClassificationService.getClassificationDescriptors(anySet())).thenReturn(classificationDescriptors);

		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> referenceFeatureValues = createFeatureValues(
				classificationDescriptors, referenceObject);
		when(backofficeClassificationService.getFeatureValues(same(referenceObject), any())).thenReturn(referenceFeatureValues);

		compareObjects.forEach(comparedObject -> {
			final Map<String, Map<FeatureDescriptor, ClassificationInfo>> comparedFeatureValues = createFeatureValues(
					classificationDescriptors, comparedObject);
			when(backofficeClassificationService.getFeatureValues(same(comparedObject), any())).thenReturn(comparedFeatureValues);
		});

		when(objectFacade.getObjectId(referenceObject)).thenReturn(id1);
	}

	@Test
	public void shouldShowNumberOfDifferentObjects()
	{
		//given
		when(objectValueService.getValue(ATTRIBUTE_STRING, referenceObject)).thenReturn(referenceObject.getStringAttribute());
		compareObjects.forEach(
				object -> when(objectValueService.getValue(ATTRIBUTE_STRING, object)).thenReturn(object.getStringAttribute()));

		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				Arrays.asList(getGroupDescriptor(Arrays.asList(getStringAttributeDescriptor()))));

		//then
		assertThat(result.isPresent()).isEqualTo(true);
		assertThat(result.get().getObjectsIdWithDifferences().size()).isEqualTo(1);
	}

	@Test
	public void shouldShowOnlyClassificationGroupDifferenceWhenCompareGroupDescriptorsAreEmpty()
	{
		//given
		final Collection<GroupDescriptor> groupDescriptors = new ArrayList<>();

		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				groupDescriptors);

		//then
		assertThat(result.isPresent()).isEqualTo(true);
		assertThat(result.get().getObjectsIdWithDifferences().size()).isEqualTo(0);
	}

	@Test
	public void shouldShowDifferencesWhenAnyAttributeIsNotEqual()
	{
		//given
		when(objectValueService.getValue(ATTRIBUTE_STRING, referenceObject)).thenReturn(referenceObject.getStringAttribute());
		compareObjects.forEach(
				object -> when(objectValueService.getValue(ATTRIBUTE_STRING, object)).thenReturn(object.getStringAttribute()));

		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				Arrays.asList(getGroupDescriptor(Arrays.asList(getStringAttributeDescriptor()))));

		//then
		assertThat(result.isPresent()).isEqualTo(true);
		final Set<String> groupWithDifferences = result.get().getGroupsWithDifferences();
		assertThat(groupWithDifferences).hasSize(1);
		assertThat(groupWithDifferences.contains(GROUP_NAME)).isEqualTo(true);
	}

	@Test
	public void shouldNotShowGroupDifferencesWhenAttributesAndFeaturesAreEqual()
	{
		//given
		when(objectValueService.getValue(ATTRIBUTE_STRING, referenceObject)).thenReturn(referenceObject.getStringAttribute());
		compareObjects.forEach(object -> when(objectValueService.getValue(ATTRIBUTE_STRING, object))
				.thenReturn(referenceObject.getStringAttribute()));


		final ClassificationInfo referenceClassificationInfo = getClassificationInfo(referenceObject, 1, CODE_CLASSIFICATION_1);
		when(referenceClassificationInfo.getValue()).thenReturn(FEATURE_VALUE_1);

		compareObjects.forEach(object -> {
			final ClassificationInfo classificationInfo = getClassificationInfo(object, 1, CODE_CLASSIFICATION_1);
			when(classificationInfo.getValue()).thenReturn(FEATURE_VALUE_1);
		});

		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				Arrays.asList(getGroupDescriptor(Arrays.asList(getStringAttributeDescriptor()))));

		//then
		assertThat(result.isPresent()).isEqualTo(true);
		final Set<String> groupsWithDifferences = result.get().getGroupsWithDifferences();

		assertThat(groupsWithDifferences).hasSize(0);
		assertThat(groupsWithDifferences.contains(GROUP_NAME)).isEqualTo(false);
		assertThat(groupsWithDifferences.contains(CLASSIFICATION_GROUP_NAME)).isEqualTo(false);
	}


	@Test
	public void shouldUseFeatureMapInsteadofFeatures()
	{
		//given
		when(objectValueService.getValue(ATTRIBUTE_STRING, referenceObject)).thenReturn(referenceObject.getStringAttribute());
		compareObjects.forEach(object -> when(objectValueService.getValue(ATTRIBUTE_STRING, object))
				.thenReturn(referenceObject.getStringAttribute()));

		final FeatureList mockFeatureList = mock(FeatureList.class);
		final Map<Object, FeatureList> featureMap = new HashMap<>();
		featureMap.put(referenceObject, mockFeatureList);
		final List<ClassificationDescriptor> classificationDescriptors = createClassificationDescriptors();
		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> referenceFeatureValues = createFeatureValues(
				classificationDescriptors, referenceObject);
		when(backofficeClassificationService.getFeatureValuesFromFeatureList(any(), eq(mockFeatureList)))
				.thenReturn(referenceFeatureValues);

		final ClassificationInfo referenceClassificationInfo = getClassificationInfo(referenceObject, 1, CODE_CLASSIFICATION_1);
		when(referenceClassificationInfo.getValue()).thenReturn(FEATURE_VALUE_1);

		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				Arrays.asList(getGroupDescriptor(Arrays.asList(getStringAttributeDescriptor()))), () -> featureMap);

		//then
		assertThat(result.isPresent()).isEqualTo(true);
		final Set<String> groupsWithDifferences = result.get().getGroupsWithDifferences();

		assertThat(groupsWithDifferences.contains(GROUP_NAME)).isEqualTo(false);
		assertThat(groupsWithDifferences.contains(CLASSIFICATION_GROUP_NAME)).isEqualTo(true);
	}

	@Test
	public void shouldShowFeatureDifferencesWhenFeaturesAreNotEqual()
	{
		//given
		final ClassificationInfo referenceClassificationInfo = getClassificationInfo(referenceObject, 1, CODE_CLASSIFICATION_1);
		when(referenceClassificationInfo.getValue()).thenReturn(FEATURE_VALUE_1);

		compareObjects.forEach(object -> {
			final ClassificationInfo classificationInfo = getClassificationInfo(object, 1, CODE_CLASSIFICATION_1);
			when(classificationInfo.getValue()).thenReturn(FEATURE_VALUE_2);
		});

		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				Arrays.asList(getGroupDescriptor(Arrays.asList(getStringAttributeDescriptor()))));

		//then
		assertThat(result.isPresent()).isEqualTo(true);
		final BackofficeComparisonResult backofficeComparisonResult = (BackofficeComparisonResult) result.get();
		final Set<String> groupsWithDifferences = backofficeComparisonResult.getGroupsWithDifferences();

		assertThat(groupsWithDifferences).hasSize(1);
		assertThat(groupsWithDifferences.contains(GROUP_NAME)).isEqualTo(false);
		assertThat(groupsWithDifferences.contains(CLASSIFICATION_GROUP_NAME)).isEqualTo(true);

		final Set<String> differentClassificationCodes = backofficeComparisonResult.getDifferentClassifications().stream()
				.map(ClassificationDescriptor::getCode).collect(Collectors.toSet());
		assertThat(differentClassificationCodes).hasSize(1);
		assertThat(differentClassificationCodes).contains(CODE_CLASSIFICATION_1);

		final FeatureDescriptor featureDescriptor = featureDescriptorsByCode.get(createFeatureCode(1, CODE_CLASSIFICATION_1));
		final Collection<ClassificationValuesContainer> differentObjectValues = backofficeComparisonResult
				.getDifferentObjectsForFeatures().get(featureDescriptor);
		final Set<Object> differentObjectsForFeature = differentObjectValues.stream()
				.map(ClassificationValuesContainer::getObjectId).collect(Collectors.toSet());
		final Set<Object> compareObjectsIds = compareObjects.stream().map(object -> objectFacade.getObjectId(object))
				.collect(Collectors.toSet());
		assertThat(differentObjectsForFeature).containsAll(compareObjectsIds);
	}

	@Test
	public void shouldShowDifferencesWhenStringAttributesAreDifferent()
	{
		final TestObject compareObject = compareObjects.get(0);

		//given
		when(objectValueService.getValue(ATTRIBUTE_STRING, referenceObject)).thenReturn(referenceObject.getStringAttribute());
		when(objectValueService.getValue(ATTRIBUTE_STRING, compareObject)).thenReturn(compareObject.getStringAttribute());

		//when and then
		verifyAttributeDifferences(true, getStringAttributeDescriptor());
	}

	@Test
	public void shouldNotShowDifferencesWhenStringAttributesAreEqual()
	{
		final TestObject compareObject = compareObjects.get(0);

		//given
		when(objectValueService.getValue(ATTRIBUTE_STRING, referenceObject)).thenReturn(referenceObject.getStringAttribute());
		when(objectValueService.getValue(ATTRIBUTE_STRING, compareObject)).thenReturn(referenceObject.getStringAttribute());

		//when then

		verifyAttributeDifferences(false, getStringAttributeDescriptor());
	}

	@Test
	public void shouldNotShowDifferencesWhenLocalizedAttributesAreNotEmptyAndEqual()
	{
		final TestObject compareObject = compareObjects.get(0);

		//given
		when(objectValueService.getValue(ATTRIBUTE_LOCALIZED, referenceObject)).thenReturn(referenceObject.getLocalizeAttribute());
		when(objectValueService.getValue(ATTRIBUTE_LOCALIZED, compareObject)).thenReturn(referenceObject.getLocalizeAttribute());

		//when and then

		verifyAttributeDifferences(false, getLocalizedAttributeDescriptor());
	}

	@Test
	public void shouldShowDifferencesWhenLocalizedAttributeForCompareObjectIsEmpty()
	{
		final TestObject compareObject = compareObjects.get(0);
		compareObject.getLocalizeAttribute().put(Locale.ENGLISH, null);

		shouldShowDifferencesWhenOneOfObjectHasNullLocalizedAttribute(referenceObject, compareObject);
	}

	@Test
	public void shouldShowDifferencesWhenLocalizedAttributeForReferenceObjectIsEmpty()
	{
		final TestObject compareObject = compareObjects.get(0);
		referenceObject.getLocalizeAttribute().put(Locale.ENGLISH, null);

		shouldShowDifferencesWhenOneOfObjectHasNullLocalizedAttribute(referenceObject, compareObject);
	}

	@Test
	public void shouldShowDifferenceWhenListAttributesAreNotEqual()
	{
		final TestObject compareObject = compareObjects.get(0);

		//given
		when(objectValueService.getValue(ATTRIBUTE_LIST, referenceObject)).thenReturn(referenceObject.getListAttribute());
		when(objectValueService.getValue(ATTRIBUTE_LIST, compareObject)).thenReturn(compareObject.getListAttribute());

		verifyAttributeDifferences(true, getListAttributeDescriptor());
	}

	@Test
	public void shouldShowDifferencesWhenElementsInListHaveDifferentOrder()
	{
		final TestObject compareObject = compareObjects.get(0);
		referenceObject.setListAttribute(Arrays.asList("Two", "One"));
		compareObject.setListAttribute(Arrays.asList("One", "Two"));

		//when
		when(objectValueService.getValue(ATTRIBUTE_LIST, referenceObject)).thenReturn(referenceObject.getListAttribute());
		when(objectValueService.getValue(ATTRIBUTE_LIST, compareObject)).thenReturn(compareObject.getListAttribute());

		//then and verify
		verifyAttributeDifferences(true, getListAttributeDescriptor());
	}

	@Test
	public void shouldShowDifferencesWhenSetsAreDifferent()
	{
		final TestObject compareObject = compareObjects.get(0);

		//when
		when(objectValueService.getValue(ATTRIBUTE_SET, referenceObject)).thenReturn(referenceObject.getListAttribute());
		when(objectValueService.getValue(ATTRIBUTE_SET, compareObject)).thenReturn(compareObject.getListAttribute());

		//then and verify
		verifyAttributeDifferences(true, getSetAttributeDescriptor());
	}

	@Test
	public void shouldNotShowDifferencesWhenSetsAreEqual()
	{
		final TestObject compareObject = compareObjects.get(0);

		//when
		when(objectValueService.getValue(ATTRIBUTE_SET, referenceObject)).thenReturn(referenceObject.getListAttribute());
		when(objectValueService.getValue(ATTRIBUTE_SET, compareObject)).thenReturn(referenceObject.getListAttribute());

		//then and verify
		verifyAttributeDifferences(false, getSetAttributeDescriptor());
	}


	private void shouldShowDifferencesWhenOneOfObjectHasNullLocalizedAttribute(final TestObject referenceObject,
			final TestObject compareObject)
	{
		//given
		when(objectValueService.getValue(ATTRIBUTE_LOCALIZED, referenceObject)).thenReturn(referenceObject.getLocalizeAttribute());
		when(objectValueService.getValue(ATTRIBUTE_LOCALIZED, compareObject)).thenReturn(compareObject.getLocalizeAttribute());

		//when and then
		verifyAttributeDifferences(true, getLocalizedAttributeDescriptor());
	}

	private void verifyAttributeDifferences(final boolean isDifference, final CompareAttributeDescriptor attributeToCompare)
	{
		//when
		final Optional<ComparisonResult> result = compareEngineService.getCompareViewResult(referenceObject, compareObjects,
				Arrays.asList(getGroupDescriptor(Arrays.asList(attributeToCompare))));

		//then
		assertThat(result.isPresent()).isEqualTo(true);

		final Collection<ObjectAttributesValueContainer> objectValuesWithDifferences = result.get()
				.getObjectsIdWithDifferences(attributeToCompare);
		final Collection<ObjectAttributesValueContainer> differentObjects = result.get()
				.getObjectsIdWithDifferences(attributeToCompare);

		if (isDifference)
		{
			assertThat(objectValuesWithDifferences).isNotEmpty();
			assertThat(differentObjects).isNotEmpty();
			differentObjects.stream().map(object -> object.getObjectId()).collect(Collectors.toSet()).contains(compareObjects);

		}
		else
		{
			assertThat(objectValuesWithDifferences).isEmpty();
			assertThat(differentObjects).isEmpty();
		}
	}

	private GroupDescriptor getGroupDescriptor(final List<CompareAttributeDescriptor> attributeDescriptors)
	{
		return new GroupDescriptor(GROUP_NAME, attributeDescriptors);
	}

	private CompareAttributeDescriptor getStringAttributeDescriptor()
	{
		return new CompareAttributeDescriptor(ATTRIBUTE_STRING, GROUP_NAME);

	}

	private CompareAttributeDescriptor getLocalizedAttributeDescriptor()
	{
		return new CompareLocalizedAttributeDescriptor(ATTRIBUTE_LOCALIZED, GROUP_NAME, Locale.ENGLISH);
	}

	private CompareAttributeDescriptor getListAttributeDescriptor()
	{
		return new CompareAttributeDescriptor(ATTRIBUTE_LIST, GROUP_NAME);
	}

	private CompareAttributeDescriptor getSetAttributeDescriptor()
	{
		return new CompareAttributeDescriptor(ATTRIBUTE_SET, GROUP_NAME);
	}

	private List<ClassificationDescriptor> createClassificationDescriptors()
	{
		final CatalogDescriptor catalogDescriptor = createCatalogDescriptor(true, true);
		final ClassificationDescriptor classificationDescriptor1 = new ClassificationDescriptor(CODE_CLASSIFICATION_1,
				NAME_CLASSIFICATION_1, catalogDescriptor,
				createFeatureDescriptors(2, CODE_CLASSIFICATION_1, FEATURE_NAME_1, CODE_CLASSIFICATION_ATTRIBUTE_1));
		final ClassificationDescriptor classificationDescriptor2 = new ClassificationDescriptor(CODE_CLASSIFICATION_2,
				NAME_CLASSIFICATION_2, catalogDescriptor,
				createFeatureDescriptors(1, CODE_CLASSIFICATION_2, FEATURE_NAME_2, CODE_CLASSIFICATION_ATTRIBUTE_2));

		return Arrays.asList(classificationDescriptor1, classificationDescriptor2);
	}

	private CatalogDescriptor createCatalogDescriptor(final boolean canRead, final boolean canWrite)
	{
		return new CatalogDescriptor(CATALOG_ID, CATALOG_NAME, CATALOG_VERSION, canRead, canWrite);
	}

	private final Set<FeatureDescriptor> createFeatureDescriptors(final int featuresNumber, final String classificationCode,
			final String name, final String attributeCode)
	{
		final Set<FeatureDescriptor> featureDescriptors = new HashSet<>();
		for (int i = 0; i < featuresNumber; i++)
		{
			final int featureIndex = i + 1;
			final String featureCode = createFeatureCode(featureIndex, classificationCode);
			final String featureName = createFeatureName(featureIndex, name);
			final String classificationAttributeCode = createClassificationAttributeCode(featureIndex, attributeCode);

			final FeatureDescriptor featureDescriptor = new FeatureDescriptor(featureCode, classificationCode, featureName,
					classificationAttributeCode);
			featureDescriptor.setCanWrite(true);
			featureDescriptor.setCanRead(true);
			featureDescriptors.add(featureDescriptor);

			featureDescriptorsByCode.put(featureCode, featureDescriptor);
		}
		return featureDescriptors;
	}

	private final String createFeatureCode(final int index, final String classificationCode)
	{
		return FEATURE_PREFIX + classificationCode + "_" + index;
	}

	private final String createFeatureName(final int index, final String name)
	{
		return FEATURE_PREFIX + name + "_" + index;
	}

	private final String createClassificationAttributeCode(final int index, final String classificationAttributeCode)
	{
		return FEATURE_PREFIX + classificationAttributeCode + "_" + index;
	}

	private Map<String, Map<FeatureDescriptor, ClassificationInfo>> createFeatureValues(
			final List<ClassificationDescriptor> classificationDescriptors, final TestObject object)
	{
		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> featureValuesByClassification = new HashMap<>();
		classificationDescriptors.forEach(classificationDescriptor -> {
			final Map<FeatureDescriptor, ClassificationInfo> featureValues = new HashMap<>();
			classificationDescriptor.getFeatures().forEach(featureDescriptor -> {
				final ClassificationInfo classificationInfo = createClassificationInfo();
				featureValues.put(featureDescriptor, classificationInfo);
			});
			featureValuesByClassification.put(classificationDescriptor.getCode(), featureValues);
		});
		featuresClassificationInfo.put(object, featureValuesByClassification);
		return featureValuesByClassification;
	}

	private ClassificationInfo createClassificationInfo()
	{
		final ClassificationInfo classificationInfo = mock(ClassificationInfo.class);
		when(classificationInfo.getValue()).thenReturn("");

		return classificationInfo;
	}

	private TestObject initializeReferenceObject()
	{
		final TestObject testReferenceObject = new TestObject();
		testReferenceObject.setStringAttribute(INITIAL_REFERENCE_STRING_VALUE);

		final Map<Locale, String> localeStringMap = new HashMap<>();
		localeStringMap.put(Locale.ENGLISH, INITIAL_REFERENCE_LOCALIZED_VALUE);

		testReferenceObject.setLocalizeAttribute(localeStringMap);
		testReferenceObject.setListAttribute(INITIAL_REFERENCE_LIST_VALUE);
		testReferenceObject.setSetAttribute(INITIAL_REFERENCE_SET_ATTRIBUTE_VALUE);

		return testReferenceObject;
	}

	private List<TestObject> initializeCompareObjects()
	{
		final TestObject testObject1 = new TestObject();
		testObject1.setStringAttribute(INITIAL_COMPARE_STRING_VALUE);

		final Map<Locale, String> localeStringMap = new HashMap<>();
		localeStringMap.put(Locale.ENGLISH, INITIAL_COMPARE_LOCALIZED_VALUE);
		testObject1.setLocalizeAttribute(localeStringMap);

		testObject1.setListAttribute(INITIAL_COMPARE_LIST_VALUE);
		testObject1.setSetAttribute(INITIAL_COMPARE_SET_ATTRIBUTE_VALUE);

		final List<TestObject> testCompareObjects = new ArrayList();
		testCompareObjects.add(testObject1);

		when(objectFacade.getObjectId(testObject1)).thenReturn(id2);

		return testCompareObjects;
	}

	private ClassificationInfo getClassificationInfo(final TestObject object, final int featureDescriptorIndex,
			final String classificationCode)
	{
		final String featureCode = createFeatureCode(featureDescriptorIndex, classificationCode);
		return featuresClassificationInfo.get(object).get(classificationCode).get(featureDescriptorsByCode.get(featureCode));
	}

	private static class TestObject extends ProductModel
	{
		private String stringAttribute;
		private Map<Locale, String> localizeAttribute;
		private List listAttribute;
		private Set setAttribute;

		public String getStringAttribute()
		{
			return stringAttribute;
		}

		public void setStringAttribute(final String stringAttribute)
		{
			this.stringAttribute = stringAttribute;
		}

		public Map<Locale, String> getLocalizeAttribute()
		{
			return localizeAttribute;
		}

		public void setLocalizeAttribute(final Map<Locale, String> localizeAttribute)
		{
			this.localizeAttribute = localizeAttribute;
		}

		public List getListAttribute()
		{
			return listAttribute;
		}

		public void setListAttribute(final List listAttribute)
		{
			this.listAttribute = listAttribute;
		}

		public Set getSetAttribute()
		{
			return setAttribute;
		}

		public void setSetAttribute(final Set setAttribute)
		{
			this.setAttribute = setAttribute;
		}

	}


}
