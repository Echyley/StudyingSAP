/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.event.PublishEventContext;

import org.junit.Test;


@UnitTest
public class ProductConfigurationCPQCacheInvalidationEventTest
{

	private final ProductConfigurationCPQCacheInvalidationEvent classUnderTest = new ProductConfigurationCPQCacheInvalidationEvent(
			CONFIG_ID);
	private final static String CONFIG_ID = "test123";

	private PublishEventContext ctxt;

	@Test
	public void testGetConfigId()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigId());
	}

	@Test
	public void testCanPublishDifferentNode()
	{
		ctxt = PublishEventContext.newBuilder().withSourceNodeId(1).withTargetNodeId(2).build();
		assertTrue(classUnderTest.canPublish(ctxt));
	}

	@Test
	public void testCanPublishSameNode()
	{
		ctxt = PublishEventContext.newBuilder().withSourceNodeId(1).withTargetNodeId(1).build();
		assertFalse(classUnderTest.canPublish(ctxt));
	}

	@Test(expected = NullPointerException.class)
	public void testCanPublishNull()
	{
		classUnderTest.canPublish(null);
	}

}
