/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Filter that puts user from the requested url into the session.
 */
public class UserMatchingFilter extends OncePerRequestFilter
{
    public static final String ROLE_ASAGENTGROUP = "ROLE_ASAGENTGROUP";
    private static final String ACTING_USER_UID = "ACTING_USER_UID";
    private static final Logger LOG = LoggerFactory.getLogger(UserMatchingFilter.class);

    private String regexp;
    private UserService userService;
    private SessionService sessionService;
    private UserMatchingService userMatchingService;
    private List<String> excludedUrls;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException
    {
        final Authentication auth = getAuth();
        if (isFilterRequired(request.getServletPath())) {

            if (hasRole(ROLE_ASAGENTGROUP, auth)) {
                getSessionService().setAttribute(ACTING_USER_UID, auth.getPrincipal());
            }

            final String userID = getUserIdFromRequest(request);

            if (userID == null) {
                if (hasRole(ROLE_ASAGENTGROUP, auth))
                {
                    setCurrentUser((String) auth.getPrincipal());
                }
                else
                {
                    // could not match any authorized role
                    throw new AccessDeniedException("Access is denied");
                }
            }
            else if (hasRole(ROLE_ASAGENTGROUP, auth))
            {
                setCurrentUser(userID);
            }
            else
            {
                // could not match any authorized role
                throw new AccessDeniedException("Access is denied");
            }
        }
        filterChain.doFilter(request, response);
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

    protected String getUserIdFromRequest(final HttpServletRequest request)
    {
        // try to get the userId from the request path
        return getValue(request, regexp);
    }

    protected void setCurrentUser(final String id)
    {
        try
        {
            final UserModel user = userMatchingService.getUserByProperty(id, UserModel.class);
            setCurrentUser(user);
        }
        catch (final UnknownIdentifierException ex)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("UnknownIdentifierException thrown when getting userModel with id: {}", id);
            }
            throw ex;
        }
    }

    protected void setCurrentUser(final UserModel user)
    {
        userService.setCurrentUser(user);
    }

    protected String getValue(final HttpServletRequest request, final String regexp)
    {
        final Matcher matcher = getMatcher(request, regexp);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }

    protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
    {
        final Pattern pattern = Pattern.compile(regexp);
        final String path = getPath(request);
        return pattern.matcher(path);
    }

    protected String getPath(final HttpServletRequest request)
    {
        return StringUtils.defaultString(request.getServletPath());
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

    protected Authentication getAuth()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public void setRegexp(final String regexp)
    {
        this.regexp = regexp;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    protected SessionService getSessionService()
    {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService)
    {
        this.sessionService = sessionService;
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
