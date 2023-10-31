/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationocctests.test.groovy.webservicetests.v2

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
import com.sap.hybris.sapcpqquoteintegrationocctests.setup.CpqDiscountsTestSetupUtils
import com.sap.hybris.sapcpqquoteintegrationocctests.test.groovy.webservicetests.v2.controllers.CpqDiscounts
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite.class)
@Suite.SuiteClasses([CpqDiscounts])
@IntegrationTest
class AllCpqDiscountsOCCSpockTests {

	@BeforeClass
	static void setUpClass() {
		TestSetupUtils.loadDataInJunit()
		CpqDiscountsTestSetupUtils.loadExtensionDataInJunit()
		TestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		TestSetupUtils.stopServer()
		TestSetupUtils.cleanData()
	}

}



