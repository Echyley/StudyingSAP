/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval.key;

import de.hybris.bootstrap.annotations.UnitTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SplitKeyEvaluatorTest
{
	private SplitKeyEvaluator evaluator = null;

	@Before
	public void prepare()
	{
		evaluator = new SplitKeyEvaluator();
		evaluator.setKeySeparator("\\*");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullKey()
	{
		evaluator.eval(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBlankKey()
	{
		evaluator.eval("   \t ");
	}


	@Test
	public void testNoSeparatorPresent()
	{
		assertEqualArrays(new String[]
		{ "a.b.c" }, evaluator.eval("a.b.c"));
	}

	@Test
	public void testSeparatorPresent()
	{
		assertEqualArrays(new String[]
		{ "a", "b.c" }, evaluator.eval("a*b.c"));
	}

	@Test
	public void testSeparatorPresentWithSpace()
	{
		assertEqualArrays(new String[]
		{ "a", "b", "c with d" }, evaluator.eval("a*b*c with d"));
	}

	private void assertEqualArrays(final String[] arrayExp, final String[] result)
	{
		Assert.assertEquals(arrayExp.length, result.length);
		for (int i = 0; i < arrayExp.length; i++)
		{
			Assert.assertEquals(arrayExp[i], result[i]);
		}
	}
}
