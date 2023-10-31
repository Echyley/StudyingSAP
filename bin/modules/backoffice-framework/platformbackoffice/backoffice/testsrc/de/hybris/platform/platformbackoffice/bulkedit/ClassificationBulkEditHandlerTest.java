/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.bulkedit.BulkEditForm;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;
import de.hybris.platform.platformbackoffice.bulkedit.dto.ClassificationChangeDto;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationBulkEditHandlerTest
{

	@Mock
	private ClassificationPersistenceHandler classificationPersistenceHandler;
	@Mock
	private BulkEditClassificationService bulkEditClassificationService;

	@Spy
	@InjectMocks
	private ClassificationBulkEditHandler classificationBulkEditHandler;

	@Test
	public void shouldNotHandleClassificationAttributesWhenStandardAttributesReturnedError()
	{
		// given
		final CustomType customType = mock(CustomType.class);
		final FlowActionHandlerAdapter flowActionHandlerAdapter = mock(FlowActionHandlerAdapter.class);
		final Map<String, String> params = new HashMap<>();
		params.put("hasValidationErrors", "true");
		doNothing().when(classificationBulkEditHandler).performForStandardAttributes(customType, flowActionHandlerAdapter, params);

		// when
		classificationBulkEditHandler.perform(customType, flowActionHandlerAdapter, params);

		// then
		verify(classificationBulkEditHandler, times(0)).performForClassificationAttributes(flowActionHandlerAdapter, params);
	}

	@Test
	public void shouldStoreChangesForEachSelectedProduct()
	{
		// given
		final CustomType customType = mock(CustomType.class);
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		final FlowActionHandlerAdapter flowActionHandlerAdapter = mock(FlowActionHandlerAdapter.class);
		final ClassificationBulkEditForm bulkEditForm = mock(ClassificationBulkEditForm.class);

		final Map<String, Feature> changedValues = new HashMap<>();
		final Map<String, String> params = new HashMap<>();
		final Collection<ClassificationChangeDto> changes = new ArrayList<>();
		final ProductModel firstProduct = mock(ProductModel.class);
		final ProductModel secondProduct = mock(ProductModel.class);

		params.put("bulkEditFormModelKey", "bulkEditForm");
		doNothing().when(classificationBulkEditHandler).performForStandardAttributes(customType, flowActionHandlerAdapter, params);
		given(flowActionHandlerAdapter.getWidgetInstanceManager()).willReturn(wim);
		given(wim.getModel()).willReturn(widgetModel);
		given(widgetModel.getValue("bulkEditForm", BulkEditForm.class)).willReturn(bulkEditForm);
		given(widgetModel.getValue("modifiedProductFeatures.pknull", Map.class)).willReturn(changedValues);
		given(bulkEditClassificationService.generateChanges(bulkEditForm, changedValues)).willReturn(changes);
		given(bulkEditForm.getItemsToEdit()).willReturn(Set.of(firstProduct, secondProduct));

		// when
		classificationBulkEditHandler.perform(customType, flowActionHandlerAdapter, params);

		// then
		verify(classificationPersistenceHandler).saveChanges(firstProduct, changes);
		verify(classificationPersistenceHandler).saveChanges(secondProduct, changes);
	}

	@Test
	public void shouldChangesBeNotFoundIfClassificationBulkEditRendererWasNotInvoked()
	{
		//given
		final WidgetInstanceManager wim = mock(WidgetInstanceManager.class);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		final ClassificationBulkEditForm bulkEditForm = mock(ClassificationBulkEditForm.class);
		final Map<String, String> params = new HashMap<>();
		final FlowActionHandlerAdapter flowActionHandlerAdapter = mock(FlowActionHandlerAdapter.class);

		given(flowActionHandlerAdapter.getWidgetInstanceManager()).willReturn(wim);
		given(wim.getModel()).willReturn(widgetModel);

		//when
		classificationBulkEditHandler.performForClassificationAttributes(flowActionHandlerAdapter, params);

		//then
		verify(classificationPersistenceHandler, never()).saveChanges(any(), any());
	}
}
