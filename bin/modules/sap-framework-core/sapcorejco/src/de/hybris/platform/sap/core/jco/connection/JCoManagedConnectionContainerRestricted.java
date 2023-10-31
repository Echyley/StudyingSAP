/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection;

/**
 * Restricted interface for internal use to react on JCoException.JCO_ERROR_COMMUNICATION.
 */
public interface JCoManagedConnectionContainerRestricted
{

	/**
	 * Removes the Stateful connection from managedConnection container.
	 * 
	 * @param connection
	 *           JCo Connection
	 */
	public void removeConnection(final JCoConnection connection);
}
