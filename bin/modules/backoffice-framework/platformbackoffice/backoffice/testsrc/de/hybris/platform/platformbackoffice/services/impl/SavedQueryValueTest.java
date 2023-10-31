/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.impl;

import de.hybris.platform.platformbackoffice.services.converters.SavedQueryValue;

import org.junit.Assert;
import org.junit.Test;


public class SavedQueryValueTest
{
	final SavedQueryValue first = new SavedQueryValue("en", "value");
	final SavedQueryValue second = new SavedQueryValue("en", "value");
	final SavedQueryValue third = new SavedQueryValue("en", "value");

	@Test
	public void equalsWithNullShouldReturnFalse()
	{
		Assert.assertFalse(first.equals(null));
	}

	@Test
	public void equalsShouldBeTransitive()
	{
		final boolean firstEqualsSecond = first.equals(second);
		final boolean secondEqualsThird = second.equals(third);
		final boolean thirdEqualsFirst = third.equals(first);
		Assert.assertTrue(firstEqualsSecond == secondEqualsThird == thirdEqualsFirst);
	}

	@Test
	public void equalsShouldBeSymmetric()
	{
		final boolean firstEqualsSameValue = first.equals(second);
		final boolean sameValueEqualsFrom = second.equals(first);
		Assert.assertEquals(Boolean.valueOf(firstEqualsSameValue), Boolean.valueOf(sameValueEqualsFrom));
	}
}
