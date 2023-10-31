/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.addressfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import junit.framework.Assert;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DistrictPopulatorTest
{
	private static final String DISTRICT_ISOCODE = "CN-11-1-1";
	private static final String DISTRICT_NAME = "Dongcheng";
	@Mock
	I18NService i18NService;

	private DistrictPopulator DistrictPopulator;

	@Before
	public void setUp()
	{
		DistrictPopulator = new DistrictPopulator();
		ReflectionTestUtils.setField(DistrictPopulator, "i18NService", i18NService);
	}

	@Test
	public void testDistrictPopulator()
	{
		final C2LItemModel DistrictModel = new DistrictModel();
		DistrictModel.setIsocode(DISTRICT_ISOCODE);

		final Locale englishLocale = new Locale("en");
		DistrictModel.setName(DISTRICT_NAME, englishLocale);

		final DistrictData DistrictData = new DistrictData();
		Mockito.when(i18NService.getCurrentLocale()).thenReturn(englishLocale);

		DistrictPopulator.populate(DistrictModel, DistrictData);

		Assert.assertEquals(DISTRICT_ISOCODE, DistrictData.getCode());
		Assert.assertEquals(DISTRICT_NAME, DistrictData.getName());
	}

}
