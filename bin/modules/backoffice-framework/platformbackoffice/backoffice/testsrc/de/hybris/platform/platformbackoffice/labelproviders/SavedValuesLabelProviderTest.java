/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.labelproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.hmc.model.SavedValuesModel;

import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@UnitTest
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class SavedValuesLabelProviderTest extends AbstractCockpitngUnitTest<SavedValuesLabelProvider>
{
	@Spy
	@InjectMocks
	private SavedValuesLabelProvider provider;

	@Mock
	private CockpitLocaleService cockpitLocaleService;

	@Before
	public void setUp()
	{
		initMocks(this);
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
	}

	@Test
	public void blankTest()
	{
		assertThat(provider.getIconPath(null)).isNull();
	}

	@Test
	public void savedValuesWithNullUserShouldNotFail()
	{
		final SavedValuesModel savedValues = mock(SavedValuesModel.class);
		when(savedValues.getUser()).thenReturn(null);
		when(savedValues.getTimestamp()).thenReturn(new Date());
		assertThat(provider.getLabel(savedValues)).isNotNull();
	}
}
