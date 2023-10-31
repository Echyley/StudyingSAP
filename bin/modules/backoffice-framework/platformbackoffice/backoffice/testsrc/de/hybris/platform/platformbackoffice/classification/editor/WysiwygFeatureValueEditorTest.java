/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */

package de.hybris.platform.platformbackoffice.classification.editor;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.platformbackoffice.classification.editor.WysiwygFeatureValueEditor;
import org.junit.Test;

import static de.hybris.platform.platformbackoffice.classification.editor.WysiwygFeatureEditor.EDITOR_PARAM_ENABLE_WYSIWYG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class WysiwygFeatureValueEditorTest
{

	WysiwygFeatureValueEditor wysiwygFeatureValueEditor = new WysiwygFeatureValueEditor();

	@Test
	public void shouldEnableWysiwygEditorWhenParamIsSet()
	{
		// given
		final EditorContext<FeatureValue> editorContextWithWysiwygEnabled = mock(EditorContext.class);
		given(editorContextWithWysiwygEnabled.getParameterAsBoolean(EDITOR_PARAM_ENABLE_WYSIWYG, false)).willReturn(true);

		// when
		final Editor editor = wysiwygFeatureValueEditor.prepareEditor(editorContextWithWysiwygEnabled);

		// then
		assertThat(editor.getDefaultEditor()).isEqualTo("com.hybris.cockpitng.editor.wysiwyg");
	}

	@Test
	public void shouldNotEnableWysiwygEditorWhenParamIsNotSet()
	{
		// given
		final EditorContext<FeatureValue> editorContextWithoutWysiwyg = mock(EditorContext.class);

		// when
		final Editor editor = wysiwygFeatureValueEditor.prepareEditor(editorContextWithoutWysiwyg);

		// then
		assertThat(editor.getDefaultEditor()).isNull();
	}
}
