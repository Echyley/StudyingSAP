/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractTab;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomTab;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Parameter;


@RunWith(MockitoJUnitRunner.class)
public class ConfigurableClassificationTabEditorAreaRendererTest
{
	@InjectMocks
	@Spy
	private ConfigurableClassificationTabEditorAreaRenderer renderer;

	@Mock
	private ClassificationSystemService classificationSystemService;

	@Mock
	private ClassificationService classificationService;

	@Mock
	private Component component;

	@Mock
	private CustomTab configuration;

	@Mock
	private ClassificationSystemModel system;

	@Mock
	private ClassificationSystemVersionModel systemVersion;

	@Mock
	private ClassificationClassModel classificationClass;

	@Mock
	private ClassificationClassModel otherClassificationClass;

	@Mock
	private ClassificationClassModel subClassificationClass;

	@Mock
	private ClassificationSystemModel otherSystem;

	@Mock
	private ClassificationSystemVersionModel otherSystemVersion;

	public ConfigurableClassificationTabEditorAreaRendererTest()
	{
	}

	@Before
	public void setUp()
	{
		renderer = spy(new ConfigurableClassificationTabEditorAreaRenderer());
		renderer.setClassificationSystemService(classificationSystemService);
		renderer.setClassificationService(classificationService);
	}

	@Test
	public void shouldReturnTrueIfContainsTheFeature()
	{
		//given
		final ProductModel product = mock(ProductModel.class);
		final Collection<CategoryModel> superCategories = new ArrayList<>();
		superCategories.add((CategoryModel) classificationClass);
		prepareConfigurationForSelectiveRendering(false);
		prepareCanRenderData(product, classificationClass);
		doReturn(ConfigurableClassificationTabEditorAreaRenderer.BEAN_ID).when(configuration).getSpringBean();

		//then
		assertThat(renderer.canRender((AbstractTab) configuration, product)).isTrue();
	}

	@Test
	public void shouldReturnFalseIfNotContainsTheFeature()
	{
		//given
		final ProductModel product = mock(ProductModel.class);
		prepareConfigurationForSelectiveRendering(false);
		doReturn(ConfigurableClassificationTabEditorAreaRenderer.BEAN_ID).when(configuration).getSpringBean();
		final FeatureList features = mock(FeatureList.class);
		doReturn(features).when(classificationService).getFeatures(product);
		doReturn(null).when(renderer).getFilteredClassificationClasses(features);

		//then
		assertThat(renderer.canRender((AbstractTab) configuration, product)).isFalse();
	}

	@Test
	public void shouldReturnTrueIfNotCustomTab()
	{
		//given

		//then
		assertThat(renderer.canRender(mock(AbstractTab.class), mock(ProductModel.class))).isTrue();
	}

	@Test
	public void shouldReturnTrueIfNotProductModel()
	{
		//given

		//then
		assertThat(renderer.canRender((AbstractTab) configuration, new Object())).isTrue();
	}

	@Test
	public void shouldReturnTrueIfBeanIdNotAdapt()
	{
		//given
		doReturn("errorBeanId").when(configuration).getSpringBean();
		//then
		assertThat(renderer.canRender((AbstractTab) configuration, mock(ProductModel.class))).isTrue();
	}

	@Test
	public void matcherOnNonConfiguredRendererShouldBePositive()
	{
		//given
		prepareConfigurationForSelectiveRendering(false);

		//then
		assertThat(renderer.systemMatches(otherClassificationClass)).isTrue();
		assertThat(renderer.systemVersionMatches(otherClassificationClass)).isTrue();
		assertThat(renderer.categoryMatches(otherClassificationClass)).isTrue();
	}

	@Test
	public void shouldFilterMatchedClassificationClass()
	{
		// given
		final FeatureList featureList = mock(FeatureList.class);

		final Set<ClassificationClassModel> features = Collections.set(otherClassificationClass, classificationClass,
				subClassificationClass);
		final Set<ClassificationClassModel> expectedResult = Collections.set(classificationClass);

		doReturn(features).when((ClassificationTabEditorAreaRenderer) renderer).getAllReadableClassificationClasses(any());
		prepareConfigurationForSelectiveRendering(true);

		// when
		renderer.applyConfiguration(configuration);
		final Set<ClassificationClassModel> filteredClassificationClasses = renderer.getFilteredClassificationClasses(featureList);

		// then
		assertThat(filteredClassificationClasses).isEqualTo(expectedResult);
	}

	@Test
	public void shouldFilterMatchedClassificationClassWhenExclusiveIsFalse()
	{
		// given
		final FeatureList featureList = mock(FeatureList.class);

		final Set<ClassificationClassModel> features = Collections.set(otherClassificationClass, classificationClass,
				subClassificationClass);
		final Set<ClassificationClassModel> expectedResult = Collections.set(classificationClass, subClassificationClass);

		doReturn(features).when((ClassificationTabEditorAreaRenderer) renderer).getAllReadableClassificationClasses(any());
		prepareConfigurationForSelectiveRendering(false);

		// when
		final Set<ClassificationClassModel> filteredClassificationClasses = renderer.getFilteredClassificationClasses(featureList);

		// then
		assertThat(filteredClassificationClasses).isEqualTo(features);
	}

	@Test
	public void renderShouldRenderEmptyTabOnInvalidConfiguration()
	{
		//given
		doThrow(new SystemException("Let's fail...")).when(renderer).applyConfiguration(any());
		doNothing().when(renderer).renderEmptyTab(component,
				ConfigurableClassificationTabEditorAreaRenderer.I18N_BACKOFFICE_DATA_CLASSIFICATION_NOT_FOUND);
		//when
		renderer.render(component, configuration, null, null, null);

		//then
		verify(renderer).renderEmptyTab(component,
				ConfigurableClassificationTabEditorAreaRenderer.I18N_BACKOFFICE_DATA_CLASSIFICATION_NOT_FOUND);
	}

	@Test
	public void applyConfigurationShouldFillInClassificationRelatedData()
	{
		//given
		prepareConfigurationForSelectiveRendering(true);

		//when
		renderer.applyConfiguration(configuration);

		//then
		assertThat(renderer.getSystem()).isEqualTo(system);
		assertThat(renderer.getSystemVersion()).isEqualTo(systemVersion);
		assertThat(renderer.getCategory()).isEqualTo(classificationClass);
		assertThat(renderer.isExclusive()).isTrue();
	}

	@Test
	public void systemMatchesShouldBeEqual()
	{
		//given
		prepareConfigurationForSelectiveRendering(true);

		//when
		renderer.applyConfiguration(configuration);

		//then
		assertThat(renderer.systemMatches(classificationClass)).isTrue();
		assertThat(renderer.systemMatches(otherClassificationClass)).isFalse();
	}

	@Test
	public void systemVersionMatchesShouldBeEqual()
	{
		//given
		prepareConfigurationForSelectiveRendering(true);

		//when
		renderer.applyConfiguration(configuration);

		//then
		assertThat(renderer.systemVersionMatches(classificationClass)).isTrue();
		assertThat(renderer.systemVersionMatches(otherClassificationClass)).isFalse();
	}

	@Test
	public void categoryMatchesShouldBeEqual()
	{
		//given
		prepareConfigurationForSelectiveRendering(true);

		//when
		renderer.applyConfiguration(configuration);

		//then
		assertThat(renderer.categoryMatches(classificationClass)).isTrue();
		assertThat(renderer.categoryMatches(otherClassificationClass)).isFalse();
	}

	@Test
	public void nonExclusiveMatchingShouldAcceptSubCategory()
	{
		//given
		prepareConfigurationForSelectiveRendering(false);

		//when
		renderer.applyConfiguration(configuration);

		//then
		assertThat(renderer.categoryMatches(subClassificationClass)).isTrue();
	}

	@Test
	public void getFilteredClassificationClassesShouldCallFilteringMethod()
	{
		//when
		renderer.getFilteredClassificationClasses(mock(FeatureList.class));

		//then
		verify(renderer).filterConfiguredClasses(any());
	}

	private void prepareConfigurationForSelectiveRendering(final boolean exclusive)
	{
		final Parameter systemParam = new Parameter();
		systemParam.setName("system");
		systemParam.setValue("ClsSystem");
		final Parameter versionParam = new Parameter();
		versionParam.setName("version");
		versionParam.setValue("1.0");
		final Parameter categoryParam = new Parameter();
		categoryParam.setName("category");
		categoryParam.setValue("Measures");
		final Parameter exclusiveParam = new Parameter();
		exclusiveParam.setName("exclusive");
		exclusiveParam.setValue(Boolean.toString(exclusive));

		doReturn(Arrays.asList(systemParam, versionParam, categoryParam, exclusiveParam)).when(configuration).getRenderParameter();

		doReturn(system).when(systemVersion).getCatalog();
		doReturn(systemVersion).when(classificationClass).getCatalogVersion();
		// doReturn(systemVersion).when(subClassificationClass).getCatalogVersion();
		final Collection<ClassificationClassModel> subCategories = new ArrayList<>();
		subCategories.add(subClassificationClass);

		doReturn(otherSystem).when(otherSystemVersion).getCatalog();
		doReturn(otherSystemVersion).when(otherClassificationClass).getCatalogVersion();

		doReturn(subCategories).when(classificationClass).getAllSubcategories();
		doReturn(system).when(classificationSystemService).getSystemForId("ClsSystem");
		doReturn(systemVersion).when(classificationSystemService).getSystemVersion("ClsSystem", "1.0");
		doReturn(classificationClass).when(classificationSystemService).getClassForCode(systemVersion, "Measures");
	}

	private void prepareCanRenderData(final ProductModel product, final ClassificationClassModel classModel)
	{
		final FeatureList features = mock(FeatureList.class);
		doReturn(features).when(classificationService).getFeatures(product);
		final Set<ClassificationClassModel> classModels = new HashSet<>();
		classModels.add(classModel);
		doReturn(classModels).when(renderer).getFilteredClassificationClasses(features);
	}
}
