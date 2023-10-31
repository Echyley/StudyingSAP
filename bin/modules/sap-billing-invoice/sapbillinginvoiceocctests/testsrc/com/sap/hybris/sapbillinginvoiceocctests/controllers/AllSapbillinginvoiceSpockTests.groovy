/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceocctests.controllers

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
import com.sap.hybris.sapbillinginvoiceocctests.setup.BillingTestSetupUtils;

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([
	BillingInvoiceTest])
@IntegrationTest
class AllSapbillinginvoiceSpockTests {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AllSapbillinginvoiceSpockTests.class)

	@BeforeClass
	public static void setUpClass() {
		TestSetupUtils.loadDataInJunit();
		BillingTestSetupUtils.loadExtensionDataInJunit();
		TestSetupUtils.startServer();
	}

	@AfterClass
	public static void tearDown() {
		TestSetupUtils.stopServer();
		TestSetupUtils.cleanData();
	}

}
