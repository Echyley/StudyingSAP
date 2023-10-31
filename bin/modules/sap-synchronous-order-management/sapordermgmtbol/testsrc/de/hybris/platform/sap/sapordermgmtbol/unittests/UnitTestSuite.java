/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.unittests;

import junit.framework.Test;
import junit.framework.TestSuite;


@SuppressWarnings("javadoc")
public class UnitTestSuite
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite("Salestransactions: Unit Tests");
		suite.addTest(beERPSuite());
		return suite;
	}



	private static Test beERPSuite()
	{
		final TestSuite suite = new TestSuite("BE ERP Layer");

		return suite;
	}

}
