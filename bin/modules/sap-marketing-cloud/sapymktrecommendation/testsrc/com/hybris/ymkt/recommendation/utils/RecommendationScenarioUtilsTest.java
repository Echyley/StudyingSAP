/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.utils;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class RecommendationScenarioUtilsTest
{

	@Test
	public void test()
	{
		Assert.assertEquals(Arrays.asList("a"), RecommendationScenarioUtils.convertBufferToList("a"));
		Assert.assertEquals(Arrays.asList("a", "b"), RecommendationScenarioUtils.convertBufferToList("a,b"));
	}
}
