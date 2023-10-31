/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.consignmenttrackingocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.consignmenttrackingocctests.controllers.ConsignmentTrackingControllerTest
import de.hybris.platform.consignmenttrackingocctests.setup.ConsignmentTrackingOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(ConsignmentTrackingControllerTest)
@IntegrationTest
class AllConsignmentTrackingOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		ConsignmentTrackingOccTestSetupUtils.loadExtensionDataInJunit();
		ConsignmentTrackingOccTestSetupUtils.startServer();
	}

	@AfterClass
	static void tearDown() {
		try {
			ConsignmentTrackingOccTestSetupUtils.stopServer();
			ConsignmentTrackingOccTestSetupUtils.cleanData();
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
