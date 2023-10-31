/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceocctests.setup;

import de.hybris.platform.core.Registry;

import org.apache.log4j.Logger;
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class BillingTestSetupUtils extends TestSetupUtils
{
	public static final Logger LOG = Logger.getLogger(BillingTestSetupUtils.class);

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loadExtensionData();
	}

	public static void loadExtensionData()
	{

		final SapBillingInvoiceOCCTestSetup sapBillingInvoiceOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapBillingInvoiceOCCTestSetup", SapBillingInvoiceOCCTestSetup.class);
		sapBillingInvoiceOCCTestSetup.loadData();

	}
}
