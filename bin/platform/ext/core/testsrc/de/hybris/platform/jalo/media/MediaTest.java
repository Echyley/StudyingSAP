/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.jalo.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MediaTest
{
	@Spy
	private final TestMedia media = new TestMedia();
	private final PK pk = PK.createFixedUUIDPK(10, 1);
	private final Long dataPK = 123456789L;
	@Mock
	private JaloSession jaloSession;
	@Mock
	private SessionContext sessionContext;
	@Mock
	private MediaFolder mediaFolder;

	@Before
	public void setUp() throws Exception
	{
		doReturn(jaloSession).when(media).getSession();
		doReturn(sessionContext).when(jaloSession).getSessionContext();
		doReturn(mediaFolder).when(media).getFolder();
		doReturn("somePath").when(mediaFolder).getPath();
		doReturn(Boolean.TRUE).when(media).hasData();
	}

	@Test
	public void shouldReturnLocationAssembledFromPKAndExtensionForRealRealOldMedia()
	{
		// given
		doReturn(null).when(media).getProperty(sessionContext, Media.DATAPK);
		doReturn(null).when(media).getProperty(sessionContext, Media.LOCATION);
		doReturn(pk).when(media).getPK();

		// when
		final String location = media.getLocation();

		// then
		assertThat(location).isNotNull().isEqualTo("somePath/2814749767106576.jpg");
	}

	@Test
	public void shouldReturnLocationAssembledFromDataPKAndExtensionForRealOldMedia()
	{
		// given
		doReturn(dataPK).when(media).getProperty(sessionContext, Media.DATAPK);
		doReturn(null).when(media).getProperty(sessionContext, Media.SUBFOLDERPATH);
		doReturn(null).when(media).getProperty(sessionContext, Media.LOCATION);

		// when
		final String location = media.getLocation();

		// then
		assertThat(location).isNotNull().isEqualTo("somePath/123456789.jpg");
	}

	@Test
	public void shouldReturnLocationAssembledFromSubFolderDataPKAndExtensionForOldMedia()
	{
		// given
		doReturn(dataPK).when(media).getProperty(sessionContext, Media.DATAPK);
		doReturn("h08/h0f/").when(media).getProperty(sessionContext, Media.SUBFOLDERPATH);
		doReturn(null).when(media).getProperty(sessionContext, Media.LOCATION);

		// when
		final String location = media.getLocation();

		// then
		assertThat(location).isNotNull().isEqualTo("somePath/h08/h0f/123456789.jpg");
	}

	@Test
	public void shouldReturnNormalLocationForNewMedia()
	{
		// given
		doReturn(dataPK).when(media).getProperty(sessionContext, Media.DATAPK);
		doReturn("somePath/h08/h0f/123456789.jpg").when(media).getProperty(sessionContext, Media.LOCATION);

		// when
		final String location = media.getLocation();

		// then
		assertThat(location).isNotNull().isEqualTo("somePath/h08/h0f/123456789.jpg");
	}

	public static class TestMedia extends Media
	{
		@Override
		protected String getFileExtensionFromMime()
		{
			return "jpg";
		}
	}
}
