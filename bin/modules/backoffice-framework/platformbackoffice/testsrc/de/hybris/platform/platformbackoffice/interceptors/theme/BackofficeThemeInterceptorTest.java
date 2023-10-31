package de.hybris.platform.platformbackoffice.interceptors.theme;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.model.CustomThemeModel;
import com.hybris.backoffice.model.ThemeModel;
import com.hybris.backoffice.theme.BackofficeThemeService;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeThemeInterceptorTest
{
	@Spy
	@InjectMocks
	private BackofficeThemeInterceptor interceptor;

	@Mock
	private BackofficeThemeService backofficeThemeService;
	@Mock
	private ModelService modelService;

	@Test(expected = RemoveUsedThemeInterceptorException.class)
	public void shouldThrowRemoveExceptionIfCurrentRemoveThemeUsedInSystemTheme() throws InterceptorException
	{
		final var themeCode = "themeCode";
		final var systemTheme = mock(CustomThemeModel.class);
		final var themeModel = mock(CustomThemeModel.class);
		when(systemTheme.getCode()).thenReturn(themeCode);
		when(themeModel.getCode()).thenReturn(themeCode);
		when(backofficeThemeService.getSystemTheme()).thenReturn(systemTheme);

		interceptor.onRemove(themeModel, mock(InterceptorContext.class));
	}

	@Test(expected = RemoveUsedThemeInterceptorException.class)
	public void shouldThrowRemoveExceptionIfCurrentRemoveThemeUsedInUserDefaultTheme() throws InterceptorException
	{
		final var themeCode = "themeCode";
		final var systemTheme = mock(ThemeModel.class);
		final var defaultUserTheme = mock(CustomThemeModel.class);
		final var themeModel = mock(CustomThemeModel.class);
		when(systemTheme.getCode()).thenReturn("other code");
		when(defaultUserTheme.getCode()).thenReturn(themeCode);
		when(themeModel.getCode()).thenReturn(themeCode);
		when(backofficeThemeService.getSystemTheme()).thenReturn(systemTheme);
		when(backofficeThemeService.getUserLevelDefaultTheme()).thenReturn(defaultUserTheme);

		interceptor.onRemove(themeModel, mock(InterceptorContext.class));
	}

	@Test(expected = ExceedMaximumThemeInterceptorException.class)
	public void shouldThrowExceedMaximumExceptionIfCreateCustomThemeAndExceedMaximum() throws InterceptorException
	{
		final var ctx = mock(InterceptorContext.class);
		final var themeModel = mock(CustomThemeModel.class);
		final var customThemes = mock(List.class);
		final var maximum = 10;
		when(ctx.getModelService()).thenReturn(modelService);
		when(modelService.isNew(themeModel)).thenReturn(Boolean.TRUE);
		when(customThemes.size()).thenReturn(maximum);
		when(backofficeThemeService.getCustomThemes()).thenReturn(customThemes);
		when(backofficeThemeService.getMaximumOfCustomTheme()).thenReturn(maximum);

		interceptor.onValidate(themeModel, ctx);
	}
}
