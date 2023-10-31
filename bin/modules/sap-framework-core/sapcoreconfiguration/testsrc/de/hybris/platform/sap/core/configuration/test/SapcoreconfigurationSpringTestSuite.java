/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.datahub.DataHubTransferHandlerTest;
import de.hybris.platform.sap.core.configuration.impl.ConfigurationPropertyAccessImplTest;
import de.hybris.platform.sap.core.configuration.impl.SAPConfigurationServiceImplTest;
import de.hybris.platform.sap.core.configuration.populators.SAPBaseStoreConfigurationMappingPopulatorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Spring testsuite for extension sapcoreconfiguration.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ ConfigurationPropertyAccessImplTest.class, SAPConfigurationServiceImplTest.class,
		SAPBaseStoreConfigurationMappingPopulatorTest.class, DataHubTransferHandlerTest.class })
public class SapcoreconfigurationSpringTestSuite
{

}
