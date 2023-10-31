/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.media.impl;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaContainer;
import de.hybris.platform.jalo.media.MediaFormat;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMediaServiceTest
{
	private DefaultMediaService mediaService;

	@Mock
	private ModelService mockModelService;
	@Mock
	private SessionService mockSessionService;
	@Mock
	private MediaDao mockMediaDao;
	@Mock
	private MediaContainer mockMediaContainer;
	@Mock
	private MediaFormat mockMediaFormat;
	@Mock
	private MediaModel mockMediaModel;
	@Mock
	private Media mockMedia;

	@Before
	public void setUp() throws Exception
	{
		mediaService = new DefaultMediaService();
		mediaService.setModelService(mockModelService);
		mediaService.setMediaDao(mockMediaDao);
		mediaService.setSessionService(mockSessionService);
	}

	@Test
	public void testModelNotFoundExceptionWhenMediaContainerModelIsNull()
	{
		// Given
		final MediaContainerModel mockMediaContainerModel = mock(MediaContainerModel.class);
		final MediaFormatModel mockMediaFormatModel = mock(MediaFormatModel.class);
		Mockito.lenient().when(mockModelService.getSource(mockMediaContainerModel)).thenReturn(mockMediaContainer);
		Mockito.lenient().when(mockModelService.getSource(mockMediaFormatModel)).thenReturn(mockMediaFormat);
		Mockito.lenient().when(mockModelService.get(mockMedia)).thenReturn(mockMediaModel);
		Mockito.lenient().when(mockMediaContainer.getMedia(mockMediaFormat)).thenReturn(mockMedia);

		// When
		try
		{
			mediaService.getMediaByFormat(mockMediaContainerModel, mockMediaFormatModel);
		}
		catch (final ModelNotFoundException e)
		{
			// OK
		}
	}

	@Test
	public void testModelNotFoundExceptionWhenMediaFormatModelIsNull()
	{
		// Given
		final MediaContainerModel mockMediaContainerModel = mock(MediaContainerModel.class);
		final MediaFormatModel mockMediaFormatModel = mock(MediaFormatModel.class);
		Mockito.lenient().when(mockModelService.getSource(mockMediaContainerModel)).thenReturn(mockMediaContainer);
		Mockito.lenient().when(mockModelService.getSource(mockMediaFormatModel)).thenReturn(mockMediaFormat);
		Mockito.lenient().when(mockModelService.get(mockMedia)).thenReturn(mockMediaModel);
		Mockito.lenient().when(mockMediaContainer.getMedia(mockMediaFormat)).thenReturn(mockMedia);

		// When
		try
		{
			mediaService.getMediaByFormat(mockMediaContainerModel, mockMediaFormatModel);
		}
		catch (final ModelNotFoundException e)
		{
			// OK
		}
	}

	@Test
	public void testModelNotFoundExceptionWhenMediaFormatNotFoundInContainer()
	{
		// Given
		final MediaContainerModel mockMediaContainerModel = mock(MediaContainerModel.class);
		final MediaFormatModel mockMediaFormatModel = mock(MediaFormatModel.class);
		Mockito.lenient().when(mockModelService.getSource(mockMediaContainerModel)).thenReturn(mockMediaContainer);
		Mockito.lenient().when(mockModelService.getSource(mockMediaFormatModel)).thenReturn(mockMediaFormat);
		Mockito.lenient().when(mockModelService.get(mockMedia)).thenReturn(null);
		Mockito.lenient().when(mockMediaContainer.getMedia(mockMediaFormat)).thenReturn(mockMedia);

		// When
		try
		{
			mediaService.getMediaByFormat(mockMediaContainerModel, mockMediaFormatModel);
		}
		catch (final ModelNotFoundException e)
		{
			// OK
		}
	}

	@Test
	public void testGetFilesWithMediaModel()
	{
		// Given
		final Collection<File> mockCollection = mock(Collection.class);
		Mockito.lenient().when(mockModelService.getSource(mockMediaModel)).thenReturn(mockMedia);
		Mockito.lenient().when(mockMedia.getFiles()).thenReturn(mockCollection);

		// When
		final Collection<File> result = mediaService.getFiles(mockMediaModel);

		Assert.assertNotNull(result);
	}

	@Test
	public void testNoDataAvailableExceptionWithNullMediaModel()
	{
		// Given
		final MediaModel mediaModel = null;

		// When
		try
		{
			mediaService.getFiles(mediaModel);
		}
		catch (final IllegalArgumentException illegalArgumentException)
		{
			// OK
		}
	}
}
