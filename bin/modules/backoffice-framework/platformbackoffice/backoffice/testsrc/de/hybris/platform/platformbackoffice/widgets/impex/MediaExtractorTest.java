/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.impex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.media.Media;


@RunWith(MockitoJUnitRunner.class)
public class MediaExtractorTest
{
	private static final String LONG_STRING = "long long long long\nlong long long long\nnew line new line\n new line";
	private static final String NAME = "newMedia.bin";
	private static final String CONTENT_TYPE = "contentType";

	@InjectMocks
	private MediaExtractor mediaExtractor;
	@Mock
	private Media media;

	@Before
	public void setUp() throws IOException
	{
		when(media.getName()).thenReturn(NAME);
		when(media.getContentType()).thenReturn(CONTENT_TYPE);

		when(media.getStringData()).thenReturn(LONG_STRING);
		when(media.getStreamData()).thenReturn(IOUtils.toInputStream(LONG_STRING, StandardCharsets.UTF_8));
	}

	@Test
	public void shouldReturnTextDataAsString()
	{
		//given
		when(media.isBinary()).thenReturn(false);
		//when
		final String data = mediaExtractor.getDataAsString();
		//then
		assertThat(data).isEqualTo(LONG_STRING);
	}

	@Test
	public void shouldReturnBinaryDataAsString()
	{
		//given
		when(media.isBinary()).thenReturn(true);
		//when
		final String data = mediaExtractor.getDataAsString();
		//then
		assertThat(data).isEqualTo(LONG_STRING);
	}


	@Test
	public void shouldReturnTextDataAsStream() throws IOException
	{
		//given
		when(media.isBinary()).thenReturn(false);
		//when
		final InputStream stream = mediaExtractor.getDataAsStream();
		//then
		assertThat(IOUtils.toString(stream)).isEqualTo(LONG_STRING);
	}

	@Test
	public void shouldReturnBinaryDataAsStream() throws IOException
	{
		//given
		when(media.isBinary()).thenReturn(true);
		//when
		final InputStream stream = mediaExtractor.getDataAsStream();
		//then
		assertThat(IOUtils.toString(stream)).isEqualTo(LONG_STRING);
	}

	@Test
	public void shouldReturnSizeOfData()
	{
		//given
		when(media.isBinary()).thenReturn(true);
		//when
		final long size = mediaExtractor.getDataSize();
		//then
		assertThat(size).isEqualTo(LONG_STRING.length());
	}

	@Test
	public void shouldReturnName()
	{
		//when
		final String name = mediaExtractor.getName();
		//then
		assertThat(name).isEqualTo(NAME);
	}

	@Test
	public void shouldReturnTargetName()
	{
		//when
		final String targetName = mediaExtractor.getTargetName();
		//then
		assertThat(targetName).containsPattern("upload_newMedia_.*\\.bin");
	}

	@Test
	public void shouldReturnContentType()
	{
		//when
		final String name = mediaExtractor.getContentType();
		//then
		assertThat(name).isEqualTo(CONTENT_TYPE);
	}

}
