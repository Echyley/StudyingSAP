/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.userprofile.appearancesetting;

import com.hybris.backoffice.masterdetail.MasterDetailService;
import com.hybris.backoffice.model.ThemeModel;
import com.hybris.backoffice.theme.BackofficeThemeLevel;
import com.hybris.backoffice.theme.BackofficeThemeService;
import com.hybris.backoffice.theme.ThemeNotFound;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AppearanceSettingControllerTest extends AbstractWidgetUnitTest<AppearanceSettingController>
{
	@Mock
	private BackofficeThemeService themeService;

	@Mock
	public Listbox themeList;

	@Mock
	private UserService userService;

	@Mock
	private MasterDetailService userProfileSettingService;

	@Spy
	@InjectMocks
	private AppearanceSettingController controller;

	private final ThemeModel darkTheme = new ThemeModel();
	private final ThemeModel lightTheme = new ThemeModel();
	private final List<ThemeModel> availableThemes = new ArrayList<>();

	@Before
	public void before() throws IOException
	{
		themeList = new Listbox();
		darkTheme.setCode("sap_quartz_dark");
		lightTheme.setCode("sap_quartz_light");
		availableThemes.add(darkTheme);
		availableThemes.add(lightTheme);
		when(themeService.getAvailableThemes()).thenReturn(availableThemes);
		when(themeService.getCurrentUserTheme()).thenReturn(lightTheme);
		when(themeService.getThemeLevel()).thenReturn(BackofficeThemeLevel.USER);
		controller.preInitialize(new Div());
		controller.initialize(new Div());
	}

	@Test
	public void shouldFetchThemesWhenPreInitialize() throws IOException
	{
		verify(themeService).getAvailableThemes();
		verify(themeService).getCurrentUserTheme();
		verify(themeService).getThemeLevel();
	}

	@Test
	public void shouldRegisterDetailWhenIsNotAdminAndIsUserMode()
	{
		verify(userProfileSettingService).registerDetail(any());
	}

	@Test
	public void shouldNotEnableSaveWhenThemeNotChange()
	{
		//given
		final Listitem li = new Listitem();
		li.setValue(lightTheme);

		//when
		controller.onThemeClick(li);

		//then
		assertThat(controller.isDataChanged()).isFalse();
		verify(userProfileSettingService, never()).enableSave(true);
	}

	@Test
	public void shouldEnableSaveWhenThemeChange()
	{
		//given
		final Listitem li = new Listitem();
		li.setValue(darkTheme);

		//when
		controller.onThemeClick(li);

		//then
		assertThat(controller.isDataChanged()).isTrue();
		verify(userProfileSettingService).enableSave(true);
	}

	@Test
	public void shouldReturnTrueWhenIsSelectedTheme()
	{
		assertThat(controller.isSelectedTheme(lightTheme.getCode())).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenIsNotSelectedTheme()
	{
		assertThat(controller.isSelectedTheme(darkTheme.getCode())).isFalse();
	}

	@Test
	public void shouldResetSelectionWhenSuccess()
	{
		//given
		final Listitem li1 = new Listitem();
		li1.setValue(darkTheme);
		themeList.appendChild(li1);
		final Listitem li2 = new Listitem();
		li2.setValue(lightTheme);
		themeList.appendChild(li2);
		themeList.setSelectedItem(li1);
		controller.themeList = themeList;

		//when
		controller.reset();

		//then
		final ThemeModel selectedItem = themeList.getSelectedItem().getValue();
		assertThat(selectedItem.getCode()).isEqualTo(lightTheme.getCode());
		assertThat(controller.isDataChanged()).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenCurrentThemeIsNullAndSave()
	{
		//given
		when(themeService.getCurrentUserTheme()).thenReturn(null);
		controller.preInitialize(new Div());
		controller.initialize(new Div());

		//when
		final boolean isSaveSuccess = controller.save();

		//then
		assertThat(isSaveSuccess).isFalse();
	}

	@Test
	public void shouldSaveCurrentThemeSuccessfully() throws ThemeNotFound
	{
		//when
		final boolean isSaveSuccess = controller.save();

		//then
		assertThat(isSaveSuccess).isTrue();
		verify(themeService).setCurrentUserTheme(lightTheme.getCode());
		verify(themeService, never()).setSystemTheme(any());
	}

	@Test
	public void shouldSaveCurrentSelectedThemeSuccessfully() throws ThemeNotFound
	{

		//given
		final var selectedItem = mock(Listitem.class);
		when(selectedItem.getValue()).thenReturn(darkTheme);
		controller.onThemeClick(selectedItem);
		//when
		final boolean isSaveSuccess = controller.save();

		//then
		assertThat(isSaveSuccess).isTrue();
		verify(themeService).setCurrentUserTheme(darkTheme.getCode());
		verify(themeService, never()).setSystemTheme(any());
	}


	@Override
	protected AppearanceSettingController getWidgetController()
	{
		return controller;
	}

}

