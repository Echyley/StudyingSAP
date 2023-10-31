/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.mediamultiprincipalsbulkediteditor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;


public class MediaMultiPrincipalsBulkEditEditorTest
		extends AbstractCockpitEditorRendererUnitTest<Collection<Object>, MediaMultiPrincipalsBulkEditEditor<Object>>
{
	@Spy
	@InjectMocks
	private MediaMultiPrincipalsBulkEditEditor<Object> editor;

	@Mock
	private Component parent;

	@Mock
	private EditorListener editorListener;

	@Override
	public MediaMultiPrincipalsBulkEditEditor getEditorInstance()
	{
		return new MediaMultiPrincipalsBulkEditEditor();
	}

	@Before
	public void setUp()
	{
		editor.setEditorContext(prepareEditorContext("permittedPrincipals"));
	}

	@Test
	public void testCanHandle()
	{
		//then
		assertThat(editor.canHandle("permittedPrincipals")).isTrue();
	}

	private EditorContext prepareEditorContext(final String qualifier)
	{
		final EditorContext editorContext = new EditorContext(Double.valueOf(0.0), null, new HashMap<>(), new HashMap<>(),
				new HashSet<>(), new HashSet<>());
		editorContext.setParameter(Editor.EDITOR_PROPERTY, String.format("%s.%s", editor.BULK_EDIT_MODEL_PREFIX, qualifier));
		editorContext.setParameter(Editor.MODEL_PREFIX, editor.BULK_EDIT_MODEL_PREFIX);
		return editorContext;
	}
}
