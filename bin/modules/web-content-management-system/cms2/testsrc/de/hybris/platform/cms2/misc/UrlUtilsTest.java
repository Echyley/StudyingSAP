/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.misc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class UrlUtilsTest
{
	private static final String MOCK_URL = "https://mock.absolute.url";

	@Mock
	private HttpServletRequest request;
	@Mock
	private CMSSiteModel cmsSiteModel;

	@Before
	public void setUp()
	{
		when(request.getScheme()).thenReturn("https");
		when(request.getServerName()).thenReturn("mock.server.name");
		when(request.getServerPort()).thenReturn(8080);
	}

	@Test
	public void testExtractHostInformationFromRequestWithPreviewUrl()
	{
		when(cmsSiteModel.getPreviewURL()).thenReturn("/some.more.info");

		final String result = UrlUtils.extractHostInformationFromRequest(request, cmsSiteModel);

		assertEquals("https://mock.server.name:8080/some.more.info", result);
	}

	@Test
	public void testExtractHostInformationFromRequestWithPreviewUrl_AbsolutePath()
	{
		when(cmsSiteModel.getPreviewURL()).thenReturn(MOCK_URL);

		final String result = UrlUtils.extractHostInformationFromRequest(request, cmsSiteModel);

		assertEquals(MOCK_URL, result);
	}

	@Test
	public void testExtractHostInformationFromRequestWithUrl()
	{
		final String result = UrlUtils.extractHostInformationFromRequest(request, "/some.more.info");

		assertEquals("https://mock.server.name:8080/some.more.info", result);
	}

	@Test
	public void testExtractHostInformationFromRequestWithUrl_AbsolutePateh()
	{
		final String result = UrlUtils.extractHostInformationFromRequest(request, MOCK_URL);

		assertEquals(MOCK_URL, result);
	}

	@Test
	public void testExtractHostInformationFromRequest()
	{
		final String result = UrlUtils.extractHostInformationFromRequest(request);

		assertEquals("https://mock.server.name:8080", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailExtractHostInformation_SchemeValueTooLong()
	{
		when(request.getScheme()).thenReturn("mock.veryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"
				+ "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy.looooooooooooooooooooooooooooooooooooooooo"
				+ "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong.scheme");

		UrlUtils.extractHostInformationFromRequest(request);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFailExtractHostInformation_ServerNameValueTooLong()
	{
		when(request.getServerName()).thenReturn("mock.veryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"
				+ "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy.looooooooooooooooooooooooooooooooooooo"
				+ "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong.server.name");

		UrlUtils.extractHostInformationFromRequest(request);
	}
}
