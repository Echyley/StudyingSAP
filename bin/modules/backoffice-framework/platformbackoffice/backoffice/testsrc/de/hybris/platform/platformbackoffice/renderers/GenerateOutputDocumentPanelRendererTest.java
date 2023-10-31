/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.commons.jalo.Document;
import de.hybris.platform.commons.jalo.Format;
import de.hybris.platform.commons.model.DocumentModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.ui.WidgetInstance;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.impl.DefaultObjectFacade;
import com.hybris.cockpitng.dataaccess.util.CockpitGlobalEventPublisher;
import com.hybris.cockpitng.editors.EditorRegistry;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class GenerateOutputDocumentPanelRendererTest
{

	@Spy
	@InjectMocks
	private GenerateOutputDocumentPanelRenderer renderer;

	@Mock
	private WidgetInstanceManager wim;

	@Mock
	private Combobox combo;

	@Mock
	private Button trigger;

	@Mock
	private WidgetModel model;

	@Mock
	private Widgetslot slot;

	@Mock
	private WidgetInstance widget;

	@Mock
	private ItemModel itemModel;

	@Mock
	private Item jalo;

	@Mock
	private Format format;

	@Mock
	private ModelService modelService;

	@Mock
	private CockpitProperties cockpitProperties;

	@Mock
	private CockpitGlobalEventPublisher eventPublisher;

	@Mock
	private EditorRegistry editorRegistry;

	@Before
	public void setUp()
	{
		when(wim.getModel()).thenReturn(model);
		when(wim.getWidgetslot()).thenReturn(slot);
		when(slot.getWidgetInstance()).thenReturn(widget);
		when(widget.getId()).thenReturn("my-id");
		when(modelService.getSource(itemModel)).thenReturn(jalo);
		doReturn(StringUtils.EMPTY).when(renderer).getLabel(anyString());
	}

	@Test
	public void handleModelDataChangedShouldDisableFieldsOnEmptySelection()
	{
		//given
		when(model.getValue(GenerateOutputDocumentPanelRenderer.MODEL_DATA_QUALIFIER, Object.class)).thenReturn(null);

		//when
		renderer.handleModelDataChanged(wim, combo, trigger);

		//then
		verify(combo).setDisabled(true);
		verify(trigger).setDisabled(true);
	}

	@Test
	public void shouldFlawlesslyHandleFormatSelectionWhenNothingSelected()
	{
		//given
		final Editor editor = mock(Editor.class);

		when(combo.getSelectedIndex()).thenReturn(-1);

		//when
		renderer.handleFormatSelection(wim, editor, combo, trigger);

		//verify

		final ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);

		verify(model).setValue(eq("filteredDocuments"), captor.capture());
		verify(editor).reload();

		final Collection value = captor.getValue();
		assertThat(value).isEmpty();
	}

	@Test
	public void handleFormatSelection()
	{
		//given
		final Editor editor = mock(Editor.class);

		final Comboitem comboItem = mock(Comboitem.class);

		when(combo.getItemAtIndex(0)).thenReturn(comboItem);
		when(comboItem.getValue()).thenReturn(format);

		when(model.getValue(GenerateOutputDocumentPanelRenderer.MODEL_DATA_QUALIFIER, Object.class)).thenReturn(itemModel);

		final Document doc1 = mock(Document.class);
		final Document doc2 = mock(Document.class);


		final DocumentModel docModel1 = mock(DocumentModel.class);
		final DocumentModel docModel2 = mock(DocumentModel.class);

		doReturn(docModel1).when(modelService).toModelLayer(doc1);
		doReturn(docModel2).when(modelService).toModelLayer(doc2);

		doReturn(Arrays.asList(doc1, doc2)).when(renderer).findDocumentsOfFormat(format, jalo);

		//when
		renderer.handleFormatSelection(wim, editor, combo, trigger);

		//verify

		final ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);

		verify(model).setValue(eq("filteredDocuments"), captor.capture());
		verify(editor).reload();

		final Collection value = captor.getValue();
		assertThat(value).contains(docModel1, docModel2);
	}

	@Test
	public void executeDocumentCreation() throws JaloBusinessException
	{
		//given
		when(combo.getSelectedIndex()).thenReturn(0);

		final Comboitem comboItem = mock(Comboitem.class);

		when(combo.getItemAtIndex(0)).thenReturn(comboItem);
		when(comboItem.getValue()).thenReturn(format);

		when(model.getValue(GenerateOutputDocumentPanelRenderer.MODEL_DATA_QUALIFIER, Object.class)).thenReturn(itemModel);

		doReturn(null).when(renderer).createDocumentOfFormat(any(), any());

		//when
		final Editor dynamicDocumentList = mock(Editor.class);
		renderer.executeDocumentCreation(wim, combo, dynamicDocumentList);

		//then
		verify(renderer).createDocumentOfFormat(format, jalo);
		verify(renderer).notifyObjectUpdated(jalo);
		verify(dynamicDocumentList).reload();
	}

	@Test
	public void prepareDynamicList()
	{
		//given
		CockpitTestUtil.mockZkEnvironment();
		CockpitTestUtil.mockBeanLookup(BeanLookupFactory.createBeanLookup().registerBean("editorRegistry", editorRegistry));

		//when
		final Editor editor = renderer.prepareDynamicDocumentList(wim);

		//then
		assertThat(editor.isReadOnly()).isTrue();
		assertThat(editor.isNestedObjectCreationDisabled()).isTrue();
		assertThat(editor.getType()).isEqualTo("ExtendedMultiReference-COLLECTION(Document)");
		assertThat(editor.getDefaultEditor()).isEqualTo("com.hybris.cockpitng.editor.extendedmultireferenceeditor");
		assertThat(editor.getProperty()).isEqualTo("filteredDocuments");
		assertThat(editor.getParameters().get("listConfigContext")).isEqualTo("outDocEditorList");
	}

	@Test
	public void populateComboWithFormats()
	{
		//given
		doReturn(Collections.singleton(format)).when(renderer).getFormatsForItem(jalo);

		//when
		renderer.populateComboWithFormats(combo, itemModel);

		//then
		final ArgumentCaptor<ListModelList> captor = ArgumentCaptor.forClass(ListModelList.class);
		verify(combo).setModel(captor.capture());
		final ListModelList value = captor.getValue();
		assertThat(value).contains(format);
		assertThat(value).hasSize(1);
	}

	@Test
	public void notifyObjectUpdatedShouldSendEventIfEnabled()
	{
		//given
		when(cockpitProperties.getProperty(DefaultObjectFacade.COCKPITNG_CRUD_COCKPIT_EVENT_NOTIFICATION))
				.thenReturn(Boolean.TRUE.toString());
		final Object object = new Object();

		//when
		renderer.notifyObjectUpdated(object);

		//then
		verify(eventPublisher).publish(eq(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT), eq(object), any());
	}
}
