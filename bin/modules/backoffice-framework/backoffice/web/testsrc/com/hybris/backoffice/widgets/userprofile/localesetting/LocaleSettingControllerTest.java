/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.userprofile.localesetting;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;

import com.hybris.backoffice.masterdetail.MasterDetailService;
import com.hybris.backoffice.masterdetail.SettingButton;
import com.hybris.backoffice.masterdetail.SettingButton.TypesEnum;
import com.hybris.backoffice.masterdetail.SettingItem;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;


public class LocaleSettingControllerTest extends AbstractWidgetUnitTest<LocaleSettingController>
{

	@Mock
	private MasterDetailService userProfileSettingService;
	@Mock
	private CockpitLocaleService cockpitLocaleService;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private Listbox uiLocalesList;
	@Mock
	private Listbox localesList;

	@Spy
	@InjectMocks
	private LocaleSettingController controller;

	@Test
	public void shouldRegisterSelfToMasterDetailServiceWhenInit()
	{
		when(cockpitUserService.getCurrentUser()).thenReturn(null);
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
		controller.initialize(mock(Component.class));
		verify(userProfileSettingService).registerDetail(controller);
	}

	@Test
	public void shouldAlwaysReturnFalseWhenCheckIfDataChanged()
	{
		assertThat(controller.isDataChanged()).isFalse();
	}

	@Test
	public void shouldReturnsCorrectSettingItemData()
	{
		final Locale currentLocale = Locale.ENGLISH;
		final String title = "userprofile.localesetting.title";
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(currentLocale);
		doReturn(title).when(controller).getLabel(title);
		final SettingItem settingItem = controller.getSettingItem();
		assertThat(settingItem.getId()).isEqualTo("backoffice-language-view");
		assertThat(settingItem.getIcon()).isEqualTo("world");
		assertThat(settingItem.getTitle()).isEqualTo(title);
		assertThat(settingItem.getSubtitle()).isEqualTo(currentLocale.getDisplayName());
		final List<SettingButton> buttons = settingItem.getButtons();
		assertThat(buttons.size()).isEqualTo(1);
		assertThat(buttons.get(0).getType()).isEqualTo(TypesEnum.CLOSE);
		assertThat(buttons.get(0).isVisible()).isTrue();
		assertThat(buttons.get(0).isDisabled()).isFalse();
	}


	@Override
	protected LocaleSettingController getWidgetController()
	{
		return controller;
	}
}
