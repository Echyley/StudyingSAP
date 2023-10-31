/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseaddressocctests.setup;

import de.hybris.platform.core.Registry;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class ChineseAddressOccTestSetupUtils extends de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
{
	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		final ChineseAddressOccTestSetup chineseAddressOccTestSetup = Registry.getApplicationContext().getBean("chineseAddressOccTestSetup", ChineseAddressOccTestSetup.class);
		chineseAddressOccTestSetup.loadData();
	}
}
