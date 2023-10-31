/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class ContextAttributeImplTest
{
	ContextAttributeImpl classUnderTest = new ContextAttributeImpl();



	@Test
	public void testContextAttributeImplAttributes()
	{
		final String name = "VBAK-MATNR";
		classUnderTest.setName(name);
		assertEquals(name, classUnderTest.getName());
		final String value = "KD990MIX";
		classUnderTest.setValue(value);
		assertEquals(value, classUnderTest.getValue());
	}

}
