/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils;
import de.hybris.platform.core.Registry;

import de.hybris.platform.servicelayer.user.UserService;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class CustomerTicketingOccTestSetupUtils extends TestSetupUtils
{
	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loginAdmin();
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		final CustomerTicketingOccTestSetup ticketingTestSetup = Registry.getApplicationContext().getBean("customerTicketingOccTestSetup", CustomerTicketingOccTestSetup.class);
		ticketingTestSetup.loadData();
	}

	private static void loginAdmin()
	{
		final UserService userService = Registry.getApplicationContext().getBean("userService", UserService.class);
		userService.setCurrentUser(userService.getAdminUser());
	}
}
