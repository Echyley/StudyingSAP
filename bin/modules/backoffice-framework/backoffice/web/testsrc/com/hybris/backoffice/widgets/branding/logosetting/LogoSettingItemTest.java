/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.branding.logosetting;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;

import com.hybris.backoffice.media.BackofficeMediaConstants;
import com.hybris.backoffice.media.MediaUtil;


public class LogoSettingItemTest
{
	private String defaultLogoUrl = "defaultLogoUrl";
	private String logoUrl = "logoUrl";
	private String logoCode = "code";
	private MediaModel media = mock(MediaModel.class);
	private LogoSettingController controller = mock(LogoSettingController.class);
	private MediaUtil mediaUtil = mock(MediaUtil.class);
	private LogoSettingItem item;

	@Before
	public void before()
	{
		when(media.getURL()).thenReturn(logoUrl);
		when(controller.getBackofficeMediaUtil()).thenReturn(mediaUtil);
		when(mediaUtil.getMedia(logoCode)).thenReturn(Optional.of(media));

		item = new LogoSettingItem(mock(Component.class), logoCode, defaultLogoUrl, "", controller);
	}

	@Test
	public void shouldRenderDefaultLogoIfNoLogoUpload()
	{
		when(mediaUtil.getMedia(logoCode)).thenReturn(Optional.empty());
		item = new LogoSettingItem(mock(Component.class), logoCode, defaultLogoUrl, "", controller);

		assertThat(item.getLogoImage().getSrc()).isEqualTo(defaultLogoUrl);
		assertThat(item.getDefaultLogoUrl()).isEqualTo(defaultLogoUrl);
		assertThat(item.getLogoCode()).isEqualTo(logoCode);
		assertThat(item.resetBtn.isDisabled()).isTrue();
	}

	@Test
	public void shouldRenderCorrectLogoIfLogoSavedBefore()
	{
		assertThat(item.getLogoImage().getSrc()).isEqualTo(logoUrl);
		assertThat(item.isDataChanged()).isFalse();
		assertThat(item.resetBtn.isDisabled()).isFalse();
	}

	@Test
	public void shouldRenderDefaultLogoIfClickResetToDefaultBtn()
	{
		item.onResetToDefault(mock(Event.class));

		assertThat(item.getLogoImage().getSrc()).isEqualTo(defaultLogoUrl);
		assertThat(item.isDataChanged()).isTrue();
		assertThat(item.resetBtn.isDisabled()).isTrue();
		verify(controller).enableSave(true);
	}

	@Test
	public void shouldEnableSaveAndRenderImageIfNewImageUpload()
	{
		final var event = mock(UploadEvent.class);
		final var image = mock(org.zkoss.image.Image.class);
		when(event.getMedia()).thenReturn(image);

		item.onFileUpload(event);

		assertThat(item.getLogoImage().getContent()).isEqualTo(image);
		assertThat(item.isDataChanged()).isTrue();
		assertThat(item.resetBtn.isDisabled()).isFalse();
		verify(controller).enableSave(true);
	}

	@Test
	public void shouldNotPerformSaveIfLogoNotChanged()
	{
		item.isDataChanged = false;

		final var isSuccess = item.save();

		assertThat(isSuccess).isTrue();
	}

	@Test
	public void shouldDeleteLogoIfResetLogoToDefault()
	{
		item.isDataChanged = true;
		item.resetBtn.setDisabled(true);

		final var isSuccess = item.save();

		assertThat(isSuccess).isTrue();
		verify(mediaUtil).deleteMedia(media);
		verify(controller).onLogoSaved(logoCode, null);
		assertThat(item.isDataChanged).isFalse();
		assertThat(item.resetBtn.isDisabled()).isTrue();
	}

	@Test
	public void shouldUpdateLogoIfLogoChanged()
	{
		item.isDataChanged = true;
		item.resetBtn.setDisabled(false);
		final var imageContent = mock(org.zkoss.image.Image.class);
		item.getLogoImage().setContent(imageContent);

		final var isSuccess = item.save();

		assertThat(isSuccess).isTrue();
		verify(mediaUtil).updateMedia(media, imageContent);
		verify(controller).onLogoSaved(logoCode, media);
		assertThat(item.isDataChanged).isFalse();
		assertThat(item.resetBtn.isDisabled()).isFalse();
	}

	@Test
	public void shouldCreateLogoIfFirstUploadALogo()
	{
		item.isDataChanged = true;
		item.resetBtn.setDisabled(false);
		final var imageContent = mock(org.zkoss.image.Image.class);
		final var mediaModel = mock(MediaModel.class);
		item.getLogoImage().setContent(imageContent);
		when(mediaUtil.getMedia(logoCode)).thenReturn(Optional.empty());
		when(mediaUtil.createMedia(logoCode, BackofficeMediaConstants.BACKOFFICE_LOGO_MEDIA_FOLDER, MediaModel._TYPECODE,
				imageContent)).thenReturn(mediaModel);

		final var isSuccess = item.save();

		assertThat(isSuccess).isTrue();
		verify(mediaUtil).createMedia(logoCode, BackofficeMediaConstants.BACKOFFICE_LOGO_MEDIA_FOLDER, MediaModel._TYPECODE,
				imageContent);
		verify(controller).onLogoSaved(logoCode, mediaModel);
		assertThat(item.isDataChanged).isFalse();
		assertThat(item.resetBtn.isDisabled()).isFalse();
	}

	@Test
	public void shouldReturnFalseIfExceptionThrowdWhenSaveLogo()
	{
		item.isDataChanged = true;
		item.resetBtn.setDisabled(true);
		when(mediaUtil.getMedia(logoCode)).thenThrow(RuntimeException.class);

		final var isSuccess = item.save();

		assertThat(isSuccess).isFalse();
	}

	@Test
	public void shouldEnableResetBtnIfLogoUploadBefore()
	{
		item.reloadLogo();
		assertThat(item.getLogoImage().getSrc()).isEqualTo(logoUrl);
		assertThat(item.resetBtn.isDisabled()).isFalse();
	}

	@Test
	public void shouldReloadDefaultLogoAndDisableResetBtnIfLogoNotUploadBefore()
	{
		when(mediaUtil.getMedia(logoCode)).thenReturn(Optional.empty());
		item.reloadLogo();
		assertThat(item.getLogoImage().getSrc()).isEqualTo(defaultLogoUrl);
		assertThat(item.resetBtn.isDisabled()).isTrue();
	}

	@Test
	public void shouldResetToCorrectData()
	{
		item.reset();
		assertThat(item.isDataChanged).isFalse();
		assertThat(item.resetBtn.isDisabled()).isFalse();
		assertThat(item.getLogoImage().getSrc()).isEqualTo(logoUrl);
	}
}
