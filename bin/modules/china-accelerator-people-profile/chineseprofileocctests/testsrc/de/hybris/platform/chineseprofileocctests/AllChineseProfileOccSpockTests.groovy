/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.chineseprofileocctests.controllers.EmailLanguageControllerTest
import de.hybris.platform.chineseprofileocctests.controllers.MobileNumberControllerTest
import de.hybris.platform.chineseprofileocctests.setup.ChineseProfileOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([EmailLanguageControllerTest,MobileNumberControllerTest])
@IntegrationTest
class AllChineseProfileOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		ChineseProfileOccTestSetupUtils.loadExtensionDataInJunit()
		ChineseProfileOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		try {
			ChineseProfileOccTestSetupUtils.stopServer()
			ChineseProfileOccTestSetupUtils.cleanData()
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
