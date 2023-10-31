/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsocctests.controllers

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercewebservicestests.setup.TestSetupUtils

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([EntitlementsInstancesControllerTest])
@IntegrationTest
class AllEntitlementsOCCSpockTests {

	private static final Logger LOG = LoggerFactory.getLogger(AllEntitlementsOCCSpockTests.class);
	@BeforeClass
	static void setUpClass() {
		LOG.info("Inside AllEntitlementsOCCSpockTests");
		TestSetupUtils.loadData();
		com.sap.hybris.sapentitlementsocctests.setup.TestSetupUtilsEntitlements.loadData()();
		TestSetupUtils.loadExtensionDataInJunit()
		TestSetupUtils.startServer()
	}

	@AfterClass
	static void tearDown() {
		TestSetupUtils.stopServer()
		TestSetupUtils.cleanData()
	}

}