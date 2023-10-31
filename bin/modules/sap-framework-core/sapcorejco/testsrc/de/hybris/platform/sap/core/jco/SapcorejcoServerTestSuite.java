/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import de.hybris.bootstrap.annotations.ManualTest;

/**
 * Server testsuite for extension sapcorejco.
 */
@ManualTest

@RunWith(Suite.class)
@SuiteClasses({ SapcorejcoSNCTest.class })
public class SapcorejcoServerTestSuite {
    SapcorejcoServerTestSuite() {

    }
}
