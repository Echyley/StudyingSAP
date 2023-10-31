/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesetaxinvoiceocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.chinesetaxinvoiceocctests.controllers.TaxInvoiceControllerTest
import de.hybris.platform.chinesetaxinvoiceocctests.setup.ChineseTaxInvoiceOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(TaxInvoiceControllerTest)
@IntegrationTest
class AllChineseTaxInvoiceOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		ChineseTaxInvoiceOccTestSetupUtils.loadExtensionDataInJunit()
		ChineseTaxInvoiceOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		try {
			ChineseTaxInvoiceOccTestSetupUtils.stopServer()
			ChineseTaxInvoiceOccTestSetupUtils.cleanData()
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
