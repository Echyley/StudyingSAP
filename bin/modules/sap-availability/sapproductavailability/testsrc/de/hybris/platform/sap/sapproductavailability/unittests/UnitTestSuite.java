/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.unittests;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapproductavailability.backend.impl.SapProductAvailabilityBackendErpUnitTests;
import de.hybris.platform.sap.sapproductavailability.service.impl.DefaultSapProductAvailabilityServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultSapProductAvailabilityServiceTest.class, SapProductAvailabilityBackendErpUnitTests.class })
public class UnitTestSuite
{
	//Left empty intentionally 
}
