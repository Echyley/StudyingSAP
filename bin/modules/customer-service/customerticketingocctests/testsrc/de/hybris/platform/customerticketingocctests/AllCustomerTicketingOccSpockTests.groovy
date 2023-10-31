/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.customerticketingocctests.controllers.TicketControllerTest
import de.hybris.platform.customerticketingocctests.setup.CustomerTicketingOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(TicketControllerTest)
@IntegrationTest
class AllCustomerTicketingOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		CustomerTicketingOccTestSetupUtils.loadExtensionDataInJunit()
		CustomerTicketingOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		try {
			CustomerTicketingOccTestSetupUtils.stopServer()
			CustomerTicketingOccTestSetupUtils.cleanData()
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
