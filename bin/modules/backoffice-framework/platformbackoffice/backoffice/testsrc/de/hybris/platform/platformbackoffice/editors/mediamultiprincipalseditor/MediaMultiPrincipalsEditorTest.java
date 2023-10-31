/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.mediamultiprincipalseditor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.media.MediaModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.editor.commonreferenceeditor.ReferenceEditorLayout;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;


public class MediaMultiPrincipalsEditorTest
		extends AbstractCockpitEditorRendererUnitTest<Collection<Object>, MediaMultiPrincipalsEditor<Object>>
{
	@Spy
	@InjectMocks
	private MediaMultiPrincipalsEditor<Object> editor;

	@Mock
	protected CockpitEventQueue cockpitEventQueue;

	@Mock
	private EditorContext editorContext;

	@Mock
	private Component parent;

	@Mock
	private EditorListener editorListener;

	@Mock
	protected ReferenceEditorLayout editorLayout;

	@Mock
	protected PermissionFacade permissionFacade;

	@Mock
	private Editor parentEditor;

	@Override
	public MediaMultiPrincipalsEditor getEditorInstance()
	{
		return new MediaMultiPrincipalsEditor();
	}

	@Test
	public void testChangeSelectedObject()
	{

		//given
		final Collection<Object> list = prepareOriginalItems();

		//when
		editor.changeSelectedObject(list);

		//then
		assertEquals(editor.originalItems, list);
	}

	@Test
	public void testAddSelectedObject()
	{

		//given
		final MediaModel mediaModel = mock(MediaModel.class);

		//when
		editor.addSelectedObject(mediaModel);
		editor.addSelectedObject(mediaModel);

		//then
		verify(editorLayout, times(1)).onAddSelectedObject(mediaModel, false);
		verify(editor, times(1)).onValueChanged(mediaModel, true);
	}

	@Test
	public void testRemoveSelectedObject()
	{

		//given
		final Collection<Object> list = prepareOriginalItems();
		editor.changeSelectedObject(list);

		//when
		editor.removeSelectedObject(list.toArray()[0]);

		//then
		verify(editorLayout, times(1)).onRemoveSelectedObject(list.toArray()[0], false);
		verify(editor, times(1)).onValueChanged(list.toArray()[0], false);
	}

	@Test
	public void testBeforeSave()
	{

		//when
		editor.beforeSave(null, null);

		//then
		verify(editorListener, times(1)).onValueChanged(any());
	}

	@Test
	public void testBeforeRefresh()
	{

		//when
		editor.beforeRefresh(null, null);

		//then
		verify(editorLayout, times(1)).clearSelection();
		verify(editor, times(1)).changeSelectedObject(editor.originalItems);
	}

	@Test
	public void testOnValueChanged()
	{

		//given
		final Object obj = new Object();
		//when
		editor.onValueChanged(obj, true);

		//then
		verify(cockpitEventQueue, times(1)).publishEvent(any());
	}

	@Test
	public void testOnAddSelectedObject()
	{
		//given
		final Collection<Object> list = prepareOriginalItems();
		editor.changeSelectedObject(list);
		final Map<String, Object> data = new HashMap<>();
		data.put("currentEditorIdentifier", list.toArray()[0].hashCode());
		data.put("currentObject", list.toArray()[0]);

		//when
		editor.onAddSelectedObject(data);

		//then
		verify(editor, times(1)).removeSelectedObject(list.toArray()[0]);
	}

	private Collection<Object> prepareOriginalItems()
	{
		final Collection<Object> list = new HashSet<>();
		list.add(mock(MediaModel.class));
		list.add(mock(MediaModel.class));
		return list;
	}

}
