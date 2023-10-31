/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Date;

import org.junit.Test;


@UnitTest
public class KBKeyImplTest
{

	@Test
	public void testEquals()
	{
		final Date aSecondAgo = new Date(new Date().getTime() - 1000);
		KBKeyImpl key1 = new KBKeyImpl("A", "B", "C", "D", aSecondAgo);

		assertNotNull(key1);
		assertNotEquals(new KBKeyImplSubClass("A", "B", "C", "D", aSecondAgo), key1);

		KBKeyImpl key2 = new KBKeyImpl("A", "B", "C", "D", aSecondAgo);
		assertEquals(key1, key2);

		key2 = new KBKeyImpl("A", "B", "C", "D", null);
		assertNotEquals(key1, key2);

		key2 = new KBKeyImpl("A", "B", "C", null, aSecondAgo);
		assertNotEquals(key1, key2);

		key2 = new KBKeyImpl("A", "B", null, "D", aSecondAgo);
		assertNotEquals(key1, key2);

		key2 = new KBKeyImpl("A", null, "C", "D", aSecondAgo);
		assertNotEquals(key1, key2);

		key2 = new KBKeyImpl(null, "B", "C", "D", aSecondAgo);
		assertNotEquals(key1, key2);

		key1 = new KBKeyImpl("A", "B", "C", null, aSecondAgo);
		key2 = new KBKeyImpl("A", "B", "C", null, aSecondAgo);
		assertEquals(key1, key2);

		key1 = new KBKeyImpl("A", "B", null, "D", aSecondAgo);
		key2 = new KBKeyImpl("A", "B", null, "D", aSecondAgo);
		assertEquals(key1, key2);

		key1 = new KBKeyImpl("A", null, "C", "D", aSecondAgo);
		key2 = new KBKeyImpl("A", null, "C", "D", aSecondAgo);
		assertEquals(key1, key2);

		key1 = new KBKeyImpl(null, "B", "C", "D", aSecondAgo);
		key2 = new KBKeyImpl(null, "B", "C", "D", aSecondAgo);
		assertEquals(key1, key2);

		key1 = new KBKeyImpl("A", "B", "C", "D", null);
		key2 = new KBKeyImpl("A", "B", "C", "D", key1.getDate());
		assertEquals(key1, key2);
	}

	@Test
	public void testHashCodeNull()
	{
		final KBKeyImpl key = new KBKeyImpl(null, null, null, null, new Date(0l));
		assertEquals(28629151, key.hashCode());
	}

	@Test
	public void testHashCode()
	{
		final KBKeyImpl key = new KBKeyImpl("A", "B", "C", "D", new Date(0l));
		assertEquals(30661827, key.hashCode());
	}


	@Test
	public void testToString()
	{
		final Date date = new Date(0l);
		final KBKeyImpl key = new KBKeyImpl(null, null, null, null, date);
		final String dateString = date.toString();
		assertEquals("KBKeyImpl [productCode=null, kbName=null, kbLogsys=null, kbVersion=null, date=" + dateString + "]",
				key.toString());
	}

	private static class KBKeyImplSubClass extends KBKeyImpl
	{
		public KBKeyImplSubClass(final String productCode, final String kbName, final String kbLogsys, final String kbVersion,
				final Date date)
		{
			super(productCode, kbName, kbLogsys, kbVersion, date);
		}
	}
}
