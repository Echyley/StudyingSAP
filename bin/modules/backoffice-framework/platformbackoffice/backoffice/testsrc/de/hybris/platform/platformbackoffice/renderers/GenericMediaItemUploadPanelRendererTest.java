/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.platformbackoffice.renderers.util.DateFormatter;
import de.hybris.platform.servicelayer.media.MediaService;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Label;

import com.google.common.collect.Streams;
import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacadeStrategy;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.services.media.MimeTypeChecker;
import com.hybris.cockpitng.services.media.ObjectPreview;
import com.hybris.cockpitng.services.media.ObjectPreviewService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class GenericMediaItemUploadPanelRendererTest extends AbstractCockpitngUnitTest<GenericMediaItemUploadPanelRenderer>
{
	private static final String SPECIFIC_TYPE = "specificType";
	public static final String CREATION_TIME = "creationtime";
	public static final String MODIFIED_TIME = "modifiedtime";

	@InjectMocks
	@Spy
	private GenericMediaItemUploadPanelRenderer renderer;
	@Mock
	private PermissionFacadeStrategy permissionFacadeStrategy;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private MediaModel media;

	@Mock
	private MimeTypeChecker mimeTypeChecker;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private ObjectPreviewService objectPreviewService;
	@Mock
	private MediaService mediaService;
	@Mock
	private DateFormatter dateFormatter;

	@Before
	public void setUp()
	{
		final DefaultWidgetModel widgetModel = mock(DefaultWidgetModel.class);
		when(widgetModel.getValue(any(), any())).thenReturn(new HashMap());
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		when(renderer.getWidgetInstanceManager()).thenReturn(widgetInstanceManager);
		when(renderer.getCockpitProperties()).thenReturn(mock(CockpitProperties.class));
		doReturn(media).when(renderer).getMediaModel();
		doReturn(true).when(permissionFacadeStrategy).canReadType(SPECIFIC_TYPE);
	}

	@Test
	public void shouldCheckSpecificDataTypeForPermissions()
	{
		final DataType dataType = mock(DataType.class);
		final Component component = mock(Component.class);
		when(dataType.getCode()).thenReturn(SPECIFIC_TYPE);

		renderer.initializeActiveView(component, dataType);

		verify(permissionFacadeStrategy).canReadType(SPECIFIC_TYPE);
		verify(permissionFacadeStrategy).canReadInstance(media);
	}

	@Test
	public void initFileUploadShouldSetUploadFieldAsDisabledWithNoOnUploadListeners()
	{
		//given
		final Component parent = mock(Component.class);
		doReturn(false).when(permissionFacade).canChangeInstance(media);

		//when
		renderer.initFileUpload(parent, null);

		//then
		final ArgumentCaptor<Fileupload> uploadCaptor = ArgumentCaptor.forClass(Fileupload.class);
		verify(parent).appendChild(uploadCaptor.capture());

		assertThat(uploadCaptor.getValue().isDisabled()).isTrue();
		verify(parent, times(0)).addEventListener(anyString(), any());
	}

	@Test
	public void shouldErrorNotificationNotBeShownWhenMediaContentTypeIsAllowed()
	{
		// given
		final Component panel = new Div();
		final Component parent = new Div();
		final Component grandparent = new Div();
		final String acceptParam = "image/*|audio/*";
		grandparent.setClientAttribute(GenericMediaItemUploadPanelRenderer.PARAM_ACCEPT, acceptParam);
		panel.setParent(parent);
		parent.setParent(grandparent);
		final DataType dataType = mock(DataType.class);
		when(permissionFacade.canChangeInstance(any())).thenReturn(true);

		final Media zkMedia = mock(Media.class);
		final String contentType = "image/gif";
		when(mimeTypeChecker.isMediaAcceptable(zkMedia, acceptParam)).thenReturn(true);

		// when
		renderer.initFileUpload(panel, dataType);
		final Fileupload fileupload = (Fileupload) panel.query("fileupload");
		CockpitTestUtil.simulateEvent(fileupload, new UploadEvent(Events.ON_UPLOAD, fileupload, new Media[]
		{ zkMedia }));

		// then
		verify(renderer, never()).showUnsupportedMediaMessage(any(), any());
	}

	@Test
	public void shouldErrorNotificationBeShownWhenMediaContentTypeIsNotAllowed()
	{
		// given
		final Component panel = new Div();
		final Component parent = new Div();
		final Component grandparent = new Div();
		final String acceptParam = "|audio/*";
		grandparent.setClientAttribute(GenericMediaItemUploadPanelRenderer.PARAM_ACCEPT, acceptParam);
		panel.setParent(parent);
		parent.setParent(grandparent);
		final DataType dataType = mock(DataType.class);
		when(permissionFacade.canChangeInstance(any())).thenReturn(true);

		final Media zkMedia = mock(Media.class);
		final String contentType = "image/gif";
		when(mimeTypeChecker.isMediaAcceptable(zkMedia, acceptParam)).thenReturn(false);
		doNothing().when(renderer).showUnsupportedMediaMessage(any(), any());

		// when
		renderer.initFileUpload(panel, dataType);
		final Fileupload fileupload = (Fileupload) panel.query("fileupload");
		CockpitTestUtil.simulateEvent(fileupload, new UploadEvent(Events.ON_UPLOAD, fileupload, new Media[]
		{ zkMedia }));

		// then
		verify(renderer).showUnsupportedMediaMessage(any(), any());
	}

	@Test
	public void shouldUseDateFormatterToRenderMediaCreationAndModificationDates()
	{
		//given
		final Component panel = new Div();
		final Component parent = new Div();
		final Component grandparent = new Div();
		panel.setParent(parent);
		parent.setParent(grandparent);

		final DataType dataType = mock(DataType.class);
		when(dataType.getCode()).thenReturn(SPECIFIC_TYPE);
		when(permissionFacade.canChangeInstance(any())).thenReturn(false);
		when(permissionFacadeStrategy.canReadInstance(media)).thenReturn(true);

		final DefaultWidgetModel widgetModel = mock(DefaultWidgetModel.class);
		when(widgetModel.getValue(startsWith("render_parameter_map_"), any())).thenReturn(new HashMap());
		when(widgetModel.getValue(startsWith("zkMedia_tmp_"), any())).thenReturn(null);
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		when(renderer.getWidgetInstanceManager()).thenReturn(widgetInstanceManager);


		when(media.getCreationtime()).thenReturn(Date.from(ZonedDateTime.now().toInstant()));
		when(media.getModifiedtime()).thenReturn(Date.from(ZonedDateTime.now().plusSeconds(1).toInstant()));
		when(objectFacade.isNew(any())).thenReturn(false);
		when(media.getPk()).thenReturn(PK.fromLong(1));
		final ObjectPreview objectPreview = mock(ObjectPreview.class);
		when(objectPreview.isFallback()).thenReturn(true);
		when(objectPreviewService.getPreview(any())).thenReturn(objectPreview);
		when(mediaService.hasData(media)).thenReturn(false);
		final Locale locale = Locale.getDefault();
		final String format = "HH:mm:ss";
		final CockpitLocaleService cockpitLocaleService = mock(CockpitLocaleService.class);
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(locale);
		renderer.setCockpitLocaleService(cockpitLocaleService);
		renderer.setFormat(format);
		when(dateFormatter.format(media.getCreationtime(), format, locale)).thenReturn(CREATION_TIME);
		when(dateFormatter.format(media.getModifiedtime(), format, locale)).thenReturn(MODIFIED_TIME);

		//when
		renderer.render(parent, null, media, dataType, widgetInstanceManager);

		//then
		verify(dateFormatter, times(2)).format(any(Date.class), eq(format), eq(locale));
		assertThat(Streams.stream(parent.queryAll("Label")).map(Label.class::cast).map(Label::getValue)).contains(CREATION_TIME,
				MODIFIED_TIME);
	}

}
