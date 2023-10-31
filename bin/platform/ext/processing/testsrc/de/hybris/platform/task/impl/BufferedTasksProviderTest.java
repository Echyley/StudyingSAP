/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl;

import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.stubbing.Answer;

import com.codahale.metrics.MetricRegistry;

@IntegrationTest
public class BufferedTasksProviderTest extends ServicelayerBaseTest
{
	MockitoSession mockito;

	@Mock
	private MetricRegistry metricRegistry;

	@Mock
	private MockRuntimeConfigHolder configHolder;

	@Mock
	private TasksProvider tasksProvider;

	@Before
	public void setup()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
	}

	@After
	public void tearDown()
	{
		mockito.finishMocking();
	}

	private void whenGetTasksToScheduleReturnNCopies(final int n)
	{
		final AtomicInteger pollCounter = new AtomicInteger();

		final Answer<List<TasksProvider.VersionPK>> answer = invocation -> nCopies(n,
				pollCounter.incrementAndGet())
				.stream()
				.map(i -> new TasksProvider.VersionPK(PK.fromLong(i), 1))
				.collect(Collectors.toList());

		when(tasksProvider.getTasksToSchedule(any(), isNull(), anyInt())).thenAnswer(answer);
	}

	@Test
	public void shouldUseDefaultGaugeName()
	{
		final BufferedTasksProvider bufferedTasksProvider = new BufferedTasksProvider(tasksProvider, metricRegistry);

		bufferedTasksProvider.getTasksToSchedule(configHolder, null, 10);

		verify(metricRegistry).register(endsWith("pooling.buffer.size"), any());
	}

	@Test
	public void shouldUseProviderGaugeName()
	{
		final String metricGaugeName = RandomStringUtils.randomAlphabetic(20);
		final BufferedTasksProvider bufferedTasksProvider = new BufferedTasksProvider(tasksProvider, metricRegistry,
				metricGaugeName, false);

		bufferedTasksProvider.getTasksToSchedule(configHolder, null, 10);

		verify(metricRegistry).register(endsWith(metricGaugeName), any());
	}

	@Test
	public void shouldNotResetBufferIfTimeoutNotReached() throws InterruptedException
	{
		final int itemsToSchedule = 20;
		final int itemsToReturnOnReset = 30;

		whenGetTasksToScheduleReturnNCopies(itemsToReturnOnReset);
		when(configHolder.getProperty(BufferedTasksProvider.POLLING_MIN_INTERVAL)).thenReturn(Duration.ofSeconds(2));

		final BufferedTasksProvider bufferedTasksProvider = new BufferedTasksProvider(tasksProvider, metricRegistry);

		final List<TasksProvider.VersionPK> poll1 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(300);
		final List<TasksProvider.VersionPK> poll2 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(300);
		final List<TasksProvider.VersionPK> poll3 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);

		assertThat(poll1).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsOnly(PK.fromLong(1));
		assertThat(poll2).hasSize(itemsToReturnOnReset - itemsToSchedule)
		                 .extracting(versionPK -> versionPK.pk)
		                 .containsOnly(PK.fromLong(1));
		assertThat(poll3).isEmpty();

		verify(tasksProvider).getTasksToSchedule(any(RuntimeConfigHolder.class), isNull(), anyInt());
	}

	@Test
	public void shouldResetBufferIfTimeoutReached() throws InterruptedException
	{
		final int itemsToSchedule = 20;
		final int itemsToReturnOnReset = 30;
		whenGetTasksToScheduleReturnNCopies(itemsToReturnOnReset);
		when(configHolder.getProperty(BufferedTasksProvider.POLLING_MIN_INTERVAL)).thenReturn(Duration.ofSeconds(1));

		final BufferedTasksProvider bufferedTasksProvider = new BufferedTasksProvider(tasksProvider, metricRegistry);

		final List<TasksProvider.VersionPK> poll1 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(1200);
		final List<TasksProvider.VersionPK> poll2 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);

		assertThat(poll1).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsOnly(PK.fromLong(1));
		assertThat(poll2).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsOnly(PK.fromLong(2));

		verify(tasksProvider, times(2)).getTasksToSchedule(any(RuntimeConfigHolder.class), isNull(), anyInt());
	}

	@Test
	public void shouldNotResetBufferIfTimeoutNotReachedWithResetOnEmptyBuffer() throws InterruptedException
	{
		final int itemsToSchedule = 20;
		final int itemsToReturnOnReset = 30;

		whenGetTasksToScheduleReturnNCopies(itemsToReturnOnReset);
		when(configHolder.getProperty(BufferedTasksProvider.POLLING_MIN_INTERVAL)).thenReturn(Duration.ofSeconds(2));

		final BufferedTasksProvider bufferedTasksProvider = new BufferedTasksProvider(tasksProvider, metricRegistry, null, true);

		final List<TasksProvider.VersionPK> poll1 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(200);
		final List<TasksProvider.VersionPK> poll2 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(200);
		final List<TasksProvider.VersionPK> poll3 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);

		assertThat(poll1).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsOnly(PK.fromLong(1));
		assertThat(poll2).hasSize(itemsToReturnOnReset - itemsToSchedule)
		                 .extracting(versionPK -> versionPK.pk)
		                 .containsOnly(PK.fromLong(1));
		assertThat(poll3).isEmpty();

		verify(tasksProvider).getTasksToSchedule(any(RuntimeConfigHolder.class), isNull(), anyInt());
	}

	@Test
	public void shouldNotResetBufferIfTimeoutReachedButStillItemsInBufferWithResetOnEmptyBuffer() throws InterruptedException
	{
		final int itemsToSchedule = 20;
		final int itemsToReturnOnReset = 50;
		whenGetTasksToScheduleReturnNCopies(itemsToReturnOnReset);
		when(configHolder.getProperty(BufferedTasksProvider.POLLING_MIN_INTERVAL)).thenReturn(Duration.ofSeconds(1));

		final BufferedTasksProvider bufferedTasksProvider = new BufferedTasksProvider(tasksProvider, metricRegistry, null, true);

		final List<TasksProvider.VersionPK> poll1 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(1200);
		final List<TasksProvider.VersionPK> poll2 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);
		Thread.sleep(1200);
		final List<TasksProvider.VersionPK> poll3 = bufferedTasksProvider.getTasksToSchedule(configHolder, null, itemsToSchedule);

		assertThat(poll1).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsOnly(PK.fromLong(1));
		assertThat(poll2).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsOnly(PK.fromLong(1));

		final LinkedList<PK> expectedPoll3 = new LinkedList<>(nCopies(10, PK.fromLong(1)));
		expectedPoll3.addAll(nCopies(10, PK.fromLong(2)));

		assertThat(poll3).hasSize(itemsToSchedule).extracting(versionPK -> versionPK.pk).containsExactlyElementsOf(expectedPoll3);

		verify(tasksProvider, times(2)).getTasksToSchedule(any(RuntimeConfigHolder.class), isNull(), anyInt());
	}

	public static class MockRuntimeConfigHolder extends RuntimeConfigHolder
	{
		@Override
		public <T> T getProperty(final TaskEngineProperty<T> parameter)
		{
			return super.getProperty(parameter);
		}


	}
}
