/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.eval.StringEvaluatedRow;
import de.hybris.platform.acceleratorservices.velocity.eval.DefaultRowsEvaluator;
import de.hybris.platform.acceleratorservices.velocity.eval.RowsEvaluator;
import de.hybris.platform.acceleratorservices.velocity.eval.key.InvalidPropertyKeyFormatException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class RenderedRowFactoryTest
{
	private RowsEvaluator factory;

	private final Map<String, String> allProperties = new HashMap<>();

	private final Map<String, StringEvaluatedRow> resultMap = new LinkedHashMap<>();

	@Before
	public void prepare()
	{
		factory = new DefaultRowsEvaluator()
		{
			@Override
			protected Map<String, StringEvaluatedRow> getContainer()
			{
				return resultMap;
			}
		};

	}

	@Test
	public void testAddForEmptyContiner()
	{
		allProperties.put("type.key.attribute1", "value1");

		factory.evaluateRows(allProperties);

		assertEquals(1, resultMap.size());
		assertNotNull(resultMap.get("key"));

		final StringEvaluatedRow row = resultMap.get("key");
		assertEquals("key", row.getKey());

		assertEquals(1, row.getValues().size());
		assertEquals("value1", row.getValues().get("attribute1"));
	}

	@Test
	public void testAddForEmptyContinerWithDescriminatorNotApplying()
	{
		allProperties.put("type.key.attribute1", "value1");

		factory.evaluateRows("other", allProperties);

		assertEquals(0, resultMap.size());
	}

	@Test
	public void testAddForEmptyContinerWithDescriminatorApplying()
	{
		allProperties.put("type.key.attribute1", "value1");

		factory.evaluateRows("type", allProperties);

		assertEquals(1, resultMap.size());
		assertNotNull(resultMap.get("key"));

		final StringEvaluatedRow row = resultMap.get("key");
		assertEquals("key", row.getKey());

		assertEquals(1, row.getValues().size());
		assertEquals("value1", row.getValues().get("attribute1"));
	}


	@Test
	public void testAddForNonEmptyContiner()
	{
		allProperties.put("type.key.attribute1", "value1");
		allProperties.put("type.key.attribute2", "value2");

		allProperties.put("type.keyOther.attribute2", "value3");

		factory.evaluateRows(allProperties);

		assertEquals(2, resultMap.size());
		assertNotNull(resultMap.get("key"));

		final StringEvaluatedRow row1 = resultMap.get("key");
		assertEquals("key", row1.getKey());

		assertEquals(2, row1.getValues().size());
		assertEquals("value1", row1.getValues().get("attribute1"));
		assertEquals("value2", row1.getValues().get("attribute2"));

		final StringEvaluatedRow row2 = resultMap.get("keyOther");
		assertEquals("keyOther", row2.getKey());

		assertEquals(1, row2.getValues().size());
		assertEquals("value3", row2.getValues().get("attribute2"));
	}

	@Test
	public void testAddForNonEmptyContinerWithDescriminatorApplying()
	{
		allProperties.put("type.key.attribute1", "value1");
		allProperties.put("type.key.attribute2", "value2");

		allProperties.put("type.keyOther.attribute2", "value3");

		factory.evaluateRows("type", allProperties);

		assertEquals(2, resultMap.size());
		assertNotNull(resultMap.get("key"));

		final StringEvaluatedRow row1 = resultMap.get("key");
		assertEquals("key", row1.getKey());

		assertEquals(2, row1.getValues().size());
		assertEquals("value1", row1.getValues().get("attribute1"));
		assertEquals("value2", row1.getValues().get("attribute2"));

		final StringEvaluatedRow row2 = resultMap.get("keyOther");
		assertEquals("keyOther", row2.getKey());

		assertEquals(1, row2.getValues().size());
		assertEquals("value3", row2.getValues().get("attribute2"));
	}

	@Test
	public void testAddForNonEmptyContinerWithDescriminatorNotApplying()
	{
		allProperties.put("type.key.attribute1", "value1");
		allProperties.put("type.key.attribute2", "value2");

		allProperties.put("type.keyOther.attribute2", "value3");

		factory.evaluateRows("notExisting", allProperties);

		assertEquals(0, resultMap.size());
	}


	@Test
	public void testMergeExisting()
	{
		allProperties.put("type.key.attribute1", "value1");
		allProperties.put("type.key.attribute2", "value2");

		factory.evaluateRows(allProperties);

		assertEquals(1, resultMap.size());
		assertNotNull(resultMap.get("key"));

		final StringEvaluatedRow row = resultMap.get("key");
		assertEquals("key", row.getKey());

		assertEquals(2, row.getValues().size());
		assertEquals("value1", row.getValues().get("attribute1"));
		assertEquals("value2", row.getValues().get("attribute2"));
	}


	@Test
	public void testMergeExistingWithDesciminatorNotApplying()
	{
		allProperties.put("type1.key.attribute1", "value1");
		allProperties.put("type1.key.attribute2", "value2");

		allProperties.put("type2.key.attribute3", "value3");

		allProperties.put("type3.key.attribute4", "value4");

		factory.evaluateRows("notExisting", allProperties);

		assertEquals(0, resultMap.size());
	}

	@Test(expected = InvalidPropertyKeyFormatException.class)
	public void testInvalidKeyAComment()
	{
		allProperties.put("#", "value1");

		factory.evaluateRows("notExisting", allProperties);
	}

	@Test(expected = InvalidPropertyKeyFormatException.class)
	public void testInvalidKeyACommentWithAddon()
	{
		allProperties.put("#.something", "value1");

		factory.evaluateRows("notExisting", allProperties);
	}

	@Test
	public void testMergeExistingWithDesciminatorApplying()
	{
		allProperties.put("type1.key.attribute1", "value1");
		allProperties.put("type1.key.attribute2", "value2");

		allProperties.put("type2.key.attribute3", "value3");

		allProperties.put("type3.key.attribute4", "value4");

		factory.evaluateRows("type1", allProperties);

		assertEquals(1, resultMap.size());
		assertNotNull(resultMap.get("key"));

		final StringEvaluatedRow row = resultMap.get("key");
		assertEquals("key", row.getKey());

		assertEquals(2, row.getValues().size());
		assertEquals("value1", row.getValues().get("attribute1"));
		assertEquals("value2", row.getValues().get("attribute2"));

	}

	/**
	 * to do order of generated properties
	 */
	@Test
	public void testKeyOrder()
	{
		allProperties.put("type1.xXx.attribute3", "value3");

		allProperties.put("type1.Aaa.attribute1", "value1");
		allProperties.put("type1.Aaa.attribute2", "value2");

		allProperties.put("type1.bbb.attribute3", "value3");

		allProperties.put("type1.zzz.attribute4", "value4");

		allProperties.put("type1.CC134.attribute3", "value3");

		factory.evaluateRows("type1", allProperties);

		assertEquals(5, resultMap.size());
		assertNotNull(resultMap.get("Aaa"));
		assertNotNull(resultMap.get("bbb"));
		assertNotNull(resultMap.get("zzz"));

		final Iterator<String> keys = resultMap.keySet().iterator();
		final String first = keys.next();
		System.err.println(first);
		final String second = keys.next();
		System.err.println(second);
		final String third = keys.next();
		System.err.println(third);
		final String fourth = keys.next();
		System.err.println(fourth);
		final String fifth = keys.next();
		System.err.println(fifth);
	}
}
