/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.http.impl;

import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;


/**
 * Implementation of HTTP Destination.
 */
public class HTTPDestinationImpl implements HTTPDestination
{

	/**
	 * The model of the HTTP destination.
	 */
	private final SAPHTTPDestinationModel model;

	/**
	 * @param model
	 *           the model
	 */
	public HTTPDestinationImpl(final SAPHTTPDestinationModel model)
	{
		super();
		this.model = model;
	}

	@Override
	public String getHttpDestinationName()
	{
		return model.getHttpDestinationName();
	}

	@Override
	public String getTargetURL()
	{
		return model.getTargetURL();
	}

	@Override
	public String getAuthenticationType()
	{
		return model.getAuthenticationType().toString();
	}

	@Override
	public String getUserid()
	{
		return model.getUserid();
	}

	@Override
	public String getPassword()
	{
		return model.getPassword();
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreconfigurationConstants.CRLF + "- HTTP Destination Name: " + getHttpDestinationName()
				+ SapcoreconfigurationConstants.CRLF + "- Target URL: " + getTargetURL() + SapcoreconfigurationConstants.CRLF
				+ "- Authentication Type: " + getAuthenticationType() + SapcoreconfigurationConstants.CRLF + "- User ID: "
				+ getUserid() + SapcoreconfigurationConstants.CRLF + "- Password: <not displayed>";
	}

}
