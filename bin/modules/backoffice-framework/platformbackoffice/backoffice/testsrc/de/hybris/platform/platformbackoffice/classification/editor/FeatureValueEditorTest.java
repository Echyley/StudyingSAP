/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.editor;

import static com.hybris.cockpitng.editors.impl.AbstractCockpitEditorRenderer.HEADER_LABEL_TOOLTIP;
import static de.hybris.platform.platformbackoffice.classification.editor.FeatureValueEditor.NULL_UNIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorDefinition;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.editors.EditorRegistry;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class FeatureValueEditorTest
{
	public static final String UNIT_TYPE = "unitType";
	public static final String SAMPLE_VALUE = "SampleValue";
	public static final int SAMPLE_INT_VALUE = 100;

	@Spy
	@InjectMocks
	private FeatureValueEditor editor;
	@Mock
	private EditorDefinition definition;
	@Mock
	private EditorListener<FeatureValue> editorListener;
	@Mock
	private ClassificationSystemService classificationSystemService;
	@Mock
	private EditorRegistry editorRegistry;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		CockpitTestUtil.mockBeanLookup(BeanLookupFactory.createBeanLookup().registerBean("editorRegistry", editorRegistry));
	}

	@Test
	public void testLabel()
	{
		// given
		final Div parent = new Div();
		final EditorContext<FeatureValue> context = createEditorContextForTestValue(null);
		context.setEditorLabel("label");
		context.setParameter(HEADER_LABEL_TOOLTIP, "tooltip");

		// when
		editor.render(parent, context, editorListener);

		// then
		final Div editorLabelWrapper = (Div) parent.getChildren().get(0).getChildren().get(0);
		final Optional<Label> label = CockpitTestUtil.findChild(editorLabelWrapper, Label.class::isInstance).map(Label.class::cast);

		assertThat(label).isPresent().hasValueSatisfying(presentLabel -> {
			assertThat(presentLabel.getValue()).isNotNull().isEqualTo("label");
			assertThat(presentLabel.getTooltiptext()).isNotNull().isEqualTo("tooltip");
		});
	}

	@Test
	public void testValue()
	{
		final Integer value = 100;
		final FeatureValue featureValue = mock(FeatureValue.class);
		when(featureValue.getValue()).thenReturn(value);

		final Div parent = new Div();
		editor.render(parent, createEditorContextForTestValue(featureValue), editorListener);

		final Editor internalEditor = (Editor) parent.getChildren().get(0).getChildren().get(1);
		assertThat(internalEditor.getValue()).isEqualTo(value);
	}

	@Test
	public void testUnitWithUnitType()
	{
		final FeatureValue featureValue = mock(FeatureValue.class);
		final ClassificationAttributeUnitModel classificationAttributeUnitModel = mock(ClassificationAttributeUnitModel.class);
		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class);

		when(featureValue.getValue()).thenReturn(SAMPLE_INT_VALUE);
		when(featureValue.getUnit()).thenReturn(classificationAttributeUnitModel);

		final Div parent = new Div();
		editor.render(parent, createEditorContextForTestUnit(featureValue, classificationAttributeUnitModel, UNIT_TYPE),
				editorListener);

		final Combobox units = (Combobox) parent.getChildren().get(0).getChildren().get(2).getChildren().get(0);
		assertThat(units.getModel().getSize()).isEqualTo(2);
		assertThat(contains(units.getModel(), classificationAttributeUnitModel)).isTrue();

		verify(classificationSystemService).getUnitsOfTypeForSystemVersion(any(), eq(UNIT_TYPE));
		verify(classificationSystemService, never()).getAttributeUnitsForSystemVersion(any());
	}

	@Test
	public void testUnitWithoutUnitType()
	{
		final FeatureValue featureValue = mock(FeatureValue.class);
		final ClassificationAttributeUnitModel classificationAttributeUnitModel = mock(ClassificationAttributeUnitModel.class);
		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class);

		when(featureValue.getValue()).thenReturn(SAMPLE_INT_VALUE);
		when(featureValue.getUnit()).thenReturn(classificationAttributeUnitModel);

		final Div parent = new Div();
		editor.render(parent, createEditorContextForTestUnit(featureValue, classificationAttributeUnitModel, null), editorListener);

		final Combobox units = (Combobox) parent.getChildren().get(0).getChildren().get(2).getChildren().get(0);
		assertThat(units.getModel().getSize()).isEqualTo(2);
		assertThat(contains(units.getModel(), classificationAttributeUnitModel)).isTrue();

		verify(classificationSystemService, never()).getUnitsOfTypeForSystemVersion(any(), any());
		verify(classificationSystemService).getAttributeUnitsForSystemVersion(any());
	}

	@Test
	public void testNAUnitRenderedWhenInitialUnitIsNull()
	{
		//given
		final EditorContext<FeatureValue> context = createEditorContext(SAMPLE_VALUE, null,
				mock(ClassificationAttributeUnitModel.class));
		final Div parent = new Div();

		//when
		editor.render(parent, context, editorListener);

		//then
		final Combobox unitSelector = (Combobox) parent.getChildren().get(0).getChildren().get(2).getChildren().get(0);
		final ListModelList<Object> model = (ListModelList<Object>) unitSelector.getModel();
		final Set<Object> selection = model.getSelection();
		assertThat(selection).contains(NULL_UNIT);
	}

	@Test
	public void testNullUnitSubmittedWhenEditorValueNotNullAndNAUnitSelected()
	{
		//given
		final Div parent = new Div();
		renderFeatureValueEditor(parent, SAMPLE_VALUE);

		//when
		changeSelectedUnit(parent, NULL_UNIT);

		//then
		final ArgumentCaptor<FeatureValue> argumentCaptor = ArgumentCaptor.forClass(FeatureValue.class);
		verify(editorListener).onValueChanged(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getUnit()).isNull();
		assertThat(argumentCaptor.getValue().getValue()).isEqualTo(SAMPLE_VALUE);
	}

	@Test
	public void testNullUnitSubmittedWhenEditorValueNotNullAndNewUnitSelected()
	{
		//given
		final Div parent = new Div();
		renderFeatureValueEditor(parent, SAMPLE_VALUE);
		final ClassificationAttributeUnitModel newUnit = mock(ClassificationAttributeUnitModel.class);

		//when
		changeSelectedUnit(parent, newUnit);

		//then
		final ArgumentCaptor<FeatureValue> argumentCaptor = ArgumentCaptor.forClass(FeatureValue.class);
		verify(editorListener).onValueChanged(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue().getUnit()).isEqualTo(newUnit);
		assertThat(argumentCaptor.getValue().getValue()).isEqualTo(SAMPLE_VALUE);
	}

	@Test
	public void testNullValueSubmittedWhenEditorValueNullAndNAUnitSelected()
	{
		//given
		final Div parent = new Div();
		renderFeatureValueEditor(parent, null);

		//when
		changeSelectedUnit(parent, NULL_UNIT);

		//then
		final ArgumentCaptor<FeatureValue> argumentCaptor = ArgumentCaptor.forClass(FeatureValue.class);
		verify(editorListener).onValueChanged(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue()).isNull();
	}

	private EditorContext<FeatureValue> createEditorContextForTestValue(final FeatureValue initialValue)
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationInfo classificationInfo = new ClassificationInfo(classAttributeAssignmentModel, initialValue);

		final EditorContext<FeatureValue> ctx = new EditorContext<>(initialValue, definition, Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptySet(), Collections.emptySet());
		ctx.setValueType(Integer.class.getCanonicalName());
		ctx.setParameter(FeatureEditor.CLASSIFICATION_INFO, classificationInfo);
		return ctx;
	}

	private EditorContext<FeatureValue> createEditorContextForTestUnit(final FeatureValue initialValue,
			final ClassificationAttributeUnitModel initialUnit, final String unitType)
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationInfo classificationInfo = new ClassificationInfo(classAttributeAssignmentModel, initialValue);
		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		final ClassificationAttributeModel classAttributeModel = mock(ClassificationAttributeModel.class);
		when(classAttributeAssignmentModel.getUnit()).thenReturn(initialUnit);
		when(classAttributeAssignmentModel.getUnit().getUnitType()).thenReturn(unitType);
		when(classAttributeAssignmentModel.getClassificationAttribute()).thenReturn(classAttributeModel);
		when(classAttributeModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		when(classAttributeAssignmentModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		when(classificationSystemService.getUnitsOfTypeForSystemVersion(classificationSystemVersionModel, unitType))
				.thenReturn(Lists.newArrayList(initialUnit));
		when(classificationSystemService.getAttributeUnitsForSystemVersion(classificationSystemVersionModel))
				.thenReturn(Lists.newArrayList(initialUnit));


		final EditorContext<FeatureValue> ctx = new EditorContext<>(initialValue, definition, Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptySet(), Collections.emptySet());
		ctx.setValueType(Integer.class.getCanonicalName());
		ctx.setParameter(FeatureEditor.CLASSIFICATION_INFO, classificationInfo);
		return ctx;
	}

	private boolean contains(final ListModel<?> model, final Object item)
	{
		for (int i = 0; i < model.getSize(); ++i)
		{
			if (item.equals(model.getElementAt(i)))
			{
				return true;
			}
		}
		return false;
	}

	private void renderFeatureValueEditor(final Div parent, final Object value)
	{
		final ClassificationAttributeUnitModel unit = mock(ClassificationAttributeUnitModel.class);
		final EditorContext<FeatureValue> context = createEditorContext(value, unit, unit);
		editor.render(parent, context, editorListener);
	}

	private EditorContext<FeatureValue> createEditorContext(final Object value, final ClassificationAttributeUnitModel initialUnit,
			final ClassificationAttributeUnitModel unitAssigmentModel)
	{
		final FeatureValue intialFeatureValue = mock(FeatureValue.class);
		when(intialFeatureValue.getValue()).thenReturn(value);
		when(intialFeatureValue.getUnit()).thenReturn(initialUnit);

		return createEditorContextForTestUnit(intialFeatureValue, unitAssigmentModel, UNIT_TYPE);
	}

	private void changeSelectedUnit(final Div parent, final ClassificationAttributeUnitModel unit)
	{
		final Combobox unitSelector = (Combobox) parent.getChildren().get(0).getChildren().get(2).getChildren().get(0);
		final Comboitem comboItem = spy(new Comboitem());
		comboItem.setValue(unit);
		doReturn(unitSelector).when(comboItem).getParent();
		unitSelector.setSelectedItem(comboItem);

		CockpitTestUtil.simulateEvent(unitSelector, Events.ON_SELECT, "SelectEventData");
	}
}
