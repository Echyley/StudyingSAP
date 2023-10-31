/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

import java.time.Duration;
import java.util.function.Supplier;

import org.slf4j.Logger;

/**
 * A test condition that is evaluated repeatedly during a reasonable period of time. It's intended to assert asynchronous changes.
 * Default evaluation period is 10 seconds and the condition is evaluated approximately every 500 milliseconds.
 */
public class EventualCondition
{
	private final static Logger LOG = Log.getLogger(EventualCondition.class);
	private static final Duration DEFAULT_DURATION = Duration.ofSeconds(10);

	private final long pollingIntervalMillis;
	private Duration expectationPeriod;


	private EventualCondition()
	{
		pollingIntervalMillis = 500;
		expectationPeriod = DEFAULT_DURATION;
	}

	/**
	 * Creates evaluation condition with default evaluation period and interval.
	 *
	 * @return a condition that is evaluated about every 500 milliseconds within 10 seconds period.
	 */
	public static EventualCondition eventualCondition()
	{
		return new EventualCondition();
	}

	/**
	 * Specifies duration period for the following eventual condition.
	 *
	 * @param duration duration to applie for the eventual conditions. Default value, if not specified, is 10 seconds.
	 * @return an {@code EventualCondition} with the duration specified.
	 */
	public EventualCondition within(final Duration duration)
	{
		expectationPeriod = duration;
		return this;
	}

	/**
	 * Evaluates the conditions represented by the code body.  For example, let's say we have
	 * a class Job that changes its state asynchronously. In this case this method can be used like this:
	 * <pre>
	 *       Job job = new Job
	 *
	 *       // start the job
	 *       job.start();
	 *
	 *       // assert the asynchronous condition that the job eventually finishes.
	 *       eventualCondition().expect(() -> assertEquals("finished", job.getState());
	 * </pre>
	 * In other words, this method waits withing the time period used by this condition until the {@code body} stops throwing
	 * exceptions. As soon, as the body did not throw an exception on its subsequent execution, the method returns. If the time
	 * period has been exceeded and the body still keeps throwing the exception, that exception will be propagated.
	 *
	 * @param body a code to evaluate. It should throw an exception when the condition is not met.
	 */
	public void expect(final Runnable body)
	{
		LOG.debug("Expecting condition within {}", expectationPeriod);
		final long deadLine = System.currentTimeMillis() + expectationPeriod.toMillis();
		boolean waiting = true;
		while (waiting)
		{
			try
			{
				body.run();
				waiting = false;
			}
			catch (final Throwable ex)
			{
				final long now = System.currentTimeMillis();
				final long remainingTime = deadLine - now;
				final long timeout = Math.min(pollingIntervalMillis, remainingTime);
				if (timeout <= 0)
				{
					throw ex;
				}
				LOG.trace("Will fail in {} millisec: {}", remainingTime, ex.getMessage());
				pause(timeout);
			}
		}
	}

	/**
	 * Evaluates whether the code body does not throw exceptions within the duration used by this condition.  For example,
	 * let's say we have a class Job that changes its state asynchronously and we want to verify that it remains in the RUNNING
	 * state for a certain period of time. In this case this method can be used like this:
	 * <pre>
	 *       Job job = new Job
	 *
	 *       // start the job
	 *       job.start();
	 *
	 *       // assert the asynchronous condition that the job eventually finishes.
	 *       eventualCondition().within(Duration.ofSeconds(3)).retains(() -> assertEquals("running", job.getState());
	 * </pre>
	 * If we use {@link #expect(Runnable)} method here, then the condition will pass on first evaluation and we immediately leave
	 * the method without waiting to check that the condition still passes at the end of the desired time frame.
	 *
	 * @param body a code to evaluate. It should throw an exception when the condition is not met.
	 */
	public void retains(final Runnable body)
	{
		LOG.debug("Checking that condition is retained within {}", expectationPeriod);
		final long deadLine = System.currentTimeMillis() + expectationPeriod.toMillis();
		long timeout = expectationPeriod.toMillis();
		do
		{
			pause(timeout);
			body.run();
			timeout = deadLine - System.currentTimeMillis();
		} while (timeout > 0);
	}

	/**
	 * Waits for the specified condition to turn {@code true} within the specified time frame. For example,
	 * let's say we have a class Job that changes its state asynchronously and we want to wait until the job finishes.
	 * This method can be used like this to achieve this scenario:
	 * <pre>
	 *       Job job = new Job
	 *
	 *       // start the job
	 *       job.start();
	 *
	 *       // assert the asynchronous condition that the job eventually finishes.
	 *       eventualCondition().within(Duration.ofSeconds(3)).waitFor(() -> job.getState() == CronJobStatus.FINISHED);
	 * </pre>
	 *
	 * @param condition a condition to wait for
	 * @see #within(Duration)
	 */
	public void waitFor(final Supplier<Boolean> condition)
	{
		LOG.debug("Waiting for a condition to turn true within {}", expectationPeriod);
		long timeNow = System.currentTimeMillis();
		final long timeout = timeNow + expectationPeriod.toMillis();
		while(!condition.get() && timeNow < timeout)
		{
			pause(pollingIntervalMillis);
			timeNow = System.currentTimeMillis();
		}
	}

	/**
	 * Pauses current thread execution for at least the specified timeout period. The thread may be paused for longer. The only
	 * condition when the thread won't be paused for the timeout period, is if it is interrupted.
	 *
	 * @param timeout number of milliseconds to sleep.
	 * @see Thread#sleep(long)
	 */
	public static void pause(final long timeout)
	{
		try
		{
			Thread.sleep(timeout);
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
