/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ConnectedDocumentImplTest extends TestCase
{

	private ConnectedDocumentImpl classUnderTest;

	@Override
	public void setUp()
	{
		classUnderTest = new ConnectedDocumentImpl();
	}

	public void testBusObjectType()
	{

		String busObjectType = "busType";
		classUnderTest.setBusObjectType(busObjectType);
		assertEquals(busObjectType, classUnderTest.getBusObjectType());
	}

	public void testRefGuid()
	{

		String refGuid = "ABC";
		classUnderTest.setRefGuid(refGuid);
		assertEquals(refGuid, classUnderTest.getRefGuid());
	}

	public void testAppTyp()
	{

		String appTyp = "X3";
		classUnderTest.setAppTyp(appTyp);
		assertEquals(appTyp, classUnderTest.getAppTyp());
	}

}
