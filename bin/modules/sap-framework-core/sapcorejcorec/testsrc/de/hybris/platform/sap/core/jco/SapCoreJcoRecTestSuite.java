/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.mock.impl.DefaultJCoMockRepositoryFactoryTest;
import de.hybris.platform.sap.core.jco.rec.SapcoreJCoRecorderTestPlaybackMode;
import de.hybris.platform.sap.core.jco.rec.impl.JCoRecManagedConnectionFactoryTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Testsuite for SapCoreJcoRec.
 */

@UnitTest
@RunWith(Suite.class)
@SuiteClasses({ DefaultJCoMockRepositoryFactoryTest.class,
        SapcoreJCoRecorderTestPlaybackMode.class,
        JCoRecManagedConnectionFactoryTest.class })
public class SapCoreJcoRecTestSuite {
    SapCoreJcoRecTestSuite() {

    }
}
