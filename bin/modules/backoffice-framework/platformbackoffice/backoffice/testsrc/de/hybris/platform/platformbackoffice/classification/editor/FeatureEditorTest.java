/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.editor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorDefinition;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.editors.EditorRegistry;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class FeatureEditorTest extends AbstractCockpitEditorRendererUnitTest<ClassificationInfo, FeatureEditor>
{
	private static final String NO_EDITOR_FAUND_MSG = "no editor found";

	private final FeatureEditor featureEditor = new FeatureEditor();

	@Mock
	private EditorRegistry editorRegistry;

	@Mock
	private EditorListener<ClassificationInfo> editorListener;

	@Override
	public FeatureEditor getEditorInstance()
	{
		return featureEditor;
	}

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		CockpitTestUtil.mockBeanLookup(BeanLookupFactory.createBeanLookup().registerBean("editorRegistry", editorRegistry));
	}

	@Test
	public void shouldRemoveLastRangeValue() throws Exception
	{
		//given
		final Div parent = new Div();
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(createMultiValueRangeNumberAttr());

		featureEditor.render(parent, editorContext, editorListener);

		final Editor editor = CockpitTestUtil.find(parent, Editor.class).orElseThrow(() -> new Exception(NO_EDITOR_FAUND_MSG));

		//when
		CockpitTestUtil.simulateEvent(editor, Editor.ON_VALUE_CHANGED, null);

		//then
		verify(editorListener).onValueChanged(any());
	}

	@Test
	public void shouldRemoveLastNumberValue() throws Exception
	{
		//given
		final Div parent = new Div();
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(createMultiValueNumberAttr());

		featureEditor.render(parent, editorContext, editorListener);

		final Editor editor = CockpitTestUtil.find(parent, Editor.class).orElseThrow(() -> new Exception(NO_EDITOR_FAUND_MSG));

		//when
		CockpitTestUtil.simulateEvent(editor, Editor.ON_VALUE_CHANGED, null);

		//then
		verify(editorListener).onValueChanged(any());
	}

	@Test
	public void shouldRemoveNullLocalizedValues() throws Exception
	{
		//given
		final Div parent = new Div();
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(createLocalizedStringAttr());

		featureEditor.render(parent, editorContext, editorListener);
		final Editor editor = CockpitTestUtil.find(parent, Editor.class).orElseThrow(() -> new Exception(NO_EDITOR_FAUND_MSG));

		final Map<Locale, Object> localizedValues = new HashMap<>();
		localizedValues.put(Locale.ENGLISH, "englishValue");
		localizedValues.put(Locale.GERMAN, "germanValue");
		localizedValues.put(Locale.FRENCH, null);

		editor.setValue(localizedValues);

		//when
		CockpitTestUtil.simulateEvent(editor, Editor.ON_VALUE_CHANGED, null);

		//then
		final ArgumentCaptor<ClassificationInfo> captor = ArgumentCaptor.forClass(ClassificationInfo.class);
		verify(editorListener).onValueChanged(captor.capture());

		final ClassificationInfo sendInfo = captor.getValue();
		final Map<Locale, Object> sendValues = (Map<Locale, Object>) sendInfo.getValue();

		assertThat(sendValues.keySet()).containsOnly(Locale.ENGLISH, Locale.GERMAN);
	}

	@Test
	public void shouldAvoidNullPointerExceptionOnNullLocalizedValue() throws Exception
	{
		//given
		final Div parent = new Div();
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(createLocalizedStringAttr());

		featureEditor.render(parent, editorContext, editorListener);
		final Editor editor = CockpitTestUtil.find(parent, Editor.class).orElseThrow(() -> new Exception(NO_EDITOR_FAUND_MSG));
		editor.setValue(null);

		//when
		CockpitTestUtil.simulateEvent(editor, Editor.ON_VALUE_CHANGED, null);

		//then
		final ArgumentCaptor<ClassificationInfo> captor = ArgumentCaptor.forClass(ClassificationInfo.class);
		verify(editorListener).onValueChanged(captor.capture());
		final ClassificationInfo sendInfo = captor.getValue();

		assertThat(sendInfo.getValue()).isNull();
	}

	private static ClassAttributeAssignmentModel createMultiValueRangeNumberAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.NUMBER);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.TRUE);
		classAttributeAssignmentModel.setRange(Boolean.TRUE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createMultiValueNumberAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.NUMBER);
		classAttributeAssignmentModel.setLocalized(Boolean.FALSE);
		classAttributeAssignmentModel.setMultiValued(Boolean.TRUE);
		classAttributeAssignmentModel.setRange(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}

	private static ClassAttributeAssignmentModel createLocalizedStringAttr()
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
		classAttributeAssignmentModel.setAttributeType(ClassificationAttributeTypeEnum.STRING);
		classAttributeAssignmentModel.setLocalized(Boolean.TRUE);
		classAttributeAssignmentModel.setMultiValued(Boolean.FALSE);
		classAttributeAssignmentModel.setRange(Boolean.FALSE);
		return classAttributeAssignmentModel;
	}

	private EditorContext<ClassificationInfo> createEditorContext(final ClassAttributeAssignmentModel testParameter)
	{
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = testParameter;
		final ClassificationInfo initialValue = new ClassificationInfo(classAttributeAssignmentModel, null);
		final ClassificationClassModel classificationClass = new ClassificationClassModel();
		classificationClass.setCode("CLASSIFICATION_CLASS");
		classAttributeAssignmentModel.setClassificationClass(classificationClass);
		final EditorDefinition editorDefinition = null;
		final Map<String, Object> parameters = Collections.emptyMap();
		final Map<String, Object> labels = Collections.emptyMap();
		final Set<Locale> readableLocales = new HashSet<>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
		final Set<Locale> writableLocales = new HashSet<>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));

		return new EditorContext<>(initialValue, editorDefinition, parameters, labels, readableLocales, writableLocales);
	}

	@Test
	public void testExtractEmbeddedTypeWithMultipleClasses()
	{
		// given
		final ClassificationAttributeModel attribute = mock(ClassificationAttributeModel.class);
		final List<ClassificationClassModel> classes = new ArrayList<>(2);
		final ClassificationClassModel classOne = mock(ClassificationClassModel.class);
		classes.add(classOne);
		final ClassificationClassModel classTwo = mock(ClassificationClassModel.class);
		when(classTwo.getCode()).thenReturn("class-2");
		classes.add(classTwo);
		when(attribute.getCode()).thenReturn("att-code");

		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationSystemVersionModel systemVersion = mock(ClassificationSystemVersionModel.class);
		final ClassificationSystemModel system = mock(ClassificationSystemModel.class);
		when(systemVersion.getCatalog()).thenReturn(system);
		when(systemVersion.getVersion()).thenReturn("2.0");
		when(system.getId()).thenReturn("demoSystem");

		when(assignment.getSystemVersion()).thenReturn(systemVersion);
		when(assignment.getClassificationAttribute()).thenReturn(attribute);
		when(assignment.getClassificationClass()).thenReturn(classTwo);
		when(assignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);

		final ClassificationInfo initialValue = new ClassificationInfo(assignment, null);
		final EditorContext<ClassificationInfo> context = new EditorContext<>(initialValue, null, null, null, null, null);

		// when
		final String type = featureEditor.extractEmbeddedType(context);

		// then
		assertThat(type).isEqualTo("FeatureValue(ClassificationEnum(demoSystem/2.0/class-2.att-code))");
	}

	@Test
	public void shouldPopulateEditorLabelFromContextToSubEditorIfItsLocalized()
	{
		// given
		final String expectedLabel = "expectedLabel";
		final Component parent = new Div();
		final ClassAttributeAssignmentModel multiValueNumberAttr = createMultiValueNumberAttr();
		multiValueNumberAttr.setLocalized(true);
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(multiValueNumberAttr);
		editorContext.setEditorLabel(expectedLabel);

		// when
		featureEditor.render(parent, editorContext, editorListener);

		// then
		assertThat(CockpitTestUtil.findChild(parent, Editor.class::isInstance).map(Editor.class::cast)).isPresent()
				.hasValueSatisfying(editor -> assertThat(editor.getEditorLabel()).isEqualTo(expectedLabel));
	}

	@Test
	public void shouldNotPopulateEditorLabelFromContextToSubEditorIfItsNotLocalized()
	{
		// given
		final String expectedLabel = "expectedLabel";
		final Component parent = new Div();
		final ClassAttributeAssignmentModel multiValueNumberAttr = createMultiValueNumberAttr();
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(multiValueNumberAttr);
		editorContext.setEditorLabel(expectedLabel);
		final EditorListener<ClassificationInfo> editorListener = mock(EditorListener.class);

		// when
		featureEditor.render(parent, editorContext, editorListener);

		// then
		assertThat(CockpitTestUtil.findChild(parent, Editor.class::isInstance).map(Editor.class::cast)).isPresent()
				.hasValueSatisfying(editor -> assertThat(editor.getEditorLabel()).isNull());
	}

	@Test
	public void shouldLabelBeAppendedForNonLocalizedFeatures()
	{
		// given
		final String expectedLabel = "expectedLabel";
		final Component parent = new Div();
		final ClassAttributeAssignmentModel multiValueNumberAttr = createMultiValueNumberAttr();
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(multiValueNumberAttr);
		editorContext.setEditorLabel(expectedLabel);
		final EditorListener<ClassificationInfo> editorListener = mock(EditorListener.class);

		// when
		featureEditor.render(parent, editorContext, editorListener);

		// then
		final Label label = (Label) parent.query("." + FeatureEditor.SCLASS_LABEL);
		assertThat(label).isNotNull();
		assertThat(label.getValue()).isEqualTo(expectedLabel);
	}

	@Test
	public void shouldLabelNotBeAppendedForLocalizedFeatures()
	{
		// given
		final String expectedLabel = "expectedLabel";
		final Component parent = new Div();
		final ClassAttributeAssignmentModel multiValueNumberAttr = createMultiValueNumberAttr();
		multiValueNumberAttr.setLocalized(true);
		final EditorContext<ClassificationInfo> editorContext = createEditorContext(multiValueNumberAttr);
		editorContext.setEditorLabel(expectedLabel);
		final EditorListener<ClassificationInfo> editorListener = mock(EditorListener.class);

		// when
		featureEditor.render(parent, editorContext, editorListener);

		// then
		final Label label = (Label) parent.query("." + FeatureEditor.SCLASS_LABEL);
		assertThat(label).isNull();
	}

}
