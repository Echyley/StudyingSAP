/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameter;
import de.hybris.platform.sap.core.jco.test.SapcoreJCoJUnitTest;


/**
 * Test class for JCo Connection Parameters.
 */
@UnitTest
@ContextConfiguration(locations =
{ "JCoConnectionParametersTest-spring.xml" })
public class JCoConnectionParametersTest extends SapcoreJCoJUnitTest
{

	@Resource(name = "sapCoreConnectionParametersJCo")
	private JCoConnectionParametersImpl classUnderTest;

	/**
	 * Test connection parameter 1.
	 */
	@Test
	public void testConnectionParameter1()
	{
		final JCoConnectionParameter connectionParameter = classUnderTest.getConnectionParameter("ZCRM_ISA_SCARR_GETLIST_1");
		assertNotNull(connectionParameter);
		assertEquals("CRM_ISA_SCARR_GETLIST", connectionParameter.getFunctionModuleToBeReplaced());
		assertEquals("default", connectionParameter.getCacheType());
		assertEquals(true, connectionParameter.getTraceBefore());
		assertEquals(false, connectionParameter.getTraceAfter());

		assertEquals(3, connectionParameter.getTraceExcludeImportParametersList().size());
		assertEquals(1, connectionParameter.getTraceExcludeExportParametersList().size());
		assertEquals(0, connectionParameter.getTraceExcludeTableParametersList().size());


	}

	/**
	 * Test connection parameter 2.
	 */
	@Test
	public void testConnectionParameter2()
	{
		final JCoConnectionParameter connectionParameter = classUnderTest.getConnectionParameter("ZCRM_ISA_SCARR_GETLIST_2");
		assertNotNull(connectionParameter);
		assertEquals(null, connectionParameter.getFunctionModuleToBeReplaced());
		assertEquals("NonDefault", connectionParameter.getCacheType());
		assertEquals(false, connectionParameter.getTraceBefore());
		assertEquals(true, connectionParameter.getTraceAfter());
	}

	/**
	 * Test connection parameter replacement.
	 */
	@Test
	public void testConnectionParameterReplacement()
	{
		final JCoConnectionParameter connectionParameter = classUnderTest
				.getConnectionParameter("CRM_ISA_SCARR_GETLIST_REPLACEMENT1");
		assertNotNull(connectionParameter);
		assertEquals("default2", connectionParameter.getCacheType());

	}



	/**
	 * Test connection parameter replacement.
	 */
	@Test
	public void testConnectionParameterInvalid()
	{
		final JCoConnectionParameter connectionParameter = classUnderTest.getConnectionParameter("ZCRM_ISA_SCARR_GETLIST_3");
		assertNull(connectionParameter);
	}

}
