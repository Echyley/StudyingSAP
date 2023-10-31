/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.comparator;

import de.hybris.platform.classification.features.Feature;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class FeatureComparatorTest
{

	private FeatureComparator featureComparator;

	@Before
	public void setUp()
	{
		featureComparator = new FeatureComparator();
	}

	@Test
	public void testEqualNames()
	{
		final Feature leftObject = Mockito.mock(Feature.class);
		final Feature rightObject = Mockito.mock(Feature.class);

		Mockito.when(leftObject.getName()).thenReturn("n1");
		Mockito.when(rightObject.getName()).thenReturn("n1");

		Assert.assertEquals(0, getFeatureComparator().compare(leftObject, rightObject));
	}

	@Test
	public void testNotEqualNames()
	{
		final Feature leftObject = Mockito.mock(Feature.class);
		final Feature rightObject = Mockito.mock(Feature.class);

		Mockito.when(leftObject.getName()).thenReturn("n1");
		Mockito.when(rightObject.getName()).thenReturn("n2");

		Assert.assertNotEquals(0, getFeatureComparator().compare(leftObject, rightObject));
	}

	public FeatureComparator getFeatureComparator()
	{
		return featureComparator;
	}
}
