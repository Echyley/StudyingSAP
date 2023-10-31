/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.configurer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Test for {@link ConfigurerEntitiesListImpl}.
 */
@UnitTest
public class ConfigurerEntitiesListImplTest
{
	private final ConfigurerEntitiesListImpl<String> classUnderTest = new ConfigurerEntitiesListImpl<String>();
	private static String URLPATTERN1 = "/airlines/*";
	private static String URLPATTERN2 = "/connections/*";


	/**
	 * Test testEntities method.
	 */
	@Test
	public void testEntities()
	{
		classUnderTest.addEntity(URLPATTERN1);
		classUnderTest.addEntity(URLPATTERN2);

		final List<String> entities = classUnderTest.getEntities();
		assertTrue(entities.size() == 2);

		assertEquals(URLPATTERN1, classUnderTest.getEntities().get(0));
	}
}
