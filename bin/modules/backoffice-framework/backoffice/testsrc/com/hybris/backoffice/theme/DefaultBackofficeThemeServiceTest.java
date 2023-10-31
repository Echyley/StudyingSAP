 /*
  * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
  */
 package com.hybris.backoffice.theme;

 import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.model.BackofficeThemeConfigModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.daos.BackofficeThemeConfigDao;
import com.hybris.backoffice.daos.BackofficeThemeDao;
 import com.hybris.backoffice.model.CustomThemeModel;
 import com.hybris.backoffice.model.ThemeModel;
 import com.hybris.backoffice.theme.impl.DefaultBackofficeCustomThemeService;
 import com.hybris.backoffice.theme.impl.DefaultBackofficeThemeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 import static org.mockito.Mockito.times;

 @RunWith(MockitoJUnitRunner.class)
 public class DefaultBackofficeThemeServiceTest
 {
 	private static final String BACKOFFICE_THEME_LEVEL_KEY = "backoffice.theme.level";
 	private static final String BACKOFFICE_SYSTEM_LEVEL_THEME_KEY = "backoffice.theme.system";
 	private static final String BACKOFFICE_USER_LEVEL_THEME_DEFAULT_KEY = "backoffice.theme.user.default";

 	@Mock
	private DefaultBackofficeCustomThemeService customThemeService;
 	@Mock
 	private ModelService modelService;
 	@Mock
 	private UserService userService;
 	@Mock
 	private BackofficeThemeDao backofficeThemeDao;
 	@Mock
 	private BackofficeThemeConfigDao backofficeThemeConfigDao;
 	@Mock
 	private BackofficeThemeConfigModel themeLevelConfig;
 	@Mock
 	private BackofficeThemeConfigModel systemThemeConfig;
 	@Mock
	private BackofficeThemeConfigModel userLevelDefaultThemeConfig;
 	@Mock
 	private ThemeModel systemTheme;
 	@Mock
 	private ThemeModel userTheme;
 	@Mock
 	private UserModel user;
 	@Mock
 	private ThemeModel defaultTheme;
 	@Mock
 	private MediaModel defaultStyle;

 	private String defaultThemeCode = "defaultThemeCode";
 	private String defaultStyleURL = "defaultStyleURL";

 	@Spy
 	@InjectMocks
 	private DefaultBackofficeThemeService defaultBackofficeThemeService;

 	@Before
 	public void setup()
 	{
 		when(modelService.create(ThemeModel.class)).thenReturn(defaultTheme);
 		when(modelService.create(MediaModel.class)).thenReturn(defaultStyle);

 		defaultBackofficeThemeService.setDefaultThemeCode(defaultThemeCode);
 	}

 	@Test
	public void getCurrentThemeShouldNotGenerateCustomThemeStyleWhenOnlyTheme()
	{
		doReturn(BackofficeThemeLevel.SYSTEM).when(defaultBackofficeThemeService).getThemeLevel();
		final CustomThemeModel theme = Mockito.mock(CustomThemeModel.class);
		final ThemeModel base = Mockito.mock(ThemeModel.class);
		doReturn(theme).when(defaultBackofficeThemeService).getSystemTheme();
		when(theme.getStyleVersion()).thenReturn(1);
		when(theme.getBase()).thenReturn(base);
		when(base.getStyleVersion()).thenReturn(1);

		var currentTheme = defaultBackofficeThemeService.getCurrentTheme();
		verify(customThemeService, times(0)).generateCustomThemeStyle(Mockito.any(CustomThemeModel.class));
	}

	 @Test
	 public void getCurrentThemeShouldGenerateCustomThemeStyle()
	 {
		 doReturn(BackofficeThemeLevel.SYSTEM).when(defaultBackofficeThemeService).getThemeLevel();
		 final CustomThemeModel theme = Mockito.mock(CustomThemeModel.class);
		 final ThemeModel base = Mockito.mock(ThemeModel.class);
		 doReturn(theme).when(defaultBackofficeThemeService).getSystemTheme();
		 when(theme.getStyleVersion()).thenReturn(null);
		 when(theme.getBase()).thenReturn(base);
		 when(base.getStyleVersion()).thenReturn(1);
		 doNothing().when(customThemeService).generateCustomThemeStyle(Mockito.any(CustomThemeModel.class));

		 var currentTheme = defaultBackofficeThemeService.getCurrentTheme();
		 verify(customThemeService, times(1)).generateCustomThemeStyle(Mockito.any(CustomThemeModel.class));
	 }

	 @Test
	 public void getCurrentThemeShouldNotGenerateCustomThemeStyleWhenStyleVersionSame()
	 {
		 doReturn(BackofficeThemeLevel.SYSTEM).when(defaultBackofficeThemeService).getThemeLevel();
		 doReturn(systemTheme).when(defaultBackofficeThemeService).getSystemTheme();

		 var theme = defaultBackofficeThemeService.getCurrentTheme();
		 verify(customThemeService, times(0)).generateCustomThemeStyle(Mockito.any(CustomThemeModel.class));
	 }

 	@Test
 	public void getSystemThemeThemeShouldGet()
 	{
 		final String systemThemeCode = "system_theme";
 		//given
 		when(systemThemeConfig.getContent()).thenReturn(systemThemeCode);
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_SYSTEM_LEVEL_THEME_KEY, true)).thenReturn(Arrays.asList(systemThemeConfig));
 		when(backofficeThemeDao.findByCode(systemThemeCode)).thenReturn(Optional.of(systemTheme));

 		//then
 		assertThat(defaultBackofficeThemeService.getSystemTheme()).isEqualTo(systemTheme);
 	}

 	@Test
 	public void getSystemThemeShouldGetDefaultWhenNoConfig()
 	{
 		//given
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_SYSTEM_LEVEL_THEME_KEY, true)).thenReturn(Collections.emptyList());
 		when(backofficeThemeDao.findByCode(defaultThemeCode)).thenReturn(Optional.of(systemTheme));

 		//then
 		assertThat(defaultBackofficeThemeService.getSystemTheme()).isEqualTo(systemTheme);
 	}

 	@Test
 	public void getSystemThemeShouldGetDefaultWhenNoTheme()
 	{
 		//given
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_SYSTEM_LEVEL_THEME_KEY, true)).thenReturn(Collections.emptyList());
 		when(backofficeThemeDao.findByCode(defaultThemeCode)).thenReturn(Optional.empty());

 		//then
 		assertThat(defaultBackofficeThemeService.getSystemTheme()).isEqualTo(defaultTheme);
 	}

 	@Test
 	public void getCurrentUserThemeShouldGetUserThemeWhenThemeSetByUser()
 	{
 		//given
 		when(userService.getCurrentUser()).thenReturn(user);
 		when(user.getThemeForBackoffice()).thenReturn(userTheme);

 		//then
 		assertThat(defaultBackofficeThemeService.getCurrentUserTheme()).isEqualTo(userTheme);
 	}

 	@Test
 	public void getCurrentUserThemeShouldGetDefaultThemeWhenNoThemeSetByUser()
 	{
 		//given
 		when(userService.getCurrentUser()).thenReturn(user);
 		when(user.getThemeForBackoffice()).thenReturn(null);

 		//then
 		assertThat(defaultBackofficeThemeService.getCurrentUserTheme()).isEqualTo(defaultTheme);
 	}

 	@Test
 	public void getAvailableThemesShouldGetAllThemes()
 	{
 		final List<ThemeModel> themes = new ArrayList<>();
 		//given
 		when(backofficeThemeDao.findAllThemes(false)).thenReturn(themes);

 		//then
 		assertThat(defaultBackofficeThemeService.getAvailableThemes()).isEqualTo(themes);
 	}

 	@Test
 	public void getCurrentUserThemeShouldGetSystemTheme()
 	{
 		//given
 		doReturn(BackofficeThemeLevel.SYSTEM).when(defaultBackofficeThemeService).getThemeLevel();
 		doReturn(systemTheme).when(defaultBackofficeThemeService).getSystemTheme();

 		//then
 		assertThat(defaultBackofficeThemeService.getCurrentTheme()).isEqualTo(systemTheme);
 	}

 	@Test
 	public void getCurrentUserThemeShouldGetUserTheme()
 	{
 		//given
 		doReturn(BackofficeThemeLevel.USER).when(defaultBackofficeThemeService).getThemeLevel();
 		doReturn(userTheme).when(defaultBackofficeThemeService).getCurrentUserTheme();

 		//then
 		assertThat(defaultBackofficeThemeService.getCurrentTheme()).isEqualTo(userTheme);
 	}

 	@Test
 	public void getThemeLevelShouldGetUserLevel()
 	{
 		//given
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_THEME_LEVEL_KEY, true)).thenReturn(Arrays.asList(themeLevelConfig));
 		when(themeLevelConfig.getContent()).thenReturn(BackofficeThemeLevel.USER.name());

 		//then
 		assertThat(defaultBackofficeThemeService.getThemeLevel()).isEqualTo(BackofficeThemeLevel.USER);
 	}

 	@Test
 	public void getThemeLevelShouldGetDefaultLevelWhenNoConfig()
 	{
 		//given
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_THEME_LEVEL_KEY, true)).thenReturn(Collections.emptyList());

 		//then
 		assertThat(defaultBackofficeThemeService.getThemeLevel()).isEqualTo(BackofficeThemeLevel.SYSTEM);
 	}

 	@Test
 	public void setThemeLevelShouldSuccWhenHasConfig()
 	{
 		//given
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_THEME_LEVEL_KEY, true)).thenReturn(Arrays.asList(themeLevelConfig));
 		when(themeLevelConfig.getContent()).thenReturn(BackofficeThemeLevel.USER.name());

 		//when
 		defaultBackofficeThemeService.setThemeLevel(BackofficeThemeLevel.SYSTEM);

 		//then
 		verify(modelService).save(Mockito.any(BackofficeThemeConfigModel.class));
 	}

 	@Test
 	public void setThemeLevelShouldSuccWhenNoConfig()
 	{
 		//given
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_THEME_LEVEL_KEY, true)).thenReturn(Collections.emptyList());
 		when(modelService.create(BackofficeThemeConfigModel.class)).thenReturn(this.themeLevelConfig);

 		//when
 		defaultBackofficeThemeService.setThemeLevel(BackofficeThemeLevel.SYSTEM);

 		//then
 		verify(modelService).save(Mockito.any(BackofficeThemeConfigModel.class));
 	}

 	@Test
 	public void setCurrentUserThemeShouldSucc() throws ThemeNotFound {
 		final String code = "valid_code";
 		//given
 		when(userService.getCurrentUser()).thenReturn(user);
 		when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.of(userTheme));

 		//when
 		defaultBackofficeThemeService.setCurrentUserTheme(code);

 		//then
 		verify(modelService).save(Mockito.any(UserModel.class));
 	}

 	@Test(expected =  ThemeNotFound.class)
 	public void setCurrentUserThemeShouldThrowExceptionWhenNoTheme() throws ThemeNotFound {
 		final String code = "invalid_code";
 		//given
 		when(userService.getCurrentUser()).thenReturn(user);
 		when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.empty());

 		//when
 		defaultBackofficeThemeService.setCurrentUserTheme(code);
 	}

 	@Test
 	public void setSystemThemeShouldSuccWhenHasConfig() throws ThemeNotFound {
 		final String code = "valid_code";
 		//given
 		when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.of(userTheme));
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_SYSTEM_LEVEL_THEME_KEY, true)).thenReturn(Arrays.asList(systemThemeConfig));
 		when(systemThemeConfig.getContent()).thenReturn("old");

 		//when
 		defaultBackofficeThemeService.setSystemTheme(code);

 		//then
 		verify(modelService).save(systemThemeConfig);
 	}

 	@Test
 	public void setSystemThemeShouldSuccWhenNoConfig() throws ThemeNotFound {
 		final String code = "valid_code";
 		//given
 		when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.of(userTheme));
 		when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_SYSTEM_LEVEL_THEME_KEY, true)).thenReturn(Collections.emptyList());
 		when(modelService.create(BackofficeThemeConfigModel.class)).thenReturn(systemThemeConfig);

 		//when
 		defaultBackofficeThemeService.setSystemTheme(code);

 		//then
 		verify(modelService).save(systemThemeConfig);
 	}

 	@Test(expected =  ThemeNotFound.class)
 	public void setSystemThemeShouldThrowExceptionWhenNoTheme() throws ThemeNotFound {
 		final String code = "invalid_code";
 		//given
 		when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.empty());

 		//when
 		defaultBackofficeThemeService.setSystemTheme(code);
 	}

	 @Test
	 public void setUserLevelDefaultThemeShouldSuccWhenHasConfig() throws ThemeNotFound {
		 final String code = "valid_code";
		 //given
		 when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.of(userTheme));
		 when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_USER_LEVEL_THEME_DEFAULT_KEY, true)).thenReturn(Arrays.asList(userLevelDefaultThemeConfig));
		 when(userLevelDefaultThemeConfig.getContent()).thenReturn("old");

		 //when
		 defaultBackofficeThemeService.setUserLevelDefaultTheme(code);

		 //then
		 verify(modelService).save(userLevelDefaultThemeConfig);
	 }

	 @Test
	 public void setUserLevelDefaultThemeShouldSuccWhenNoConfig() throws ThemeNotFound {
		 final String code = "valid_code";
		 //given
		 when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.of(userTheme));
		 when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_USER_LEVEL_THEME_DEFAULT_KEY, true)).thenReturn(Collections.emptyList());
		 when(modelService.create(BackofficeThemeConfigModel.class)).thenReturn(userLevelDefaultThemeConfig);

		 //when
		 defaultBackofficeThemeService.setUserLevelDefaultTheme(code);

		 //then
		 verify(modelService).save(userLevelDefaultThemeConfig);
	 }

	 @Test
	 public void setUserLevelDefaultThemeShouldSuccWhenNoActiveConfigHasInactiveConfig() throws ThemeNotFound {
		 final String code = "valid_code";
		 //given
		 when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.of(userTheme));
		 when(backofficeThemeConfigDao.findByCodeAndActive(BACKOFFICE_USER_LEVEL_THEME_DEFAULT_KEY, true)).thenReturn(Collections.emptyList());
		 final var inactiveConfigs = Arrays.asList(userLevelDefaultThemeConfig);
		 when(backofficeThemeConfigDao.findByCode(BACKOFFICE_USER_LEVEL_THEME_DEFAULT_KEY)).thenReturn(inactiveConfigs);
		 doNothing().when(modelService).removeAll(inactiveConfigs);
		 when(modelService.create(BackofficeThemeConfigModel.class)).thenReturn(userLevelDefaultThemeConfig);

		 //when
		 defaultBackofficeThemeService.setUserLevelDefaultTheme(code);

		 //then
		 verify(modelService).removeAll(inactiveConfigs);
		 verify(modelService).save(userLevelDefaultThemeConfig);
	 }

 	@Test(expected =  ThemeNotFound.class)
 	public void setUserLevelDefaultThemeShouldThrowExceptionWhenNoTheme() throws ThemeNotFound {
		 final String code = "invalid_code";
		 //given
		 when(backofficeThemeDao.findByCode(code)).thenReturn(Optional.empty());

		 //when
		 defaultBackofficeThemeService.setUserLevelDefaultTheme(code);
	 }

	 @Test
	 public void getDefaultThemeShouldReturnWhenNoThemeFound()
	 {
		 when(backofficeThemeDao.findByCode(defaultThemeCode)).thenReturn(Optional.empty());
		 ThemeModel theme = defaultBackofficeThemeService.getDefaultTheme();

		 assertThat(theme).isNotNull();
	 }

 }

