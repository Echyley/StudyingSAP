/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapqualtricsaddon.jalo;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

/**
 * JUnit Tests for the Sapqualtricsaddon extension
 */

public class SapqualtricsaddonTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SapqualtricsaddonTest.class.getName());

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
	public void testSapqualtricsaddon()
	{
		final boolean testTrue = true;
		assertTrue("true is not true", testTrue);
	}
}

