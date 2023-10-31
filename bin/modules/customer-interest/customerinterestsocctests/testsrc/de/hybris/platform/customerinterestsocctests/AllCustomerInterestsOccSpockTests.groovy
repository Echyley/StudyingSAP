/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerinterestsocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.customerinterestsocctests.setup.CustomerInterestsOccTestSetupUtils
import de.hybris.platform.customerinterestsocctests.controllers.CustomerInterestsOccControllerTests
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(CustomerInterestsOccControllerTests)
@IntegrationTest
class AllCustomerInterestsOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		CustomerInterestsOccTestSetupUtils.loadExtensionDataInJunit();
		CustomerInterestsOccTestSetupUtils.startServer();
	}

	@AfterClass
	static void tearDown() {
		try {
			CustomerInterestsOccTestSetupUtils.stopServer();
			CustomerInterestsOccTestSetupUtils.cleanData();
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
