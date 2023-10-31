/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesetaxinvoiceocctests.setup;

import de.hybris.platform.core.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class ChineseTaxInvoiceOccTestSetupUtils extends de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(ChineseTaxInvoiceOccTestSetupUtils.class);

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loadExtensionData();
	}

	public static void loadExtensionData()
	{
		final ChineseTaxInvoiceOccTestSetup chineseTaxInvoiceOccTestSetup = Registry.getApplicationContext().getBean("chineseTaxInvoiceOccTestSetup", ChineseTaxInvoiceOccTestSetup.class);
		chineseTaxInvoiceOccTestSetup.loadData();
		LOG.info("Data loaded.");
	}
}
