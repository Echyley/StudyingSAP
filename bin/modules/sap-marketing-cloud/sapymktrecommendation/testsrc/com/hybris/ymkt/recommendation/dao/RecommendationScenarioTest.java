/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.dao;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

import com.hybris.ymkt.common.YMKTTestTool;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;


@UnitTest
public class RecommendationScenarioTest
{

	@Test
	public void test()
	{
		YMKTTestTool.assertEqualsHashCode( //
				new BasketObject("t1", "id1"), //
				new BasketObject("t1", "id1"), //
				new BasketObject("t1", "id2"), //
				new BasketObject("t2", "id1"), //

				new LeadingObject("t1", "id1"), //
				new LeadingObject("t1", "id1"), //
				new LeadingObject("t1", "id2"), //
				new LeadingObject("t2", "id1"));

	}

}
