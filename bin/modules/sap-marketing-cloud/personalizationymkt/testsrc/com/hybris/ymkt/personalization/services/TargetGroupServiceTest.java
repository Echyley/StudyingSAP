/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.personalization.services;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class TargetGroupServiceTest
{

	@Test
	public void partitionTest()
	{
		final List<String> a = Arrays.asList("a");
		final List<String> ab = Arrays.asList("a", "b");
		final List<String> b = Arrays.asList("b");

		Assert.assertEquals(Collections.emptyList(), TargetGroupService.partition(Collections.emptyList(), 1));

		Assert.assertEquals(Arrays.asList(a), TargetGroupService.partition(a, 1));
		Assert.assertEquals(Arrays.asList(ab), TargetGroupService.partition(ab, 2));
		Assert.assertEquals(Arrays.asList(a, b), TargetGroupService.partition(ab, 1));
	}



}
