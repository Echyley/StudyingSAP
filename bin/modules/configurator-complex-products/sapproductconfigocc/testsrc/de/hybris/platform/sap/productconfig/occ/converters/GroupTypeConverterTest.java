/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.converters;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.GroupType;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class GroupTypeConverterTest
{

	private GroupTypeConverter classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new GroupTypeConverter();
	}

	@Test
	public void testConvertTo()
	{
		assertEquals(GroupType.CONFLICT, classUnderTest.convertFrom(GroupType.CONFLICT.toString(), null, null));
	}

	@Test
	public void testConvertFrom()
	{
		assertEquals(GroupType.CONFLICT.toString(), classUnderTest.convertTo(GroupType.CONFLICT, null, null));
	}
}
