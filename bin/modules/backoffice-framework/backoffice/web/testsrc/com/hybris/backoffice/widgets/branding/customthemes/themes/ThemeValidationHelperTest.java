package com.hybris.backoffice.widgets.branding.customthemes.themes;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.model.StandardModelKeys;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.validation.ValidationHandler;
import com.hybris.cockpitng.validation.model.ValidationResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ThemeValidationHelperTest
{
	@Mock
	private ValidationHandler validationHandler;

	@Mock
	private Component parent;

	@Mock
	private ThemesController controller;

	@Spy
	@InjectMocks
	private ThemeValidationHelper themeValidationHelper;

	@Test
	public void shouldInitValidationForEditor()
	{
		final var editor = mock(Editor.class);

		themeValidationHelper.initValidation(parent, controller, editor);

		verify(editor).initValidation(any(ThemeValidatable.class), eq(validationHandler));
	}

	@Test
	public void shouldCreateValidationResultModelWhenResultModelIsNull()
	{
		final var model = mock(WidgetModel.class);

		themeValidationHelper.prepareValidationResultModel(model);

		verify(model).setValue(StandardModelKeys.VALIDATION_RESULT_KEY, new ValidationResult());
	}

	@Test
	public void shouldDoNothingWhenResultModelIsNotNull()
	{
		final var model = mock(WidgetModel.class);
		when(model.getValue(StandardModelKeys.VALIDATION_RESULT_KEY, ValidationResult.class)).thenReturn(new ValidationResult());

		themeValidationHelper.prepareValidationResultModel(model);

		verify(model).setValue(StandardModelKeys.VALIDATION_RESULT_KEY, new ValidationResult());
	}

	@Test
	public void shouldClearResultModel()
	{
		final var model = mock(WidgetModel.class);

		themeValidationHelper.clearValidationResultModel(model);

		verify(model).setValue(StandardModelKeys.VALIDATION_RESULT_KEY, new ValidationResult());
	}
}
