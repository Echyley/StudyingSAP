/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cronjob.mbeans.event;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.mbeans.MetricsCronJobHolder;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class MetricsCronJobEventListenerTest extends ServicelayerBaseTest
{
	@Resource
	EventService eventService;

	@Resource
	MetricsCronJobHolder metricsCronJobHolder;

	private MetricsCronJobEventListener metricsCronJobEventListener;
	private MetricsCronJobListenerDecorator listener;
	private final Collection<String> listCj = Arrays.asList("aaa", "b", "cccccc");

	@Before
	public void setUp()
	{
		metricsCronJobEventListener = new MetricsCronJobEventListener(metricsCronJobHolder);
		listener = new MetricsCronJobListenerDecorator(metricsCronJobEventListener);
		eventService.registerEventListener(listener);
	}

	@After
	public void tearDown() throws Exception
	{
		eventService.unregisterEventListener(listener);
	}

	@Test
	public void shouldReturnCronJobInfoEvent() throws InterruptedException
	{
		final CronJobInfoEvent cronJobInfoEvent = new CronJobInfoEvent(listCj);
		eventService.publishEvent(cronJobInfoEvent);
		Thread.sleep(500);

		assertThat(listener.getLastEvent()).isNotNull();
		assertThat(Objects.requireNonNull(listener.getLastEvent()).getCronJobs().contains("aaa")).isTrue();
		assertThat(Objects.requireNonNull(listener.getLastEvent()).getCronJobs().toString()).isEqualTo(cronJobInfoEvent.getCronJobs().toString());
	}

	@Test
	public void shouldCacheCronJobList() throws InterruptedException
	{
		final CronJobInfoEvent cronJobInfoEvent = new CronJobInfoEvent(listCj);

		eventService.publishEvent(cronJobInfoEvent);
		Thread.sleep(500);
		final Collection<String> cronJobList = metricsCronJobHolder.getRunningCronJobs();

		assertThat(listener.getLastEvent()).isNotNull();
		assertThat(cronJobList).isNotNull();
		assertThat(cronJobList.toString()).hasToString(listCj.toString());
	}

	private static class MetricsCronJobListenerDecorator extends AbstractEventListener<CronJobInfoEvent>
	{
		private final List<CronJobInfoEvent> lastEvents = new CopyOnWriteArrayList<>();
		private final MetricsCronJobEventListener metricsCronJobEventListener;

		private MetricsCronJobListenerDecorator(final MetricsCronJobEventListener metricsCronJobEventListener)
		{
			this.metricsCronJobEventListener = metricsCronJobEventListener;
		}

		@Override
		protected void onEvent(final CronJobInfoEvent event)
		{
			this.lastEvents.add(0, event);
			metricsCronJobEventListener.onEvent(event);
		}

		public CronJobInfoEvent getLastEvent()
		{
			return lastEvents.isEmpty() ? null : lastEvents.get(0);
		}
	}
}