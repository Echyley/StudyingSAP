/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.web.payment.interceptors;

import de.hybris.platform.acceleratorservices.web.payment.constants.PaymentMockConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PaymentMockControlHandlerInterceptor implements HandlerInterceptor {

    private static final String SOP_MOCK_ENABLED = "acceleratorservices.payment.sopmock.enabled";
    private static final String HOP_MOCK_ENABLED = "acceleratorservices.payment.hopmock.enabled";

    private ConfigurationService configurationService;

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI();
        if (invalidSopMockRequest(requestUrl) || invalidHopMockRequest(requestUrl)) {
            response.sendError(HttpStatus.SC_NOT_FOUND);
            return false;
        }
        return true;
    }

    private boolean invalidSopMockRequest(String requestUrl) {
        return StringUtils.contains(requestUrl, PaymentMockConstants.SOP_MOCK) && !isSopMockEnabled();
    }

    private boolean isSopMockEnabled() {
        return getConfigurationService().getConfiguration().getBoolean(SOP_MOCK_ENABLED, false);
    }

    private boolean invalidHopMockRequest(String requestUrl) {
        return StringUtils.contains(requestUrl, PaymentMockConstants.HOP_MOCK) && !isHopMockEnabled();
    }

    private boolean isHopMockEnabled() {
        return getConfigurationService().getConfiguration().getBoolean(HOP_MOCK_ENABLED, false);
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
