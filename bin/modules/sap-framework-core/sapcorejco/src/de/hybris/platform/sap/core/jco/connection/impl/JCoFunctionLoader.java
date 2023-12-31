/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

// JCo imports
import de.hybris.platform.regioncache.CacheValueLoadException;

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

/**
 * This class acts as a loader for JCo functions.
 * 
 * @version 1.0
 */
public class JCoFunctionLoader implements CacheValueLoader<JCoFunction> {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(JCoFunctionLoader.class
            .getName());
    /**
     * function attribute.
     */
    private JCoFunctionLoader.JCoFunctionAttr funcAttr = null;

    /**
     * Setter-Method for property {@link #funcAttr}. <br>
     * 
     * @param funcAttr
     *            The {@link #funcAttr} to set.
     */
    public void setFuncAttr(final JCoFunctionLoader.JCoFunctionAttr funcAttr) {
        this.funcAttr = funcAttr;
    }

    /**
   *
   */
    public static class JCoFunctionAttr {

        /**
         * Destination.
         */
        private final JCoDestination destination;
        /**
         * Function.
         */
        private final JCoFunction function;

        /**
         * Constructor for a data container used by
         * <code>JCoFunctionLoader</code>.
         * 
         * @param destination
         *            JCo destination
         * @param function
         *            JCo function
         */
        public JCoFunctionAttr(final JCoDestination destination,
                final JCoFunction function) {
            this.destination = destination;
            this.function = function;
        }

        /**
         * Gets the JCoDestination.
         * 
         * @return JCo destination
         */
        public JCoDestination getJCoDestination() {
            return destination;
        }

        /**
         * Gets the JCoFunction.
         * 
         * @return JCo function
         */
        public JCoFunction getFunction() {
            return function;
        }

    }

    /**
     * This method is used to return a <code>JCO.Function</code> to the cache.
     * This function is already prepared to be accessed by several threads.
     * 
     * @param cacheKey
     *            cache key
     * @return JCoFunction function
     * @throws CacheValueLoadException
     *             CacheValueLoadException
     * @see de.hybris.platform.regioncache.CacheValueLoader#load(de.hybris.platform.regioncache.key.CacheKey)
     */
    @Override
    public JCoFunction load(final CacheKey cacheKey)
            throws CacheValueLoadException {

        if (!validJCoAttributes()) {
            throw new CacheValueLoadException(
                    "Load () for JCoFunctionLoader can not be processed as JCO attributes missing or invalid.");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("key=" + cacheKey.toString() + ", destination="
                    + funcAttr.getJCoDestination().toString() + ", function="
                    + funcAttr.getFunction().toString());
        }

        final JCoFunction oldFunc = funcAttr.getFunction();

        final JCoDestination destination = funcAttr.getJCoDestination();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Calling function module '" + oldFunc.getName()
                    + "' delegated to JCoFunctionLoader");
        }

        try {
            oldFunc.execute(destination);

        } catch (final JCoException ex) {
            throw new CacheValueLoadException(ex.toString(), ex);
        }

        return oldFunc;

    }

    /**
     * Check if JCoDestination and JCoFunction are provided correctly.<br>
     * 
     * @return validation flag
     */
    private boolean validJCoAttributes() {
        return (funcAttr != null) && (funcAttr.getJCoDestination() != null)
                && (funcAttr.getFunction() != null);
    }

}
