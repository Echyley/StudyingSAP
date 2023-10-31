/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineselogisticocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.chineselogisticocctests.controllers.LogisticsControllerTest
import de.hybris.platform.chineselogisticocctests.setup.ChineseLogisticOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(LogisticsControllerTest)
@IntegrationTest
class AllChineseLogisticOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		ChineseLogisticOccTestSetupUtils.loadExtensionDataInJunit()
		ChineseLogisticOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		try {
			ChineseLogisticOccTestSetupUtils.stopServer()
			ChineseLogisticOccTestSetupUtils.cleanData()
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
