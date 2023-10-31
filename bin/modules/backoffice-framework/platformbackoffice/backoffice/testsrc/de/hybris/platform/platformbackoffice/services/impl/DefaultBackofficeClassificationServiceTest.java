/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.impl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogVersionService;
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
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;
import de.hybris.platform.platformbackoffice.widgets.compare.model.CatalogDescriptor;
import de.hybris.platform.platformbackoffice.widgets.compare.model.ClassificationDescriptor;
import de.hybris.platform.platformbackoffice.widgets.compare.model.FeatureDescriptor;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeClassificationServiceTest
{


	private static final String FEATURE_CODE_1 = "featureCode1";
	private static final String FEATURE_CODE_2 = "featureCode2";
	private static final String FEATURE_CODE_3 = "featureCode3";
	private static final String CLASSIFICATION_CLASS_CODE_1 = "classificationClassCode1";
	private static final String CLASSIFICATION_CLASS_CODE_2 = "classificationClassCode2";
	private static final String CLASSIFICATION_CLASS_NAME_1 = "classificationClassName1";
	private static final String CLASSIFICATION_CLASS_NAME_2 = "classificationClassName2";
	private static final String CLASSIFICATION_ATTRIBUTE_CODE_1 = "classificationAttributeCode1";
	private static final String CLASSIFICATION_ATTRIBUTE_CODE_2 = "classificationAttributeCode2";
	private static final String CATALOG_VERSION_VERSION = "catalogVersionVersion";
	private static final String CATALOG_NAME = "catalogName";
	private static final String CATALOG_ID = "catalogId";

	@Spy
	@InjectMocks
	private DefaultBackofficeClassificationService backofficeClassificationService;

	@Mock
	private ClassificationService classificationService;

	@Mock
	private UserService userService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private UserModel currentUser;

	@Mock
	private FeatureList featureList;

	@Mock
	private ProductModel product;

	@Mock
	private ClassificationClassModel classificationClass1;

	@Mock
	private ClassificationClassModel classificationClass2;

	@Mock
	private ClassificationAttributeModel classificationAttribute1;

	@Mock
	private ClassificationAttributeModel classificationAttribute2;

	@Mock
	private ClassificationSystemVersionModel catalogVersion;

	@Mock
	private ClassificationSystemModel catalog;

	@Mock
	private ClassAttributeAssignmentModel classAttributeAssignment1;

	@Mock
	private ClassAttributeAssignmentModel classAttributeAssignment2;


	@Before
	public void setUp()
	{
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(catalogVersionService.canRead(catalogVersion, currentUser)).thenReturn(true);
		when(catalogVersionService.canWrite(catalogVersion, currentUser)).thenReturn(true);
		when(classificationService.getFeatures(product)).thenReturn(featureList);

		when(featureList.getClassificationClasses()).thenReturn(Collections.set(classificationClass1, classificationClass2));

		when(classAttributeAssignment1.getClassificationClass()).thenReturn(classificationClass1);
		when(classAttributeAssignment1.getClassificationAttribute()).thenReturn(classificationAttribute1);

		when(classAttributeAssignment2.getClassificationClass()).thenReturn(classificationClass2);
		when(classAttributeAssignment2.getClassificationAttribute()).thenReturn(classificationAttribute2);

		when(classificationClass1.getCatalogVersion()).thenReturn(catalogVersion);
		when(classificationClass1.getCode()).thenReturn(CLASSIFICATION_CLASS_CODE_1);
		when(classificationClass1.getName()).thenReturn(CLASSIFICATION_CLASS_NAME_1);

		when(classificationClass2.getCatalogVersion()).thenReturn(catalogVersion);
		when(classificationClass2.getCode()).thenReturn(CLASSIFICATION_CLASS_CODE_2);
		when(classificationClass2.getName()).thenReturn(CLASSIFICATION_CLASS_NAME_2);

		when(classificationAttribute1.getCode()).thenReturn(CLASSIFICATION_ATTRIBUTE_CODE_1);
		when(classificationAttribute2.getCode()).thenReturn(CLASSIFICATION_ATTRIBUTE_CODE_2);

		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION_VERSION);
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalog.getName()).thenReturn(CATALOG_NAME);
		when(catalog.getId()).thenReturn(CATALOG_ID);
	}

	@Test
	public void shouldGetClassificationDescriptorsForProducts()
	{
		//given
		final Feature featureMock1 = mock(Feature.class);
		mockFeatureWithValue(featureMock1, null, FEATURE_CODE_1, classAttributeAssignment1);

		final Feature featureMock2 = mock(Feature.class);
		mockFeatureWithValue(featureMock2, null, FEATURE_CODE_2, classAttributeAssignment2);

		final Feature featureMock3 = mock(Feature.class);
		mockFeatureWithValue(featureMock3, null, FEATURE_CODE_3, classAttributeAssignment2);

		when(featureList.getFeatures()).thenReturn(Collections.list(featureMock1, featureMock2, featureMock3));

		//when
		final List<ClassificationDescriptor> classificationDescriptors = backofficeClassificationService
				.getClassificationDescriptors(getProducts());

		//then
		assertThat(classificationDescriptors).hasSize(2);

		final ClassificationDescriptor classificationDescriptor1 = classificationDescriptors.get(0);
		assertThat(classificationDescriptor1.getName()).isEqualTo(CLASSIFICATION_CLASS_NAME_1);
		assertThat(classificationDescriptor1.getCode()).isEqualTo(CLASSIFICATION_CLASS_CODE_1);

		final CatalogDescriptor catalogDescriptor1 = classificationDescriptor1.getCatalogDescriptor();
		assertThat(catalogDescriptor1.getId()).isEqualTo(CATALOG_ID);
		assertThat(catalogDescriptor1.getName()).isEqualTo(CATALOG_NAME);
		assertThat(catalogDescriptor1.getVersion()).isEqualTo(CATALOG_VERSION_VERSION);

		assertThat(classificationDescriptor1.getFeatures()).hasSize(1);

		final FeatureDescriptor feature1 = classificationDescriptor1.getFeatures().iterator().next();
		assertThat(feature1.getCode()).isEqualTo(FEATURE_CODE_1);
		assertThat(feature1.getClassificationCode()).isEqualTo(CLASSIFICATION_CLASS_CODE_1);

		final ClassificationDescriptor classificationDescriptor2 = classificationDescriptors.get(1);
		assertThat(classificationDescriptor2.getName()).isEqualTo(CLASSIFICATION_CLASS_NAME_2);
		assertThat(classificationDescriptor2.getCode()).isEqualTo(CLASSIFICATION_CLASS_CODE_2);

		assertThat(classificationDescriptor2.getFeatures()).hasSize(2);

		final boolean isFeature2Present = classificationDescriptor2.getFeatures().stream()
				.anyMatch(featureDescriptor -> featureDescriptor.getCode().equals(FEATURE_CODE_2)
						&& featureDescriptor.getClassificationCode().equals(CLASSIFICATION_CLASS_CODE_2));
		assertThat(isFeature2Present).isTrue();

		final boolean isFeature3Present = classificationDescriptor2.getFeatures().stream()
				.anyMatch(featureDescriptor -> featureDescriptor.getCode().equals(FEATURE_CODE_3)
						&& featureDescriptor.getClassificationCode().equals(CLASSIFICATION_CLASS_CODE_2));
		assertThat(isFeature3Present).isTrue();
	}

	@Test
	public void shouldGetFeatureValues()
	{
		//given
		when(classAttributeAssignment1.getLocalized()).thenReturn(false);
		when(classAttributeAssignment1.getRange()).thenReturn(false);
		when(classAttributeAssignment1.getMultiValued()).thenReturn(false);

		final FeatureValue featureValue1 = mock(FeatureValue.class);
		final Feature featureMock1 = mock(Feature.class);
		mockFeatureWithValue(featureMock1, featureValue1, FEATURE_CODE_1, classAttributeAssignment1);

		final FeatureValue featureValue2 = mock(FeatureValue.class);
		final Feature featureMock2 = mock(Feature.class);
		mockFeatureWithValue(featureMock2, featureValue2, FEATURE_CODE_2, classAttributeAssignment1);

		final FeatureValue featureValue3 = mock(FeatureValue.class);
		final Feature featureMock3 = mock(Feature.class);
		mockFeatureWithValue(featureMock3, featureValue3, FEATURE_CODE_3, classAttributeAssignment1);

		final FeatureDescriptor featureDescriptor1 = createFeatureDescriptor(FEATURE_CODE_1);
		final FeatureDescriptor featureDescriptor2 = createFeatureDescriptor(FEATURE_CODE_2);
		final FeatureDescriptor featureDescriptor3 = createFeatureDescriptor(FEATURE_CODE_3);

		//when
		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> featureValues = backofficeClassificationService
				.getFeatureValues(product, getClassificationDescriptors(featureDescriptor1, featureDescriptor2, featureDescriptor3));

		//then
		assertThat(featureValues).hasSize(2);

		final Map<FeatureDescriptor, ClassificationInfo> featureValuesForClassification1 = featureValues
				.get(CLASSIFICATION_CLASS_CODE_1);
		assertThat(featureValuesForClassification1).hasSize(2);
		final ClassificationInfo classificationInfo1 = featureValuesForClassification1.get(featureDescriptor1);
		assertThat(classificationInfo1.getValue()).isSameAs(featureValue1);
		final ClassificationInfo classificationInfo2 = featureValuesForClassification1.get(featureDescriptor2);
		assertThat(classificationInfo2.getValue()).isSameAs(featureValue2);


		final Map<FeatureDescriptor, ClassificationInfo> featureValuesForClassification2 = featureValues
				.get(CLASSIFICATION_CLASS_CODE_2);
		assertThat(featureValuesForClassification2).hasSize(1);

		final ClassificationInfo classificationInfo3 = featureValuesForClassification2.get(featureDescriptor3);
		assertThat(classificationInfo3.getValue()).isSameAs(featureValue3);
	}

	private FeatureDescriptor createFeatureDescriptor(final String featureCode)
	{
		final FeatureDescriptor featureDescriptor = mock(FeatureDescriptor.class);
		when(featureDescriptor.getCode()).thenReturn(featureCode);
		return featureDescriptor;
	}

	private void mockFeatureWithValue(final Feature featureMock, final FeatureValue featureValue, final String featureCode,
			final ClassAttributeAssignmentModel classAttributeAssignment)
	{
		when(featureMock.getCode()).thenReturn(featureCode);
		when(featureMock.getClassAttributeAssignment()).thenReturn(classAttributeAssignment);
		when(featureMock.getValue()).thenReturn(featureValue);
		when(featureList.getFeatureByCode(featureCode)).thenReturn(featureMock);
	}

	private Set<ProductModel> getProducts()
	{
		return Collections.set(product);
	}

	private List<ClassificationDescriptor> getClassificationDescriptors(final FeatureDescriptor featureDescriptor1,
			final FeatureDescriptor featureDescriptor2, final FeatureDescriptor featureDescriptor3)
	{
		final ClassificationDescriptor classificationDescriptor1 = mock(ClassificationDescriptor.class);
		when(classificationDescriptor1.getCode()).thenReturn(CLASSIFICATION_CLASS_CODE_1);
		final ClassificationDescriptor classificationDescriptor2 = mock(ClassificationDescriptor.class);
		when(classificationDescriptor2.getCode()).thenReturn(CLASSIFICATION_CLASS_CODE_2);

		when(classificationDescriptor1.getFeatures()).thenReturn(Collections.set(featureDescriptor1, featureDescriptor2));
		when(classificationDescriptor2.getFeatures()).thenReturn(Collections.set(featureDescriptor3));

		return Collections.list(classificationDescriptor1, classificationDescriptor2);
	}


}
