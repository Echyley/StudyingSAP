/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.multisite.impl.DefaultMultiSiteUidDecorationService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCustomerEmailResolutionServiceTest
{

	@Mock
	private ConfigurationService configurationService;
	@Spy
	private DefaultMultiSiteUidDecorationService multiSiteUidDecorationService;
	@InjectMocks
	private DefaultCustomerEmailResolutionService customerEmailResolutionService;
	@Mock
	private CustomerModel customer1;
	@Mock
	private CustomerModel customer2;
	@Mock
	private CustomerModel customer3;
	@Mock
	private CustomerModel customer4;
	@Mock
	private CustomerModel customer5;
	@Mock
	private CustomerModel customer6;
	@Mock
	private CustomerModel customer7;
	@Mock
	private CustomerModel customer8;
	@Mock
	private BaseSiteModel isolationSite;

	@Before
	public void setUp() throws Exception
	{
		final Configuration configuration = mock(Configuration.class);
		final String dummy = "dummy";
		given(isolationSite.getDataIsolationEnabled()).willReturn(true);
		given(isolationSite.getUid()).willReturn("site");
	}

	@Test
	public void shouldReturnEmailWithStandardCustomer()
	{
		// Registered Customer
		given(customer1.getOriginalUid()).willReturn("Customer1@demo.com");
		given(customer1.getType()).willReturn(CustomerType.REGISTERED);

		String email = customerEmailResolutionService.getEmailForCustomer(customer1);
		assertEquals("customer1@demo.com", email);

		// Guest customer
		given(customer2.getOriginalUid()).willReturn("abc" + "|" + "cuStomer2@demo.com");
		given(customer2.getType()).willReturn(CustomerType.GUEST);
		email = customerEmailResolutionService.getEmailForCustomer(customer2);
		assertEquals("customer2@demo.com", email);
	}

	@Test
	public void shouldReturnEmailWithIsolatedCustomer()
	{
		// Registered Customer
		given(customer3.getOriginalUid()).willReturn("Customer3@demo.com");
		given(customer3.getType()).willReturn(CustomerType.REGISTERED);
		String email = customerEmailResolutionService.getEmailForCustomer(customer3);
		assertEquals("customer3@demo.com", email);

		// Guest customer
		given(customer4.getOriginalUid()).willReturn("abc" + "|" + "custoMer4@demo.com");
		given(customer4.getType()).willReturn(CustomerType.GUEST);
		email = customerEmailResolutionService.getEmailForCustomer(customer4);
		assertEquals("customer4@demo.com", email);
	}

	@Test
	public void shouldReturnEmailWithStandardCustomerAndOriginalUidMissing()
	{
		// Registered Customer
		given(customer5.getUid()).willReturn("customer5@demo.com");
		given(customer5.getType()).willReturn(CustomerType.REGISTERED);
		given(customer5.getSite()).willReturn(null);

		String email = customerEmailResolutionService.getEmailForCustomer(customer5);
		assertEquals("customer5@demo.com", email);

		// Guest customer
		given(customer6.getOriginalUid()).willReturn("abc" + "|" + "cuStomer6@demo.com");
		given(customer6.getType()).willReturn(CustomerType.GUEST);
		email = customerEmailResolutionService.getEmailForCustomer(customer6);
		assertEquals("customer6@demo.com", email);
	}

	@Test
	public void shouldReturnEmailWithIsolatedCustomerAndOriginalUidMissing()
	{
		// Registered Customer
		given(customer7.getUid()).willReturn("customer7@demo.com" + "|" + "site");
		given(customer7.getType()).willReturn(CustomerType.REGISTERED);
		given(customer7.getSite()).willReturn(isolationSite);
		String email = customerEmailResolutionService.getEmailForCustomer(customer7);
		assertEquals("customer7@demo.com", email);

		// Guest customer
		given(customer8.getUid()).willReturn("abc" + "|" + "customer8@demo.com" + "|" + "site");
		given(customer8.getType()).willReturn(CustomerType.GUEST);
		given(customer8.getSite()).willReturn(isolationSite);
		email = customerEmailResolutionService.getEmailForCustomer(customer8);
		assertEquals("customer8@demo.com", email);
	}
}
