/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for {@link SchedulerOperationTimeoutLogger}
 */
@UnitTest
public class SchedulerOperationTimeoutLoggerTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerOperationTimeoutLoggerTest.class);

	@Test
	public void shouldTimeoutTest()
	{
		// given
		final Instant startTime = Instant.now().minusMillis(11L);
		final RuntimeConfigHolder runtimeConfigHolder = mock(RuntimeConfigHolder.class);
		doReturn(Duration.ofMillis(10L)).when(runtimeConfigHolder)
		                                .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);
		final SchedulerOperationTimeoutLogger timeoutLogger = new SchedulerOperationTimeoutLogger(runtimeConfigHolder, startTime,
				new TestTimeoutConsumer());

		//when
		final boolean timeout = timeoutLogger.isTimeout();

		// then
		assertThat(timeout).as("It should timeout").isTrue();
		verify(runtimeConfigHolder).getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);
	}

	@Test
	public void shouldNotTimeoutWhenSchedulerIntervalIsZero()
	{
		final Instant startTime = Instant.now().minusMillis(11L);
		final RuntimeConfigHolder runtimeConfigHolder = mock(RuntimeConfigHolder.class);
		doReturn(Duration.ZERO).when(runtimeConfigHolder)
		                       .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);

		shouldNotTimeout(runtimeConfigHolder, startTime);
	}

	@Test
	public void shouldNotTimeoutWhenSchedulerIntervalIsNegative()
	{
		final Instant startTime = Instant.now().minusMillis(11L);
		final RuntimeConfigHolder runtimeConfigHolder = mock(RuntimeConfigHolder.class);
		doReturn(Duration.ofMillis(-10L)).when(runtimeConfigHolder)
		                                 .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);

		shouldNotTimeout(runtimeConfigHolder, startTime);
	}

	@Test
	public void shouldNotTimeoutWhenSchedulerIntervalIsNotOutdated()
	{
		final Instant startTime = Instant.now();
		final RuntimeConfigHolder runtimeConfigHolder = mock(RuntimeConfigHolder.class);
		doReturn(Duration.ofMillis(100L)).when(runtimeConfigHolder)
		                                 .getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);

		shouldNotTimeout(runtimeConfigHolder, startTime);
	}

	private void shouldNotTimeout(final RuntimeConfigHolder runtimeConfigHolder, final Instant startTime)
	{
		// given
		final SchedulerOperationTimeoutLogger timeoutLogger = new SchedulerOperationTimeoutLogger(runtimeConfigHolder, startTime,
				new TestTimeoutConsumer());

		//when
		final boolean timeout = timeoutLogger.isTimeout();

		// then
		assertThat(timeout).as("It shouldn't timeout").isFalse();
		verify(runtimeConfigHolder).getProperty(AuxiliaryTablesBasedTaskProvider.Params.SCHEDULER_INTERVAL);
	}

	@Test
	public void shouldConsumeTimeoutOnceTest()
	{
		// given
		final Duration expectedMinimumElapsedTime = Duration.ofMillis(100L);
		final Consumer<Duration> timeoutConsumer = spy(new TestTimeoutConsumer());
		final Instant startTime = Instant.now().minus(expectedMinimumElapsedTime);
		final RuntimeConfigHolder runtimeConfigHolder = mock(RuntimeConfigHolder.class);
		final SchedulerOperationTimeoutLogger timeoutLogger = new SchedulerOperationTimeoutLogger(runtimeConfigHolder, startTime,
				timeoutConsumer);

		//when
		timeoutLogger.logTimeoutOnce();
		timeoutLogger.logTimeoutOnce();

		// then
		final ArgumentCaptor<Duration> elapsedTimeCaptor = ArgumentCaptor.forClass(Duration.class);
		verify(timeoutConsumer).accept(elapsedTimeCaptor.capture());
		assertThat(elapsedTimeCaptor.getValue()).as("Elapsed time should be valid")
		                                        .isGreaterThanOrEqualTo(expectedMinimumElapsedTime);
	}

	private static class TestTimeoutConsumer implements Consumer<Duration>
	{
		@Override
		public void accept(final Duration elapsedTime)
		{
			LOGGER.debug(
					"scheduler operation is taking too long, elapsed time: {}", elapsedTime);
		}
	}
}
