/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.proxy.impl;

import de.hybris.platform.xyformsservices.proxy.ProxyService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit test suite for {@link DefaultProxyFacade}
 */
public class DefaultProxyFacadeTest
{
	@InjectMocks
	private DefaultProxyFacade proxyFacade;
	@Mock
	private ProxyService proxyService;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	private Map<String, String> extraHeaders;

	@Before
	public void setUp() throws MalformedURLException
	{
		proxyFacade = new DefaultProxyFacade();
		MockitoAnnotations.initMocks(this);

		extraHeaders = new HashMap<>();
		extraHeaders.put("header", "value");
		given(request.getRequestURL()).willReturn(new StringBuffer("https://test.url"));
		given(proxyService.rewriteURL("https://test.url", false)).willReturn("https://new.url");
		when(proxyService.getExtraHeaders()).thenAnswer(new Answer<Map<String, String>>()
		{
			@Override
			public Map<String, String> answer(final InvocationOnMock invocation) throws Throwable
			{
				final Map<String, String> clonedExtraHeaders = new HashMap<>();
				clonedExtraHeaders.putAll(extraHeaders);
				return clonedExtraHeaders;
			}
		});
	}

	@Test
	public void shouldEncodeNamespace() throws Exception
	{
		given(proxyService.extractNamespace(request)).willReturn("uuid_nam3spac3<some#tag>");

		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(eq(request), eq(response), eq("uuid_nam3spac3%3csome%23tag%3e"),
				eq("https://new.url"), eq(false), eq(extraHeaders));
	}

	@Test
	public void shouldNotAffectValidNamespace() throws Exception
	{
		given(proxyService.extractNamespace(request)).willReturn("uuid_nam3spac3");

		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(eq(request), eq(response), eq("uuid_nam3spac3"),
				eq("https://new.url"), eq(false), eq(extraHeaders));
	}

	@Test
	public void shouldAllowNullNamespace() throws Exception
	{
		given(proxyService.extractNamespace(request)).willReturn(null);

		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(eq(request), eq(response), eq(null),
				eq("https://new.url"), eq(false), eq(extraHeaders));
	}

	@Test
	public void shouldProxyWithOrbeonClientHeader() throws Exception
	{
		when(request.getParameter("Orbeon-Client")).thenReturn("-");
		proxyFacade.proxy(request, response);

		extraHeaders.put("Orbeon-Client", "-");
		verify(proxyService).proxy(request, response, null, "https://new.url", false, extraHeaders);
	}

	@Test
	public void shouldProxyWithoutOrbeonClientHeaderWhenEmpty() throws Exception
	{
		when(request.getParameter("Orbeon-Client")).thenReturn("");
		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(request, response, null, "https://new.url", false, extraHeaders);
	}

	@Test
	public void shouldProxyWithoutOrbeonClientHeaderWhenNull() throws Exception
	{
		when(request.getParameter("Orbeon-Client")).thenReturn(null);
		proxyFacade.proxy(request, response);

		verify(proxyService).proxy(request, response, null, "https://new.url", false, extraHeaders);
	}

	@Test
	public void shouldHandleOrbeonClientHeaderRequestIndependently() throws Exception
	{
		when(request.getParameter("Orbeon-Client")).thenReturn(null);
		proxyFacade.proxy(request, response);
		verify(proxyService).proxy(request, response, null, "https://new.url", false, extraHeaders);

		when(request.getParameter("Orbeon-Client")).thenReturn("-");
		proxyFacade.proxy(request, response);
		extraHeaders.put("Orbeon-Client", "-");
		verify(proxyService).proxy(request, response, null, "https://new.url", false, extraHeaders);

		when(request.getParameter("Orbeon-Client")).thenReturn("");
		proxyFacade.proxy(request, response);
		extraHeaders.remove("Orbeon-Client");
		verify(proxyService).proxy(request, response, null, "https://new.url", false, extraHeaders);
	}
}
