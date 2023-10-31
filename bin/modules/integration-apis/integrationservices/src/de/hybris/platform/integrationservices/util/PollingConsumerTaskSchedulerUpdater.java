/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.scheduling.TaskScheduler;

import com.google.common.base.Preconditions;


/**
 * Injects provided {@link TaskScheduler} into the specified {@link PollingConsumer}
 */
public class PollingConsumerTaskSchedulerUpdater
{
	private static final Logger LOG = Log.getLogger(PollingConsumerTaskSchedulerUpdater.class);

	/**
	 * A utility service that injects a TaskScheduler into a Polling consumer. It does the injections in the constructor for the convenience of use in the spring xml files.
	 * @param pollingConsumer  The pollingConsumer to use
	 * @param taskScheduler The taskScheduler to use
	 */
	public PollingConsumerTaskSchedulerUpdater(@NotNull final PollingConsumer pollingConsumer, @NotNull final TaskScheduler taskScheduler)
	{
		Preconditions.checkArgument(pollingConsumer != null, "pollingConsumer cannot be null");
		Preconditions.checkArgument(taskScheduler != null, "taskScheduler cannot be null");
		pollingConsumer.setTaskScheduler(taskScheduler);
		LOG.info("Updated the task scheduler of PollingConsumer with id {}", pollingConsumer.getBeanName());
	}
}