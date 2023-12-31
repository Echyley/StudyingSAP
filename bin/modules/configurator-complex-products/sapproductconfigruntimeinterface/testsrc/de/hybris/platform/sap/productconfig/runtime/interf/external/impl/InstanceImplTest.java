/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class InstanceImplTest
{
	InstanceImpl classUnderTest = new InstanceImpl();

	@Test
	public void testId()
	{
		final String id = "1";
		classUnderTest.setId(id);
		assertEquals(id, classUnderTest.getId());
	}

	@Test
	public void testInstanceAttributes()
	{
		final String objectType = "MARA";
		classUnderTest.setObjectType(objectType);
		assertEquals(objectType, classUnderTest.getObjectType());
		final String classType = "300";
		classUnderTest.setClassType(classType);
		assertEquals(classType, classUnderTest.getClassType());
		final String objectKey = "KD990MIX";
		classUnderTest.setObjectKey(objectKey);
		assertEquals(objectKey, classUnderTest.getObjectKey());
		final String objText = "KD990MIX En";
		classUnderTest.setObjectText(objText);
		assertEquals(objText, classUnderTest.getObjectText());
		final String author = "8";
		classUnderTest.setAuthor(author);
		assertEquals(author, classUnderTest.getAuthor());
		final String quantity = "1.0";
		classUnderTest.setQuantity(quantity);
		assertEquals(quantity, classUnderTest.getQuantity());
		final String unit = "ST";
		classUnderTest.setQuantityUnit(unit);
		assertEquals(unit, classUnderTest.getQuantityUnit());
		final boolean bool = true;
		classUnderTest.setComplete(bool);
		assertTrue(classUnderTest.isComplete());
		classUnderTest.setConsistent(bool);
		assertTrue(classUnderTest.isConsistent());


	}

}
