/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.user.UserConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSimpleProxyEngineTest {
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletRequest request;
    @Spy
    private Map<String, String> extraHeaders;
    @InjectMocks
    private DefaultSimpleProxyEngine defaultSimpleProxyEngine;

    private static final String HYBRIS_USERNAME = "hybris-Username";
    private static final String HYBRIS_ROLES = "hybris-Roles";

    @Test
    public void shouldSetHeadersWhenUserPrincipalIsNotNull() {
        SimpleGrantedAuthority customerGroupAuthority = new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP");
        List customerGroupAuthorityCollection = new ArrayList<>();
        customerGroupAuthorityCollection.add(customerGroupAuthority);

        when((Authentication) request.getUserPrincipal()).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(customerGroupAuthorityCollection);
        when(request.getRemoteUser()).thenReturn("testUser");

        defaultSimpleProxyEngine.setHeaders(request, extraHeaders);

        verify(extraHeaders).put(HYBRIS_USERNAME, "testUser");
        verify(extraHeaders).put(HYBRIS_ROLES, "ROLE_CUSTOMERGROUP");
    }

    @Test
    public void shouldSetHeadersWhenUserPrincipalIsNull() {
        defaultSimpleProxyEngine.setHeaders(request, extraHeaders);

        verify(extraHeaders).put(HYBRIS_USERNAME, UserConstants.ANONYMOUS_CUSTOMER_UID);
        verify(extraHeaders).put(HYBRIS_ROLES, "ROLE_ANONYMOUS");
    }

}
