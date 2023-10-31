/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Test suite for {@link AssistedServiceSessionFilter}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class AssistedServiceSessionFilterTest {
    private static final String EXCLUDED_URL = ".*swagger.*";

    private static final String ASAGENT_UID = "asagent";

    private static final String PATH = "/baseSite/users/user1@test.net/customer360";

    private static final String ROLE_UNKNOWN = "ROLE_UNKNOWN";

    @Mock
    private AssistedServiceFacade assistedServiceFacade;
    @Mock
    private UserMatchingService userMatchingService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private TestingAuthenticationToken authentication;
    @Mock
    private GrantedAuthority grantedAuthority;
    @Mock
    private AssistedServiceSession assistedServiceSession;
    @Mock
    private UserModel principalUserModel;
    private Collection<GrantedAuthority> authorities;
    private AssistedServiceSessionFilter assistedServiceSessionFilter;

    @Before
    public void setUp()
    {
        assistedServiceSessionFilter = new AssistedServiceSessionFilter()
        {
            @Override
            protected Authentication getAuth() { return authentication; }
        };
        assistedServiceSessionFilter.setAssistedServiceFacade(assistedServiceFacade);
        assistedServiceSessionFilter.setUserMatchingService(userMatchingService);
        assistedServiceSessionFilter.setExcludedUrls(Arrays.asList(EXCLUDED_URL));
        authorities = new ArrayList<>();
    }

    @Test
    public void testHasASAgentGroupRole() throws ServletException, IOException {
        given(httpServletRequest.getServletPath()).willReturn(PATH);
        given(assistedServiceFacade.getAsmSession()).willReturn(assistedServiceSession);
        createAuthority(UserMatchingFilter.ROLE_ASAGENTGROUP, ASAGENT_UID);

        assistedServiceSessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(assistedServiceFacade, times(1)).launchAssistedServiceMode();
        verify(assistedServiceFacade, times(1)).getAsmSession();
        verify(assistedServiceSession, times(1)).setAgent(principalUserModel);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDoesNotHaveASAgentGroupRole() throws ServletException, IOException {
        createAuthority(ROLE_UNKNOWN, "unknown");

        assistedServiceSessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(assistedServiceFacade, never()).launchAssistedServiceMode();
        verify(assistedServiceFacade, never()).getAsmSession();
        verify(assistedServiceSession, never()).setAgent(any(UserModel.class));
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void testFilterNotInvokedForExcludedUrl() throws ServletException, IOException
    {
        given(httpServletRequest.getServletPath()).willReturn("/webjars/springfox-swagger-ui/images/throbber.gif");

        assistedServiceSessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        verify(assistedServiceFacade, never()).launchAssistedServiceMode();
        verify(assistedServiceFacade, never()).getAsmSession();
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }
    public void createAuthority(final String role, final String principal)
    {
        given(grantedAuthority.getAuthority()).willReturn(role);
        authorities.add(grantedAuthority);
        given(authentication.getAuthorities()).willReturn(authorities);
        given(authentication.getPrincipal()).willReturn(principal);
        given(userMatchingService.getUserByProperty(principal, UserModel.class)).willReturn(principalUserModel);
    }
}
