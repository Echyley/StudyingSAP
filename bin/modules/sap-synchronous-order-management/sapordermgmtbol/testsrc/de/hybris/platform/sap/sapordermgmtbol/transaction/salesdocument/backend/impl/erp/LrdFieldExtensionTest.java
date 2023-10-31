/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension.FieldType;

import junit.framework.TestCase;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class LrdFieldExtensionTest extends TestCase
{

	private LrdFieldExtension classUndertest;

	@Override
	public void setUp()
	{
		classUndertest = new LrdFieldExtension(FieldType.HeadComR);
	}

	@Test
	public void testGetFieldType()
	{
		classUndertest = new LrdFieldExtension(FieldType.HeadComR);
		assertEquals(FieldType.HeadComR, classUndertest.getFieldType());

		classUndertest = new LrdFieldExtension(FieldType.HeadComV);
		assertEquals(FieldType.HeadComV, classUndertest.getFieldType());

		classUndertest = new LrdFieldExtension(FieldType.ItemComR);
		assertEquals(FieldType.ItemComR, classUndertest.getFieldType());

		classUndertest = new LrdFieldExtension(FieldType.ItemComV);
		assertEquals(FieldType.ItemComV, classUndertest.getFieldType());

	}

	@Test
	public void testGetFieldnames_empty()
	{
		assertEquals(0, classUndertest.getFieldnames().size());
	}

	@Test
	public void testAddField_getFieldnames()
	{
		classUndertest.addField("123");
		classUndertest.addField("abc");
		assertEquals(2, classUndertest.getFieldnames().size());
		assertEquals("123", classUndertest.getFieldnames().get(0));
		assertEquals("abc", classUndertest.getFieldnames().get(1));
	}

	@Test
	public void testFieldType_getObjectType()
	{
		assertEquals(LrdFieldExtension.objectHead, FieldType.HeadComR.getObjectType());
		assertEquals(LrdFieldExtension.objectHead, FieldType.HeadComV.getObjectType());
		assertEquals(LrdFieldExtension.objectItem, FieldType.ItemComR.getObjectType());
		assertEquals(LrdFieldExtension.objectItem, FieldType.ItemComV.getObjectType());

	}

	@Test
	public void testFieldType_enumMethodss()
	{
		assertEquals(4, FieldType.values().length);
		assertEquals(FieldType.HeadComR, FieldType.valueOf("HeadComR"));
		assertEquals(FieldType.HeadComV, FieldType.valueOf("HeadComV"));
		assertEquals(FieldType.ItemComR, FieldType.valueOf("ItemComR"));
		assertEquals(FieldType.ItemComV, FieldType.valueOf("ItemComV"));
		try
		{
			FieldType.valueOf("blah");
			fail("'Bla' is not a valid enum constant");
		}
		catch (IllegalArgumentException ex)
		{
			assertNotNull(ex.getMessage());
		}
	}
}
