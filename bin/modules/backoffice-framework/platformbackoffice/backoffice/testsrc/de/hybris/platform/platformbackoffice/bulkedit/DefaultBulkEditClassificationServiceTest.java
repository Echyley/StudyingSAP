/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.features.Feature;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import de.hybris.platform.platformbackoffice.bulkedit.dto.ClassificationChangeDto;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBulkEditClassificationServiceTest
{

	@Mock
	private BulkEditSelectedAttributesHelper selectedAttributesHelper;
	@Mock
	private ObjectFacade objectFacade;

	@Spy
	@InjectMocks
	private DefaultBulkEditClassificationService bulkEditClassificationService;

	@Test
	public void shouldReturnEmptyListWhenNoAttributeHasBeenSelected()
	{
		// given
		final ClassificationBulkEditForm form = new ClassificationBulkEditForm();
		final AttributeChooserForm classificationForm = new AttributeChooserForm();
		form.setClassificationAttributesForm(classificationForm);
		final Map<String, Feature> features = new HashMap<>();

		// when
		final Collection<ClassificationChangeDto> changeDtos = bulkEditClassificationService.generateChanges(form, features);

		// then
		assertThat(changeDtos).isEmpty();
	}

	@Test
	public void shouldReturnChangeForSelectedAttribute() throws Exception
	{
		// given
		final String encodedQualifier = "encodedQualifier";
		final Feature feature = mock(Feature.class);
		final ClassificationBulkEditForm form = new ClassificationBulkEditForm();
		final Map<String, Feature> features = new HashMap<>();
		features.put(encodedQualifier, feature);
		mockAttributeAndValue(form, "qualifier", encodedQualifier, null, new HashSet<>(), new HashSet<>());

		// when
		final Collection<ClassificationChangeDto> changeDtos = bulkEditClassificationService.generateChanges(form, features);

		// then
		assertThat(changeDtos).hasSize(1);
		final ClassificationChangeDto change = changeDtos.iterator().next();
		assertThat(change.getFeature()).isEqualTo(feature);
		assertThat(change.getEncodedQualifier()).isEqualTo(encodedQualifier);
		assertThat(change.getIsoCode()).isNull();
		assertThat(change.isClear()).isFalse();
		assertThat(change.isMerge()).isFalse();
	}

	@Test
	public void shouldReturnChangeForLocalizedAttribute() throws Exception
	{
		// given
		final String encodedQualifier = "encodedQualifier";
		final Feature feature = mock(Feature.class);
		final ClassificationBulkEditForm form = new ClassificationBulkEditForm();
		final Map<String, Feature> features = new HashMap<>();
		features.put(encodedQualifier, feature);
		mockAttributeAndValue(form, "qualifier", encodedQualifier, "de", new HashSet<>(), new HashSet<>());

		// when
		final Collection<ClassificationChangeDto> changeDtos = bulkEditClassificationService.generateChanges(form, features);

		// then
		assertThat(changeDtos).hasSize(1);
		final ClassificationChangeDto change = changeDtos.iterator().next();
		assertThat(change.getFeature()).isEqualTo(feature);
		assertThat(change.getEncodedQualifier()).isEqualTo(encodedQualifier);
		assertThat(change.getIsoCode()).isEqualTo("de");
		assertThat(change.isClear()).isFalse();
		assertThat(change.isMerge()).isFalse();
	}

	@Test
	public void shouldReturnChangeForValueWhichShouldBeCleared() throws Exception
	{
		// given
		final String encodedQualifier = "encodedQualifier";
		final Feature feature = mock(Feature.class);
		final ClassificationBulkEditForm form = new ClassificationBulkEditForm();
		final Map<String, Feature> features = new HashMap<>();
		features.put(encodedQualifier, feature);
		mockAttributeAndValue(form, "qualifier", encodedQualifier, null, Set.of("qualifier"), new HashSet<>());

		// when
		final Collection<ClassificationChangeDto> changeDtos = bulkEditClassificationService.generateChanges(form, features);

		// then
		assertThat(changeDtos).hasSize(1);
		final ClassificationChangeDto change = changeDtos.iterator().next();
		assertThat(change.getFeature()).isEqualTo(feature);
		assertThat(change.getEncodedQualifier()).isEqualTo(encodedQualifier);
		assertThat(change.getIsoCode()).isNull();
		assertThat(change.isClear()).isTrue();
		assertThat(change.isMerge()).isFalse();
	}

	@Test
	public void shouldReturnChangeForValueWhichShouldBeMerged() throws Exception
	{
		// given
		final String encodedQualifier = "encodedQualifier";
		final Feature feature = mock(Feature.class);
		final ClassificationBulkEditForm form = new ClassificationBulkEditForm();
		final Map<String, Feature> features = new HashMap<>();
		features.put(encodedQualifier, feature);
		mockAttributeAndValue(form, "qualifier", encodedQualifier, null, new HashSet<>(), Set.of("qualifier"));

		// when
		final Collection<ClassificationChangeDto> changeDtos = bulkEditClassificationService.generateChanges(form, features);

		// then
		assertThat(changeDtos).hasSize(1);
		final ClassificationChangeDto change = changeDtos.iterator().next();
		assertThat(change.getFeature()).isEqualTo(feature);
		assertThat(change.getEncodedQualifier()).isEqualTo(encodedQualifier);
		assertThat(change.getIsoCode()).isNull();
		assertThat(change.isClear()).isFalse();
		assertThat(change.isMerge()).isTrue();
	}

	private void mockAttributeAndValue(final ClassificationBulkEditForm form, final String qualifier,
			final String encodedQualifier, final String isoCode, final Set<String> qualifiersToBeClear,
			final Set<String> qualifiersToBeMerged) throws Exception
	{
		form.setQualifiersToBeCleared(qualifiersToBeClear);
		form.setQualifiersToMerge(qualifiersToBeMerged);
		final AttributeChooserForm classificationForm = new AttributeChooserForm();
		form.setClassificationAttributesForm(classificationForm);
		final Attribute attribute = new Attribute(qualifier, null, false);
		attribute.setIsoCode(isoCode);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		classificationForm.setChosenAttributes(Set.of(attribute));
		given(selectedAttributesHelper.findLeaves(Set.of(attribute))).willReturn(List.of(attribute));
		given(objectFacade.load(qualifier)).willReturn(assignment);
		doReturn(encodedQualifier).when(bulkEditClassificationService).generateEncodedQualifier(assignment);
	}
}
