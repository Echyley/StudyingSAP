package com.hybris.backoffice.widgets.branding.customthemes.themes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.MimeTypeUtils;

import com.hybris.backoffice.media.BackofficeMediaConstants;
import com.hybris.backoffice.media.MediaUtil;
import com.hybris.backoffice.model.CustomThemeModel;


@RunWith(MockitoJUnitRunner.class)
public class ThemeSaveHelperTest
{

	private static final String TEST_THEME_NAME = "My Theme";

	@Spy
	@InjectMocks
	private ThemeSaveHelper themeSaveHelper;
	@Mock
	private ModelService modelService;
	@Mock
	private MediaUtil backofficeMediaUtil;

	@Test
	public void shouldDoInitWithTheme()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		themeSaveHelper.setTheme(theme).init(t -> {
			t.setName(TEST_THEME_NAME);
		});
		verify(theme, times(1)).setName(TEST_THEME_NAME);
	}

	@Test
	public void shouldDoInitStyleIfExists()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		final MediaModel mediaStyle = mock(MediaModel.class);
		when(theme.getStyle()).thenReturn(mediaStyle);
		themeSaveHelper.setTheme(theme).initStyle(m -> {
			m.setCode("hi");
		});
		verify(mediaStyle, times(1)).setCode("hi");
		verify(theme, never()).setStyle(any(MediaModel.class));
	}

	@Test
	public void shouldDoInitStyleIfNotExists()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		final MediaModel mediaStyle = mock(MediaModel.class);
		when(theme.getStyle()).thenReturn(null);
		when(backofficeMediaUtil.createMedia(anyString(), eq(BackofficeMediaConstants.BACKOFFICE_THEMES_FOLDER), eq(MediaModel._TYPECODE),
				eq("text/css"), anyString())).thenReturn(mediaStyle);

		themeSaveHelper.setTheme(theme).initStyle(m -> {
			m.setCode("hi");
		});
		verify(mediaStyle, times(1)).setCode("hi");
		verify(theme, times(1)).setStyle(mediaStyle);
	}

	@Test
	public void shouldDoInitThumbnailIfExists()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		final MediaModel mediaThumbnail = mock(MediaModel.class);
		when(theme.getThumbnail()).thenReturn(mediaThumbnail);
		themeSaveHelper.setTheme(theme).initThumbnail(m -> {
			m.setCode("hi");
		});
		verify(mediaThumbnail, times(1)).setCode("hi");
		verify(theme, never()).setThumbnail(any(MediaModel.class));
	}

	@Test
	public void shouldDoInitThumbnailIfNotExists()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		final MediaModel mediaThumbnail = mock(MediaModel.class);
		when(theme.getThumbnail()).thenReturn(null);
		when(backofficeMediaUtil.createMedia(anyString(), eq(BackofficeMediaConstants.BACKOFFICE_THEMES_FOLDER), eq(MediaModel._TYPECODE),
				eq(MimeTypeUtils.IMAGE_PNG_VALUE), anyString())).thenReturn(mediaThumbnail);

		themeSaveHelper.setTheme(theme).initThumbnail(m -> {
			m.setCode("hi");
		});
		verify(mediaThumbnail, times(1)).setCode("hi");
		verify(theme, times(1)).setThumbnail(mediaThumbnail);
	}

	@Test
	public void shouldCreateThemeIfNull()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		final MediaModel mediaStyle = mock(MediaModel.class);
		final MediaModel mediaThumbnail = mock(MediaModel.class);
		when(modelService.create(CustomThemeModel._TYPECODE)).thenReturn(theme);
		when(backofficeMediaUtil.createMedia(anyString(), eq(BackofficeMediaConstants.BACKOFFICE_THEMES_FOLDER), eq(MediaModel._TYPECODE),
				eq("text/css"), anyString())).thenReturn(mediaStyle);
		when(backofficeMediaUtil.createMedia(anyString(), eq(BackofficeMediaConstants.BACKOFFICE_THEMES_FOLDER), eq(MediaModel._TYPECODE),
				eq(MimeTypeUtils.IMAGE_PNG_VALUE), anyString())).thenReturn(mediaThumbnail);

		themeSaveHelper.setTheme(null).init(t -> {
			t.setName(TEST_THEME_NAME);
		}).initStyle(m -> {
			m.setDescription("set style media.");
		}).initThumbnail(m -> {
			m.setDescription("set thumbnail.");
		}).save();

		verify(mediaStyle, times(1)).setDescription("set style media.");
		verify(theme, times(1)).setStyle(mediaStyle);
		verify(mediaThumbnail, times(1)).setDescription("set thumbnail.");
		verify(theme, times(1)).setThumbnail(mediaThumbnail);
		verify(modelService, times(1)).save(theme);
	}

	@Test
	public void shouldSaveAExistedTheme()
	{
		final CustomThemeModel theme = mock(CustomThemeModel.class);
		final MediaModel mediaStyle = mock(MediaModel.class);
		final MediaModel mediaThumbnail = mock(MediaModel.class);
		when(theme.getStyle()).thenReturn(mediaStyle);
		when(theme.getThumbnail()).thenReturn(mediaThumbnail);

		themeSaveHelper.setTheme(theme).init(t -> {
			t.setName(TEST_THEME_NAME);
		}).initStyle(m -> {
			m.setDescription("set style media.");
		}).initThumbnail(m -> {
			m.setDescription("set thumbnail.");
		}).save();

		verify(mediaStyle, times(1)).setDescription("set style media.");
		verify(theme, never()).setStyle(mediaStyle);
		verify(mediaThumbnail, times(1)).setDescription("set thumbnail.");
		verify(theme, never()).setThumbnail(mediaThumbnail);
		verify(modelService, never()).create(CustomThemeModel._TYPECODE);
		verify(modelService, times(1)).save(theme);
	}

}
