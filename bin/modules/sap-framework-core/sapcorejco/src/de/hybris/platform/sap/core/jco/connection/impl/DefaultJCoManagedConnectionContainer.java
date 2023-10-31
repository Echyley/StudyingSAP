/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainer;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainerRestricted;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Container for managed connections. <br>
 * Container is session scoped. In case of the session is destroyed the destroy
 * method is called, which cleanups stateful connections.
 */
public class DefaultJCoManagedConnectionContainer implements
        JCoManagedConnectionContainer, JCoManagedConnectionContainerRestricted {
	
	private static final int MAP_INITIAL_SIZE = 4;
    /**
     * Managed connection factory.
     */
    protected JCoManagedConnectionFactory managedConnectionFactory; // NOPMD
    /**
     * Map of managed connections.
     */
    protected Map<String, JCoConnection> managedConnections = new HashMap<String, JCoConnection>(
   		 MAP_INITIAL_SIZE); // NOPMD

    /**
     * Setter for connection factory.
     * 
     * @param manangedConnectionFactory
     *            managed connection factory.
     */
    public void setManagedConnectionFactory(
            final JCoManagedConnectionFactory manangedConnectionFactory) {
        this.managedConnectionFactory = manangedConnectionFactory;
    }

    @Override
    public JCoConnection getManagedConnection(final String connectionName) {
        return getManagedConnection(connectionName, null);
    }

    @Override
    public JCoConnection getManagedConnection(final String connectionName,
            final String destinationName) {
        return getManagedConnection(connectionName, destinationName, null);
    }

    @Override
    public JCoConnection getManagedConnection(final String connectionName,
            final String destinationName, final String scopeId) {

        final StringBuilder managedConnectionKeyBuffer = new StringBuilder();
        managedConnectionKeyBuffer.append(connectionName).append("@")
                .append(destinationName).append("@").append(scopeId);
        final String managedConnectionKey = managedConnectionKeyBuffer
                .toString();
        if (managedConnections.containsKey(managedConnectionKey)) {
            return managedConnections.get(managedConnectionKey);
        }

        final JCoConnection con;
        if (destinationName != null) {
            con = managedConnectionFactory.getManagedConnection(connectionName,
                    this.getClass().getName(), destinationName);
        } else {
            con = managedConnectionFactory.getManagedConnection(connectionName,
                    this.getClass().getName());
        }

        // store only Stateful connections
        if (con instanceof JCoStateful) {
            managedConnections.put(managedConnectionKey, con);
        }

        return con;

    }

    /**
     * Destroy hosted managed connections.
     * 
     * @throws BackendException
     *             Exception in case of failure.
     */
    protected void destroy() throws BackendException {
        for (final JCoConnection con : managedConnections.values()) {
            if (con instanceof JCoStateful) {
                ((JCoStateful) con).destroy();
            }
        }
        managedConnections.clear();
        managedConnections = null;
    }

    @Override
    public void removeConnection(final JCoConnection connection) {
    	managedConnections.values().removeIf(entry -> entry.equals(connection));
    }
}
