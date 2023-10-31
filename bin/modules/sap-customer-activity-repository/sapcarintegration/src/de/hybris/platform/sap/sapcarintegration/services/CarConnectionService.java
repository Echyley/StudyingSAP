/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcarintegration.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;


/**
 * This interface provides the means to obtain a connection over HTTP to the CAR located at the provided URL
 */
public interface CarConnectionService
{

	/**
	 * 
	 * @param absoluteUri
	 *           Absolute target URI for CAR
	 * @param contentType
	 *           Content Type expected to be received
	 * @param httpMethod
	 *           HTTP Method to be used (PUT/POST/GET etc...)
	 * @return HTTP Connection established
	 * @throws IOException
	 *            Exception thrown if an exception occurs when opening the connection
	 * @throws MalformedURLException
	 *            Thrown if an unknown or unspecified protocol is provided
	 */
	public abstract HttpURLConnection createConnection(String absoluteUri, String contentType, String httpMethod)
			throws IOException, MalformedURLException;

}
