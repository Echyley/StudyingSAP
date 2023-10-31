/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.bulkedit.renderer.BulkEditAttributesSelectorRenderer;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.components.validation.EditorValidationFactory;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.editors.EditorRegistry;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultClassificationEditorBulkEditRendererTest
{

	protected WidgetInstanceManager wim;

	@Mock
	private ObjectFacade objectFacade;

	@Spy
	@InjectMocks
	DefaultClassificationEditorBulkEditRenderer renderer;


	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		wim = CockpitTestUtil.mockWidgetInstanceManager();

		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup()
				.registerBean("editorRegistry", mock(EditorRegistry.class)).registerBean("labelService", mock(LabelService.class))
				.registerBean("permissionFacade", mock(PermissionFacade.class)).registerBean("objectFacade", mock(ObjectFacade.class))
				.registerBean("typeFacade", mock(TypeFacade.class))
				.registerBean("editorValidationFactory", mock(EditorValidationFactory.class)).getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);
	}

	@Test
	public void shouldRenderClassificationEditorWithoutLocalizations() throws Exception
	{
		// given
		final Attribute selectedAttribute = new Attribute("classificationAttributePk", "My classification attribute", false);
		final Map<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(objectFacade.load("classificationAttributePk")).willReturn(assignmentModel);
		doReturn("featureQualifier").when(renderer).createFeatureQualifier(assignmentModel);
		doReturn("featureQualifierEncoded").when(renderer).createFeatureQualifierEncoded("featureQualifier");

		// when
		final Editor editor = renderer.render(wim, selectedAttribute, params);

		// then
		assertThat(editor).isNotNull();
		assertThat(editor.getType()).isEqualTo("Feature");
		assertThat(editor.getReadableLocales()).isEmpty();
		assertThat(editor.getWritableLocales()).isEmpty();
		assertThat(editor.isOptional()).isTrue();
		assertThat(editor.getProperty()).isEqualTo("bulkEditForm.templateObject['featureQualifierEncoded']");
		assertThat(editor.getParameters().get("editedPropertyQualifier")).isEqualTo("featureQualifierEncoded");
		assertThat(editor.getParameters().containsKey("allowInfiniteEndpoints")).isFalse();
	}

	@Test
	public void shouldRenderClassificationEditorWithLocalizations() throws Exception
	{
		// given
		final Attribute selectedAttributeInEn = new Attribute("classificationLocalizedAttributePkEn",
				"My localized classification attribute EN", "en", false);
		final Attribute selectedAttributeInDe = new Attribute("classificationLocalizedAttributePkDe",
				"My localized classification attribute DE", "de", false);
		final Attribute selectedAttribute = new Attribute("classificationLocalizedAttributePk",
				"My localized classification attribute", false);
		selectedAttribute.setSubAttributes(Set.of(selectedAttributeInEn, selectedAttributeInDe));
		final Map<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(objectFacade.load("classificationLocalizedAttributePk")).willReturn(assignmentModel);
		doReturn("localizedFeatureQualifier").when(renderer).createFeatureQualifier(assignmentModel);
		doReturn("localizedFeatureQualifierEncoded").when(renderer).createFeatureQualifierEncoded("localizedFeatureQualifier");

		// when
		final Editor editor = renderer.render(wim, selectedAttribute, params);

		// then
		assertThat(editor).isNotNull();
		assertThat(editor.getType()).isEqualTo("Feature");
		assertThat(editor.getReadableLocales()).contains(Locale.ENGLISH, Locale.GERMAN);
		assertThat(editor.getWritableLocales()).contains(Locale.ENGLISH, Locale.GERMAN);
		assertThat(editor.isOptional()).isTrue();
		assertThat(editor.getProperty()).isEqualTo("bulkEditForm.templateObject['localizedFeatureQualifierEncoded']");
		assertThat(editor.getParameters().get("editedPropertyQualifier")).isEqualTo("localizedFeatureQualifierEncoded");
		assertThat(editor.getParameters().containsKey("allowInfiniteEndpoints")).isFalse();
	}

	@Test
	public void shouldRenderMandatoryClassificationEditor() throws Exception
	{
		// given
		final Attribute selectedAttribute = new Attribute("mandatoryClassificationAttributePk",
				"My mandatory classification attribute", false);
		final Map<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(objectFacade.load("mandatoryClassificationAttributePk")).willReturn(assignmentModel);
		given(assignmentModel.getMandatory()).willReturn(true);
		doReturn("mandatoryFeatureQualifier").when(renderer).createFeatureQualifier(assignmentModel);
		doReturn("mandatoryFeatureQualifierEncoded").when(renderer).createFeatureQualifierEncoded("mandatoryFeatureQualifier");

		// when
		final Editor editor = renderer.render(wim, selectedAttribute, params);

		// then
		assertThat(editor).isNotNull();
		assertThat(editor.getType()).isEqualTo("Feature");
		assertThat(editor.isOptional()).isFalse();
		assertThat(editor.getProperty()).isEqualTo("bulkEditForm.templateObject['mandatoryFeatureQualifierEncoded']");
		assertThat(editor.getParameters().get("editedPropertyQualifier")).isEqualTo("mandatoryFeatureQualifierEncoded");
		assertThat(editor.getParameters().containsKey("allowInfiniteEndpoints")).isFalse();
	}


	@Test
	public void shouldRenderRangeClassificationEditor() throws Exception
	{
		// given
		final Attribute selectedAttribute = new Attribute("rangeClassificationAttributePk", "My range classification attribute",
				false);
		final Map<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(objectFacade.load("rangeClassificationAttributePk")).willReturn(assignmentModel);
		given(assignmentModel.getRange()).willReturn(true);
		doReturn("rangeFeatureQualifier").when(renderer).createFeatureQualifier(assignmentModel);
		doReturn("rangeFeatureQualifierEncoded").when(renderer).createFeatureQualifierEncoded("rangeFeatureQualifier");

		// when
		final Editor editor = renderer.render(wim, selectedAttribute, params);

		// then
		assertThat(editor).isNotNull();
		assertThat(editor.getType()).isEqualTo("Feature");
		assertThat(editor.getProperty()).isEqualTo("bulkEditForm.templateObject['rangeFeatureQualifierEncoded']");
		assertThat(editor.getParameters().get("editedPropertyQualifier")).isEqualTo("rangeFeatureQualifierEncoded");
		assertThat(editor.getParameters().containsKey("allowInfiniteEndpoints")).isTrue();
	}

	@Test
	public void shouldReuseValueFromModelInsteadOfInitializingIt() throws ObjectNotFoundException
	{
		// given
		final ClassificationInfo existingValue = mock(ClassificationInfo.class);
		final String anyQualifier = "anyQualifier";

		final Attribute selectedAttribute = new Attribute(anyQualifier, "Any attribute", false);

		final Map<String, String> params = new HashMap<>();
		params.put(BulkEditAttributesSelectorRenderer.PARAM_BULK_EDIT_FORM_MODEL_KEY, "bulkEditForm");
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);

		final String encodedQualifier = "ClassificationFeatureEncoded_RGVmYXVsdC9TdGFnZWQvY2F0LnN0cg_$$$_$$$";
		final String modelProperty = String.format("%s['%s']", "bulkEditForm.templateObject", encodedQualifier);

		given(existingValue.getValue()).willReturn(new Object());
		doReturn(anyQualifier).when(renderer).createFeatureQualifier(assignmentModel);
		doReturn(encodedQualifier).when(renderer).createFeatureQualifierEncoded(any());
		given(objectFacade.load(anyQualifier)).willReturn(assignmentModel);
		given(wim.getModel().getValue(modelProperty, ClassificationInfo.class)).willReturn(existingValue);

		// when
		final Editor editor = renderer.render(wim, selectedAttribute, params);

		// then
		assertThat(editor.getValue()).isEqualTo(existingValue);
	}

}
