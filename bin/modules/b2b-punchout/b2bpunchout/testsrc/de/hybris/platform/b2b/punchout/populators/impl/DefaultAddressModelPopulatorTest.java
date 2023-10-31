/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.cxml.Address;
import org.cxml.City;
import org.cxml.Country;
import org.cxml.CountryCode;
import org.cxml.Email;
import org.cxml.Fax;
import org.cxml.Phone;
import org.cxml.PostalAddress;
import org.cxml.State;
import org.cxml.Street;
import org.cxml.TelephoneNumber;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThrows;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAddressModelPopulatorTest
{
	private static final String CITY_CODE = "TC";
	private static final String CITY_VALUE = "Test City";
	private static final String COUNTRY_CODE = "DE";
	private static final String COUNTRY_VALUE = "Germany";
	private static final String STATE_CODE = "TS";
	private static final String STATE_VALUE = "Test State";
	private static final String POSTAL_CODE = "12345";
	private static final String TEST_NAME = "Test Name";
	private static final String TEST_STREET = "Test Street";
	private static final String TEST_EMAIL = "test@test.de";
	private static final String TELEPHONE_NUMBER = "125341253451235";
	private static final String PHONE_EXTENSION = "123";

	@InjectMocks
	private DefaultAddressModelPopulator defaultAddressModelPopulator;

	@Mock
	private CommonI18NService commonI18NService;

	private AddressModel target;
	private Address source;

	@Mock
	private CountryModel countryModel;

	@Mock
	private RegionModel regionModel;

	@Before
	public void setUp()
	{
		target = new AddressModel();
		source = new Address();

		final PostalAddress postalAddress = new PostalAddress();

		City city = new City();
		city.setCityCode(CITY_CODE);
		city.setvalue(CITY_VALUE);

		postalAddress.setCity(city);

		final Country country = new Country();
		country.setIsoCountryCode(COUNTRY_CODE);
		country.setvalue(COUNTRY_VALUE);

		postalAddress.setCountry(country);

		final State state = new State();
		state.setIsoStateCode(STATE_CODE);
		state.setvalue(STATE_VALUE);

		postalAddress.setState(state);
		postalAddress.setPostalCode(POSTAL_CODE);
		postalAddress.setName(TEST_NAME);

		final Street street = new Street();
		street.setvalue(TEST_STREET);
		postalAddress.getStreet().add(street);

		source.setPostalAddress(postalAddress);

		when(commonI18NService.getCountry(COUNTRY_CODE)).thenReturn(countryModel);
		when(commonI18NService.getRegion(countryModel, COUNTRY_CODE + "-" + STATE_VALUE)).thenReturn(regionModel);
	}

	@Test
	public void normalPopulation()
	{
		defaultAddressModelPopulator.populate(source, target);

		assertThat(countryModel).isEqualTo(target.getCountry());
		assertThat(target).hasFieldOrPropertyWithValue("postalcode", POSTAL_CODE)
						  .hasFieldOrPropertyWithValue("firstname", TEST_NAME)
						  .hasFieldOrPropertyWithValue("email", null)
						  .hasFieldOrPropertyWithValue("fax", null)
						  .hasFieldOrPropertyWithValue("phone1", null);
	}

	@Test
	public void testEmailPopulation()
	{
		final Email email = new Email();
		email.setvalue(TEST_EMAIL);
		source.setEmail(email);

		defaultAddressModelPopulator.populate(source, target);
		assertThat(target.getEmail()).isEqualTo(TEST_EMAIL);
	}

	@Test
	public void testPhoneAndFaxPopulation()
	{
		final Phone phone = new Phone();
		final TelephoneNumber telePhoneNumber = new TelephoneNumber();
		telePhoneNumber.setNumber(TELEPHONE_NUMBER);

		final CountryCode countryCode = new CountryCode();
		countryCode.setIsoCountryCode(COUNTRY_CODE);

		telePhoneNumber.setCountryCode(countryCode);
		telePhoneNumber.setExtension(PHONE_EXTENSION);

		phone.setTelephoneNumber(telePhoneNumber);
		source.setPhone(phone);

		final Fax fax = new Fax();
		fax.setName(TEST_NAME);
		fax.getTelephoneNumberOrURLOrEmail().add(phone);
		source.setFax(fax);

		defaultAddressModelPopulator.populate(source, target);

		assertThat(target.getFax()).isNotNull();
		assertThat(target.getPhone1()).isNotNull();
	}

	@Test
	public  void testMissCityInAddressModelPopulation()
	{
		source.getPostalAddress().setCity(null);
		final PunchOutException punchOutException = assertThrows(PunchOutException.class, () -> defaultAddressModelPopulator.populate(source, target));
		assertThat(punchOutException).hasMessage("Miss required information in address")
				.hasFieldOrPropertyWithValue("errorCode", Integer.toString(HttpStatus.BAD_REQUEST.value()));

	}
}
