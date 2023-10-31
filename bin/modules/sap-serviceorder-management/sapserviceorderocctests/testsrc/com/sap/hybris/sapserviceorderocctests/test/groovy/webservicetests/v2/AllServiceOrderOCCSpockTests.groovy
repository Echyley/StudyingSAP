/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocctests.test.groovy.webservicetests.v2

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.b2bocctests.setup.TestSetupUtils
import com.sap.hybris.sapserviceorderocctests.setup.ServiceOrderTestSetupUtils
import com.sap.hybris.sapserviceorderocctests.test.groovy.webservicetests.v2.controllers.ServiceProductsControllerTest

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([ServiceProductsControllerTest])
@IntegrationTest
class AllServiceOrderOCCSpockTests {

	@BeforeClass
	static void setUpClass() {
		TestSetupUtils.loadExtensionDataInJunit()
		ServiceOrderTestSetupUtils.loadExtensionDataInJunit()
		TestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		TestSetupUtils.stopServer()
		TestSetupUtils.cleanData()
	}
}
