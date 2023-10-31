/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.util.CockpitGlobalEventPublisher;
import de.hybris.platform.platformbackoffice.bulkedit.dto.ClassificationChangeDto;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationPersistenceHandlerTest
{

	@Mock
	private ClassificationService classificationService;
	@Mock
	private CockpitGlobalEventPublisher cockpitGlobalEventPublisher;
	@Mock
	private ModelService modelService;

	@InjectMocks
	@Spy
	private ClassificationPersistenceHandler classificationPersistenceHandler;

	@Before
	public void setup()
	{
		doNothing().when(classificationPersistenceHandler).saveFeatures(any(), any());
	}

	@Test
	public void shouldSkipStoringValueWhenProductIsNotAssignedToClassification()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final Feature feature = new UnlocalizedFeature("changed value", List.of());
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setFeature(feature);
		given(classificationService.getFeature(eq(product), any())).willReturn(null);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, Feature> value = captor.getValue();
		assertThat(value).isEmpty();
	}

	@Test
	public void shouldNotStoreValueWhenEditorIsEmptyAndClearValueCheckboxIsNotChecked()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final Feature feature = new UnlocalizedFeature("value", List.of());
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setFeature(feature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, Feature> value = captor.getValue();
		assertThat(value).isEmpty();
	}

	@Test
	public void shouldStoreUnlocalizedValueWhenEditorIsNotEmpty()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final Feature feature = new UnlocalizedFeature("value", List.of(featureValue));
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setFeature(feature);
		change.setEncodedQualifier("qualifier");
		final Feature currentFeature = new UnlocalizedFeature("value", List.of(new FeatureValue("Current feature value")));
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, Feature> value = captor.getValue();
		assertThat(value.get("qualifier")).isNotNull();
		assertThat(value.get("qualifier").getValues()).hasSize(1);
		assertThat(value.get("qualifier").getValues().get(0).getValue()).isEqualTo("My value");
	}

	@Test
	public void shouldStoreLocalizedValueWhenEditorIsNotEmpty()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final Feature feature = new LocalizedFeature("value", Map.of(Locale.ENGLISH, Arrays.asList(featureValue)), Locale.GERMAN);
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setFeature(feature);
		change.setEncodedQualifier("qualifier");
		change.setIsoCode("en");
		final List<FeatureValue> currentValues = new ArrayList<>();
		currentValues.add(new FeatureValue("Current value"));
		final Feature currentFeature = new LocalizedFeature("Current value", Map.of(Locale.ENGLISH, currentValues), Locale.GERMAN);
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, LocalizedFeature> value = captor.getValue();
		assertThat(value.get("qualifier")).isNotNull();
		assertThat(value.get("qualifier").getValues(Locale.ENGLISH)).hasSize(1);
		assertThat(value.get("qualifier").getValues(Locale.ENGLISH).get(0).getValue()).isEqualTo("My value");
	}

	@Test
	public void shouldClearUnlocalizedValueWhenCheckboxIsChecked()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final Feature feature = new UnlocalizedFeature("value", List.of(featureValue));
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setEncodedQualifier("qualifier");
		change.setFeature(feature);
		change.setClear(true);
		final Feature currentFeature = new UnlocalizedFeature("value", List.of(new FeatureValue("Current feature value")));
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, Feature> value = captor.getValue();
		assertThat(value.get("qualifier")).isNotNull();
		assertThat(value.get("qualifier").getValues()).isEmpty();
	}

	@Test
	public void shouldClearLocalizedValueWhenCheckboxIsChecked()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final List<FeatureValue> featureValues = new ArrayList<>();
		featureValues.add(featureValue);
		final Feature feature = new LocalizedFeature("value", Map.of(Locale.ENGLISH, featureValues), Locale.GERMAN);
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setEncodedQualifier("qualifier");
		change.setFeature(feature);
		change.setClear(true);
		change.setIsoCode("en");
		final List<FeatureValue> currentValues = new ArrayList<>();
		currentValues.add(new FeatureValue("Current value"));
		final Feature currentFeature = new LocalizedFeature("Current value", Map.of(Locale.ENGLISH, currentValues), Locale.GERMAN);
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, LocalizedFeature> value = captor.getValue();
		assertThat(value.get("qualifier")).isNotNull();
		assertThat(value.get("qualifier").getValues()).isEmpty();
	}

	@Test
	public void shouldMergeUnlocalizedValueWhenCheckboxIsChecked()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final Feature feature = new UnlocalizedFeature("value", List.of(featureValue));
		final FeatureValue currentFeatureValue = new FeatureValue("Current value");
		final Feature currentFeature = new UnlocalizedFeature("Current value", List.of(currentFeatureValue));
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setEncodedQualifier("qualifier");
		change.setFeature(feature);
		change.setMerge(true);
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, Feature> value = captor.getValue();
		assertThat(value.get("qualifier")).isNotNull();
		assertThat(value.get("qualifier").getValues()).hasSize(2);
		assertThat(value.get("qualifier").getValues()).extracting("value").containsExactly("Current value", "My value");
	}

	@Test
	public void shouldMergeLocalizedValueWhenCheckboxIsChecked()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final FeatureValue currentFeatureValue = new FeatureValue("Current value EN");
		final List<FeatureValue> featureValues = new ArrayList<>();
		final List<FeatureValue> currentFeatureValues = new ArrayList<>();
		featureValues.add(featureValue);
		currentFeatureValues.add(currentFeatureValue);
		final Feature feature = new LocalizedFeature("value", Map.of(Locale.ENGLISH, featureValues), Locale.GERMAN);
		final Feature currentFeature = new LocalizedFeature("value", Map.of(Locale.ENGLISH, currentFeatureValues), Locale.GERMAN);
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setEncodedQualifier("qualifier");
		change.setFeature(feature);
		change.setMerge(true);
		change.setIsoCode("en");
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(classificationPersistenceHandler).saveFeatures(eq(product), captor.capture());
		final Map<String, Feature> value = captor.getValue();
		assertThat(value.get("qualifier")).isNotNull();
		assertThat(((LocalizedFeature) value.get("qualifier")).getValues(Locale.ENGLISH)).hasSize(2);
		assertThat(((LocalizedFeature) value.get("qualifier")).getValues(Locale.ENGLISH)).extracting("value")
				.containsExactly("Current value EN", "My value");
	}

	@Test
	public void shouldEventBeSentAfterSavingTheFeatures()
	{
		final ProductModel product = mock(ProductModel.class);
		final FeatureValue featureValue = new FeatureValue("My value");
		final Feature feature = new UnlocalizedFeature("value", List.of(featureValue));
		final FeatureValue currentFeatureValue = new FeatureValue("Current value");
		final Feature currentFeature = new UnlocalizedFeature("Current value", List.of(currentFeatureValue));
		final ClassificationChangeDto change = new ClassificationChangeDto();
		change.setEncodedQualifier("qualifier");
		change.setFeature(feature);
		given(classificationService.getFeature(eq(product), any())).willReturn(currentFeature);

		// when
		classificationPersistenceHandler.saveChanges(product, Set.of(change));

		// then
		final InOrder inOrder = inOrder(classificationPersistenceHandler, modelService, cockpitGlobalEventPublisher);
		inOrder.verify(classificationPersistenceHandler, times(1)).saveFeatures(eq(product), any());
		inOrder.verify(modelService, times(1)).refresh(eq(product));
		inOrder.verify(cockpitGlobalEventPublisher, times(1)).publish(eq(ObjectFacade.OBJECTS_UPDATED_EVENT), eq(product), any());
	}

}
