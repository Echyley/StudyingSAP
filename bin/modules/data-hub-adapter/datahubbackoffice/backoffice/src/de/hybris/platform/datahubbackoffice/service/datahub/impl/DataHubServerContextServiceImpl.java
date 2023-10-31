/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.datahubbackoffice.service.datahub.impl;

import de.hybris.platform.datahubbackoffice.service.datahub.DataHubNameService;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServer;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServerAware;
import de.hybris.platform.datahubbackoffice.service.datahub.DataHubServerContextService;
import de.hybris.platform.datahubbackoffice.service.datahub.InaccessibleDataHubServer;
import de.hybris.platform.dataimportcommons.ApplicationBeans;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

import com.google.common.base.Preconditions;

/**
 * A factory for creating REST clients based on the DataHub instance selected by the user. Selected DataHub instance is preserved
 * for the duration of the session or until it's changed.
 */
public class DataHubServerContextServiceImpl implements DataHubServerAware, DataHubServerContextService, Serializable
{
	@Serial
	private static final long serialVersionUID = -2098492553148032422L;

	private transient DataHubServer dataHubServer;
	private transient Collection<DataHubServer> allServers;
	private transient DataHubNameService nameService;

	@Override
	public DataHubServer getContextDataHubServer()
	{
		return dataHubServer != null ? dataHubServer : getDefaultServer();
	}

	@Override
	public Collection<DataHubServer> getAllServers()
	{
		if (allServers == null)
		{
			allServers = initializeServers();
		}
		return allServers;
	}

	private Collection<DataHubServer> initializeServers()
	{
		return getNameService().getAllServers().stream()
		                  .map(DataHubServer::new)
		                  .toList();
	}

	private DataHubServer getDefaultServer()
	{
		final DataHubServer server = getAllServers().stream()
		                                            .filter(DataHubServer::isAccessibleWithTimeout)
		                                            .findFirst()
		                                            .orElseGet(InaccessibleDataHubServer::new);
		setDataHubServer(server);
		return server;
	}

	@Override
	public void reset()
	{
		dataHubServer = null;
		allServers = null;
	}

	/**
	 * Sets DataHub server the user will operate with.
	 *
	 * @param server a DataHub server to be used as the context server.
	 * @see #getContextDataHubServer()
	 * @throws IllegalArgumentException if provided server is {@code null}
	 */
	@Override
	public void setDataHubServer(final DataHubServer server)
	{
		Preconditions.checkArgument(server != null);
		dataHubServer = server;
	}

	/**
	 * Injects DataHub server name service to be used.
	 *
	 * @param service a service implementation to use.
	 */
	public void setNameService(final DataHubNameService service)
	{
		nameService = service;
	}

	private DataHubNameService getNameService()
	{
		if (nameService == null)
		{
			nameService = ApplicationBeans.getBean("dataHubNameService", DataHubNameService.class);
		}
		return nameService;
	}
}
