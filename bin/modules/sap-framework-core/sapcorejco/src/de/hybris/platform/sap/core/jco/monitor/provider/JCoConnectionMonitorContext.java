/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.monitor.provider;

import java.util.List;

import com.sap.conn.jco.monitor.JCoConnectionData;

/**
 * Interface to get JCo connection monitor context data.
 */

public interface JCoConnectionMonitorContext {

    /**
     * Returns current JCo connection data.
     * 
     * @return JCo connection data list
     */
    @java.lang.SuppressWarnings("squid:S1452")
    public List<? extends JCoConnectionData> getJCoConnectionData();

    /**
     * Returns the time stamp of the snapshot.
     * 
     * @return snapshot time stamp
     */
    public long getSnapshotTimestamp();

}
