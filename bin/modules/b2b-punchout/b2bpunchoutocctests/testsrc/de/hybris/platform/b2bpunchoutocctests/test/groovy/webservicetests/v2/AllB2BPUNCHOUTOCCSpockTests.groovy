/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2

import de.hybris.platform.util.Config
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.b2bpunchoutocctests.setup.B2BPunchoutOccTestSetupUtils
import de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers.B2BPunchoutOrderCreationTest
import de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers.B2BPunchoutProfileTest
import de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers.B2BPunchoutSetupTest
import de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.controllers.B2BPunchoutSessionTest;
import de.hybris.platform.b2bpunchoutocctests.test.groovy.webservicetests.v2.Filter.PunchOutRequestMatchingFilterTest

@RunWith(Suite.class)
@Suite.SuiteClasses([B2BPunchoutProfileTest,B2BPunchoutSetupTest ,B2BPunchoutOrderCreationTest, B2BPunchoutSessionTest, PunchOutRequestMatchingFilterTest])
@IntegrationTest
class AllB2BPUNCHOUTOCCSpockTests {

	@BeforeClass
	static void setUpClass() {
		B2BPunchoutOccTestSetupUtils.loadExtensionDataInJunit()
		Config.setParameter("api.compatibility.b2c.channels", "B2B");
		B2BPunchoutOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		Config.setParameter("api.compatibility.b2c.channels", "B2C");
		B2BPunchoutOccTestSetupUtils.stopServer()
		B2BPunchoutOccTestSetupUtils.cleanData()
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
