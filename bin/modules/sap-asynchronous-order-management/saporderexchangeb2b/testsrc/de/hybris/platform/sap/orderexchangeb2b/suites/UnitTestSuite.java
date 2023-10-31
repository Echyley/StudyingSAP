/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchangeb2b.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hybris.platform.sap.orderexchangeb2b.outbound.impl.DefaultB2BOrderContributorTest;
import de.hybris.platform.sap.orderexchangeb2b.outbound.impl.DefaultB2BPartnerContributorTest;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultB2BOrderContributorTest.class,DefaultB2BPartnerContributorTest.class })
public class UnitTestSuite
{

}
