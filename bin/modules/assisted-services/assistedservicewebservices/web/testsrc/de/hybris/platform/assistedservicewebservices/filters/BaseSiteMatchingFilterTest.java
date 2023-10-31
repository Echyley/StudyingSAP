/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test suite for {@link BaseSiteMatchingFilter}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BaseSiteMatchingFilterTest
{
	private static final String CURRENT_URL = "/customerlists";
	private static final String INCLUDED_URL1 = ".*/users/.*/customer360.*";

	private static final String[] SITE_IN_PARA_URLS = { "/customers", "/customers/search", "/customers/autocomplete", "/bind-cart",
			"/customerlists", "/customerlists/.*" ,".*/users/.*/customer360"};

	static final String UNKNOWN_BASE_SITE = "unknownBaseSiteId";
	static final String BASE_SITE_PARAM = "baseSite";
	static final String BASE_SITE_PATH = "/baseSite/users/user1@test.net/customer360";
	static final String BASE_SITE_PATH_UPPER_CASE = "/baseSite/UsErS/user1@test.net/customer360";
	static final String INVALID_BASE_SITE_PATH = "/baseSite/aaas/user1@test.net/customer360";
	static final String REGEX = "/([^/]+)(/users/)([^/]+)(/customer360)";

	private BaseSiteMatchingFilter baseSiteMatchingFilter;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private FilterChain filterChain;
	@Mock
	private BaseSiteModel baseSiteModel;
	@Mock
	private BaseSiteModel currentBaseSiteModel;

	@Before
	public void setUp()
	{
		baseSiteMatchingFilter = new BaseSiteMatchingFilter();
		baseSiteMatchingFilter.setBaseSiteService(baseSiteService);
		baseSiteMatchingFilter.setIncludedURLs(Arrays.asList(SITE_IN_PARA_URLS));
		baseSiteMatchingFilter.setRegexp(REGEX);
		Mockito.lenient().when(httpServletRequest.getPathInfo()).thenReturn(CURRENT_URL);
		Mockito.lenient().when(httpServletRequest.getServletPath()).thenReturn(CURRENT_URL);
		given(httpServletRequest.getDispatcherType()).willReturn(DispatcherType.REQUEST);
	}

	@Test
	public void testNullPathInfo() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(null);

		assertThatThrownBy(
				() -> baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain)).withFailMessage(
				"Request must have a baseSite").isInstanceOf(RequestParameterException.class);
		verify(baseSiteService, never()).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(baseSiteService, never()).setCurrentBaseSite(anyString(), anyBoolean());
	}

	@Test
	public void testUnknownBaseSite() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(UNKNOWN_BASE_SITE);

		given(baseSiteService.getBaseSiteForUID(UNKNOWN_BASE_SITE)).willReturn(null);

		assertThatThrownBy(
				() -> baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain)).withFailMessage(
				"The requested BaseSite: " + UNKNOWN_BASE_SITE + " doesn't exist").isInstanceOf(RequestParameterException.class);
	}

	@Test
	public void testKnownBaseSite() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(BASE_SITE_PARAM);
		given(baseSiteService.getBaseSiteForUID(BASE_SITE_PARAM)).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(currentBaseSiteModel);

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, times(1)).setCurrentBaseSite(baseSiteModel, true);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testBaseSiteThatEqualsCurrentSite() throws ServletException, IOException
	{
		given(httpServletRequest.getParameter(BASE_SITE_PARAM)).willReturn(BASE_SITE_PARAM);
		given(baseSiteService.getBaseSiteForUID(BASE_SITE_PARAM)).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, never()).setCurrentBaseSite(baseSiteModel, true);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testBaseSitePathThatEqualsCurrentSite() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn(BASE_SITE_PATH);
		given(baseSiteService.getBaseSiteForUID(anyString())).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(new BaseSiteModel());

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, times(1)).setCurrentBaseSite(baseSiteModel, true);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testBaseSitePathThatEqualsCurrentSiteWithUpperCase() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn(BASE_SITE_PATH_UPPER_CASE);
		given(baseSiteService.getBaseSiteForUID(anyString())).willReturn(baseSiteModel);
		given(baseSiteService.getCurrentBaseSite()).willReturn(new BaseSiteModel());

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, times(1)).setCurrentBaseSite(baseSiteModel, true);
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testBaseSitePathThatNotContainUsers() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn(INVALID_BASE_SITE_PATH);
		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
		verify(baseSiteService, never()).setCurrentBaseSite(baseSiteModel, true);
	}

	@Test
	public void testFilterNotInvokedForExcludedUrl() throws ServletException, IOException
	{
		given(httpServletRequest.getServletPath()).willReturn("/webjars/springfox-swagger-ui/images/throbber.gif");

		baseSiteMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(baseSiteService, never()).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(baseSiteService, never()).setCurrentBaseSite(anyString(), anyBoolean());
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

}
