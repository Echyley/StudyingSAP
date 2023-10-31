/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.event.PublishEventContext;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigurationCacheInvalidationEventTest
{
	private static final String ATTR_VALUE = "ATTR_VALUE";
	private static final String ATTR_KEY = "ATTR_KEY";
	private static final String CONFIG_ID = "c123";
	private ProductConfigurationCacheInvalidationEvent classUnderTest;
	private Map<String, String> contextAttr;

	@Before
	public void setUp()
	{
		contextAttr = new HashMap<>();
		contextAttr.put(ATTR_KEY, ATTR_VALUE);
		classUnderTest = new ProductConfigurationCacheInvalidationEvent(CONFIG_ID, contextAttr);
	}

	@Test
	public void testCreationStableConstructor()
	{
		classUnderTest = new ProductConfigurationCacheInvalidationEvent(CONFIG_ID);
		assertEquals(CONFIG_ID, classUnderTest.getConfigId());
		assertTrue(classUnderTest.getContextAttributes().isEmpty());
	}

	@Test
	public void testCreation()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigId());
		assertEquals(1, classUnderTest.getContextAttributes().size());
		assertEquals(ATTR_VALUE, classUnderTest.getContextAttributes().get(ATTR_KEY));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetContextAttributesReturnsImmutableMap()
	{
		classUnderTest.getContextAttributes().put(ATTR_KEY, ATTR_VALUE);
	}

	@Test(expected = NullPointerException.class)
	public void testCanPublishNullCtxt()
	{
		classUnderTest.canPublish(null);
	}

	@Test
	public void testCanPublishSameNode()
	{
		final PublishEventContext ctxt = PublishEventContext.newBuilder().withSourceNodeId(5).withTargetNodeId(5).build();
		assertFalse(CONFIG_ID, classUnderTest.canPublish(ctxt));
	}

	@Test
	public void testCanPublishOtherNode()
	{
		final PublishEventContext ctxt = PublishEventContext.newBuilder().withSourceNodeId(5).withTargetNodeId(1).build();
		assertTrue(CONFIG_ID, classUnderTest.canPublish(ctxt));
	}
}
