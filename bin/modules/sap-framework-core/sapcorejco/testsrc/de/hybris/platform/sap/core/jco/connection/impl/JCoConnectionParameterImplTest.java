/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameters;


/**
 * Test for JCoConnectionParameterImpl.
 */
@UnitTest
public class JCoConnectionParameterImplTest
{

	/**
	 * Self-explanatory.
	 */
	@Test
	public void testInit()
	{

		final JCoConnectionParameterImpl classUnderTest = new JCoConnectionParameterImpl();


		final JCoConnectionParameters connectionParameters = new JCoConnectionParametersImpl();
		classUnderTest.setConnectionParameters(connectionParameters);


		classUnderTest.setTraceExcludeExportParameters("red,green,blue");

		classUnderTest.setTraceExcludeImportParameters("black");

		classUnderTest.setTraceExcludeTableParameters("");

		classUnderTest.init();

		Assert.assertTrue(classUnderTest.getTraceExcludeExportParametersList().size() == 3);

		Assert.assertTrue(classUnderTest.getTraceExcludeImportParametersList().size() == 1);

		Assert.assertTrue(classUnderTest.getTraceExcludeTableParametersList().size() == 0);

		Assert.assertTrue(classUnderTest.getConnectionParameters().getConnectionParameterMap().size() == 1);

		try
		{
			classUnderTest.getTraceExcludeExportParametersList().add("try to add something to this list");
			Assert.fail("list should be unmodifieable");
		}
		catch (final UnsupportedOperationException e)
		{
			//OK

		}

		Assert.assertTrue(!classUnderTest.toString().isEmpty());

	}

	/**
	 * Self-explanatory.
	 */
	@Test
	public void testInitWithNullValues()
	{

		final JCoConnectionParameterImpl classUnderTest = new JCoConnectionParameterImpl();

		final JCoConnectionParameters connectionParameters = new JCoConnectionParametersImpl();
		classUnderTest.setConnectionParameters(connectionParameters);


		classUnderTest.setTraceExcludeExportParameters(null);

		classUnderTest.setTraceExcludeImportParameters(null);

		classUnderTest.setTraceExcludeTableParameters(null);

		classUnderTest.init();

		Assert.assertTrue(classUnderTest.getTraceExcludeExportParametersList().isEmpty());

		Assert.assertTrue(classUnderTest.getTraceExcludeImportParametersList().isEmpty());

		Assert.assertTrue(classUnderTest.getTraceExcludeTableParametersList().isEmpty());


	}
}
