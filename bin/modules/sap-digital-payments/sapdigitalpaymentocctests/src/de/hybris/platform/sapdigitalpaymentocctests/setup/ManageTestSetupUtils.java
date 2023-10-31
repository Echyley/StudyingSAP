/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentocctests.setup;

import de.hybris.platform.core.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class ManageTestSetupUtils extends de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(ManageTestSetupUtils.class);

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		// implement your OCC extension test data loading logic here
		LOG.info("No data for current OCC extension.");
	}
}
