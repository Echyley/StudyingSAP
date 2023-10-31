/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.b2bsapproductavailability.jalo;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the B2bsapproductavailability extension
 */
public class B2bsapproductavailabilityTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2bsapproductavailabilityTest.class.getName());

	@Before
	public void setUp()
	{
		// implement here code executed before each test
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}

	/**
	 * This is a sample test method.
	 */
	@Test
	public void testB2bsapproductavailability()
	{
		final boolean testTrue = true;
		assertTrue("true is not true", testTrue);
	}
}
