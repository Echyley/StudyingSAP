/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.search.impl.comparators;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import org.junit.Test;


@UnitTest
public class FacetNameComparatorTest
{
	private final FacetNameComparator facetNameComparator = new FacetNameComparator();

	@Test
	public void bothFacetValuesNull() throws Exception
	{
		final FacetValue value1 = null;
		final FacetValue value2 = null;

		final int result = facetNameComparator.compare(value1, value2);
		assertTrue("expected to be equal", result == 0);
	}

	@Test
	public void firstFacetValueNull() throws Exception
	{
		final FacetValue value1 = null;
		final FacetValue value2 = new FacetValue("name2", 1, true);

		final int result = facetNameComparator.compare(value1, value2);
		assertTrue("expected to be greater than", result == 1);
	}

	@Test
	public void secondFacetValueNull() throws Exception
	{
		final FacetValue value1 = new FacetValue("name1", 1, true);
		final FacetValue value2 = null;

		final int result = facetNameComparator.compare(value1, value2);
		assertTrue("expected to be less than", result == -1);
	}

	@Test
	public void firstFacetNameLessThanSecondFacetName() throws Exception
	{
		final FacetValue value1 = new FacetValue("name1", 1, true);
		final FacetValue value2 = new FacetValue("name2", 1, true);

		final int result = facetNameComparator.compare(value1, value2);
		assertTrue("expected to be less tha", result == -1);
	}

}
