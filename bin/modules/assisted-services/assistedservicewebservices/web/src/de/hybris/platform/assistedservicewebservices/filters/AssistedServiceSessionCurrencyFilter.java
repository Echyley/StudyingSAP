/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.platform.assistedservicewebservices.context.ContextInformationLoader;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AssistedServiceSessionCurrencyFilter extends OncePerRequestFilter {
    private ContextInformationLoader contextInformationLoader;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        getContextInformationLoader().setCurrencyFromRequest(request);

        filterChain.doFilter(request, response);
    }

    protected ContextInformationLoader getContextInformationLoader() {
        return contextInformationLoader;
    }

    public void setContextInformationLoader(final ContextInformationLoader contextInformationLoader) {
        this.contextInformationLoader = contextInformationLoader;
    }
}
