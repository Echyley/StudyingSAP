/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.filters;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fest.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.CookieGenerator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaloginaddon.util.GigyaCookieValueGenerator;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionLead;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaLoginFilterTest {

	@InjectMocks
	private final GigyaLoginFilter gigyaLoginFilter = new GigyaLoginFilter() {

		@Override
		protected CookieBasedLoginToken getCookieBasedLoginToken(Cookie siteCookie) {
			return cookieBasedLoginToken;
		}

		@Override
		protected String generateCookieValue(GigyaSessionConfigModel sessionConfig, GigyaConfigModel gigyaConfig,
				Cookie gltCookie) {
			return "value";
		}

	};

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private UserService userService;

	@Mock
	private RedirectStrategy redirectStrategy;

	@Mock
	private AutoLoginStrategy gigyaAutoLoginStrategy;

	@Mock
	private CookieGenerator cookieGenerator;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GigyaSessionConfigModel gigyaSessionConfig;

	@Mock
	private CustomerModel customer;

	@Mock
	private Cookie gltCookie;

	@Mock
	private Cookie siteCookie;

	@Mock
	private CookieBasedLoginToken cookieBasedLoginToken;

	@Mock
	private User user;

	@Mock
	private UserModel userModel;
	
	@Before
	public void setUp() {
		BDDMockito.given(request.getDispatcherType()).willReturn(DispatcherType.REQUEST);
	}

	@Test
	public void testWhenBaseSiteDoesntExist() throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(null);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(baseSite);
		Mockito.verifyZeroInteractions(userService);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaConfigDoesntExist() throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(null);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(gigyaConfig);
		Mockito.verifyZeroInteractions(userService);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaSessionConfigDoesntExist() throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(null);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(gigyaSessionConfig);
		Mockito.verifyZeroInteractions(userService);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCommerce() throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.COMMERCE);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(userService);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCDCAndSessionTypeIsSlidingWithoutUserAndWithoutSiteCookie()
			throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.lenient().when(gigyaSessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(null);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCDCAndSessionTypeIsSlidingWithAnonymousUserAndWithoutSiteCookie()
			throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.lenient().when(gigyaSessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.lenient().when(userService.isAnonymousUser(customer)).thenReturn(Boolean.TRUE);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCDCAndSessionTypeIsSlidingWithAnonymousUserAndWithSiteAndGltCookies()
			throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.lenient().when(gigyaSessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.lenient().when(userService.isAnonymousUser(customer)).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(request.getCookies()).thenReturn(Arrays.array(gltCookie, siteCookie));
		Mockito.lenient().when(gltCookie.getName()).thenReturn("glt_abc");
		Mockito.lenient().when(siteCookie.getName()).thenReturn("electronics-LoginToken");
		Mockito.lenient().when(gigyaConfig.getGigyaApiKey()).thenReturn("abc");
		Mockito.lenient().when(baseSite.getUid()).thenReturn("electronics");
		Mockito.lenient().when(cookieBasedLoginToken.getUser()).thenReturn(user);
		Mockito.lenient().when(userService.getUserForUID(Mockito.anyString())).thenReturn(userModel);

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verify(redirectStrategy).sendRedirect(request, response, "/logout");
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
		Mockito.verifyZeroInteractions(cookieGenerator);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCDCAndSessionTypeIsSlidingWithUser() throws ServletException, IOException {
		Cookie gltExpCookie = Mockito.mock(Cookie.class);
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.lenient().when(gigyaSessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.lenient().when(userService.isAnonymousUser(customer)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(request.getCookies()).thenReturn(Arrays.array(gltCookie, gltExpCookie));
		Mockito.lenient().when(gltCookie.getName()).thenReturn("glt_abc");
		Mockito.lenient().when(gltExpCookie.getName()).thenReturn("gltexp_abc");
		Mockito.lenient().when(gigyaConfig.getGigyaApiKey()).thenReturn("abc");
		Mockito.lenient().when(baseSite.getUid()).thenReturn("electronics");

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verify(cookieGenerator).addCookie(Mockito.any(), Mockito.any());

		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCDCAndSessionTypeIsNotSlidingAndGltCookieExists()
			throws ServletException, IOException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.lenient().when(gigyaSessionConfig.getSessionType()).thenReturn(GigyaSessionType.FIXED);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.lenient().when(userService.isAnonymousUser(customer)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(request.getCookies()).thenReturn(Arrays.array(gltCookie, siteCookie));
		Mockito.lenient().when(gltCookie.getName()).thenReturn("glt_abc");
		Mockito.lenient().when(siteCookie.getName()).thenReturn("electronics-LoginToken");
		Mockito.lenient().when(gigyaConfig.getGigyaApiKey()).thenReturn("abc");
		Mockito.lenient().when(baseSite.getUid()).thenReturn("electronics");

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(cookieGenerator);
		Mockito.verifyZeroInteractions(redirectStrategy);
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
	}

	@Test
	public void testWhenGigyaSessionLeadIsCDCAndSessionTypeIsNotSlidingAndGltCookieDoesntExists()
			throws IOException, ServletException {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getGigyaSessionConfig()).thenReturn(gigyaSessionConfig);
		Mockito.lenient().when(gigyaSessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.lenient().when(gigyaSessionConfig.getSessionType()).thenReturn(GigyaSessionType.FIXED);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.lenient().when(userService.isAnonymousUser(customer)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(request.getCookies()).thenReturn(Arrays.array(siteCookie));
		Mockito.lenient().when(gltCookie.getName()).thenReturn("glt_abc");
		Mockito.lenient().when(siteCookie.getName()).thenReturn("electronics-LoginToken");
		Mockito.lenient().when(gigyaConfig.getGigyaApiKey()).thenReturn("abc");
		Mockito.lenient().when(baseSite.getUid()).thenReturn("electronics");

		gigyaLoginFilter.doFilter(request, response, filterChain);

		Mockito.verify(filterChain).doFilter(request, response);
		Mockito.verifyZeroInteractions(cookieGenerator);
		Mockito.verify(redirectStrategy).sendRedirect(request, response, "/logout");
		Mockito.verifyZeroInteractions(gigyaAutoLoginStrategy);
	}

}
