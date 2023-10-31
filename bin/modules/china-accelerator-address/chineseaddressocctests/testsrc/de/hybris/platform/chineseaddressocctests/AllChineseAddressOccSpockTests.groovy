/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseaddressocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.chineseaddressocctests.controllers.CitiesControllerTest
import de.hybris.platform.chineseaddressocctests.setup.ChineseAddressOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(CitiesControllerTest)
@IntegrationTest
class AllChineseAddressOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		ChineseAddressOccTestSetupUtils.loadExtensionDataInJunit()
		ChineseAddressOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		try {
			ChineseAddressOccTestSetupUtils.stopServer()
			ChineseAddressOccTestSetupUtils.cleanData()
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
