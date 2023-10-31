/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.clickstream.aspects;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ClickStreamAspectTest
{

	@Spy
	private ClickStreamAspect clickStreamAspect;

	@Test
	public void validateSuccessTest()
	{
		clickStreamAspect.clickStreamAspect();
		verify(clickStreamAspect, times(1)).clickStreamAspect();
	}
}
