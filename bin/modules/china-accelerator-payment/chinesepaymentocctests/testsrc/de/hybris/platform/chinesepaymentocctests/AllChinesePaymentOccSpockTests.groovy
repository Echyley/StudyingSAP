/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesepaymentocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.chinesepaymentocctests.controllers.PaymentInfoControllerTest
import de.hybris.platform.chinesepaymentocctests.setup.ChinesePaymentOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(PaymentInfoControllerTest)
@IntegrationTest
class AllChinesePaymentOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		ChinesePaymentOccTestSetupUtils.loadExtensionDataInJunit()
		ChinesePaymentOccTestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		try {
			ChinesePaymentOccTestSetupUtils.stopServer()
			ChinesePaymentOccTestSetupUtils.cleanData()
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
