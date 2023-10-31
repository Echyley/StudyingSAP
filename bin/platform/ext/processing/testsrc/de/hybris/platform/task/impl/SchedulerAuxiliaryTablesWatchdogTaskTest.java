/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.suspend.SystemIsSuspendedException;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.time.Duration;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

/**
 * Integration tests for {@link SchedulerAuxiliaryTablesWatchdogTask}
 */
@IntegrationTest
public class SchedulerAuxiliaryTablesWatchdogTaskTest extends ServicelayerBaseTest
{
	private static final long STOP_TIMEOUT_MS = 100L;
	private static final long START_TIMEOUT_MS = 100L;
	private static final String TEST_TASK_NAME = "watchdog-test-task";

	private Tenant tenant;
	private AutoCloseable mockito;
	private ExecutorService singleThreadExecutor;

	@Before
	public void setUp()
	{
		mockito = MockitoAnnotations.openMocks(this);
		tenant = Registry.getCurrentTenantNoFallback();
		singleThreadExecutor = Executors.newSingleThreadExecutor();
	}

	@After
	public void tearDown() throws Exception
	{
		singleThreadExecutor.shutdownNow();
		mockito.close();
	}

	private void createTaskTest(final String expectedName, final Tenant tenant)
	{
		try (final SchedulerAuxiliaryTablesWatchdogTask testAuxiliaryTablesWatchdogTask = new SchedulerAuxiliaryTablesWatchdogTask(
				expectedName, tenant, () -> {
		}, () -> Duration.ofSeconds(1L)))
		{
			assertThat(testAuxiliaryTablesWatchdogTask.getTaskName()).isEqualTo(expectedName);
		}
	}

	@Test
	public void shouldThrowExceptionWhenNameIsNull()
	{
		shouldThrowExceptionWhenNameIsInvalid(null);
	}

	@Test
	public void shouldThrowExceptionWhenNameIsEmpty()
	{
		shouldThrowExceptionWhenNameIsInvalid("");
	}

	@Test
	public void shouldThrowExceptionWhenNameIsBlank()
	{
		shouldThrowExceptionWhenNameIsInvalid(" ");
	}

	private void shouldThrowExceptionWhenNameIsInvalid(final String taskName)
	{
		assertThatThrownBy(() -> createTaskTest(taskName, tenant))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("watchdog task name cannot be null or a blank string");
	}

	@Test
	public void shouldReturnNotBlankName()
	{
		createTaskTest(TEST_TASK_NAME, tenant);
	}

	@Test
	public void shouldThrowExceptionWhenTenantIsNull()
	{
		assertThatThrownBy(() -> createTaskTest(TEST_TASK_NAME, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("non-null tenant must be provided");
	}

	@Test
	public void shouldStopWatchdogTaskIfCalculatedWaitingTimeIsNull() throws InterruptedException
	{
		shouldStopWatchdogTaskIfCalculatedWaitingTimeIsInvalid(null);
	}

	@Test
	public void shouldStopWatchdogTaskIfCalculatedWaitingTimeIsZero() throws InterruptedException
	{
		shouldStopWatchdogTaskIfCalculatedWaitingTimeIsInvalid(Duration.ZERO);
	}

	@Test
	public void shouldStopWatchdogTaskIfCalculatedWaitingTimeIsNegative() throws InterruptedException
	{
		shouldStopWatchdogTaskIfCalculatedWaitingTimeIsInvalid(Duration.ofMillis(-1L));
	}

	public void shouldStopWatchdogTaskIfCalculatedWaitingTimeIsInvalid(final Duration waitingTime) throws InterruptedException
	{
		// given
		final Runnable watchdogOperationMock = mock(Runnable.class);
		final SchedulerAuxiliaryTablesWatchdogTask spyTestTask = spy(new SchedulerAuxiliaryTablesWatchdogTask(
				TEST_TASK_NAME, tenant, watchdogOperationMock, () -> waitingTime));

		final CountDownLatch stopLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			invocation.callRealMethod();
			stopLatch.countDown();
			return null;
		}).when(spyTestTask).run();

		// when
		singleThreadExecutor.execute(spyTestTask);
		final boolean taskStopped = stopLatch.await(STOP_TIMEOUT_MS, TimeUnit.MILLISECONDS);

		// then
		assertThat(taskStopped).as("Watchdog task should stop").isTrue();
		verify(spyTestTask).run();
		verify(spyTestTask).isStopping(waitingTime);
		verify(watchdogOperationMock, never()).run();
	}

	@Test
	public void shouldCalculateWaitingTimeInRuntime() throws InterruptedException
	{
		// given
		final Runnable watchdogOperationMock = mock(Runnable.class);
		final List<Duration> waitingTimes = Stream.of(20L, 25L, 10L, 10000L)
		                                          .map(Duration::ofMillis)
		                                          .toList();
		final ListIterator<Duration> waitingTimesIterator = waitingTimes.listIterator();
		final Supplier<Duration> waitingTimeSupplier = () -> waitingTimesIterator.next();

		final SchedulerAuxiliaryTablesWatchdogTask spyTestTask = spy(new SchedulerAuxiliaryTablesWatchdogTask(
				TEST_TASK_NAME, tenant, watchdogOperationMock, waitingTimeSupplier));

		final int expectedOperationCount = waitingTimes.size() - 1;

		final CountDownLatch calculationLatch = new CountDownLatch(expectedOperationCount);
		doAnswer(invocation -> {
			calculationLatch.countDown();
			return null;
		}).when(watchdogOperationMock).run();

		final CountDownLatch stopLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			invocation.callRealMethod();
			stopLatch.countDown();
			return null;
		}).when(spyTestTask).run();

		// when
		singleThreadExecutor.execute(spyTestTask);
		final boolean calculated = calculationLatch.await(1L, TimeUnit.SECONDS);
		spyTestTask.close();
		final boolean taskStopped = stopLatch.await(STOP_TIMEOUT_MS, TimeUnit.MILLISECONDS);

		// then
		verify(spyTestTask).run();
		assertThat(calculated).as("watchdog task should calculate waiting time specified number of times").isTrue();
		assertThat(taskStopped).as("watchdog task should stop").isTrue();
		verify(watchdogOperationMock, times(expectedOperationCount)).run();
		final ArgumentCaptor<Duration> waitingTimeArgumentCaptor = ArgumentCaptor.forClass(Duration.class);
		verify(spyTestTask, times(waitingTimes.size())).isStopping(waitingTimeArgumentCaptor.capture());
		assertThat(waitingTimeArgumentCaptor.getAllValues()).containsExactlyElementsOf(waitingTimes);
	}

	@Test
	public void shouldTerminateRunningWhenStoppedBeforeStart() throws InterruptedException
	{
		// given
		final Duration expectedWaitingTime = Duration.ofMillis(5L);
		final Runnable watchdogOperationMock = mock(Runnable.class);
		final SchedulerAuxiliaryTablesWatchdogTask spyTestTask = spy(new SchedulerAuxiliaryTablesWatchdogTask(
				TEST_TASK_NAME, tenant, watchdogOperationMock, () -> expectedWaitingTime));

		final CountDownLatch stopLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			invocation.callRealMethod();
			stopLatch.countDown();
			return null;
		}).when(spyTestTask).run();

		// when
		spyTestTask.close();
		singleThreadExecutor.execute(spyTestTask);
		final boolean taskStopped = stopLatch.await(STOP_TIMEOUT_MS, TimeUnit.MILLISECONDS);

		// then
		assertThat(taskStopped).as("watchdog task should stop on first check").isTrue();
		verify(spyTestTask).run();
		verify(watchdogOperationMock, never()).run();
		final ArgumentCaptor<Duration> waitingTimeArgumentCaptor = ArgumentCaptor.forClass(Duration.class);
		verify(spyTestTask).isStopping(waitingTimeArgumentCaptor.capture());
		assertThat(waitingTimeArgumentCaptor.getValue()).isEqualTo(expectedWaitingTime);
	}

	@Test
	public void shouldNotAllowToExecuteTheSameTaskAtTheSameTime() throws InterruptedException
	{
		// given
		final Runnable operation = mock(Runnable.class);
		final Duration expectedWaitingTime = Duration.ofMillis(10L);
		final SchedulerAuxiliaryTablesWatchdogTask spyTask = spy(new SchedulerAuxiliaryTablesWatchdogTask(
				TEST_TASK_NAME, tenant, operation, () -> expectedWaitingTime));

		final CountDownLatch startLatch = new CountDownLatch(2);
		final CountDownLatch terminateLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			startLatch.countDown();
			invocation.callRealMethod();
			terminateLatch.countDown();
			return null;
		}).when(spyTask).run();

		final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);

		try
		{
			// when
			fixedThreadPool.execute(spyTask);
			fixedThreadPool.execute(spyTask);

			final boolean tasksStarted = startLatch.await(START_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			final boolean tasksTerminated = terminateLatch.await(STOP_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			spyTask.close();

			// then
			assertThat(tasksStarted).as("watchdog tasks should start").isTrue();
			assertThat(tasksTerminated).as("next watchdog tasks should be terminated").isTrue();
			verify(spyTask, times(2)).run();
			final ArgumentCaptor<Duration> waitingTimeArgumentCaptor = ArgumentCaptor.forClass(Duration.class);
			verify(spyTask, timeout(1000L).atLeastOnce()).isStopping(waitingTimeArgumentCaptor.capture());
			assertThat(waitingTimeArgumentCaptor.getValue()).isEqualTo(expectedWaitingTime);
		}
		finally
		{
			fixedThreadPool.shutdownNow();
		}
	}

	@Test
	public void shouldContinueRunningOnSystemSuspend() throws InterruptedException
	{
		shouldContinueRunningOnRuntimeException(SystemIsSuspendedException.class);
	}

	@Test
	public void shouldContinueRunningOnUnexpectedRuntimeException() throws InterruptedException
	{
		shouldContinueRunningOnRuntimeException(RuntimeException.class);
	}

	private void shouldContinueRunningOnRuntimeException(final Class<? extends RuntimeException> runtimeExceptionClass)
			throws InterruptedException
	{
		// given
		final Runnable watchdogOperationMock = mock(Runnable.class);
		doThrow(runtimeExceptionClass).when(watchdogOperationMock).run();
		final int expectedCalculationCount = 2;
		final long calculationTimeoutMs = 100L;
		final CountDownLatch calculationLatch = new CountDownLatch(expectedCalculationCount);
		final Supplier<Duration> waitingTimeSupplier = () -> {
			calculationLatch.countDown();
			return Duration.ofMillis(10L);
		};
		final SchedulerAuxiliaryTablesWatchdogTask spyTestTask = spy(new SchedulerAuxiliaryTablesWatchdogTask(
				TEST_TASK_NAME, tenant, watchdogOperationMock, waitingTimeSupplier));

		final CountDownLatch stopLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			invocation.callRealMethod();
			stopLatch.countDown();
			return null;
		}).when(spyTestTask).run();

		// when
		singleThreadExecutor.execute(spyTestTask);
		final boolean calculated = calculationLatch.await(calculationTimeoutMs, TimeUnit.MILLISECONDS);
		spyTestTask.close();
		final boolean tasksStopped = stopLatch.await(STOP_TIMEOUT_MS, TimeUnit.MILLISECONDS);

		// then
		assertThat(calculated).as("watchdog tasks should calculate wait time %s times", expectedCalculationCount).isTrue();
		assertThat(tasksStopped).as("watchdog task should stop").isTrue();
		verify(watchdogOperationMock, atLeast(expectedCalculationCount - 1)).run();
	}

	@Test
	public void shouldTerminateOnInterruptedException() throws InterruptedException
	{
		// given
		final long runOperationTimeoutMs = 1000L;
		final Duration expectedWaitingTime = Duration.ofMillis(10L);
		final Runnable operation = mock(Runnable.class);
		final SchedulerAuxiliaryTablesWatchdogTask spyTask = spy(new SchedulerAuxiliaryTablesWatchdogTask(
				TEST_TASK_NAME, tenant, operation, () -> expectedWaitingTime));

		final CountDownLatch runOperationLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			runOperationLatch.countDown();
			return null;
		}).when(operation).run();

		final CountDownLatch stopLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			invocation.callRealMethod();
			stopLatch.countDown();
			return null;
		}).when(spyTask).run();
		final Thread taskThread = new Thread(spyTask);

		// when
		try (spyTask)
		{
			taskThread.start();
			final boolean operationExecuted = runOperationLatch.await(runOperationTimeoutMs, TimeUnit.MILLISECONDS);
			taskThread.interrupt();
			final boolean tasksStopped = stopLatch.await(STOP_TIMEOUT_MS, TimeUnit.MILLISECONDS);

			// then
			assertThat(operationExecuted).as("watchdog task should execute operation").isTrue();
			assertThat(tasksStopped).as("watchdog task should terminate when thread interrupted").isTrue();
			verify(operation, atLeastOnce()).run();
			verify(spyTask, atLeastOnce()).isStopping(expectedWaitingTime);
			verify(spyTask, never()).close();
		}
	}
}
