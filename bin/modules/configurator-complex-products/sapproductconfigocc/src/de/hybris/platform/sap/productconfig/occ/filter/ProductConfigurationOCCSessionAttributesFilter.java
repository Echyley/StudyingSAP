/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.filter;

import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Sets session attribute that indicates that configurations for an OCC session behave different with respect to their
 * lifecycle
 *
 */
public class ProductConfigurationOCCSessionAttributesFilter extends OncePerRequestFilter
{

	private SessionService sessionService;

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		getSessionService().setAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS, true);
		filterChain.doFilter(request, response);
	}

	/**
	 * @param sessionService
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
