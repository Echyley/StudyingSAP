/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.configuration.dao.impl.DefaultGenericConfigurationDaoTest;
import de.hybris.platform.sap.core.configuration.datahub.SAPDataHubTransferConfigurationManagerTest;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceTest;
import de.hybris.platform.sap.core.configuration.http.impl.HTTPDestinationServiceTest;
import de.hybris.platform.sap.core.configuration.populators.GenericModel2DtoPopulatorTest;
import de.hybris.platform.sap.core.configuration.populators.GenericModel2MapPopulatorTest;
import de.hybris.platform.sap.core.configuration.rfc.test.RFCDestinationServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Server testsuite for extension sapcoreconfiguration.
 */
@IntegrationTest
@RunWith(Suite.class)
@SuiteClasses(
{ HTTPDestinationServiceTest.class, SAPGlobalConfigurationServiceTest.class, GenericModel2MapPopulatorTest.class,
		SAPDataHubTransferConfigurationManagerTest.class, DefaultGenericConfigurationDaoTest.class,
		RFCDestinationServiceTest.class, GenericModel2DtoPopulatorTest.class })
public class SapcoreconfigurationServerTestSuite
{

}
