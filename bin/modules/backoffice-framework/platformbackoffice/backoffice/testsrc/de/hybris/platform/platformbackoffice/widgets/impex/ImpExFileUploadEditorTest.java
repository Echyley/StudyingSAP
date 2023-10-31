/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.impex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorDefinition;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ImpExFileUploadEditorTest extends AbstractCockpitEditorRendererUnitTest<ImpExFileUploadResult, ImpExFileUploadEditor>
{
	@Spy
	@InjectMocks
	private ImpExFileUploadEditor impExFileUploadEditor;
	@Mock
	private EditorDefinition editorDefinition;
	@Mock
	private EditorListener<ImpExFileUploadResult> editorListener;
	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;
	@Mock
	private ImpExMediaModel impExMediaModel;
	@Mock
	private MediaExtractor mediaExtractor;
	@Mock
	private Editor editor;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private ImpExImportForm impExImportForm;
	@Mock
	private WidgetModel widgetModel;
	@Mock
	private Media media;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		when(modelService.create(ImpExMediaModel.class)).thenReturn(impExMediaModel);
		when(mediaExtractor.getDataAsString()).thenReturn("data as string");
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		when(widgetModel.getValue(any(), any())).thenReturn(impExImportForm);
		when(media.getName()).thenReturn("mediaName");
		when(media.isBinary()).thenReturn(false);
		when(media.getStringData()).thenReturn("data in media");

		doReturn(new Div()).when(impExFileUploadEditor).createMainLayout();
		doReturn(new Div()).when(impExFileUploadEditor).createUploadToolBarLayout();
	}

	@Test
	public void shouldCreateMediaFromUploadedFile()
	{
		//given
		final Editor parent = new Editor();
		parent.setWidgetInstanceManager(widgetInstanceManager);

		impExFileUploadEditor.render(parent, createEditorContext(), editorListener);
		//when
		CockpitTestUtil.simulateEvent(impExFileUploadEditor.getCreateMediaButton(), Events.ON_CLICK, null);
		//then
		assertThat(impExFileUploadEditor.getUploadButton()).isNotNull();
		verify(mediaService).setStreamForMedia(any(), any(), any(), any());
		verify(modelService, times(2)).save(any());
	}

	@Test
	public void shouldUploadMedia()
	{
		//given
		final Editor parent = new Editor();
		parent.setWidgetInstanceManager(widgetInstanceManager);
		final UploadEvent uploadEvent = new UploadEvent("onUpload", impExFileUploadEditor.getUploadButton(), new Media[]
		{ media });

		impExFileUploadEditor.render(parent, createEditorContext(), editorListener);
		//when
		CockpitTestUtil.simulateEvent(impExFileUploadEditor.getUploadButton(), uploadEvent);
		//then
		assertThat(impExFileUploadEditor.getMediaExtractor()).isNotEqualTo(mediaExtractor);
		verify(media).getStringData();
	}

	@Test
	public void shouldResetUploadedFile()
	{
		//given
		final Editor parent = new Editor();
		parent.setWidgetInstanceManager(widgetInstanceManager);
		final EditorContext<ImpExFileUploadResult> context = createEditorContext();

		impExFileUploadEditor.render(parent, context, editorListener);
		//when
		CockpitTestUtil.simulateEvent(impExFileUploadEditor.getResetButton(), Events.ON_CLICK, null);
		//then
		assertThat(impExFileUploadEditor.getUploadButton().isDisabled()).isFalse();
		assertThat(impExFileUploadEditor.getCreateMediaButton().isDisabled()).isTrue();
		verify(context, times(4)).getLabel(any());
	}

	@Test
	public void shouldShowPreview()
	{
		//given
		final Editor parent = new Editor();
		parent.setWidgetInstanceManager(widgetInstanceManager);
		final EditorContext<ImpExFileUploadResult> context = createEditorContext();

		impExFileUploadEditor.render(parent, context, editorListener);
		//when
		CockpitTestUtil.simulateEvent(impExFileUploadEditor.getPreviewButton(), Events.ON_CLICK, null);
		//then
		assertThat(impExFileUploadEditor.getUploadButton().isDisabled()).isFalse();
		assertThat(impExFileUploadEditor.getCreateMediaButton().isDisabled()).isTrue();
		verify(context, times(5)).getLabel(any());
	}

	@Override
	public ImpExFileUploadEditor getEditorInstance()
	{
		return impExFileUploadEditor;
	}

	private EditorContext<ImpExFileUploadResult> createEditorContext()
	{
		final EditorContext<ImpExFileUploadResult> context = new EditorContext<>(new ImpExFileUploadResult(), editorDefinition,
				new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>());

		context.setParameter("bindProperty", "property");
		context.setParameter("preview", "true");

		return spy(context);
	}
}
