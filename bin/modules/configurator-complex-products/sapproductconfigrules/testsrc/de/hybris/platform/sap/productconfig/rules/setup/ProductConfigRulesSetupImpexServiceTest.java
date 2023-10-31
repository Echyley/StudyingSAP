/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class ProductConfigRulesSetupImpexServiceTest
{

	private final ProductConfigRulesSetupImpexServiceTestHelper classUnderTest = new ProductConfigRulesSetupImpexServiceTestHelper();
	@Test
	public void testBuildMacroHeaderEmpty()
	{
		assertEquals("", classUnderTest.buildMacroHeader(Collections.EMPTY_MAP));
	}

	@Test
	public void testBuildMacroHeader()
	{
		final Map<String, Object> props = createTestProperties();
		assertEquals("$param2=value2\n$param1=value1\n", classUnderTest.buildMacroHeader(props));
	}

	@Test
	public void testImportWithParameters()
	{
		final Map<String, Object> props = createTestProperties();
		assertTrue(classUnderTest.importImpexFile("/sapproductconfigrules/test/test.impex", props, false));
		assertTrue(classUnderTest.getContentToBeImported().startsWith("$param"));
		assertTrue(classUnderTest.getContentToBeImported().contains("$param2=value2"));
		assertTrue(classUnderTest.getContentToBeImported().contains("$param1=value1"));
	}

	@Test
	public void testImportNoParameters()
	{
		assertTrue(classUnderTest.importImpexFile("/sapproductconfigrules/test/test.impex", new ImpexMacroParameterData(), false));
		assertTrue(classUnderTest.getContentToBeImported().startsWith("INSERT_UPDATE TestTable;param1;param2"));
		assertFalse(classUnderTest.getContentToBeImported().contains("$param2=value2"));
		assertFalse(classUnderTest.getContentToBeImported().contains("$param1=value1"));

	}

	@Test
	public void testImportNotExistingLogsErr()
	{
		assertFalse(classUnderTest.importImpexFile("NOT_EXISTING.impex", Collections.emptyMap(), true));
		assertNull(classUnderTest.getContentToBeImported());
	}

	@Test
	public void testImportNotExistingLogsInfo()
	{
		assertFalse(classUnderTest.importImpexFile("NOT_EXISTING.impex", Collections.emptyMap(), false));
		assertNull(classUnderTest.getContentToBeImported());
	}

	protected Map<String, Object> createTestProperties()
	{
		final Map<String, Object> props = new HashMap();
		props.put("param1", "value1");
		props.put("$param2", "value2");
		return props;
	}
}
