/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import java.util.Properties;

import com.sap.conn.jco.JCoDestination;

/**
 * Stateless RFC connection. <br>
 * Establishes a stateless RFC connection to the backend. Connection is taken
 * from the connection pool and released automatically to the pool after
 * execution was done.
 */
public class JCoConnectionStateless extends JCoConnectionImpl {

    /**
     * Cache for managed functions.
     */
    protected JCoManagedFunctionCache managedFunctionCache = new JCoManagedFunctionCache(); // NOPMD

    /**
     * Constructor.
     * 
     * @param properties
     *            properties
     */
    public JCoConnectionStateless(final Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isFunctionCached(final JCoManagedFunction managedFunction) {
        return managedFunctionCache != null
                && managedFunctionCache.isFunctionCached(managedFunction);
    }

    @Override
    protected void executeCachedFunction(final JCoDestination destination,
            final JCoManagedFunction managedFunction) throws BackendException {
        managedFunctionCache.executeCachedFunction(getDestination(),
                managedFunction);
    }

}
