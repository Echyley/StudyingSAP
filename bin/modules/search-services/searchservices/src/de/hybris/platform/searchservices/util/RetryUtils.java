/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.util;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Retry utilities.
 */
public final class RetryUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(RetryUtils.class);

	private RetryUtils()
	{
	}

	public static <E extends Throwable> void waitForConditionOrElseThrow(final BooleanSupplier condition,
			final RetryConfiguration configuration, final Supplier<? extends E> exceptionSupplier) throws InterruptedException, E
	{
		waitForConditionOrElseThrow(condition::getAsBoolean, v -> v, configuration, value -> exceptionSupplier.get());
	}

	public static <T, E extends Throwable> T waitForConditionOrElseThrow(final Supplier<T> valueSupplier,
			final Predicate<T> condition, final RetryConfiguration configuration, final Function<T, ? extends E> exceptionSupplier)
			throws InterruptedException, E
	{

		final long initialTime = System.currentTimeMillis();

		if (configuration.getInitialDelay() != null)
		{
			sleep(configuration.getInitialDelay().toMillis());
		}

		T value = null;

		try
		{
			value = valueSupplier.get();
			if (condition.test(value))
			{
				return value;
			}
		}
		catch (final RuntimeException e)
		{
			LOG.warn(e.getMessage(), e);
		}

		final int maxAttempts = configuration.getMaxAttempts();
		final long timeout = configuration.getTimeout().toMillis();
		final double multiplier = configuration.getMultiplier() != null ? configuration.getMultiplier() : 1;

		int attempts = 1;
		long interval = configuration.getInterval().toMillis();

		while (attempts < maxAttempts && (System.currentTimeMillis() + interval - initialTime) <= timeout)
		{
			sleep(interval);

			try
			{
				value = valueSupplier.get();
				if (condition.test(value))
				{
					return value;
				}
			}
			catch (final RuntimeException e)
			{
				LOG.warn(e.getMessage(), e);
			}

			attempts = attempts + 1;
			interval = (long) (interval * multiplier);

			if (configuration.getMaxInterval() != null)
			{
				interval = Math.min(interval, configuration.getMaxInterval().toMillis());
			}
		}

		throw exceptionSupplier.apply(value);
	}

	private static void sleep(final long interval) throws InterruptedException
	{
		Thread.sleep(interval);
	}
}
