/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JCoMonitorJAXBHandlerTest;
import de.hybris.platform.sap.core.jco.monitor.provider.JCoConnectionMonitorClusterProviderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Spring testsuite for extension sapcorejco.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ JCoMonitorJAXBHandlerTest.class, JCoConnectionMonitorClusterProviderTest.class, RFCDirectoryDestinationDataProviderTest.class })
public class SapcorejcoSpringTestSuite
{

}
