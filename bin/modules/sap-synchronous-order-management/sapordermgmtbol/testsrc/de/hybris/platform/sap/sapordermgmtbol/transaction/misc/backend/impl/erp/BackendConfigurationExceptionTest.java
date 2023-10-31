/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp;

import junit.framework.TestCase;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class BackendConfigurationExceptionTest extends TestCase
{
	private BackendConfigurationException classUnderTest;
	final private static String MESSAGE = "myMessage";

	@Override
	protected void setUp()
	{
		classUnderTest = new BackendConfigurationException(MESSAGE);
	}

	@Test
	public void testGetMessage()
	{
		assertEquals(MESSAGE, classUnderTest.getMessage());
	}

}
