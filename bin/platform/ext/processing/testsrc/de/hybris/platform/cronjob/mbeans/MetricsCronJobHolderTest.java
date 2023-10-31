/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cronjob.mbeans;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.impl.DefaultCronJobService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

@IntegrationTest
public class MetricsCronJobHolderTest extends ServicelayerBaseTest
{
	@Mock
	private CronJobService cronJobService;
	@Mock
	private EventService eventService;

	private MetricsCronJobHolder metricsCronJobHolder;
	private final Collection<String> listCj = Arrays.asList("aaa", "b", "cccccc");
	private PropertyConfigSwitcher metricsCronJobsRunningCacheTtlMs;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		metricsCronJobsRunningCacheTtlMs = new PropertyConfigSwitcher(
				MetricsCronJobHolder.METRICS_CRON_JOBS_RUNNING_CACHE_TTL_MS);
	}

	@After
	public void tearDown() throws Exception
	{
		metricsCronJobsRunningCacheTtlMs.switchBackToDefault();
	}

	@Test
	public void shouldReturnTheeSameSizeOFRunningCronJobsList()
	{
		metricsCronJobHolder = Mockito.spy(new MetricsCronJobHolder(cronJobService, eventService));
		final Collection<String> cjList = metricsCronJobHolder.getRunningCronJobs();
		assertThat(cronJobService.getRunningOrRestartedCronJobs().size()).isEqualTo(cjList.size());
	}

	@Test
	public void shouldCallCronJobServiceOnlyWhenCacheIsExpired() throws InterruptedException
	{
		metricsCronJobsRunningCacheTtlMs.switchToValue("100");
		metricsCronJobHolder = Mockito.spy(new MetricsCronJobHolder(cronJobService, eventService));
		metricsCronJobHolder.getRunningCronJobs();
		metricsCronJobHolder.getRunningCronJobs();
		Mockito.verify(cronJobService, Mockito.times(1)).getRunningOrRestartedCronJobs();
		// ... second attempt
		Thread.sleep(100);
		metricsCronJobHolder.getRunningCronJobs();
		metricsCronJobHolder.getRunningCronJobs();
		metricsCronJobHolder.getRunningCronJobs();
		Mockito.verify(cronJobService, Mockito.times(2)).getRunningOrRestartedCronJobs();
	}

	@Test
	public void shouldUpdateCachedValues()
	{
		metricsCronJobHolder = Mockito.spy(new MetricsCronJobHolder(cronJobService, eventService));
		metricsCronJobHolder.cacheCronJobMetrics(Instant.now(), listCj);
		final Collection<String> cronJobRunning = metricsCronJobHolder.getRunningCronJobs();
		Mockito.doReturn(listCj).when(metricsCronJobHolder).getRunningCronJobs();

		assertThat(metricsCronJobHolder.getRunningCronJobs().size()).isEqualTo(listCj.size());
		assertThat(cronJobRunning).isEqualTo(listCj);
	}

	@Test
	public void shouldCatchException()
	{
		metricsCronJobHolder = Mockito.spy(new MetricsCronJobHolder(cronJobService, eventService));

		Mockito.when(cronJobService.getRunningOrRestartedCronJobs()).thenThrow(new SystemException("Returned result for running cron jobs is null "));

		final Collection<String> cronJobRunning = metricsCronJobHolder.getRunningCronJobs();
		assertThat(cronJobRunning).isEmpty();
	}

	@Test
	public void shouldCatchExceptionAndReturnCachedList() throws InterruptedException
	{
		metricsCronJobsRunningCacheTtlMs.switchToValue("1000");
		metricsCronJobHolder = Mockito.spy(new MetricsCronJobHolder(cronJobService, eventService));

		metricsCronJobHolder.cacheCronJobMetrics(Instant.now(), listCj);
		Collection<String> cronJobRunning = metricsCronJobHolder.getRunningCronJobs();

		assertThat(cronJobRunning).isEqualTo(listCj);

		Thread.sleep(1000);

		Mockito.when(cronJobService.getRunningOrRestartedCronJobs()).thenThrow(new SystemException("Returned result for running cron jobs is null "));

		cronJobRunning = metricsCronJobHolder.getRunningCronJobs();
		assertThat(cronJobRunning).isEqualTo(listCj);

	}
}
