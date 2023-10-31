/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SitePreferencePopulatorTest
{
	private final PointOfServiceModel misato = new PointOfServiceModel();
	private final PointOfServiceData misatoData = new PointOfServiceData();
	private AbstractPopulatingConverter<SitePreferenceModel, SitePreferenceData> converter;

	@Before
	public void setup()
	{
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

		final CountryData japanCountryData = new CountryData();
		japanCountryData.setIsocode("JP");
		japanCountryData.setName("Japan");

		misatoData.setName("Misato");
		misatoData.setDisplayName("Misato");
		GeoPoint misatoGeoPoint = new GeoPoint();
		misatoGeoPoint.setLatitude(35.8269);
		misatoGeoPoint.setLongitude(139.8701);
		misatoData.setGeoPoint(misatoGeoPoint);
		AddressData misatoAddressData = new AddressData();
		misatoAddressData.setLine1("Tokyo-Gaikan Expy");
		misatoAddressData.setCellphone("+81 4376 5760");
		misatoAddressData.setTown("Tokio");
		misatoAddressData.setCountry(japanCountryData);
		misatoData.setAddress(misatoAddressData);
		converter = new AbstractPopulatingConverter<>();
		converter.setPopulators(List.of(new SitePreferencePopulator()));
		converter.setTargetClass(SitePreferenceData.class);
	}

	@Test
	public void testConvertAllPreferences()
	{
		final SitePreferenceModel sitePreferenceModel = new SitePreferenceModel();
		sitePreferenceModel.setPickUpLocation(misato);

		final SitePreferenceData sitePreferenceData = converter.convert(sitePreferenceModel);

		assertThat(sitePreferenceData).isNotNull();
		assertThat(sitePreferenceData.getPickUpLocationName()).isEqualTo(misatoData.getName());
	}

	@Test
	public void testNotConvertNullOptionalPreferences()
	{
		final SitePreferenceModel sitePreferenceModel = new SitePreferenceModel();

		final SitePreferenceData sitePreferenceData = converter.convert(sitePreferenceModel);
		assertThat(sitePreferenceData.getPickUpLocationName()).isNull();
	}

}
