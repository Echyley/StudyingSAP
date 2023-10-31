/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.test;

import de.hybris.platform.sap.core.jco.rec.SapcoreJCoRecJUnitTest;

import org.springframework.test.context.ContextConfiguration;


/**
 * Base test class for junit test in the hybris environment without starting the server.
 */
@ContextConfiguration(locations =
{ "classpath:sapcorebol-spring.xml", "classpath:global-sapcorejco-spring.xml", "sapcorebol-test-spring.xml" })
public abstract class SapcorebolSpringJUnitTest extends SapcoreJCoRecJUnitTest//SapcoreJCoJUnitTest // NOPMD
{

}
