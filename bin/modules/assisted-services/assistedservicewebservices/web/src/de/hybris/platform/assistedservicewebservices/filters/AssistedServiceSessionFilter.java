/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.UserModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AssistedServiceSessionFilter extends OncePerRequestFilter {

    public static final String ROLE_ASAGENTGROUP = "ROLE_ASAGENTGROUP";
    private List<String> excludedUrls;
    private AssistedServiceFacade assistedServiceFacade;
    private UserMatchingService userMatchingService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final Authentication auth = getAuth();

        if (isFilterRequired(request.getServletPath()))
        {
            if (hasRole(ROLE_ASAGENTGROUP, auth))
            {
                assistedServiceFacade.launchAssistedServiceMode();
                setAsmSessionAgent((String) auth.getPrincipal());
            }
            else
            {
                throw new AccessDeniedException("Access is denied");
            }
        }
        filterChain.doFilter(request, response);
    }

    protected void setAsmSessionAgent(String id)
    {
        final UserModel user = userMatchingService.getUserByProperty(id, UserModel.class);
        assistedServiceFacade.getAsmSession().setAgent(user);
    }

    protected Authentication getAuth()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected boolean hasRole(final String role, final Authentication auth)
    {
        if (auth != null)
        {
            for (final GrantedAuthority ga : auth.getAuthorities())
            {
                if (ga.getAuthority().equals(role))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFilterRequired(final String url)
    {
        return url == null || excludedUrls == null
                || excludedUrls.stream().noneMatch(excluded -> excluded != null && url.matches(excluded));
    }

    protected List<String> getExcludedUrls()
    {
        return excludedUrls;
    }

    public void setExcludedUrls(final List<String> excludedUrls)
    {
        this.excludedUrls = excludedUrls;
    }

    protected AssistedServiceFacade getAssistedServiceFacade()
    {
        return assistedServiceFacade;
    }

    public void setAssistedServiceFacade(final AssistedServiceFacade assistedServiceFacade)
    {
        this.assistedServiceFacade = assistedServiceFacade;
    }

    protected UserMatchingService getUserMatchingService()
    {
        return userMatchingService;
    }

    public void setUserMatchingService(final UserMatchingService userMatchingService)
    {
        this.userMatchingService = userMatchingService;
    }
}
