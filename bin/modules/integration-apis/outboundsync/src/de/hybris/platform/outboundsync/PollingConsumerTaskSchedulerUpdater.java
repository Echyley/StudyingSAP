/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync;

import de.hybris.platform.integrationservices.util.Log;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.scheduling.TaskScheduler;

import com.google.common.base.Preconditions;


/**
 * Update the {@link PollingConsumer} with a custom {@link TaskScheduler}
 * @deprecated since 2205. Use {@link PollingConsumerTaskSchedulerUpdater} from the  de.hybris.platform.integrationservices.util package
 */
@Deprecated(since = "2205", forRemoval = true)
public class PollingConsumerTaskSchedulerUpdater implements ApplicationContextAware
{
	private static final Logger LOG = Log.getLogger(PollingConsumerTaskSchedulerUpdater.class);

	private final String inputChannelId;
	private final TaskScheduler taskScheduler;

	/**
	 * Instantiates the PollingConsumerTaskSchedulerUpdater
	 *
	 * @param inputChannelId The bean name of the input channel associated to the {@link PollingConsumer}
	 * @param taskScheduler The {@link TaskScheduler} to update the PollingConsumer with
	 */
	public PollingConsumerTaskSchedulerUpdater(@NotNull final String inputChannelId, @NotNull final TaskScheduler taskScheduler)
	{
		Preconditions.checkArgument(inputChannelId != null, "Input channel id cannot be null");
		Preconditions.checkArgument(taskScheduler != null, "Task scheduler cannot be null");
		this.inputChannelId = inputChannelId;
		this.taskScheduler = taskScheduler;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		updateTaskScheduler(applicationContext);
	}

	private void updateTaskScheduler(final ApplicationContext applicationContext)
	{
		final var pollingConsumerBeans = applicationContext.getBeansOfType(PollingConsumer.class);
		pollingConsumerBeans.values().stream()
				.filter(p -> p.getInputChannel() instanceof QueueChannel && inputChannelId.equals(((QueueChannel) p.getInputChannel()).getBeanName()))
				.findFirst()
				.ifPresent(p -> {
					p.setTaskScheduler(taskScheduler);
					LOG.info("Updated the task scheduler of PollingConsumer with input channel id {}", inputChannelId);
				});
	}
}
