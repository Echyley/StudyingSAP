/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.support.MetricType;

/**
 * Mbean wrapper for {@link org.springframework.integration.channel.QueueChannel}
 * Copy of {@link org.springframework.integration.support.management.MessageChannelMetrics} with one addiction
 */
@ManagedResource(
      description = "Gives an overview of spring-integration queues"
)
public interface QueueInfoBean
{
    @ManagedAttribute(
            description = "Overview of beanName",
            persistPeriod = 1
    )
    String getBeanName();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "QueueChannel Queue Size"
    )
    int getQueueSize();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "QueueChannel Remaining Capacity"
    )
    int getRemainingCapacity();

}
