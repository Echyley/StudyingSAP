/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.test;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Base test class for sapcoreconfiguration junit test in the hybris environment without starting the server.
 */
// Standard spring xml cannot be added since it uses objects which are only available at server runtime
//  "classpath:sapcoreconfiguration-spring.xml" and "classpath:sapcoreconfiguration-datahub-spring.xml" explicitly omitted due to server dependencies 
public abstract class SapcoreconfigurationSpringJUnitTest extends SapcoreSpringJUnitTest // NOPMD
{

	// 

}
