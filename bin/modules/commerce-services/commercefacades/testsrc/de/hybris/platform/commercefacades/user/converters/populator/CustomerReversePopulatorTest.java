/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import de.hybris.platform.site.BaseSiteService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerReversePopulatorTest
{
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private AddressReversePopulator addressReversePopulator;
	@Mock
	private CustomerNameStrategy customerNameStrategy;
	@Mock
	private Converter<SitePreferenceData, SitePreferenceModel> sitePreferenceReverseConverter;
	@Mock
	private BaseSiteService baseSiteService;

	private CustomerReversePopulator customerReversePopulator;

	@Before
	public void setUp()
	{
		customerReversePopulator = new CustomerReversePopulator();
		customerReversePopulator.setCommonI18NService(commonI18NService);
		customerReversePopulator.setAddressReversePopulator(addressReversePopulator);
		customerReversePopulator.setCustomerNameStrategy(customerNameStrategy);
		customerReversePopulator.setSitePreferenceReverseConverter(sitePreferenceReverseConverter);
		customerReversePopulator.setBaseSiteService(baseSiteService);
	}

	@Test
	public void testPopulateEssencial()
	{
		final CustomerData customerData = mock(CustomerData.class);
		final CustomerModel customerModel = new CustomerModel();

		given(customerData.getFirstName()).willReturn("firstName");
		given(customerData.getLastName()).willReturn("lastName");
		given(customerData.getTitleCode()).willReturn(null);
		given(customerNameStrategy.getName("firstName", "lastName")).willReturn("firstName lastName");

		customerReversePopulator.populate(customerData, customerModel);
		Assert.assertEquals("firstName lastName", customerModel.getName());
		Assert.assertNull(customerModel.getTitle());
	}

	@Test
	public void testPopulateAll()
	{
		final CustomerData customerData = mock(CustomerData.class);
		final CustomerModel customerModel = new CustomerModel();

		final CurrencyData currencyData = mock(CurrencyData.class);
		final LanguageData languageData = mock(LanguageData.class);
		final SitePreferenceData sitePreferenceData = mock(SitePreferenceData.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final SitePreferenceModel sitePreferenceModel = mock(SitePreferenceModel.class);
		final BaseSiteModel baseSite = new BaseSiteModel();

		given(customerData.getFirstName()).willReturn("firstName");
		given(customerData.getLastName()).willReturn("lastName");
		given(customerData.getTitleCode()).willReturn(null);
		given(customerNameStrategy.getName("firstName", "lastName")).willReturn("firstName lastName");

		given(customerData.getCurrency()).willReturn(currencyData);
		given(currencyData.getIsocode()).willReturn("USD");
		given(currencyModel.getIsocode()).willReturn("USD");
		given(commonI18NService.getCurrency("USD")).willReturn(currencyModel);

		given(customerData.getLanguage()).willReturn(languageData);
		given(languageData.getIsocode()).willReturn("en");
		given(languageModel.getIsocode()).willReturn("en");
		given(commonI18NService.getLanguage("en")).willReturn(languageModel);

		given(customerData.getSitePreference()).willReturn(sitePreferenceData);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(sitePreferenceReverseConverter.convert(sitePreferenceData)).willReturn(sitePreferenceModel);

		customerReversePopulator.populate(customerData, customerModel);
		Assert.assertEquals("firstName lastName", customerModel.getName());
		Assert.assertNull(customerModel.getTitle());
		Assert.assertEquals("USD", customerModel.getSessionCurrency().getIsocode());
		Assert.assertEquals("en", customerModel.getSessionLanguage().getIsocode());
		Assert.assertEquals(Collections.singletonList(sitePreferenceModel), customerModel.getSitePreferences());
	}

	@Test(expected = ConversionException.class)
	public void testPopulateCurrencyUnknownIdent()
	{
		final CustomerData customerData = mock(CustomerData.class);
		final CustomerModel customerModel = new CustomerModel();
		final CurrencyData currencyData = mock(CurrencyData.class);
		given(customerData.getCurrency()).willReturn(currencyData);
		given(currencyData.getIsocode()).willReturn("currencyIso");
		given(commonI18NService.getCurrency("currencyIso")).willThrow(new UnknownIdentifierException(""));
		customerReversePopulator.populate(customerData, customerModel);
	}

	@Test(expected = ConversionException.class)
	public void testPopulateLanguageUnknownIdent()
	{
		final CustomerData customerData = mock(CustomerData.class);
		final CustomerModel customerModel = new CustomerModel();
		final LanguageData languageData = mock(LanguageData.class);
		given(customerData.getLanguage()).willReturn(languageData);
		given(languageData.getIsocode()).willReturn("languageIso");
		given(commonI18NService.getLanguage("languageIso")).willThrow(new UnknownIdentifierException(""));
		customerReversePopulator.populate(customerData, customerModel);
	}

}
