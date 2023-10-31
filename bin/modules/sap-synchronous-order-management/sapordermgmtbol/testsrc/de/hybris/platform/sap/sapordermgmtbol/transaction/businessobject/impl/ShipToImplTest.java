/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class ShipToImplTest extends SapordermanagmentBolSpringJunitTest
{

	protected ShipToImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (ShipToImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIP_TO);
	}

	@Test
	public void testConstructor()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testClone()
	{

		classUnderTest.setId("123");
		final ShipToImpl clone = classUnderTest.clone();
		assertNotSame(clone, classUnderTest);
		assertEquals(classUnderTest.getId(), clone.getId());


	}

}
