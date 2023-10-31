/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.consignmenttrackingocctests.setup;

import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils;
import de.hybris.platform.core.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class ConsignmentTrackingOccTestSetupUtils extends TestSetupUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(ConsignmentTrackingOccTestSetupUtils.class);

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		final ConsignmentTrackingOccSetup occSetup = Registry.getApplicationContext()
				.getBean("consignmentTrackingOccSetup", ConsignmentTrackingOccSetup.class);
		occSetup.loadData();
		LOG.info("Data loaded.");
	}
}
