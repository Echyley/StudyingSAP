/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.numberwithclassificationunit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.platformbackoffice.services.ClassificationAttributeAssignmentService;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;


@RunWith(MockitoJUnitRunner.class)
public class NumberWithClassificationUnitEditorTest
		extends AbstractCockpitEditorRendererUnitTest<Number, NumberWithClassificationUnitEditor>
{

	@Mock
	private ClassificationAttributeAssignmentService classificationAttributeAssignmentService;

	@Mock
	private ClassificationSystemService classificationSystemService;

	@Mock
	private LabelService labelService;

	@InjectMocks
	private NumberWithClassificationUnitEditor numberWithClassificationUnitEditor;

	@Override
	public NumberWithClassificationUnitEditor getEditorInstance()
	{
		return new NumberWithClassificationUnitEditor();
	}

	@Test
	public void shouldFindClassificationUnitBasedOnFourAttributes()
	{
		// given
		final EditorContext context = prepareEditorContext();
		final String catalogId = "ElectronicsClassification";
		final String systemVersion = "1.0";
		final String classificationClass = "40";
		final String classificationAttribute = "Weight, 94";
		context.setParameter(NumberWithClassificationUnitEditor.CATALOG_ID, catalogId);
		context.setParameter(NumberWithClassificationUnitEditor.SYSTEM_VERSION, systemVersion);
		context.setParameter(NumberWithClassificationUnitEditor.CLASSIFICATION_CLASS_CODE, classificationClass);
		context.setParameter(NumberWithClassificationUnitEditor.CLASSIFICATION_ATTRIBUTE_CODE, classificationAttribute);
		context.setParameter(NumberWithClassificationUnitEditor.CLASSIFICATION_ATTRIBUTE_QUALIFIER,
				"fakeClassification/5.0/123.Weight, 4321");

		// when
		numberWithClassificationUnitEditor.findClassificationUnit(context);

		// then
		verify(classificationAttributeAssignmentService).findClassAttributeAssignment(catalogId, systemVersion, classificationClass,
				classificationAttribute);
	}

	@Test
	public void shouldFindClassificationUnitBasedOnCode()
	{
		// given
		final EditorContext context = prepareEditorContext();
		final String catalogId = "ElectronicsClassification";
		final String systemVersion = "1.0";
		final String unitCode = "123";
		context.setParameter(NumberWithClassificationUnitEditor.CATALOG_ID, catalogId);
		context.setParameter(NumberWithClassificationUnitEditor.SYSTEM_VERSION, systemVersion);
		context.setParameter(NumberWithClassificationUnitEditor.CLASSIFICATION_ATTRIBUTE_UNIT_CODE, unitCode);
		context.setParameter(NumberWithClassificationUnitEditor.CLASSIFICATION_ATTRIBUTE_QUALIFIER,
				"fakeClassification/5.0/123.Weight, 4321");

		// when
		numberWithClassificationUnitEditor.findClassificationUnit(context);

		// then
		verify(classificationSystemService).getSystemVersion(catalogId, systemVersion);
		verify(classificationSystemService).getAttributeUnitForCode(any(), eq(unitCode));
	}

	@Test
	public void shouldFindClassificationUnitBasedOnQualifier()
	{
		// given
		final EditorContext context = prepareEditorContext();
		final String qualifier = "ElectronicsClassification/1.0/40.Weight, 94";
		context.setParameter(NumberWithClassificationUnitEditor.CLASSIFICATION_ATTRIBUTE_QUALIFIER, qualifier);

		// when
		numberWithClassificationUnitEditor.findClassificationUnit(context);

		// then
		verify(classificationAttributeAssignmentService).findClassAttributeAssignment(qualifier);
	}

	@Test
	public void shouldConvertValueByConversionFactor()
	{
		// given
		final Double conversionFactor = Double.valueOf(2.37);
		final Combobox combobox = Mockito.mock(Combobox.class);
		final Comboitem comboitem = Mockito.mock(Comboitem.class);
		final Editor editor = Mockito.mock(Editor.class);
		final ClassificationAttributeUnitModel selectedUnit = Mockito.mock(ClassificationAttributeUnitModel.class);
		final Integer editorValue = Integer.valueOf(123);
		given(editor.getValue()).willReturn(editorValue);
		given(combobox.getSelectedItem()).willReturn(comboitem);
		given(comboitem.getValue()).willReturn(selectedUnit);
		given(selectedUnit.getConversionFactor()).willReturn(conversionFactor);

		// when
		final Number changedValue = numberWithClassificationUnitEditor.onValueChange(editor, combobox);

		// then
		assertThat(changedValue).isEqualTo(Double.valueOf(conversionFactor.doubleValue() * editorValue.intValue()));
	}

	private EditorContext prepareEditorContext()
	{
		return new EditorContext(Double.valueOf(0.0), null, new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>());
	}
}
