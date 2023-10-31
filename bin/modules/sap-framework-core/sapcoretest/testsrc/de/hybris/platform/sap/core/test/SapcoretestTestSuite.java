/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.test.property.PropertyAccessFactoryTest;
import de.hybris.platform.sap.core.test.property.PropertyAccessImplTest;
import de.hybris.platform.sap.core.test.property.PropertyAccessTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Testsuite for extension sapcoretest.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ PropertyAccessFactoryTest.class, PropertyAccessImplTest.class, PropertyAccessTest.class })
public class SapcoretestTestSuite
{

}
