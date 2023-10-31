/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.consent.filters;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_TRACKING_PAUSE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Generates the consent reference for the user and stores it in a cookie and in session
 */
public class ConsentLayerFilter extends OncePerRequestFilter
{

    private static final Logger LOG = Logger.getLogger(ConsentLayerFilter.class);
    private static final String CONSENT_LAYER_FILTER_INVOKED = "CONSENT_LAYER_FILTER_INVOKED";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private ConsentService consentService;
    private SessionService sessionService;
    private boolean enabled;
    private String excludeUrlPatterns;
    private UserService userService;

    private ProfileConfigurationService profileConfigurationService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (isConsentLayerFilterInvoked(httpServletRequest)) {
            return;
        }

        runFilterLogic(httpServletRequest, Optional.of(httpServletResponse));

        httpServletRequest.setAttribute(CONSENT_LAYER_FILTER_INVOKED, true);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public void runFilterLogic(HttpServletRequest httpServletRequest) {
        runFilterLogic(httpServletRequest, Optional.empty());
    }

    protected void runFilterLogic(HttpServletRequest httpServletRequest, Optional<HttpServletResponse> httpServletResponse) {
        if (isConsentLayerFilterInvoked(httpServletRequest)) {
            return;
        }

        if (isProfileTrackingPaused(httpServletRequest)) {
            LOG.debug("Profile tracking disabled");
            return;
        }

        if (isProfileTrackingConsentGiven(httpServletRequest) && isActiveAccount()){
            LOG.debug("Profile tracking consent given");
            getConsentService().saveConsentReferenceInSessionAndConsentModel(httpServletRequest);
            if (httpServletResponse.isPresent()) {
                setProfileConsentCookieAndSession(httpServletRequest, httpServletResponse.get(), true);
                setProfileIdCookieInSession(httpServletRequest, httpServletResponse.get());
            }
        } else {
            LOG.debug("Profile tracking consent withdrawn | account is deactivated");
            getConsentService().removeConsentReferenceInSession();
            if (httpServletResponse.isPresent()) {
                setProfileConsentCookieAndSession(httpServletRequest, httpServletResponse.get(), false);
            }
        }
    }



    private boolean isConsentLayerFilterInvoked(HttpServletRequest httpServletRequest) {
        return (httpServletRequest == null) ||
                (httpServletRequest.getAttribute(CONSENT_LAYER_FILTER_INVOKED) != null &&
                httpServletRequest.getAttribute(CONSENT_LAYER_FILTER_INVOKED) == Boolean.TRUE);
    }

    protected boolean isProfileTrackingPaused(HttpServletRequest httpServletRequest) {

        final Optional<Cookie> pauseProfileTrackingCookie = Utils.getCookie(httpServletRequest, PROFILE_TRACKING_PAUSE);
        getProfileConfigurationService().setProfileTrackingPauseValue(pauseProfileTrackingCookie.isPresent());

        return  pauseProfileTrackingCookie.isPresent();
    }

    protected boolean isProfileTrackingConsentGiven(HttpServletRequest httpServletRequest){
        return getConsentService().isProfileTrackingConsentGiven(httpServletRequest);
    }

    protected void setProfileConsentCookieAndSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean profileTrackingConsentGiven){
        getConsentService().setProfileConsentCookieAndSession(httpServletRequest, httpServletResponse, profileTrackingConsentGiven);
    }


    protected void setProfileIdCookieInSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        final String consentReferenceId = getConsentService().getConsentReferenceFromConsentModel();
        if(!StringUtils.isBlank(consentReferenceId)){
            getConsentService().setProfileIdCookie(httpServletRequest,httpServletResponse,consentReferenceId);
        }
    }

    protected boolean isActiveAccount(){
        final UserModel currentUser = getUserService().getCurrentUser();

        return currentUser.getDeactivationDate() == null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        if (isBlank(excludeUrlPatterns)){
            return false;
        }

        final List<String> patterns = Arrays.asList(excludeUrlPatterns.split("\\s*,\\s*"));

        return patterns.stream()
                .anyMatch(p -> (request.getRequestURI() !=null && pathMatcher.match(p, request.getRequestURI()))
                        || (request.getQueryString() !=null && pathMatcher.match(p, request.getQueryString())));
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public void setExcludeUrlPatterns(String excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
