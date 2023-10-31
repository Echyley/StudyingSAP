/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.task.impl.gateways.SchedulerStateGateway;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

/**
 * Integration tests for {@link SchedulerAuxiliaryTablesWatchdog}
 */
@IntegrationTest
public class SchedulerAuxiliaryTablesWatchdogTest extends ServicelayerBaseTest
{
	private static final Duration SCHEDULER_INTERVAL = Duration.ofMillis(100L);
	private static final long TIMEOUT_MS = 1000L;
	@Resource
	private AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory;
	private RuntimeConfigHolder runtimeConfigHolder;
	private TaskEngineParameters taskEngineParameters;
	private AutoCloseable mockito;
	private TestSchedulerAuxiliaryTablesWatchdog watchdog;
	private SchedulerStateGateway schedulerStateGatewaySpy;

	@Before
	public void setUp()
	{
		mockito = MockitoAnnotations.openMocks(this);

		runtimeConfigHolder = mock(RuntimeConfigHolder.class);
		doReturn(true).when(runtimeConfigHolder)
		              .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_WATCHDOG_ENABLED);
		doReturn(SCHEDULER_INTERVAL).when(runtimeConfigHolder)
		                            .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);

		taskEngineParameters = mock(TaskEngineParameters.class);
		doReturn(Registry.getCurrentTenantNoFallback()).when(taskEngineParameters).getTenant();

		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(auxiliaryTablesGatewayFactory);
		schedulerStateGatewaySpy = spy(auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		doReturn(schedulerStateGatewaySpy).when(spyGatewayFactory).getSchedulerStateGateway();
		watchdog = new TestSchedulerAuxiliaryTablesWatchdog(spyGatewayFactory);
	}

	@After
	public void tearDown() throws Exception
	{
		watchdog.destroy();
		mockito.close();
	}

	@Test
	public void shouldThrowExceptionWhenWatchdogNameIsNull()
	{
		shouldThrowExceptionWhenWatchdogNameIsInvalid(null);
	}

	@Test
	public void shouldThrowExceptionWhenWatchdogNameIsEmpty()
	{
		shouldThrowExceptionWhenWatchdogNameIsInvalid("");
	}

	@Test
	public void shouldThrowExceptionWhenWatchdogNameIsBlank()
	{
		shouldThrowExceptionWhenWatchdogNameIsInvalid(" ");
	}

	private void shouldThrowExceptionWhenWatchdogNameIsInvalid(final String name)
	{
		assertThatThrownBy(() -> new SchedulerAuxiliaryTablesWatchdog(name, auxiliaryTablesGatewayFactory))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("watchdog name cannot be null or a blank string");
	}

	@Test
	public void shouldThrowExceptionWhenRuntimeConfigHandlerIsNull()
	{
		assertThatThrownBy(() -> watchdog.startWatchdog(null, taskEngineParameters))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("non-null runtimeConfigHolder must be provided to watchdog");
	}

	@Test
	public void shouldThrowExceptionWhenTaskParametersIsNull()
	{
		assertThatThrownBy(() -> watchdog.startWatchdog(runtimeConfigHolder, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("non-null taskEngineParameters must be provided to watchdog");
	}

	@Test
	public void shouldExecuteAndStopTask() throws InterruptedException
	{
		// given
		final CountDownLatch runOperationLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			runOperationLatch.countDown();
			return invocation.callRealMethod();
		}).when(schedulerStateGatewaySpy).updateSchedulerRow(any(Duration.class));

		final CountDownLatch stopLatch = new CountDownLatch(1);
		watchdog.setStopLatch(stopLatch);
		// when
		try (final AuxiliaryTablesWatchdogTask task = watchdog.startWatchdog(runtimeConfigHolder, taskEngineParameters))
		{
			// then
			assertThat(task).as("watchdog task should be closable").isInstanceOf(AutoCloseable.class);
			assertThat(runOperationLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)).as("watchdog task should start").isTrue();
		}

		final ArgumentCaptor<Duration> checkIntervalCaptor = ArgumentCaptor.forClass(Duration.class);
		verify(schedulerStateGatewaySpy, atLeastOnce()).updateSchedulerRow(checkIntervalCaptor.capture());
		assertThat(checkIntervalCaptor.getAllValues())
				.as("check intervals of watchdog update operation should be less than scheduler interval")
				.allMatch(interval -> interval.compareTo(SCHEDULER_INTERVAL) < 0);
		assertThat(stopLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)).as("watchdog task should stop").isTrue();
	}

	@Test
	public void shouldRejectNextTaskExecutionIfPreviousStillInProgress() throws InterruptedException
	{
		// given
		final CountDownLatch runOperationLatch = new CountDownLatch(1);
		doAnswer(invocation -> {
			runOperationLatch.countDown();
			return invocation.callRealMethod();
		}).when(schedulerStateGatewaySpy).updateSchedulerRow(any(Duration.class));
		final CountDownLatch stopLatch = new CountDownLatch(1);
		watchdog.setStopLatch(stopLatch);

		// when
		try (final AuxiliaryTablesWatchdogTask ignored = watchdog.startWatchdog(runtimeConfigHolder, taskEngineParameters))
		{
			// then
			assertThat(runOperationLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)).as("watchdog task should start").isTrue();
			final Throwable throwable = catchThrowable(() -> watchdog.startWatchdog(runtimeConfigHolder, taskEngineParameters));
			assertThat(throwable).as("watchdog should reject next task if previous task is still in progress")
			                     .isNotNull()
			                     .isInstanceOf(IllegalStateException.class)
			                     .hasMessageEndingWith(
					                     "watchdog rejected next task due to previous task still in progress or watchdog destroyed");
		}
		verify(schedulerStateGatewaySpy, atLeastOnce()).updateSchedulerRow(any(Duration.class));
		assertThat(stopLatch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS)).as("first watchdog task should stop").isTrue();
		assertThat(watchdog.getRunTaskCounter()).as("only one task should run").isEqualTo(1);
		assertThat(watchdog.getStartTaskCounter()).as("there should be two attempts to start the task").isEqualTo(2);
	}

	@Test
	public void shouldRejectTaskExecutionIfWatchdogDestroyed() throws Exception
	{
		// when
		watchdog.destroy();
		// then
		final Throwable taskRejectedException = catchThrowable(
				() -> watchdog.startWatchdog(runtimeConfigHolder, taskEngineParameters));

		assertThat(taskRejectedException).as("destroyed watchdog should reject task")
		                                 .isInstanceOf(IllegalStateException.class)
		                                 .hasMessageEndingWith(
				                                 "watchdog rejected next task due to previous task still in progress or watchdog destroyed");

		verify(schedulerStateGatewaySpy, never()).updateSchedulerRow(any(Duration.class));
		assertThat(watchdog.getRunTaskCounter()).as("task should not run").isZero();
		assertThat(watchdog.getStartTaskCounter()).as("there should be one attempt to start the task").isEqualTo(1);
		verify(watchdog.getSpyTask(), never()).run();
	}

	@Test
	public void shouldNotExecuteTaskIfWatchdogIsDisabled()
	{
		// given
		doReturn(false).when(runtimeConfigHolder)
		               .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_WATCHDOG_ENABLED);
		shouldNotExecuteWatchdogTask();
	}

	@Test
	public void shouldNotExecuteTaskIfSchedulerIntervalIsZero()
	{
		// given
		doReturn(Duration.ZERO).when(runtimeConfigHolder)
		                       .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);
		shouldNotExecuteWatchdogTask();
	}

	@Test
	public void shouldNotExecuteTaskIfSchedulerIntervalIsNegative()
	{
		// given
		doReturn(Duration.ofMillis(-1L)).when(runtimeConfigHolder)
		                                .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);
		shouldNotExecuteWatchdogTask();
	}

	private void shouldNotExecuteWatchdogTask()
	{
		// when
		try (final AuxiliaryTablesWatchdogTask watchdogTask = watchdog.startWatchdog(runtimeConfigHolder, taskEngineParameters))
		{
			// then
			assertThat(watchdogTask).as("watchdog task should be closable")
			                        .isInstanceOf(AutoCloseable.class);
		}
		assertThat(watchdog.getRunTaskCounter()).as("task should not run").isZero();
		verify(watchdog.getSpyTask(), never()).run();
	}
}

