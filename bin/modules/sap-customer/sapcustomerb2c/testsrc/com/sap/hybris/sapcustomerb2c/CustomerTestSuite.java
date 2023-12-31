/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sap.hybris.sapcustomerb2c.inbound.CustomerImportServiceTest;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerExportServiceTest;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerPublishActionTest;
import com.sap.hybris.sapcustomerb2c.outbound.CustomerRegistrationEventListenerTest;
import com.sap.hybris.sapcustomerb2c.outbound.DefaultAddressInterceptorTest;
import com.sap.hybris.sapcustomerb2c.outbound.DefaultCustomerInterceptorTest;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Test suite for all JUnit tests
 */
@UnitTest
@RunWith(Suite.class)
@Suite.SuiteClasses(
{ CustomerExportServiceTest.class, CustomerPublishActionTest.class,
		CustomerRegistrationEventListenerTest.class, CustomerImportServiceTest.class,
		DefaultCustomerInterceptorTest.class, DefaultAddressInterceptorTest.class })
public class CustomerTestSuite
{
	// empty block should be documented
}
