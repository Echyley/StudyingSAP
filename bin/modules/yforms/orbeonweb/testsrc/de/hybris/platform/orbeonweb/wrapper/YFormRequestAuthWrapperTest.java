/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.orbeonweb.wrapper;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
public class YFormRequestAuthWrapperTest
{
	@Mock
	private HttpServletRequest request;
	@Mock
	private Authentication authentication;

	private final String hybrisProxyHeader = "hybris-Proxy-aaeaed1c-1dba-4d07-8f81-3ee86d985fcf";
	private final String hybrisProxyValue = "48bf3390-1c23-4a80-8a00-8e7d4a684c96";
	private final String HYBRIS_USERNAME = "hybris-username";
	private final String HYBRIS_ROLES = "hybris-roles";


	@Before
	public void setUp() throws MalformedURLException
	{
		MockitoAnnotations.initMocks(this);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	public void testWhenAnonymousUser()
	{

		when(authentication.getPrincipal()).thenReturn("anonymous");

		final HttpServletRequest requestWrapper = new YFormRequestAuthWrapper(request, hybrisProxyHeader, hybrisProxyValue);
		assertEquals(hybrisProxyValue, requestWrapper.getHeader(hybrisProxyHeader));
		assertEquals("anonymous", requestWrapper.getHeader(HYBRIS_USERNAME));
		assertEquals("ROLE_ANONYMOUS", requestWrapper.getHeader(HYBRIS_ROLES));
	}

	@Test
	public void testWhenNotAnonymousUser()
	{
		SimpleGrantedAuthority customerGroupAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP");
		List customerGroupAuthorityCollection = new ArrayList<>();
		customerGroupAuthorityCollection.add(customerGroupAuthority);

		when(authentication.getAuthorities()).thenReturn(customerGroupAuthorityCollection);
		when(authentication.getPrincipal()).thenReturn("keenreviewer1@hybris.com");

		final HttpServletRequest requestWrapper = new YFormRequestAuthWrapper(request, hybrisProxyHeader, hybrisProxyValue);
		assertEquals(hybrisProxyValue, requestWrapper.getHeader(hybrisProxyHeader));
		assertEquals("keenreviewer1@hybris.com", requestWrapper.getHeader(HYBRIS_USERNAME));
		assertEquals("ROLE_CUSTOMERGROUP", requestWrapper.getHeader(HYBRIS_ROLES));
	}

	@Test
	public void testWhenUserWithMultipleRoles()
	{
		SimpleGrantedAuthority customerGroupAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP");
		SimpleGrantedAuthority b2bGroupAuthority = new SimpleGrantedAuthority("ROLE_B2BGROUP");
		List rolesList = new ArrayList<>();
		rolesList.add(customerGroupAuthority);
		rolesList.add(b2bGroupAuthority);

		when(authentication.getAuthorities()).thenReturn(rolesList);
		when(authentication.getPrincipal()).thenReturn("keenreviewer1@hybris.com");

		final HttpServletRequest requestWrapper = new YFormRequestAuthWrapper(request, hybrisProxyHeader, hybrisProxyValue);
		assertEquals(hybrisProxyValue, requestWrapper.getHeader(hybrisProxyHeader));
		assertEquals("keenreviewer1@hybris.com", requestWrapper.getHeader(HYBRIS_USERNAME));
		assertEquals("ROLE_CUSTOMERGROUP,ROLE_B2BGROUP", requestWrapper.getHeader(HYBRIS_ROLES));
	}
}
