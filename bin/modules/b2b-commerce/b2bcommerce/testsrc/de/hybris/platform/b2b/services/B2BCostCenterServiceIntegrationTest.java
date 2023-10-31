/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.services;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class B2BCostCenterServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService;
	@Resource
	private UserService userService;

	private UserModel rusticCustomerUser1;
	private UserModel rusticCustomerUser2;
	private UserModel rusticCustomerUser3;

	private static final String RUSTIC_CUSTOMER_USER1 = "frank.test@rustic-hw.com";
	private static final String RUSTIC_CUSTOMER_USER2 = "frank.test2@rustic-hw.com";
	private static final String RUSTIC_CUSTOMER_USER3 = "frank.test3@rustic-hw.com";

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createDefaultCatalog();
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");
		importCsv("/b2bcommerce/test/testB2BCostCenterService.impex", "UTF-8");
		rusticCustomerUser1 = userService.getUserForUID(RUSTIC_CUSTOMER_USER1);
		rusticCustomerUser2 = userService.getUserForUID(RUSTIC_CUSTOMER_USER2);
		rusticCustomerUser3 = userService.getUserForUID(RUSTIC_CUSTOMER_USER3);
	}

	@Test
	public void testGetCurrencyByUserWhenUnitHasCurrency()
	{
		final CurrencyModel currency = b2bCostCenterService.getAvailableCurrencyByUser(rusticCustomerUser1);
		assertThat(currency).isNotNull().hasFieldOrPropertyWithValue("isocode", "IE");
	}

	/**
	 * 子元素有的
	 */
	@Test
	public void testGetCurrencyByUserWhenSubunitHasCurrency()
	{
		final CurrencyModel currency = b2bCostCenterService.getAvailableCurrencyByUser(rusticCustomerUser2);
		assertThat(currency).isNotNull().hasFieldOrPropertyWithValue("isocode", "AD");
	}

	@Test
	public void testGetCurrencyByUserWhenNoCurrency()
	{
		final CurrencyModel currency = b2bCostCenterService.getAvailableCurrencyByUser(rusticCustomerUser3);
		assertThat(currency).isNull();
	}
}
