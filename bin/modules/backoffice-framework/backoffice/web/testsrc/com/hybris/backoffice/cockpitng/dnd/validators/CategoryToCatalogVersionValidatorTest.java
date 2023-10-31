/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.cockpitng.dnd.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.dnd.DefaultDragAndDropContext;
import com.hybris.cockpitng.dnd.DragAndDropActionType;
import com.hybris.cockpitng.dnd.DropOperationData;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationSeverity;


@RunWith(MockitoJUnitRunner.class)
public class CategoryToCatalogVersionValidatorTest
{
	@InjectMocks
	private CategoryToCatalogVersionValidator validator;

	@Mock
	private DefaultDragAndDropContext context;

	@Mock
	private DropOperationData operationData;

	@Mock
	private CategoryModel draggedCategory;

	@Mock
	private CatalogVersionModel targetCatalogVersion;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		when(context.getActionType()).thenReturn(DragAndDropActionType.REPLACE);
		when(operationData.getDragged()).thenReturn(draggedCategory);
		when(operationData.getTarget()).thenReturn(targetCatalogVersion);
		when(draggedCategory.getCatalogVersion()).thenReturn(targetCatalogVersion);
	}

	@Test
	public void testIsApplicable() throws Exception
	{
		// when
		final boolean applicable = validator.isApplicable(operationData, context);

		// then
		assertThat(applicable).isTrue();
	}

	@Test
	public void testIsApplicableClassificationClass() throws Exception
	{
		// given
		when(operationData.getDragged()).thenReturn(mock(ClassificationClassModel.class));

		// when
		final boolean applicable = validator.isApplicable(operationData, context);

		// then
		assertThat(applicable).isTrue();
	}

	@Test
	public void testIsNotApplicable() throws Exception
	{
		// given
		when(operationData.getDragged()).thenReturn(mock(ProductModel.class));

		// when
		final boolean applicable = validator.isApplicable(operationData, context);

		// then
		assertThat(applicable).isFalse();
	}

	@Test
	public void testValidateNoAppend() throws Exception
	{
		// given
		when(context.getActionType()).thenReturn(DragAndDropActionType.APPEND);

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).hasSize(1);
		assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
	}

	@Test
	public void testValidateHasSupercategory() throws Exception
	{
		// given
		when(operationData.getModified()).thenReturn(draggedCategory);
		when(draggedCategory.getSupercategories()).thenReturn(Collections.singletonList(mock(CategoryModel.class)));

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).hasSize(1);
		assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.WARN);
	}

	@Test
	public void testValidateOk() throws Exception
	{
		// given
		when(operationData.getModified()).thenReturn(draggedCategory);
		when(draggedCategory.getSupercategories()).thenReturn(Collections.emptyList());

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).isEmpty();
	}

	@Test
	public void shouldAllowToDropOnTheSameCatalogVersion()
	{
		// given
		when(draggedCategory.getCatalogVersion()).thenReturn(targetCatalogVersion);

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).isEmpty();
	}

	@Test
	public void shouldNotAllowToDropOnAnotherCatalogVersion()
	{
		// given
		when(draggedCategory.getCatalogVersion()).thenReturn(mock(CatalogVersionModel.class));

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).hasSize(1);
		assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
	}
}
