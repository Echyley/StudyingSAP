/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.proxy;

import de.hybris.platform.xyformsservices.enums.YFormDataActionEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;
import de.hybris.platform.xyformsservices.proxy.ProxyException;
import de.hybris.platform.xyformsservices.proxy.ProxyService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @deprecated
 * Orchestrates calls to {@link ProxyService}
 */
@Deprecated(since = "2211", forRemoval = true)
public interface ProxyFacade
{
	/**
	 * Gets the embedded HTML representation of a form definition.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param action
	 * 			the form action
	 * @param formDataId
	 * 			the form data id of the form definition
	 *	@return the inline representation of the form definition as string
	 * @throws YFormServiceException if request with specified parameter cannot be proxied or response is corrupted
	 */
	public String getInlineFormHtml(final String applicationId, final String formId, final YFormDataActionEnum action,
			final String formDataId) throws YFormServiceException;

	/**
	 * Creates empty YFormData for a form definition via orbeon background API.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 *	@return new id for created YFormData
	 * @throws YFormServiceException if request with specified parameter cannot be proxied or response is corrupted
	 */
	public String createEmptyDataForForm(final String applicationId, final String formId) throws YFormServiceException;

	/**
	 * Proxies content
	 *
	 * @param request
	 *           the {@link HttpServletRequest} associated with the call
	 * @param response
	 *           the {@link HttpServletResponse} associated with the call
	 * @throws ProxyException when request cannot be proxied
	 */
	public void proxy(final HttpServletRequest request, final HttpServletResponse response) throws ProxyException;
}
