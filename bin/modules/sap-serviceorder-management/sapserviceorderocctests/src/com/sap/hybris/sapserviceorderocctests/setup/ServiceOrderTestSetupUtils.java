/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocctests.setup;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class ServiceOrderTestSetupUtils extends de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
{

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loginAdmin();
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		final SapServiceOrderOCCTestSetup sapServiceOrderOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapServiceOrderOCCTestSetup",
				SapServiceOrderOCCTestSetup.class);
		sapServiceOrderOCCTestSetup.loadData();
	}

	private static void loginAdmin()
	{
		final UserService userService = Registry.getApplicationContext().getBean("userService", UserService.class);
		userService.setCurrentUser(userService.getAdminUser());
	}
}
