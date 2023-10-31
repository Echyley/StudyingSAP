/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.notificationocctests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.notificationocctests.controllers.NotificationOccControllerTest
import de.hybris.platform.notificationocctests.setup.NotificationOccTestSetupUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses(NotificationOccControllerTest)
@IntegrationTest
class AllNotificationOccSpockTests {

	@BeforeClass
	static void setUpClass() {
		NotificationOccTestSetupUtils.loadExtensionDataInJunit();
		NotificationOccTestSetupUtils.startServer();
	}

	@AfterClass
	static void tearDown() {
		try {
			NotificationOccTestSetupUtils.stopServer();
			NotificationOccTestSetupUtils.cleanData();
		} catch (Exception e) {
			// ignore;
		}
	}

	@Test
	static void testing() {
		//dummy test class
	}
}
