/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.datahubbackoffice.service.datahub;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.hybris.datahub.client.BasicSecurityCredentialsInfo;
import com.hybris.datahub.client.ClientConfiguration;
import com.hybris.datahub.client.DataHubStatusClient;
import com.hybris.datahub.client.SecurityCredentialsInfo;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * A DataHub server to communicate to.
 */
// Hint: This class can be eventually used to retrieve all REST clients necessary for the communication.
public class DataHubServer implements Serializable
{
	private static final long serialVersionUID = -5646804422170238681L;
	private static final int TIMEOUT_VALUE = 5000;

	private final DataHubServerInfo serverInfo;

	public DataHubServer(@NotNull final DataHubServerInfo info)
	{
		serverInfo = info;
	}

	/**
	 * Determines whether the server is accessible within the timeout
	 *
	 * @return <code>true</code>, if the server is accessible and the program can interact with it; <code>false</code>, otherwise.
	 */
	public boolean isAccessibleWithTimeout()
	{
		final var cfg = new ClientConfiguration();
		final SecurityCredentialsInfo securityCredentialsInfo = new BasicSecurityCredentialsInfo(
				serverInfo.getUserName(),
				serverInfo.getPassword());

		cfg.addProperty(ClientProperties.READ_TIMEOUT, TIMEOUT_VALUE);
		cfg.addProperty(ClientProperties.CONNECT_TIMEOUT, TIMEOUT_VALUE);
		cfg.setSecurityCredentialsInfo(securityCredentialsInfo);
		cfg.setSecurityClientFilter(HttpAuthenticationFeature.basic(serverInfo.getUserName(), serverInfo.getPassword()));

		return new DataHubStatusClient(cfg, serverInfo.getLocation()).isDataHubRunning();
	}

	/**
	 * Reads name of this server.
	 *
	 * @return name of this server as it was configured in the {@link DataHubNameService}
	 */
	public String getName()
	{
		return serverInfo.getName();
	}

	/**
	 * Reads location URL of this server.
	 *
	 * @return URL of this server as it was configured in the {@link DataHubNameService}
	 */
	public String getLocation()
	{
		return serverInfo.getLocation();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof DataHubServer && serverInfo.equals(((DataHubServer) obj).serverInfo);
	}

	@Override
	public int hashCode()
	{
		return serverInfo.hashCode();
	}
}
