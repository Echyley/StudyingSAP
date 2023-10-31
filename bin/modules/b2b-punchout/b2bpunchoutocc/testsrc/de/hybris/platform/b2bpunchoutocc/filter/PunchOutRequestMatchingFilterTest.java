/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.filter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutocc.exception.BadPunchOutRequestException;
import de.hybris.platform.b2bpunchoutocc.exception.PunchOutCartMissingException;
import de.hybris.platform.core.model.order.CartModel;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutRequestMatchingFilterTest
{
	@InjectMocks
	@Spy
	private PunchOutRequestMatchingFilter punchOutRequestMatchingFilter;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private PunchOutSessionService punchoutSessionService;
	@Mock
	private FilterChain chain;

	private static final String PUNCHOUT_OAUTH_ROLE = "ROLE_PUNCHOUTOAUTH2";
	private static final HashMap<String, Object> allowedUrlMap = new HashMap<>();

	@Before
	public void setUp()
	{
		allowedUrlMap.put("methods", new String[] {"GET"});
		allowedUrlMap.put("isolationLevel", "DATA");
		allowedUrlMap.put("url", "/{baseSiteId}/users/{userId}/carts/{cartId}/entries*/**");
	}

	@Test
	public void testTokenExchangeFromOauth2Success() throws Exception{
		final UsernamePasswordAuthenticationToken auth = Mockito.mock(UsernamePasswordAuthenticationToken.class);
		SecurityContextHolder.getContext().setAuthentication(auth);
		punchOutRequestMatchingFilter.doFilterInternal(request, response, chain);
		verify(punchOutRequestMatchingFilter, Mockito.times(1)).containsRole(any(), anyString());
		verify(chain, Mockito.times(1)).doFilter(request, response);
	}

	@Test
	public void testTokenExchangeFromSidReturnTrue() {
		final UsernamePasswordAuthenticationToken auth = Mockito.mock(UsernamePasswordAuthenticationToken.class);
		final Collection<GrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority(PUNCHOUT_OAUTH_ROLE));
		when(auth.getAuthorities()).thenReturn(authorityList);
		Assert.assertTrue(punchOutRequestMatchingFilter.containsRole(auth, PUNCHOUT_OAUTH_ROLE));
	}

	@Test
	public void testNotAllowedUrlReturnFalse()
	{
		final String requestUrl = "/powertools/users/address/123";
		final String requestMethod = "GET";
		final String sid = "validSid";
		Assert.assertFalse(punchOutRequestMatchingFilter.matchAllowedList(requestUrl, requestMethod, allowedUrlMap, sid));
	}

	@Test
	public void testSidNotProvidedThrowException()
	{
		final String sid = null;
		final String requestUrl = "/powertools/users/current/carts/123/entries";
		final String requestMethod = "GET";
		assertThatThrownBy( () -> punchOutRequestMatchingFilter.matchAllowedList(requestUrl, requestMethod, allowedUrlMap, sid))
				.isInstanceOf(BadPunchOutRequestException.class)
				.hasMessageContaining("no sid found");
	}

	@Test
	public void testSidInvalidThrowException()
	{
		final String requestUrl = "/powertools/users/current/carts/123/entries";
		final String requestMethod = "GET";
		final String sid = "invalid";
		assertThatThrownBy( () -> punchOutRequestMatchingFilter.matchAllowedList(requestUrl, requestMethod, allowedUrlMap, sid))
				.isInstanceOf(PunchOutCartMissingException.class)
				.hasMessageContaining("There was an internal error due to empty cart. Please try again.");
	}

	@Test
	public void testCartIdNotMatchReturnFalse()
	{
		final String requestUrl = "/powertools/users/current/carts/123/entries";
		final String requestMethod = "GET";
		final String sid = "invalid";
		final StoredPunchOutSessionModel storedPunchOutSessionModel = Mockito.mock(StoredPunchOutSessionModel.class);
		when(punchoutSessionService.loadStoredPunchOutSessionModel(sid)).thenReturn(storedPunchOutSessionModel);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		when(storedPunchOutSessionModel.getCart()).thenReturn(cartModel);
		Assert.assertFalse(punchOutRequestMatchingFilter.matchAllowedList(requestUrl, requestMethod, allowedUrlMap, sid));
	}

	@Test
	public void testCartIdMatchReturnTrue()
	{
		final String requestUrl = "/powertools/users/current/carts/123/entries";
		final String requestMethod = "GET";
		final String sid = "invalid";
		final StoredPunchOutSessionModel storedPunchOutSessionModel = Mockito.mock(StoredPunchOutSessionModel.class);
		when(punchoutSessionService.loadStoredPunchOutSessionModel(sid)).thenReturn(storedPunchOutSessionModel);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		when(cartModel.getCode()).thenReturn("123");
		when(storedPunchOutSessionModel.getCart()).thenReturn(cartModel);
		Assert.assertTrue(punchOutRequestMatchingFilter.matchAllowedList(requestUrl, requestMethod, allowedUrlMap, sid));
	}
}
