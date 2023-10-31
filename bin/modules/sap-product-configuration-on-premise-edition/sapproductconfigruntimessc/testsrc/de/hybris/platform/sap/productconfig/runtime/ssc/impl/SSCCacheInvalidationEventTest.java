/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SSCCacheInvalidationEventTest
{

	private SSCCacheInvalidationEvent classUndertest;
	private final List<String> keys = new ArrayList();

	@Before
	public void setUp()
	{
		keys.add("test");
		classUndertest = new SSCCacheInvalidationEvent(keys, "region");
	}

	@Test
	public void testGetters()
	{
		assertEquals(1, classUndertest.getCacheInvalidationKeys().size());
		assertEquals("test", classUndertest.getCacheInvalidationKeys().get(0));
		assertSame("region", classUndertest.getCacheRegionName());
	}

	@Test
	public void testCanPublish()
	{
		assertTrue(classUndertest.canPublish(null));
	}



}
