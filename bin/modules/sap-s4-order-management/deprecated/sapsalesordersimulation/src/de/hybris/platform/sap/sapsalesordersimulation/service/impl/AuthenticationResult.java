/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import org.springframework.http.HttpHeaders;


/**
 * Container for Http Header
 */
public class AuthenticationResult
{


	private HttpHeaders responseHeader;

	/**
	 * @return the responseHeader
	 */
	public HttpHeaders getResponseHeader()
	{
		return responseHeader;
	}

	/**
	 * @param responseHeader
	 *           the responseHeader to set
	 */
	public void setResponseHeader(final HttpHeaders responseHeader)
	{
		this.responseHeader = responseHeader;
	}

}
