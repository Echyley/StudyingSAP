/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.impl.DefaultPointOfServiceService;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SitePreferenceReversePopulatorTest
{
	@Mock
	DefaultPointOfServiceService pointOfServiceService;

	private AbstractPopulatingConverter<SitePreferenceData, SitePreferenceModel> converter;
	private PointOfServiceModel misato = new PointOfServiceModel();
	private PointOfServiceModel koto = new PointOfServiceModel();

	@Before
	public void setup()
	{
		converter = new AbstractPopulatingConverter<>();
		converter.setPopulators(List.of(new SitePreferenceReversePopulator(pointOfServiceService)));
		converter.setTargetClass(SitePreferenceModel.class);

		final CountryModel japanModel = new CountryModel();

		misato.setName("Misato");
		misato.setDisplayName("Misato");
		misato.setLatitude(35.8269);
		misato.setLongitude(139.8701);
		AddressModel misatoAddress = new AddressModel();
		misatoAddress.setLine1("Tokyo-Gaikan Expy");
		misatoAddress.setCellphone("+81 4376 5760");
		misatoAddress.setTown("Tokio");
		misatoAddress.setCountry(japanModel);
		misato.setAddress(misatoAddress);

		koto.setName("Koto");
		koto.setDisplayName("Koto");
		koto.setLatitude(35.6923);
		koto.setLongitude(139.8201);
		AddressModel kotoAddress = new AddressModel();
		kotoAddress.setLine1("Shin-Ohashi Dori");
		kotoAddress.setLine2("10");
		kotoAddress.setTown("Tokio");
		kotoAddress.setCellphone("+81 6176 3426");
		kotoAddress.setCountry(japanModel);
		koto.setAddress(kotoAddress);

		when(pointOfServiceService.getPointOfServiceForName("Misato")).thenReturn(misato);
		when(pointOfServiceService.getPointOfServiceForName("Koto")).thenReturn(koto);
	}

	@Test
	public void testAddAllPreferences()
	{
      final SitePreferenceData sitePreferenceData = new SitePreferenceData();
		sitePreferenceData.setPickUpLocationName("Misato");
		SitePreferenceModel sitePreferenceModel = new SitePreferenceModel();

		converter.convert(sitePreferenceData, sitePreferenceModel);

		verify(pointOfServiceService, times(1)).getPointOfServiceForName("Misato");
		assertThat(sitePreferenceModel.getPickUpLocation()).isEqualToComparingFieldByFieldRecursively(misato);
	}

	@Test
	public void testReplacePreferences()
	{
		final SitePreferenceData sitePreferenceData = new SitePreferenceData();
		sitePreferenceData.setPickUpLocationName("Koto");
		final SitePreferenceModel sitePreferenceModel = new SitePreferenceModel();
		sitePreferenceModel.setPickUpLocation(misato);

		converter.convert(sitePreferenceData, sitePreferenceModel);

		verify(pointOfServiceService, times(1)).getPointOfServiceForName("Koto");
		assertThat(sitePreferenceModel.getPickUpLocation()).isEqualToComparingFieldByFieldRecursively(koto);
	}

	@Test
	public void testDeleteNullPreferences()
	{
      final SitePreferenceData sitePreferenceData = new SitePreferenceData();
		sitePreferenceData.setPickUpLocationName(null);
		final SitePreferenceModel sitePreferenceModel = new SitePreferenceModel();
		sitePreferenceModel.setPickUpLocation(misato);

		converter.convert(sitePreferenceData, sitePreferenceModel);

		verify(pointOfServiceService, times(0)).getPointOfServiceForName("Misato");
		assertThat(sitePreferenceModel.getPickUpLocation()).isNull();
	}

}
