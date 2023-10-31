/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.PrettyPrinter;

import junit.framework.TestCase;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class PrettyPrinterTest extends TestCase
{


	private PrettyPrinter classUnderTest;

	@Override
	protected void setUp()
	{
		classUnderTest = new PrettyPrinter("MyPrettyPrinter");
	}

	@Test
	public void testToString()
	{
		assertEquals("MyPrettyPrinter", classUnderTest.toString());
	}

	@Test
	public void testToAdd()
	{
		classUnderTest.add(classUnderTest, "added String");
		assertEquals("MyPrettyPrinter\nadded String=[MyPrettyPrinter]", classUnderTest.toString());
	}

}
