/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */

package de.hybris.platform.platformbackoffice.classification.editor;

import static de.hybris.platform.platformbackoffice.classification.editor.WysiwygFeatureEditor.EDITOR_PARAM_ENABLE_WYSIWYG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;

import de.hybris.platform.platformbackoffice.classification.editor.WysiwygFeatureEditor;
import org.junit.Test;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;


public class WysiwygFeatureEditorTest
{

	WysiwygFeatureEditor wysiwygFeatureEditor = new WysiwygFeatureEditor();

	@Test
	public void shouldAddWysiwygParamToEditorWhenClassificationHasWysiwygFeatureEnabled()
	{
		// given
		final ClassAttributeAssignmentModel assignmentWithWysiwygEnabled = mock(ClassAttributeAssignmentModel.class);
		given(assignmentWithWysiwygEnabled.getEnableWysiwygEditor()).willReturn(true);

		final EditorContext<ClassificationInfo> editorContext = prepareEditorContextWithAssignment(assignmentWithWysiwygEnabled);

		// when
		final Editor editor = wysiwygFeatureEditor.prepareEditor(editorContext);

		// then
		assertThat(editor.getParameters()).containsEntry(EDITOR_PARAM_ENABLE_WYSIWYG, true);
	}

	@Test
	public void shouldNotAddWysiwygParamWhenClassificationHasWysiwygDisabled()
	{
		// given
		final ClassAttributeAssignmentModel assignmentWithWysiwygDisabled = mock(ClassAttributeAssignmentModel.class);
		final EditorContext<ClassificationInfo> editorContext = prepareEditorContextWithAssignment(assignmentWithWysiwygDisabled);

		// when
		final Editor editor = wysiwygFeatureEditor.prepareEditor(editorContext);

		// then
		assertThat(editor.getParameters()).doesNotContainKey(EDITOR_PARAM_ENABLE_WYSIWYG);
	}

	@Test
	public void shouldNotAddWysiwygParamWhenClassificationHasWysiwygAttributeSetToNull()
	{
		// given
		final ClassAttributeAssignmentModel assignmentWithWysiwygNull = mock(ClassAttributeAssignmentModel.class);
		given(assignmentWithWysiwygNull.getEnableWysiwygEditor()).willReturn(null);
		final EditorContext<ClassificationInfo> editorContext = prepareEditorContextWithAssignment(assignmentWithWysiwygNull);

		// when
		final Editor editor = wysiwygFeatureEditor.prepareEditor(editorContext);

		// then
		assertThat(editor.getParameters()).doesNotContainKey(EDITOR_PARAM_ENABLE_WYSIWYG);
	}

	private EditorContext<ClassificationInfo> prepareEditorContextWithAssignment(
			final ClassAttributeAssignmentModel assignmentWithWysiwygEnabled)
	{
		final ClassificationInfo classificationInfo = mock(ClassificationInfo.class);
		given(classificationInfo.getAssignment()).willReturn(assignmentWithWysiwygEnabled);

		final EditorContext<ClassificationInfo> editorContext = mock(EditorContext.class);
		given(editorContext.getInitialValue()).willReturn(classificationInfo);
		return editorContext;
	}
}
