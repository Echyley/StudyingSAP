/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.userprofile.passwordsetting;

import static com.hybris.backoffice.widgets.userprofile.passwordsetting.PasswordSettingController.CHANGEPASSWORD_CONFIRM_PWD_LABEL;
import static com.hybris.backoffice.widgets.userprofile.passwordsetting.PasswordSettingController.CHANGEPASSWORD_NEW_PWD_LABEL;
import static com.hybris.backoffice.widgets.userprofile.passwordsetting.PasswordSettingController.CHANGEPASSWORD_OLD_PWD_LABEL;
import static com.hybris.backoffice.widgets.userprofile.passwordsetting.PasswordSettingController.ENABLE_CHANGE_PASSWORD_SETTING;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.hybris.backoffice.masterdetail.MasterDetailService;
import com.hybris.backoffice.widgets.userprofile.passwordsetting.changepassword.ChangePasswordRendererUtil;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.util.notifications.NotificationService;


@RunWith(MockitoJUnitRunner.class)
public class PasswordSettingControllerTest extends AbstractWidgetUnitTest<PasswordSettingController>
{

	@Mock
	private MasterDetailService userProfileSettingService;
	@Mock
	private ChangePasswordRendererUtil changePasswordRendererUtil;
	@Mock
	private NotificationService notificationService;
	@Mock
	private UserService userService;
	@Mock
	private ModelService modelService;
	@Mock
	protected Textbox oldInput;
	@Mock
	protected Textbox pwdInput;
	@Mock
	protected Textbox confInput;
	@Mock
	protected Label label;

	@Spy
	@InjectMocks
	private PasswordSettingController controller;

	@Before
	public void before()
	{
		when(changePasswordRendererUtil.createNoticeLabel(any(), any())).thenReturn(label);
		when(changePasswordRendererUtil.createPasswordLine(any(), eq(CHANGEPASSWORD_OLD_PWD_LABEL))).thenReturn(oldInput);
		when(changePasswordRendererUtil.createPasswordLine(any(), eq(CHANGEPASSWORD_NEW_PWD_LABEL))).thenReturn(pwdInput);
		when(changePasswordRendererUtil.createPasswordLine(any(), eq(CHANGEPASSWORD_CONFIRM_PWD_LABEL))).thenReturn(confInput);
		doReturn(true).when(controller).isChangePasswordEnabled();
		doReturn(mock(UserModel.class)).when(userService).getCurrentUser();
		controller.initialize(new Div());
	}

	@Test
	public void shouldRegisterDetail()
	{
		verify(userProfileSettingService).registerDetail(any());
	}

	@Test
	public void shouldDisplayAccountSettings()
	{
		doReturn(true).when(controller).isChangePasswordEnabled();
		controller.preInitialize(new Div());
		verify(controller.getWidgetSettings()).put(ENABLE_CHANGE_PASSWORD_SETTING, true);
	}

	@Test
	public void shouldHideAccountSettings()
	{
		doReturn(false).when(controller).isChangePasswordEnabled();
		controller.preInitialize(new Div());
		verify(controller.getWidgetSettings()).put(ENABLE_CHANGE_PASSWORD_SETTING, false);
	}

	@Test
	public void shouldSaveNewPwdWhenValidateSuccess()
	{
		//given
		when(changePasswordRendererUtil.validateOldPassword(any(), any())).thenReturn(true);

		//when
		final boolean isSaveSuccess = controller.save();

		//then
		verify(changePasswordRendererUtil).clearValidation();
		verify(userService).setPassword((UserModel) anyObject(), any());
		assertThat(isSaveSuccess).isTrue();
	}

	public void shouldReturnFalseIfValidateFailWhenSavePwd()
	{
		//given
		when(changePasswordRendererUtil.validateOldPassword(any(), any())).thenReturn(false);

		//when
		final boolean isSaveSuccess = controller.save();

		//then
		verify(changePasswordRendererUtil, never()).clearValidation();
		verify(userService, never()).setPassword((UserModel) anyObject(), any());
		assertThat(isSaveSuccess).isFalse();
	}

	@Override
	protected PasswordSettingController getWidgetController()
	{
		return controller;
	}
}
