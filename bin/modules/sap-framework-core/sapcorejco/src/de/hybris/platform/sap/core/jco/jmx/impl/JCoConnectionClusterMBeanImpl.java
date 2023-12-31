/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.jmx.impl;

import de.hybris.platform.jmx.mbeans.impl.AbstractJMXMBean;
import de.hybris.platform.sap.core.jco.jmx.JCoConnectionClusterMBean;
import de.hybris.platform.sap.core.jco.monitor.event.JCoConnectionsSnapshotClusterHandler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * JCoConnection cluster monitoring JMX MBean.
 */
@ManagedResource(description = "Overview of the SAP JCo Connections of all nodes of the cluster.")
public class JCoConnectionClusterMBeanImpl extends AbstractJMXMBean implements
        JCoConnectionClusterMBean {

    private JCoConnectionsSnapshotClusterHandler snapshotClusterHandler;

    /**
     * Injection setter for {@link JCoConnectionsSnapshotClusterHandler}.
     * 
     * @param snapshotClusterHandler
     *            the snapshotClusterHandler to set
     */
    @Required
    public void setSnapshotClusterHandler(
            final JCoConnectionsSnapshotClusterHandler snapshotClusterHandler) {
        this.snapshotClusterHandler = snapshotClusterHandler;
    }

    @Override
    @ManagedAttribute(description = "Shows the total number of JCo connections of all nodes of the cluster.")
    public Integer getTotalCount() {

        return new TenantAwareExecutorGetTotalCount().getResult();
    }

    @Override
    @ManagedAttribute(description = "Shows the number of JCo connections with a long lifetime of all nodes of the cluster.")
    public Integer getLongRunnerCount() {
        return new TenantAwareExecutorGetLongRunnerCount().getResult();
    }

    @Override
    @ManagedAttribute(description = "Shows the number of RFC destinations whose pool size is reached of all nodes of the cluster.")
    public Integer getPoolLimitReachedCount() {
        return new TenantAwareExecutorGetPoolLimitReachedCount().getResult();
    }

    @Override
    @ManagedAttribute(description = "Shows the total number of nodes of the cluster.")
    public Integer getNodesCount() {
        return new TenantAwareExecutorGetNodesCount().getResult();
    }

    @Override
    @ManagedAttribute(description = "Shows the total number of nodes which returned no result of the cluster .")
    public Integer getNodesWithoutResultCount() {
        return new TenantAwareExecutorGetNodesWithoutResultCount().getResult();
    }

    @Override
    @ManagedAttribute(description = "Shows the time stamp of the JCo connections cluster attributes cache.")
    public Date getCacheTimestamp() {
        return new TenantAwareExecutorGetCacheTimestamp().getResult();
    }

    @Override
    @ManagedAttribute(description = "Shows the time stamp the JCo connections cluster attribute caches is valid to.")
    public Date getCacheExpirationTimestamp() {
        return new TenantAwareExecutorGetCacheExpirationTimestamp().getResult();
    }

    @Override
    @ManagedOperation(description = "Resets the JCo connections cluster attributes cache.")
    public String resetCache() {
        return new TenantAwareExecutorGetResetString().getResult();
    }

    @Override
    @ManagedOperation(description = "Returns all current JCo connections of all nodes of the cluster as XML string.")
    public String createSnapshotXML() {
        return new TenantAwareExecutorSnapshotXML().getResult();
    }

    @Override
    @ManagedOperation(description = "Create JCo connections cluster snapshot file of all nodes of the cluster.")
    public String createSnapshotFile() {
        return new TenantAwareExecutorSnapshotFile().getResult();
    }

    protected class TenantAwareExecutorGetTotalCount extends
            TenantAwareExecutor<Integer> {
        @Override
        protected Integer doExecute() {
            return snapshotClusterHandler.getTotalCount();
        }
    }

    protected class TenantAwareExecutorGetLongRunnerCount extends
            TenantAwareExecutor<Integer> {
        @Override
        protected Integer doExecute() {
            return snapshotClusterHandler.getLongRunnerCount();
        }
    }

    protected class TenantAwareExecutorGetPoolLimitReachedCount extends
            TenantAwareExecutor<Integer> {
        @Override
        protected Integer doExecute() {
            return snapshotClusterHandler.getPoolLimitReachedCount();
        }
    }

    protected class TenantAwareExecutorGetNodesCount extends
            TenantAwareExecutor<Integer> {
        @Override
        protected Integer doExecute() {
            return snapshotClusterHandler.getNodesCount();
        }
    }

    protected class TenantAwareExecutorGetNodesWithoutResultCount extends
            TenantAwareExecutor<Integer> {
        @Override
        protected Integer doExecute() {
            return snapshotClusterHandler.getNodesWithoutResultCount();
        }
    }

    protected class TenantAwareExecutorGetCacheTimestamp extends
            TenantAwareExecutor<Date> {
        @Override
        protected Date doExecute() {
            return snapshotClusterHandler.getCacheTimestamp();
        }
    }

    protected class TenantAwareExecutorGetCacheExpirationTimestamp extends
            TenantAwareExecutor<Date> {
        @Override
        protected Date doExecute() {
            return snapshotClusterHandler.getCacheExpirationTimestamp();
        }
    }

    protected class TenantAwareExecutorGetResetString extends
            TenantAwareExecutor<String> {
        @Override
        protected String doExecute() {
            snapshotClusterHandler.resetCache();
            return "JCo connections cluster snapshot cache has been reset";
        }
    }

    protected class TenantAwareExecutorSnapshotXML extends
            TenantAwareExecutor<String> {
        @Override
        protected String doExecute() {
            return snapshotClusterHandler.createSnapshotXML();
        }
    }

    protected class TenantAwareExecutorSnapshotFile extends
            TenantAwareExecutor<String> {
        @Override
        protected String doExecute() {
            return snapshotClusterHandler.createSnapshotFile();
        }
    }
}
