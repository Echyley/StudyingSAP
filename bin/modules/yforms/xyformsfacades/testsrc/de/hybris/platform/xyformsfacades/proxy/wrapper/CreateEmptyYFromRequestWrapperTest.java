/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.proxy.wrapper;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;



@UnitTest
public class CreateEmptyYFromRequestWrapperTest
{
	@Mock
	private HttpServletRequest request;

	@Before
	public void setUp() throws MalformedURLException
	{
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void getMethodTest()
	{
		final HttpServletRequest requestWrapper = new CreateEmptyYFromRequestWrapper(request);
		assertEquals("POST", requestWrapper.getMethod());
	}
}
