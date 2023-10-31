/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.media;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeLogoServiceTest
{
	@Mock
	private MediaUtil backofficeMediaUtil;

	@InjectMocks
	private DefaultBackofficeLogoService service;

	@Test
	public void shouldReturnNullIfLoginLogoNotUpload()
	{
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_LOGINPAGE_LOGO_CODE)).thenReturn(Optional.empty());

		final var url = service.getLoginPageLogoUrl();

		assertThat(url).isEmpty();
	}

	@Test
	public void shouldReturnCorrectUrlIfLoginLogoUpload()
	{
		final var media = mock(MediaModel.class);
		final var url = "login logo url";
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_LOGINPAGE_LOGO_CODE)).thenReturn(Optional.of(media));
		when(media.getURL()).thenReturn(url);

		final var logoUrl = service.getLoginPageLogoUrl();

		assertThat(logoUrl).isEqualTo(url);
	}

	@Test
	public void shouldReturnNullIfShellBarLogoNotUpload()
	{
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_SHELLBAR_LOGO_CODE)).thenReturn(Optional.empty());

		final var url = service.getShellBarLogoUrl();

		assertThat(url).isEmpty();
	}

	@Test
	public void shouldReturnCorrectUrlIfShellBarLogoUpload()
	{
		final var media = mock(MediaModel.class);
		final var url = "shell bar logo url";
		when(backofficeMediaUtil.getMedia(BackofficeMediaConstants.BACKOFFICE_SHELLBAR_LOGO_CODE)).thenReturn(Optional.of(media));
		when(media.getURL()).thenReturn(url);

		final var logoUrl = service.getShellBarLogoUrl();

		assertThat(logoUrl).isEqualTo(url);
	}
}
