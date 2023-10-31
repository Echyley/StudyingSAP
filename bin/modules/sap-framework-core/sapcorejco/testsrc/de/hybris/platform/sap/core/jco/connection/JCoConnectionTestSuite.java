/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionParameterImplTest;
import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionParametersTest;
import de.hybris.platform.sap.core.jco.connection.impl.JCoExceptionSpliterTest;
import de.hybris.platform.sap.core.jco.test.RFCDirectoryDestinationDataProviderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ JCoConnectionParameterImplTest.class,
        JCoConnectionParametersTest.class, JCoExceptionSpliterTest.class,
        RFCDirectoryDestinationDataProviderTest.class })
public class JCoConnectionTestSuite {
    JCoConnectionTestSuite() {

    }
}
