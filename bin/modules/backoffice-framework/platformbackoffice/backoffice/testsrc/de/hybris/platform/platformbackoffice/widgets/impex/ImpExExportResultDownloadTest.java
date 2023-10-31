/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.impex;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ImpExExportResultDownloadTest
{

	public static final String TEST_MIME = "x-test/data";
	public static final String MEDIA_CODE = "demo_media_code";

	@Spy
	@InjectMocks
	private ImpExExportResultDownload download;

	@Mock
	private MediaService mediaService;

	@Mock
	private ImpExMediaModel media;

	@Mock
	private InputStream stream;

	@Before
	public void setUp()
	{
		doNothing().when(download).executeBrowserMediaDownload(any(), anyString(), anyString());
		when(mediaService.getStreamFromMedia(media)).thenReturn(stream);
		when(media.getMime()).thenReturn(TEST_MIME);
		when(media.getCode()).thenReturn(MEDIA_CODE);
	}

	@Test
	public void executeMediaDownload()
	{
		//when
		download.executeMediaDownload(media);

		//then
		verify(download).executeBrowserMediaDownload(stream, TEST_MIME, MEDIA_CODE);

	}

}
