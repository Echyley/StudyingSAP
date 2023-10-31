/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.assertj.core.util.Lists;
import org.junit.Test;

import com.hybris.cockpitng.compare.model.CompareAttributeDescriptor;
import com.hybris.cockpitng.compare.model.GroupDescriptor;
import com.hybris.cockpitng.compare.model.ObjectAttributesValueContainer;


public class BackofficeComparisonResultTest
{

	private static final String NAME = "name";
	private static final String UPDATED_NAME = "updated name";
	private static final String NAME_QUALIFIER = "name qualifier";
	private static final String OWNER_QUALIFIER = "owner qualifier";
	private static final String OWNER = "owner";
	private static final String GROUP_NAME = "group name";
	private static final String NAME_CLASSIFICATION_1 = "classificationName1";
	private static final String CODE_CLASSIFICATION_1 = "classificationCode1";
	private static final String NAME_CLASSIFICATION_2 = "classificationName2";
	private static final String CODE_CLASSIFICATION_2 = "classificationCode2";
	private static final String CATALOG_NAME = "catalogName";
	private static final String CATALOG_VERSION = "catalogVersion";
	private static final String CATALOG_ID = "id";
	private static final String CLASSIFICATION_GROUP_NAME = "classificationGroupName";


	@Test
	public void shouldMergeTwoResults()
	{
		//given
		final TestObject referenceObject = new TestObject(1, NAME, null);
		final TestObject comparedObject = new TestObject(2, NAME, null);
		final TestObject comparedObject2 = new TestObject(3, NAME, OWNER);

		final BackofficeComparisonResult comparisonResult = new BackofficeComparisonResult(referenceObject,
				createDifferencesForObject(comparedObject, Collections.singletonList(NAME_QUALIFIER)), createGroupsDescriptors(),
				createFeatureDifferencesForObject(comparedObject, createFeatureDescriptors(3, CODE_CLASSIFICATION_1)),
				createClassificationGroupDescriptors());

		final BackofficeComparisonResult partialComparisonResult = new BackofficeComparisonResult(referenceObject,
				createDifferencesForObject(comparedObject2, Collections.singletonList(OWNER_QUALIFIER)), createGroupsDescriptors(),
				Collections.emptyMap(), Collections.emptySet());

		//when
		comparisonResult.merge(partialComparisonResult, comparedObject2);

		//then
		assertThat(comparisonResult.getAttributesWithDifferences().size(), equalTo(2));
		assertThat(comparisonResult.getGroupsWithDifferences().size(), equalTo(2));
		assertThat(comparisonResult.getObjectsWithDifferences().size(), equalTo(2));
		assertThat(comparisonResult.getDifferentClassifications().size(), equalTo(1));
		assertThat(comparisonResult.getDifferentObjectsForFeatures().size(), equalTo(3));
	}

	@Test
	public void shouldContainOneDifferenceAfterMergingTwoTimesTheSameResult()
	{
		//given
		final TestObject referenceObject = new TestObject(1, NAME, null);
		final TestObject comparedObject = new TestObject(2, NAME, null);

		final BackofficeComparisonResult comparisonResult = new BackofficeComparisonResult(referenceObject, getEmptyDifferences(),
				createGroupsDescriptors(), Collections.emptyMap(), createClassificationGroupDescriptors());

		final BackofficeComparisonResult partialComparisonResult = new BackofficeComparisonResult(referenceObject,
				createDifferencesForObject(comparedObject, Collections.singletonList(NAME_QUALIFIER)), createGroupsDescriptors(),
				createFeatureDifferencesForObject(comparedObject, createFeatureDescriptors(1, CODE_CLASSIFICATION_1)),
				createClassificationGroupDescriptors());

		//when
		comparisonResult.merge(partialComparisonResult, comparedObject);
		comparisonResult.merge(partialComparisonResult, comparedObject);

		//then
		assertThat(comparisonResult.getAttributesWithDifferences().size(), equalTo(1));
		assertThat(comparisonResult.getGroupsWithDifferences().size(), equalTo(2));
		assertThat(comparisonResult.getObjectsWithDifferences().size(), equalTo(1));
		assertThat(comparisonResult.getDifferentClassifications().size(), equalTo(1));
		assertThat(comparisonResult.getDifferentObjectsForFeatures().size(), equalTo(1));
	}

	@Test
	public void shouldContainUpdatedDifference()
	{
		//given
		final TestObject referenceObject = new TestObject(1, NAME, null);
		final TestObject comparedObject = new TestObject(2, NAME, null);
		final TestObject updatedObject = new TestObject(2, UPDATED_NAME, null);

		final BackofficeComparisonResult comparisonResult = new BackofficeComparisonResult(referenceObject, getEmptyDifferences(),
				createGroupsDescriptors(), Collections.emptyMap(), createClassificationGroupDescriptors());

		final BackofficeComparisonResult partialComparisonResult = new BackofficeComparisonResult(referenceObject,
				createDifferencesForObject(comparedObject, Collections.singletonList(NAME_QUALIFIER)), createGroupsDescriptors(),
				createFeatureDifferencesForObject(comparedObject, createFeatureDescriptors(1, CODE_CLASSIFICATION_1)),
				createClassificationGroupDescriptors());

		final BackofficeComparisonResult partialComparisonResult2 = new BackofficeComparisonResult(referenceObject,
				createDifferencesForObject(updatedObject, Collections.singletonList(NAME_QUALIFIER)), createGroupsDescriptors(),
				createFeatureDifferencesForObject(updatedObject, createFeatureDescriptors(1, CODE_CLASSIFICATION_1)),
				createClassificationGroupDescriptors());

		//when
		comparisonResult.merge(partialComparisonResult, comparedObject);
		comparisonResult.merge(partialComparisonResult2, updatedObject);

		//then
		assertThat(comparisonResult.getAttributesWithDifferences().size(), equalTo(1));
		assertThat(comparisonResult.getGroupsWithDifferences().size(), equalTo(2));
		assertThat(comparisonResult.getObjectsWithDifferences().size(), equalTo(1));
		assertThat(comparisonResult.getDifferentClassifications().size(), equalTo(1));
		assertThat(comparisonResult.getDifferentObjectsForFeatures().size(), equalTo(1));

		final Object object = comparisonResult.getObjectsWithDifferences().iterator().next();
		assertThat(object, equalTo(updatedObject));
	}

	@Test
	public void shouldRemoveDifferencesWhenLastAttributeDifferenceRemoved()
	{
		//given
		final TestObject referenceObject = new TestObject(1, NAME, null);
		final TestObject comparedObject = new TestObject(2, NAME, null);
		final TestObject updatedObject = new TestObject(2, UPDATED_NAME, null);

		final BackofficeComparisonResult comparisonResult = new BackofficeComparisonResult(referenceObject, getEmptyDifferences(),
				createGroupsDescriptors(), Collections.emptyMap(), createClassificationGroupDescriptors());

		final BackofficeComparisonResult partialComparisonResult = new BackofficeComparisonResult(referenceObject,
				getEmptyDifferences(), createGroupsDescriptors(),
				createFeatureDifferencesForObject(comparedObject, createFeatureDescriptors(1, CODE_CLASSIFICATION_1)),
				createClassificationGroupDescriptors());

		final BackofficeComparisonResult partialComparisonResult2 = new BackofficeComparisonResult(referenceObject,
				getEmptyDifferences(), createGroupsDescriptors(), Collections.emptyMap(), createClassificationGroupDescriptors());

		//when
		comparisonResult.merge(partialComparisonResult, comparedObject);
		comparisonResult.merge(partialComparisonResult2, updatedObject);

		//then
		assertThat(comparisonResult.getAttributesWithDifferences().size(), equalTo(0));
		assertThat(comparisonResult.getGroupsWithDifferences().size(), equalTo(0));
		assertThat(comparisonResult.getObjectsWithDifferences().size(), equalTo(0));
		assertThat(comparisonResult.getDifferentClassifications().size(), equalTo(0));
		assertThat(comparisonResult.getDifferentObjectsForFeatures().size(), equalTo(0));
	}

	private Map<ObjectAttributesValueContainer, Set<CompareAttributeDescriptor>> getEmptyDifferences()
	{
		return new HashMap<>();
	}

	private Set<GroupDescriptor> createGroupsDescriptors()
	{
		final GroupDescriptor group = createGroupDescriptor(GROUP_NAME, Arrays.asList(NAME_QUALIFIER, OWNER_QUALIFIER));

		final Set<GroupDescriptor> groups = new HashSet<>();
		groups.add(group);

		return groups;
	}

	private GroupDescriptor createGroupDescriptor(final String groupName, final Collection<String> attributeQualifier)
	{
		final List<CompareAttributeDescriptor> attributeDescriptors = attributeQualifier.stream()
				.map(CompareAttributeDescriptor::new).collect(Collectors.toList());

		return new GroupDescriptor(groupName, attributeDescriptors);
	}

	private Map<ObjectAttributesValueContainer, Set<CompareAttributeDescriptor>> createDifferencesForObject(final Object object,
			final Collection<String> attributeQualifiers)
	{
		return createDifferencesForObject(object, attributeQualifiers, new HashMap<>());
	}

	private Map<ObjectAttributesValueContainer, Set<CompareAttributeDescriptor>> createDifferencesForObject(final Object object,
			final Collection<String> attributeQualifiers,
			final Map<ObjectAttributesValueContainer, Set<CompareAttributeDescriptor>> differences)
	{
		final ObjectAttributesValueContainer valueContainer = new ObjectAttributesValueContainer(object);
		final Set<CompareAttributeDescriptor> attributeDescriptors = attributeQualifiers.stream()
				.map(CompareAttributeDescriptor::new).collect(Collectors.toSet());

		differences.put(valueContainer, attributeDescriptors);
		return differences;
	}

	private Map<ClassificationValuesContainer, Set<FeatureDescriptor>> createFeatureDifferencesForObject(final Object object,
			final Set<FeatureDescriptor> featureDifferences)
	{
		final ClassificationValuesContainer classificationValuesContainer = new ClassificationValuesContainer(object);
		final Map<ClassificationValuesContainer, Set<FeatureDescriptor>> differences = new HashMap<>();
		differences.put(classificationValuesContainer, featureDifferences);
		return differences;
	}

	private Set<ClassificationGroupDescriptor> createClassificationGroupDescriptors()
	{
		return Collections.singleton(createClassificationGroupDescriptor());
	}

	private ClassificationGroupDescriptor createClassificationGroupDescriptor()
	{

		final CatalogDescriptor catalogDescriptor = createCatalogDescriptor(true, true);
		final ClassificationDescriptor classificationDescriptor1 = new ClassificationDescriptor(CODE_CLASSIFICATION_1,
				NAME_CLASSIFICATION_1, catalogDescriptor, createFeatureDescriptors(3, CODE_CLASSIFICATION_1));
		final ClassificationDescriptor classificationDescriptor2 = new ClassificationDescriptor(CODE_CLASSIFICATION_2,
				NAME_CLASSIFICATION_2, catalogDescriptor, createFeatureDescriptors(4, CODE_CLASSIFICATION_2));

		final ClassificationGroupDescriptor classificationGroupDescriptor = new ClassificationGroupDescriptor(
				CLASSIFICATION_GROUP_NAME, Lists.emptyList(), Arrays.asList(classificationDescriptor1, classificationDescriptor2));

		return classificationGroupDescriptor;
	}

	private CatalogDescriptor createCatalogDescriptor(final boolean canRead, final boolean canWrite)
	{
		return new CatalogDescriptor(CATALOG_ID, CATALOG_NAME, CATALOG_VERSION, canRead, canWrite);
	}

	private final Set<FeatureDescriptor> createFeatureDescriptors(final int featuresNumber, final String classificationCode)
	{
		final Set<FeatureDescriptor> featureDescriptors = new HashSet<>();
		for (int i = 0; i < featuresNumber; i++)
		{
			final int featureIndex = i + 1;
			final FeatureDescriptor featureDescriptor = new FeatureDescriptor("feature" + featureIndex, classificationCode,
					"name" + featureIndex, "attributeCode" + featureIndex);
			featureDescriptor.setCanWrite(true);
			featureDescriptor.setCanRead(true);
			featureDescriptors.add(featureDescriptor);
		}
		return featureDescriptors;
	}

	private class TestObject
	{
		private final int id;
		private final String name;
		private final String owner;

		public TestObject(final int id, final String name, final String owner)
		{
			this.id = id;
			this.name = name;
			this.owner = owner;
		}

		public int getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public String getOwner()
		{
			return owner;
		}

		@Override
		public boolean equals(final Object object)
		{
			if (object == null || !(object.getClass().equals(this.getClass())))
			{
				return false;
			}
			return this.id == ((TestObject) object).getId();
		}

		@Override
		public int hashCode()
		{
			return new HashCodeBuilder().append(getId()).toHashCode();
		}
	}


}
