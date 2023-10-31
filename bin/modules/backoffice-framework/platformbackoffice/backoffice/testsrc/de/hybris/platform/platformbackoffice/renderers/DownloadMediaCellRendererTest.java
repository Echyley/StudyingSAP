/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DownloadMediaCellRendererTest
{

	@Spy
	private DownloadMediaCellRenderer renderer;

	@Mock
	private MediaModel media;

	@Test
	public void extractFileNameShouldReturnMediasFileName()
	{
		//given
		final String realName = "real-name";
		when(media.getRealFileName()).thenReturn(realName);

		//when
		final String extractFileName = renderer.extractFileName(media);

		//then
		assertThat(extractFileName).isEqualTo(realName);
	}

    @Test
    public void extractFileNameShouldReturnFallbackForBlankMediasFileName()
    {
        //given
        when(media.getRealFileName()).thenReturn("\t\n");
        final String fallbackName = "fallbackName";
        doReturn(fallbackName).when(renderer).createFallbackFileName(media);

        //when
        final String extractFileName = renderer.extractFileName(media);

        //then
        assertThat(extractFileName).isEqualTo(fallbackName);
    }

	@Test
	public void shouldReturnMediaPkAsFallbackName()
	{
		when(media.getPk()).thenReturn(PK.fromLong(123));

		final String fallbackFileName = renderer.createFallbackFileName(media);

		assertThat(fallbackFileName).isEqualTo("123");
	}
}
