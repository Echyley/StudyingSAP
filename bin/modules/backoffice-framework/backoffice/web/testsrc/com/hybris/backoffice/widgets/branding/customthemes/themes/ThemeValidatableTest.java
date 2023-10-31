package com.hybris.backoffice.widgets.branding.customthemes.themes;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.validation.model.ValidationResult;
import com.hybris.cockpitng.validation.model.ValidationResultSet;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ThemeValidatableTest
{
	@Mock
	private ThemesController controller;

	@Spy
	@InjectMocks
	private ThemeValidatable themeValidatable;

	@Test
	public void shouldReturnCurrentObjectWhenPathStartsWithCurrentObject()
	{
		final String path = String.format("%s.%s", ThemesController.MODEL_CURRENT_OBJECT, "name");
		final Object currentObject = new Object();
		when(controller.getCurrentObject()).thenReturn(currentObject);

		assertEquals(themeValidatable.getCurrentObject(path), currentObject);
		verify(controller).getCurrentObject();
	}

	@Test
	public void shouldNullWhenPathNotStartsWithCurrentObject()
	{
		final String path = "name";

		assertEquals(themeValidatable.getCurrentObject(path), null);
		verify(controller, never()).getCurrentObject();
	}

	@Test
	public void shouldReturnRelativePathWhenPathStartsWithCurrentObject()
	{
		final String qualifier = "name";
		final String path = String.format("%s.%s", ThemesController.MODEL_CURRENT_OBJECT, qualifier);

		final String returnedPath = themeValidatable.getCurrentObjectPath(path);

		assertEquals(returnedPath, qualifier);
	}

	@Test
	public void shouldReturnEmptyWhenPathEqualsToCurrentObject()
	{
		final String path = ThemesController.MODEL_CURRENT_OBJECT;

		assertEquals(themeValidatable.getCurrentObjectPath(path), StringUtils.EMPTY);
	}

	@Test
	public void shouldReturnFullPathWhenPathEqualsToCurrentObject()
	{
		final String path = "name";

		assertEquals(themeValidatable.getCurrentObjectPath(path), path);
	}

	@Test
	public void shouldReturnTrueWhenPathEqualsToCurrentObject()
	{
		final String path = ThemesController.MODEL_CURRENT_OBJECT;

		assertTrue(themeValidatable.isRootPath(path));
	}

	@Test
	public void shouldReturnTrueWhenPathIsEmpty()
	{
		assertTrue(themeValidatable.isRootPath(StringUtils.EMPTY));
	}

	@Test
	public void shouldReturnFalseWhenPathNotEqualsToCurrentObject()
	{
		final String path = String.format("%s.%s", ThemesController.MODEL_CURRENT_OBJECT, "name");
		assertFalse(themeValidatable.isRootPath(path));
	}

	@Test
	public void shouldReactOnValidationChangeWhenPathIsResultModel()
	{
		assertTrue(themeValidatable.reactOnValidationChange(themeValidatable.VALIDATION_RESULT_MODEL_PATH));
	}

	@Test
	public void shouldNotReactOnValidationChangeWhenPathIsNotResultModel()
	{
		final String path = String.format("%s.%s", ThemesController.MODEL_CURRENT_OBJECT, "name");

		assertFalse(themeValidatable.reactOnValidationChange(path));
	}

	@Test
	public void shouldReturnCurrentValidationResultWhenIsRootPath()
	{
		final String path = ThemesController.MODEL_CURRENT_OBJECT;
		final ValidationResult validationResult = mock(ValidationResult.class);
		when(controller.getCurrentValidationResult()).thenReturn(validationResult);

		final ValidationResult returnedValidationResult = themeValidatable.getCurrentValidationResult(path);

		assertEquals(returnedValidationResult, validationResult);
	}

	@Test
	public void shouldReturnCurrentValidationResultWhenIsNotRootPath()
	{
		final String path = String.format("%s.%s", ThemesController.MODEL_CURRENT_OBJECT, "name");
		final ValidationResult validationResult1 = mock(ValidationResult.class);
		final ValidationResult validationResult2 = mock(ValidationResult.class);
		final ValidationResultSet validationResultSet = mock(ValidationResultSet.class);
		when(validationResultSet.wrap()).thenReturn(validationResult2);
		when(validationResult1.find(path)).thenReturn(validationResultSet);
		when(controller.getCurrentValidationResult()).thenReturn(validationResult1);

		final ValidationResult returnedValidationResult = themeValidatable.getCurrentValidationResult(path);

		assertEquals(returnedValidationResult, validationResult2);
	}
}
