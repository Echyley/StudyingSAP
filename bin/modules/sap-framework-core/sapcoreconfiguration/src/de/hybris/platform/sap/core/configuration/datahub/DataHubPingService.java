/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.datahub;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.util.localization.Localization;


/**
 * This class handles pinging the DataHub system to confirm communication
 *
 */
public class DataHubPingService
{

	private static final Logger LOGGER = LogManager.getLogger(DataHubPingService.class);
	private String dataHubUrl;
	private int timeout;

	/**
	 * Ping method to determine whether communication with DataHub is possible
	 * 
	 * @return Boolean response to status of communication feasability
	 */
	public boolean ping()
	{

		String url = getDataHubUrl().replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

		try
		{

			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(getTimeout());
			connection.setReadTimeout(getTimeout());
			connection.connect();
			return true;

		}
		catch (Exception exception)
		{

			final String logMessage = Localization.getLocalizedString("dataTransfer.dataHub.unableCommunication.exception");
			LOGGER.warn(logMessage);
			LOGGER.error(exception);

			return false;
		}
	}

	public String getDataHubUrl()
	{
		return dataHubUrl;
	}

	@Required
	public void setDataHubUrl(String dataHubUrl)
	{
		this.dataHubUrl = dataHubUrl;
	}

	public int getTimeout()
	{
		return timeout;
	}

	@Required
	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}



}
