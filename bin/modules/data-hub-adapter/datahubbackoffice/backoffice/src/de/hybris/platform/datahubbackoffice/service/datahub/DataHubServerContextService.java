/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.datahubbackoffice.service.datahub;

import java.util.Collection;

/**
 * A service containing context of the DataHub server the user is working with now.
 */
public interface DataHubServerContextService
{
	/**
	 * Returns the DataHub server the user is working with.
	 *
	 * @return DataHub server currently being in the context of the user operations.
	 */
	DataHubServer getContextDataHubServer();

	/**
	 * Retrieves all DataHub Servers available.
	 *
	 * @return a collection of all servers available for the user to choose or an empty collection, if no servers configured.
	 */
	Collection<DataHubServer> getAllServers();

	/**
	 * Resets all servers and current context DataHub server in the context of this service.
	 * Implementations must provide this functionality. Default implementation in this interface does nothing.
	 *
	 * @see #getContextDataHubServer()
	 * @see #getAllServers()
	 */
	default void reset()
	{
	}
}
