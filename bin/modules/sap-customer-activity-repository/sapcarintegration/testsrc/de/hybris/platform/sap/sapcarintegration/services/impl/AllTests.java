/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcarintegration.services.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ 
	DefaultCarDataProviderServiceTest.class, 
	DefaultCarOrderHistoryExtractorServiceTest.class,
	DefaultCarOrderHistoryServiceTest.class,
	DefaultMultichannelDataProviderServiceTest.class, 
	DefaultMultichannelOrderHistoryExtractorServiceTest.class,
	DefaultMultichannelOrderHistoryServiceTest.class
})
public class AllTests
{

}
