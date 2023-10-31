/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class B2BPunchoutOccTestSetupUtils extends de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(B2BPunchoutOccTestSetupUtils.class);

	public static void loadExtensionDataInJunit() 
	{
		Registry.setCurrentTenantByID("junit");
		loginAdmin();
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		// implement your OCC extension test data loading logic here
		LOG.info("Loading data for b2bpunchoutocctests OCC extension.");
		final B2BPUNCHOUTOCCTestSetup b2bOccTestSetup = Registry.getApplicationContext().getBean("b2BPUNCHOUTOCCTestSetup", B2BPUNCHOUTOCCTestSetup.class);
		b2bOccTestSetup.loadData();
	}
	
	private static void loginAdmin()
	{
		final UserService userService = Registry.getApplicationContext().getBean("userService", UserService.class);
		userService.setCurrentUser(userService.getAdminUser());

	}
}
