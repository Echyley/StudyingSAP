/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection;

/**
 * Interface for Managed Connection Factory.
 */
public interface JCoManagedConnectionFactory
{

	/**
	 * Returns a managed connection by a given connection name.
	 * 
	 * @param connectionName
	 *           name of the connection
	 * @param callerId
	 *           id for identifying the caller used for debugging
	 * @return managed connection instance
	 */
	public JCoConnection getManagedConnection(final String connectionName, final String callerId);

	/**
	 * Returns a managed connection by a given connection name.
	 * 
	 * @param connectionName
	 *           name of the connection
	 * @param callerId
	 *           id for identifying the caller used for debugging
	 * @param destinationName
	 *           name of the destination to be used
	 * @return managed connection instance
	 */
	public JCoConnection getManagedConnection(final String connectionName, final String callerId, final String destinationName);

}
