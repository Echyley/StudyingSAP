/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Test suite for {@link UserMatchingFilter}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UserMatchingFilterTest
{
    static final String DEFAULT_REGEXP = "^/[^/]+/(?:users|orgUsers)/([^/]+)";
    static final String ANONYMOUS_UID = "anonymous";
    static final String CUSTOMER_UID = "customerUID";
    static final String ROLE_UNKNOWN = "ROLE_UNKNOWN";
    static final String ROLE_CUSTOMERGROUP = "ROLE_CUSTOMERGROUP";
    static final String EXCLUDED_URL = ".*swagger.*";
    private UserMatchingFilter userMatchingFilter;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserService userService;
    @Mock
    private UserMatchingService userMatchingService;
    @Mock
    private SessionService sessionService;
    @Mock
    private CustomerModel principalUserModel;
    @Mock
    private CustomerModel customerUserModel;
    @Mock
    private TestingAuthenticationToken authentication;
    @Mock
    private GrantedAuthority grantedAuthority;
    private Collection<GrantedAuthority> authorities;

    @Before
    public void setUp()
    {
        userMatchingFilter = new UserMatchingFilter()
        {
            @Override
            protected Authentication getAuth()
            {
                return authentication;
            }
        };
        userMatchingFilter.setRegexp(DEFAULT_REGEXP);
        userMatchingFilter.setUserService(userService);
        userMatchingFilter.setUserMatchingService(userMatchingService);
        userMatchingFilter.setSessionService(sessionService);
        authorities = new ArrayList<>();
        userMatchingFilter.setExcludedUrls(Arrays.asList(EXCLUDED_URL));
        given(httpServletRequest.getDispatcherType()).willReturn(DispatcherType.REQUEST);
    }

    public void createAuthority(final String role, final String principal)
    {
        given(grantedAuthority.getAuthority()).willReturn(role);
        authorities.add(grantedAuthority);
        given(authentication.getAuthorities()).willReturn(authorities);
        given(authentication.getPrincipal()).willReturn(principal);
    }

    public void testNullPathInfo(final String role, final String principal) throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn(null);
        createAuthority(role, principal);

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(userService, times(1)).setCurrentUser(principalUserModel);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test(expected = AccessDeniedException.class)
    public void testNullPathInfoOnCustomer() throws ServletException, IOException
    {
        testNullPathInfo(ROLE_CUSTOMERGROUP, CUSTOMER_UID);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFailMatchingPathForUnknownRole() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/wsTest/users/admin");
        createAuthority(ROLE_UNKNOWN, "unknown");

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFailMatchingPathForAuthenticatedCustomer() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/wsTest/users/" + CUSTOMER_UID + "/and/more");
        createAuthority(ROLE_CUSTOMERGROUP, CUSTOMER_UID);

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFailMatchingPathForUnauthenticatedCustomer() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/wsTest/users/admin/and/more");
        createAuthority(ROLE_CUSTOMERGROUP, CUSTOMER_UID);

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFailMatchingFilterForAnonymousUser() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/wsTest/users/" + ANONYMOUS_UID + "/and/more");

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Test(expected = AccessDeniedException.class)
    public void testFailMatchingPathForCurrentCustomer() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/wsTest/users/current/and/more");
        createAuthority(ROLE_CUSTOMERGROUP, CUSTOMER_UID);

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    public void testMatchingPathForASAgentUser(final String role, final String principal)
            throws IOException, ServletException
    {
        given(httpServletRequest.getServletPath()).willReturn("/wsTest/users/" + CUSTOMER_UID + "/and/more");
        createAuthority(role, principal);
        given(userMatchingService.getUserByProperty(CUSTOMER_UID, UserModel.class)).willReturn(customerUserModel);

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(userService, times(1)).setCurrentUser(customerUserModel);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testMatchingPathForASAgent() throws ServletException, IOException
    {
        testMatchingPathForASAgentUser(UserMatchingFilter.ROLE_ASAGENTGROUP, "asagent");
    }

    @Test
    public void testFilterNotInvokedForExcludedUrl() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/webjars/springfox-swagger-ui/images/throbber.gif");

        userMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(userService, never()).setCurrentUser(any(UserModel.class));
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }
}
