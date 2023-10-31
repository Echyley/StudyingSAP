/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.jalo;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;


/**
 * JUnit Tests for the Gigyaloginaddon extension
 */
public class GigyaloginaddonTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GigyaloginaddonTest.class.getName());

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
	public void testGigyaloginaddon()
	{
		final boolean testTrue = true;
		assertTrue("true is not true", testTrue);
	}
}
