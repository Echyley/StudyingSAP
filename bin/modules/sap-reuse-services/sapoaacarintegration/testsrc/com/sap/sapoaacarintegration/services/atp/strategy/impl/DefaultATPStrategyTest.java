/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.atp.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.impl.DefaultATPAggregationStrategy;


/**
 */
@UnitTest
public class DefaultATPStrategyTest extends DefaultATPAggregationStrategy
{

	@Test
	public void sumAggregatedATPAvailabilityTest()
	{
		final List<ATPAvailability> atpAvailabilityList = new ArrayList<>();
		final ATPAvailability line1 = new ATPAvailability(new Double(5), null);
		final ATPAvailability line2 = new ATPAvailability(new Double(8), null);
		final ATPAvailability line3 = new ATPAvailability(new Double(9), null);
		final ATPAvailability line4 = new ATPAvailability(new Double(1), null);
		final ATPAvailability line5 = new ATPAvailability(new Double(0), null);

		atpAvailabilityList.add(line1);
		atpAvailabilityList.add(line2);
		atpAvailabilityList.add(line3);
		atpAvailabilityList.add(line4);

		Assert.assertEquals(23, super.aggregateAvailability(null, null, null, atpAvailabilityList).longValue());

		atpAvailabilityList.clear();

		atpAvailabilityList.add(line1);
		atpAvailabilityList.add(line2);

		Assert.assertEquals(13, this.aggregateAvailability(null, null, null, atpAvailabilityList).longValue());

		atpAvailabilityList.clear();

		atpAvailabilityList.add(line3);
		atpAvailabilityList.add(line4);

		Assert.assertEquals(10, this.aggregateAvailability(null, null, null, atpAvailabilityList).longValue());

		atpAvailabilityList.clear();

		atpAvailabilityList.add(line4);
		atpAvailabilityList.add(line5);

		Assert.assertEquals(1, this.aggregateAvailability(null, null, null, atpAvailabilityList).longValue());

		atpAvailabilityList.clear();

		atpAvailabilityList.add(line5);

		Assert.assertEquals(0, this.aggregateAvailability(null, null, null, atpAvailabilityList).longValue());
	}
}
