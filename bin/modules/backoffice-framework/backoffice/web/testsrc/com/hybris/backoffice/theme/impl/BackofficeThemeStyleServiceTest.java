 /*
  * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
  */
 package com.hybris.backoffice.theme.impl;

 import de.hybris.platform.core.model.media.MediaModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.model.CustomThemeModel;
import com.hybris.backoffice.model.ThemeModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


 @RunWith(MockitoJUnitRunner.class)
 public class BackofficeThemeStyleServiceTest
 {
	 @Mock
	 private DefaultBackofficeThemeService defaultBackofficeThemeService;
	 @Mock
	 private ThemeModel theme;
	 @Mock
	 private CustomThemeModel customTheme;
	 @Mock
	 private MediaModel themeStyle;
	 @Mock
	 private ThemeModel defaultTheme;
	 @Mock
	 private MediaModel defaultThemeStyle;
	 @Mock
	 private ThemeStyleUtil themeStyleUtil;

	 @Spy
	 @InjectMocks
	 private BackofficeThemeStyleService themeService;

	 @Test
	 public void getCurrentThemeStyleShouldGetDefaultWhenNoStyle()
	 {
		 //given
		 when(defaultBackofficeThemeService.getCurrentTheme()).thenReturn(theme);
		 when(defaultBackofficeThemeService.getDefaultTheme()).thenReturn(defaultTheme);
		 when(theme.getStyle()).thenReturn(null);
		 when(defaultTheme.getStyle()).thenReturn(defaultThemeStyle);
		 when(defaultThemeStyle.getURL()).thenReturn("defaulturl");

		 //then
		 assertThat(themeService.getCurrentThemeStyle()).isEqualTo("defaulturl");
	 }

	 @Test
	 public void getCurrentThemeStyleShouldSuccess()
	 {
		 //given
		 when(defaultBackofficeThemeService.getCurrentTheme()).thenReturn(theme);
		 when(theme.getStyle()).thenReturn(themeStyle);
		 when(themeStyle.getURL()).thenReturn("url");

		 //then
		 assertThat(themeService.getCurrentThemeStyle()).isEqualTo("url");
	 }

	 @Test
	 public void shouldGetCurrentThemeId()
	 {
		 //given
		 when(defaultBackofficeThemeService.getCurrentTheme()).thenReturn(theme);
		 when(theme.getCode()).thenReturn("theme1");

		 //then
		 assertThat(themeService.getCurrentThemeId()).isEqualTo("theme1");
	 }

	 @Test
	 public void shouldReturnCurrentThemeStyleMapSuccess()
	 {
		 //given
		 final Map<String, String> currentStyleMap = new HashMap<>();
		 when(defaultBackofficeThemeService.getCurrentTheme()).thenReturn(theme);
		 when(theme.getStyle()).thenReturn(themeStyle);
		 when(themeStyleUtil.convertStyleMediaToVariableMap(themeStyle)).thenReturn(currentStyleMap);

		 //when
		 themeService.getCurrentThemeStyleMap();

		 //then
		 verify(themeStyleUtil).convertStyleMediaToVariableMap(themeStyle);
	 }

	 @Test
	 public void shouldReturnSelfCodeAsBaseThemeIdForOOTBTheme()
	 {
		 //given
		 final String themeCode = "light";
		 when(defaultBackofficeThemeService.getCurrentTheme()).thenReturn(theme);
		 when(theme.getCode()).thenReturn(themeCode);
		 //then
		 assertThat(themeService.getBaseThemeId()).isEqualTo(themeCode);
	 }

	 @Test
	 public void shouldReturnBaseThemeCodeAsBaseThemeIdForCustomTheme()
	 {
		 //given
		 final String baseThemeCode = "light";
		 when(defaultBackofficeThemeService.getCurrentTheme()).thenReturn(customTheme);
		 when(theme.getCode()).thenReturn(baseThemeCode);
		 when(customTheme.getBase()).thenReturn(theme);
		 //then
		 assertThat(themeService.getBaseThemeId()).isEqualTo(baseThemeCode);
	 }
 }

