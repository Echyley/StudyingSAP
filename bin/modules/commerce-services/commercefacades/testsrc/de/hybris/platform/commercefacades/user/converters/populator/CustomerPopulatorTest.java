/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.core.model.user.TitleModel;

import de.hybris.platform.site.BaseSiteService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerPopulatorTest
{
	@Mock
	private AbstractConverter<CurrencyModel, CurrencyData> currencyConverter;
	@Mock
	private AbstractConverter<LanguageModel, LanguageData> languageConverter;
	@Mock
	private CustomerNameStrategy customerNameStrategy;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private AbstractConverter<SitePreferenceModel, SitePreferenceData> sitePreferenceConverter;

	private AbstractPopulatingConverter<CustomerModel, CustomerData> customerConverter;


	private static final String DUMMY = "dummy";


	@Before
	public void setUp()
	{
		final CustomerPopulator customerPopulator = new CustomerPopulator();
		customerPopulator.setCurrencyConverter(currencyConverter);
		customerPopulator.setCustomerNameStrategy(customerNameStrategy);
		customerPopulator.setLanguageConverter(languageConverter);
		customerPopulator.setBaseSiteService(baseSiteService);
		customerPopulator.setSitePreferenceConverter(sitePreferenceConverter);
		customerConverter = new AbstractPopulatingConverter<>();
		customerConverter.setPopulators(List.of(customerPopulator));
		customerConverter.setTargetClass(CustomerData.class);
	}

	@Test
	public void testConvertAll()
	{
		final CustomerModel userModel = mock(CustomerModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final CurrencyData currencyData = mock(CurrencyData.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final LanguageData languageData = mock(LanguageData.class);
		final TitleModel titleModel = mock(TitleModel.class);
		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		final SitePreferenceModel sitePreferenceModel = mock(SitePreferenceModel.class);

		given(userModel.getSessionCurrency()).willReturn(currencyModel);
		given(currencyConverter.convert(currencyModel)).willReturn(currencyData);
		given(sitePreferenceModel.getSite()).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);
		given(userModel.getSitePreferences()).willReturn(Collections.singletonList(sitePreferenceModel));

		given(userModel.getSessionLanguage()).willReturn(languageModel);
		given(languageConverter.convert(languageModel)).willReturn(languageData);
		given(userModel.getName()).willReturn("userName1 userName2");
		given(userModel.getTitle()).willReturn(titleModel);
		given(titleModel.getCode()).willReturn("titleCode");
		given(userModel.getUid()).willReturn("userUid");
		given(userModel.getCustomerID()).willReturn("customerId");
		given(customerNameStrategy.splitName("userName1 userName2")).willReturn(new String[]
		{ "userName1", "userName2" });

		final CustomerData customerData = customerConverter.convert(userModel);

		Assert.assertNotNull(customerData);
		Assert.assertEquals(currencyData, customerData.getCurrency());
		Assert.assertEquals(languageData, customerData.getLanguage());
		Assert.assertEquals("userName1", customerData.getFirstName());
		Assert.assertEquals("userName2", customerData.getLastName());
		Assert.assertEquals("titleCode", customerData.getTitleCode());
		Assert.assertEquals("userName1 userName2", customerData.getName());
		Assert.assertEquals("userUid", customerData.getUid());
		Assert.assertEquals("customerId", customerData.getCustomerId());

		verify(sitePreferenceConverter).convert(any(SitePreferenceModel.class), any(SitePreferenceData.class));
		Assert.assertNotNull(customerData.getSitePreference());
	}

	@Test
	public void testConvertEssential()
	{
		final CustomerModel userModel = mock(CustomerModel.class, withSettings().lenient());
		final TitleModel titleModel = mock(TitleModel.class, withSettings().lenient());
		given(userModel.getSessionCurrency()).willReturn(null);
		given(userModel.getDefaultPaymentAddress()).willReturn(null);
		given(userModel.getDefaultShipmentAddress()).willReturn(null);
		given(userModel.getSessionLanguage()).willReturn(null);
		given(userModel.getName()).willReturn("userName1 userName2");
		given(titleModel.getCode()).willReturn("titleCode");
		given(userModel.getUid()).willReturn("userUid");
		given(userModel.getCustomerID()).willReturn("customerId");
		given(customerNameStrategy.splitName("userName1 userName2")).willReturn(new String[]
		{ "userName1", "userName2" });

		final CustomerData customerData = customerConverter.convert(userModel);

		Assert.assertNotNull(customerData);
		Assert.assertNull(customerData.getCurrency());
		Assert.assertNull(customerData.getDefaultBillingAddress());
		Assert.assertNull(customerData.getDefaultShippingAddress());
		Assert.assertNull(customerData.getLanguage());
		Assert.assertNull(customerData.getTitleCode());
		Assert.assertEquals("userName1", customerData.getFirstName());
		Assert.assertEquals("userName2", customerData.getLastName());
		Assert.assertEquals("userName1 userName2", customerData.getName());
		Assert.assertEquals("userUid", customerData.getUid());
		Assert.assertEquals("customerId", customerData.getCustomerId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertSourceNull()
	{
		customerConverter.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertPrototypeNull()
	{
		final CustomerModel userModel = mock(CustomerModel.class);
		customerConverter.convert(userModel, null);
	}

	@Test
	public void testConvertWithUidForCustomer()
	{

		final CustomerModel user = new CustomerModel();
		user.setUid(DUMMY);

		final CustomerData customer = new CustomerData();

		customerConverter.populate(user, customer);

		Assert.assertEquals(DUMMY, customer.getUid());
		Assert.assertNull(customer.getDisplayUid());
	}

	@Test
	public void testConvertWithOrignalUidForCustomer()
	{

		final CustomerModel user = new CustomerModel();
		user.setOriginalUid(DUMMY);
		user.setUid(null);

		final CustomerData customer = new CustomerData();

		customerConverter.populate(user, customer);

		Assert.assertNull(customer.getUid());
		Assert.assertEquals(DUMMY, customer.getDisplayUid());

	}

	@Test
	public void testConvertWithoutOrignalUidOrUidForCustomer()
	{

		final CustomerModel user = new CustomerModel();
		user.setOriginalUid(null);
		user.setUid(null);

		final CustomerData customer = new CustomerData();

		customerConverter.populate(user, customer);

		Assert.assertNull(customer.getUid());
		Assert.assertNull(customer.getDisplayUid());
	}
}
