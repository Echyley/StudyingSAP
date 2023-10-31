/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.http;

/**
 * Interface to read RFC destination properties.
 */
public interface HTTPDestination
{
	/**
	 * @return the rfcDestinationName
	 */
	public String getHttpDestinationName();

	/**
	 * @return the targetHost
	 */
	public String getTargetURL();

	/**
	 * @return the authentication
	 */
	public String getAuthenticationType();

	/**
	 * @return the userid
	 */
	public String getUserid();

	/**
	 * @return the password
	 */
	public String getPassword();

}
