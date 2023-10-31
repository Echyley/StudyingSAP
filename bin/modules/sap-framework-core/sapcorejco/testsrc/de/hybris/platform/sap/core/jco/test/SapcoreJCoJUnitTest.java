/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.test;

import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest;


/**
 * Base test class for JCO unit tests.
 */
@ContextConfiguration(locations =
{ "sap-passport-test-spring.xml", "classpath:sapcorejco-spring.xml", "classpath:sapcorejco-monitor-spring.xml", "classpath:sapcorejco-test-spring.xml",
		"classpath:global-sapcorejco-spring.xml" })
public class SapcoreJCoJUnitTest extends SapcoreSpringJUnitTest
{

}
