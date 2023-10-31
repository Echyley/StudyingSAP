/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerinterestsocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils;
import de.hybris.platform.core.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class CustomerInterestsOccTestSetupUtils extends TestSetupUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(CustomerInterestsOccTestSetupUtils.class);

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		final CustomerInterestsOccTestsSetup occSetup = Registry.getApplicationContext()
				.getBean("customerInterestsOccTestsSetup", CustomerInterestsOccTestsSetup.class);
		occSetup.loadData();
		LOG.info("Data loaded.");
	}
}
