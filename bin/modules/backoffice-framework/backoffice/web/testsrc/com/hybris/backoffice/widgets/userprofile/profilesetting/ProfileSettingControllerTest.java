/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.userprofile.profilesetting;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.tx.Transaction;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.hybris.backoffice.masterdetail.MasterDetailService;
import com.hybris.backoffice.masterdetail.SettingButton;
import com.hybris.backoffice.masterdetail.SettingItem;
import com.hybris.backoffice.media.BackofficeMediaConstants;
import com.hybris.backoffice.media.MediaUtil;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;


@DeclaredInput(value = ProfileSettingController.SOCKET_INPUT_CROP_FINISHED, socketType = Media.class)
public class ProfileSettingControllerTest extends AbstractWidgetUnitTest<ProfileSettingController>
{
	@Mock
	protected Image avatarImage;
	@Mock
	protected Button uploadBtn;
	@Mock
	protected Label userNameLabel;
	@Mock
	private MasterDetailService userProfileSettingService;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private MediaUtil backofficeMediaUtil;
	@Mock
	private NotificationService notificationService;
	@Spy
	@InjectMocks
	private ProfileSettingController controller;

	@Test
	public void shouldRegisterSelfToMasterDetailServiceWhenInit()
	{
		final var user = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(null);
		when(user.getDisplayName()).thenReturn("name");

		controller.initialize(mock(Component.class));

		verify(userProfileSettingService).registerDetail(controller);
	}

	@Test
	public void shouldUseDefaultAvatarIfUserAvatarNotExist()
	{
		final var user = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(null);

		controller.initialize(mock(Component.class));

		verify(avatarImage).setSrc(controller.getWidgetRoot() + "/../images/user.png");

	}

	@Test
	public void shouldCorrectInitialized()
	{
		final var user = mock(UserModel.class);
		final var avatar = mock(MediaModel.class);
		final var url = "url";
		final var name = "name";
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(avatar);
		when(user.getDisplayName()).thenReturn(name);
		when(avatar.getURL()).thenReturn(url);
		controller.initialize(mock(Component.class));
		verify(userProfileSettingService).registerDetail(controller);
		verify(avatarImage).setSrc(url);
		verify(userNameLabel).setValue(name);
		verify(uploadBtn).addEventListener(eq(Events.ON_UPLOAD), any());
		verify(uploadBtn).setUpload(String.format("true,maxsize=%d,accept=%s", BackofficeMediaConstants.FILE_UPLOAD_MAX_SIZE,
				BackofficeMediaConstants.FILE_UPLOAD_AVATAR_ACCEPT_TYPE));
	}

	@Test
	public void shouldSendOutPutToShowCropperWhenFileUpload()
	{
		final var event = mock(UploadEvent.class);
		final var image = mock(org.zkoss.image.Image.class);

		when(event.getMedia()).thenReturn(image);
		controller.onFileUpload(event);
		assertSocketOutput(ProfileSettingController.SOCKET_OUTPUT_SHOW_CROPPER, image);
	}

	@Test
	public void shouldShowAvatarAfterImageCropped()
	{
		final var image = mock(org.zkoss.image.Image.class);

		executeInputSocketEvent(ProfileSettingController.SOCKET_INPUT_CROP_FINISHED, image);

		verify(avatarImage).setContent(image);
		verify(userProfileSettingService).enableSave(true);
		assertThat(controller.isDataChanged()).isTrue();
	}

	@Test
	public void shouldNotShowAvatarAfterImageCroppedWithNotSupportedType()
	{
		executeInputSocketEvent(ProfileSettingController.SOCKET_INPUT_CROP_FINISHED, mock(Media.class));
		verify(avatarImage, never()).setContent((org.zkoss.image.Image) any());
		verify(userProfileSettingService, never()).enableSave(true);
		assertThat(controller.isDataChanged()).isFalse();
	}

	@Test
	public void shouldCreateAvatarIfUserUploadAvatarFirstTime()
	{
		final var image = mock(org.zkoss.image.Image.class);
		final var user = mock(UserModel.class);
		final var avatarMediaModel = mock(MediaModel.class);
		final var userId = "userid";
		when(avatarImage.getContent()).thenReturn(image);
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(null);
		when(user.getUid()).thenReturn(userId);
		when(backofficeMediaUtil.createMedia(String.format("%s%s", ProfileSettingController.USER_PROFILE_AVATAR_PREFIX, userId),
				BackofficeMediaConstants.BACKOFFICE_USER_AVATAR_MEDIA_FOLDER, MediaModel._TYPECODE, image))
						.thenReturn(avatarMediaModel);

		final var isSuccess = controller.save();
		assertThat(isSuccess).isTrue();
		verify(user).setAvatar(avatarMediaModel);
		verify(modelService).save(user);
		verify(userProfileSettingService).enableSave(false);
		assertThat(controller.isDataChanged()).isFalse();
		verify(notificationService).notifyUser(ProfileSettingController.NOTIFICATION_AREA,
				ProfileSettingController.NOTIFICATION_TYPE_USER_PROFILE_CHANGED, NotificationEvent.Level.SUCCESS);
		assertSocketOutput(ProfileSettingController.SOCKET_OUTPUT_AVATAR_CHANGED, avatarMediaModel);
	}

	@Test
	public void shouldRollBackTransactionIfSaveUserFailed()
	{
		final var image = mock(org.zkoss.image.Image.class);
		final var user = mock(UserModel.class);
		final var userId = "userid";
		when(avatarImage.getContent()).thenReturn(image);
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(null);
		when(user.getUid()).thenReturn(userId);
		when(backofficeMediaUtil.createMedia(String.format("%s%s", ProfileSettingController.USER_PROFILE_AVATAR_PREFIX, userId),
				BackofficeMediaConstants.BACKOFFICE_USER_AVATAR_MEDIA_FOLDER, MediaModel._TYPECODE, image))
						.thenReturn(mock(MediaModel.class));
		doThrow(ModelSavingException.class).when(modelService).save(user);

		try (final MockedStatic<Transaction> transactionMock = mockStatic(Transaction.class))
		{
			final var tx = spy(Transaction.class);
			transactionMock.when(() -> Transaction.current()).thenReturn(tx);
			final var isSuccess = controller.save();

			verify(tx).rollback();
			assertThat(isSuccess).isFalse();
			verify(notificationService).notifyUser(ProfileSettingController.NOTIFICATION_AREA,
					ProfileSettingController.NOTIFICATION_TYPE_USER_PROFILE_CHANGED, NotificationEvent.Level.FAILURE);
			assertNoSocketOutputInteractions(ProfileSettingController.SOCKET_OUTPUT_AVATAR_CHANGED);
		}
	}

	@Test
	public void shouldRollBackTransactionIfCreateMediaFailed()
	{
		final var image = mock(org.zkoss.image.Image.class);
		final var user = mock(UserModel.class);
		final var userId = "userid";
		when(avatarImage.getContent()).thenReturn(image);
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(null);
		when(user.getUid()).thenReturn(userId);
		when(backofficeMediaUtil.createMedia(any(), any(), any(), any())).thenThrow(ModelSavingException.class);

		try (final MockedStatic<Transaction> transactionMock = mockStatic(Transaction.class))
		{
			final var tx = spy(Transaction.class);
			transactionMock.when(() -> Transaction.current()).thenReturn(tx);
			final var isSuccess = controller.save();

			verify(tx).rollback();
			assertThat(isSuccess).isFalse();
			verify(notificationService).notifyUser(ProfileSettingController.NOTIFICATION_AREA,
					ProfileSettingController.NOTIFICATION_TYPE_USER_PROFILE_CHANGED, NotificationEvent.Level.FAILURE);
			assertNoSocketOutputInteractions(ProfileSettingController.SOCKET_OUTPUT_AVATAR_CHANGED);
		}
	}

	@Test
	public void shouldUpdateAvatarIfUserChangeAvatar()
	{
		final var image = mock(org.zkoss.image.Image.class);
		final var user = mock(UserModel.class);
		final var avatarMediaModel = mock(MediaModel.class);
		when(avatarImage.getContent()).thenReturn(image);
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(avatarMediaModel);

		final var isSuccess = controller.save();
		assertThat(isSuccess).isTrue();
		verify(user, never()).setAvatar(avatarMediaModel);
		verify(modelService, never()).save(user);
		verify(userProfileSettingService).enableSave(false);
		verify(backofficeMediaUtil).updateMedia(avatarMediaModel, image);
		assertThat(controller.isDataChanged()).isFalse();
		verify(notificationService).notifyUser(ProfileSettingController.NOTIFICATION_AREA,
				ProfileSettingController.NOTIFICATION_TYPE_USER_PROFILE_CHANGED, NotificationEvent.Level.SUCCESS);
		assertSocketOutput(ProfileSettingController.SOCKET_OUTPUT_AVATAR_CHANGED, avatarMediaModel);
	}

	@Test
	public void shouldReturnsCorrectSettingItemData()
	{
		final var user = mock(UserModel.class);
		final String name = "name";
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getDisplayName()).thenReturn(name);
		final String title = "profile.setting.title";
		doReturn(title).when(controller).getLabel(title);

		final SettingItem settingItem = controller.getSettingItem();

		assertThat(settingItem.getId()).isEqualTo("backoffice-profile-view");
		assertThat(settingItem.getIcon()).isEqualTo("customer");
		assertThat(settingItem.getTitle()).isEqualTo(title);
		assertThat(settingItem.getSubtitle()).isEqualTo(name);
		final List<SettingButton> buttons = settingItem.getButtons();
		assertThat(buttons.size()).isEqualTo(3);
		assertThat(buttons.get(0).getType()).isEqualTo(SettingButton.TypesEnum.SAVE);
		assertThat(buttons.get(0).isVisible()).isTrue();
		assertThat(buttons.get(0).isDisabled()).isTrue();

		assertThat(buttons.get(1).getType()).isEqualTo(SettingButton.TypesEnum.SAVE_AND_CLOSE);
		assertThat(buttons.get(1).isVisible()).isTrue();
		assertThat(buttons.get(1).isDisabled()).isTrue();

		assertThat(buttons.get(2).getType()).isEqualTo(SettingButton.TypesEnum.CANCEL);
		assertThat(buttons.get(2).isVisible()).isTrue();
		assertThat(buttons.get(2).isDisabled()).isFalse();
	}

	@Test
	public void shouldResetData()
	{
		final var user = mock(UserModel.class);
		final var avatar = mock(MediaModel.class);
		final var url = "url";
		when(userService.getCurrentUser()).thenReturn(user);
		when(user.getAvatar()).thenReturn(avatar);
		when(avatar.getURL()).thenReturn(url);

		controller.reset();

		assertThat(controller.isDataChanged()).isFalse();
		verify(avatarImage).setSrc(url);
	}

	@Override
	protected ProfileSettingController getWidgetController()
	{
		return controller;
	}
}
