/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */

package com.hybris.backoffice.widgets.userprofile.passwordsetting.changepassword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.nullable;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.persistence.security.EJBPasswordEncoderNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.PasswordPolicyService;
import de.hybris.platform.servicelayer.user.PasswordPolicyViolation;
import de.hybris.platform.servicelayer.user.impl.DefaultPasswordPolicyViolation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.core.Executable;


@RunWith(MockitoJUnitRunner.class)
public class ChangePasswordRendererUtilTest
{

	@Mock
	private ModelService modelService;

	@Mock
	private PasswordPolicyService passwordPolicyService;

	@Spy
	@InjectMocks
	private ChangePasswordRendererUtil rendererUtil;

	@Test
	public void shouldCreateNoticeLabel()
	{
		final Div container = new Div();
		final String labelKey = "lable.title";
		rendererUtil.createNoticeLabel(container, labelKey);
		final Div lineDiv = (Div) container.getFirstChild();
		assertThat(lineDiv.getChildren().size()).isEqualTo(1);
	}

	@Test
	public void shouldCreateValidationInfoLine()
	{
		final Div container = new Div();
		rendererUtil.createValidationInfoLine(container);
		assertThat(container.getChildren().size()).isEqualTo(1);
		final Div validationDiv = (Div) container.getFirstChild();
		assertThat(validationDiv.isVisible()).isFalse();
		assertThat(validationDiv.getChildren().size()).isEqualTo(0);
	}

	@Test
	public void shouldCreatePasswordLine()
	{
		final Div container = new Div();
		final String labelKey = "lable.newpassword";
		final Textbox passwordBox = rendererUtil.createPasswordLine(container, labelKey);
		assertThat(container.getChildren().size()).isEqualTo(1);
		final Div lineDiv = (Div) container.getFirstChild();
		assertThat(lineDiv.getChildren().size()).isEqualTo(2);
		final Textbox box = (Textbox) lineDiv.getLastChild();
		assertThat(box.getType()).isEqualTo("password");
		assertThat(box.isInstant()).isTrue();
		assertThat(box).isSameAs(passwordBox);
	}

	@Test
	public void shouldDisplayValidationWhenInvalidOldPassword() throws EJBPasswordEncoderNotFoundException
	{
		//given
		final Div container = new Div();
		rendererUtil.createValidationInfoLine(container);
		final UserModel user = new UserModel();
		final PasswordChangeData passwordChangeData = new PasswordChangeData();
		final String password = "invalid-old-password";
		final Textbox input = new Textbox(password);
		doReturn(false).when(rendererUtil).checkPassword(any(), any());

		//when
		rendererUtil.onOldPasswordTextChanged(input, passwordChangeData, mock(Executable.class));
		rendererUtil.validateOldPassword(user, passwordChangeData);

		//then
		assertThat(passwordChangeData.getOldPassword()).isEqualTo(password);
		assertThat(passwordChangeData.getValidations().size()).isEqualTo(1);
		assertThat(rendererUtil.getValidationDiv().isVisible()).isTrue();
	}

	@Test
	public void shouldDisplayValidationWhenInvalidNewPassword()
	{
		//given
		final Div container = new Div();
		rendererUtil.createValidationInfoLine(container);
		final UserModel user = new UserModel();
		final PasswordChangeData passwordChangeData = new PasswordChangeData();
		final String password = "invalid-new-password";
		final Textbox input = new Textbox(password);
		final String violationMessage = "The password must contain at least one or more special characters.";
		final List<PasswordPolicyViolation> passwordPolicyViolations = new ArrayList<>();
		passwordPolicyViolations.add(new DefaultPasswordPolicyViolation("2020", violationMessage));

		doReturn(passwordPolicyViolations).when(passwordPolicyService).verifyPassword(eq(user), eq(password), nullable(String.class));

		//when
		rendererUtil.onNewPasswordTextChanged(user, input, passwordChangeData, mock(Executable.class));

		//then
		assertThat(passwordChangeData.getNewPassword()).isEqualTo(password);
		assertThat(passwordChangeData.isValidationPassed()).isFalse();
		assertThat(passwordChangeData.getValidations().size()).isEqualTo(1);
		assertThat(passwordChangeData.getValidations().get(0)).isEqualTo(violationMessage);
		assertThat(rendererUtil.getValidationDiv().isVisible()).isTrue();
	}

	@Test
	public void shouldDisplayValidationWhenInvalidConfirmPassword()
	{
		//given
		final Div container = new Div();
		rendererUtil.createValidationInfoLine(container);
		final UserModel user = new UserModel();
		final PasswordChangeData passwordChangeData = new PasswordChangeData();
		passwordChangeData.setNewPassword("password-new");
		final String password = "password-confirm";
		final Textbox input = new Textbox(password);

		//when
		rendererUtil.onConfirmPwdTextChanged(user, input, passwordChangeData, mock(Executable.class));

		//then
		assertThat(passwordChangeData.getConfirmPassword()).isEqualTo(password);
		assertThat(passwordChangeData.isValidationPassed()).isFalse();
		assertThat(passwordChangeData.getValidations().size()).isEqualTo(1);
		assertThat(rendererUtil.getValidationDiv().isVisible()).isTrue();
	}

	@Test
	public void shouldHideValidationWhenAllPasswordValid() throws EJBPasswordEncoderNotFoundException
	{
		//given
		final Div container = new Div();
		rendererUtil.createValidationInfoLine(container);
		final UserModel user = new UserModel();
		final PasswordChangeData passwordChangeData = new PasswordChangeData();
		final String password = "password2020";
		passwordChangeData.setNewPassword(password);
		final Textbox input = new Textbox(password);
		doReturn(new ArrayList<PasswordPolicyViolation>()).when(passwordPolicyService).verifyPassword(eq(user), eq(password),
				any());

		//when
		rendererUtil.onConfirmPwdTextChanged(user, input, passwordChangeData, mock(Executable.class));

		//then
		assertThat(passwordChangeData.isValidationPassed()).isTrue();
		assertThat(rendererUtil.getValidationDiv().isVisible()).isFalse();
	}
}
