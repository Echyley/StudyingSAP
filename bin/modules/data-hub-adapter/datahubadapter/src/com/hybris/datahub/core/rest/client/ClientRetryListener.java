/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class ClientRetryListener implements RetryListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientRetryListener.class);

	private int initialInterval;
	private int multiplier;
	private int maxAttempts;
	private int maxInterval;

	@Override
	public <T, E extends Throwable> boolean open(final RetryContext retryContext, final RetryCallback<T, E> retryCallback)
	{
		return true;
	}

	@Override
	public <T, E extends Throwable> void close(final RetryContext retryContext, final RetryCallback<T, E> retryCallback, final Throwable throwable)
	{
		if (throwable != null)
		{
			LOGGER.error(throwable.getMessage(), throwable);
			if (throwable.getCause() != null)
			{
				LOGGER.error("Caused by: {}.", throwable.getCause().getMessage());
			}
		}
	}

	@Override
	public <T, E extends Throwable> void onError(final RetryContext retryContext, final RetryCallback<T, E> retryCallback, final Throwable throwable)
	{
		if (retryContext.getRetryCount() < maxAttempts-1)
		{
			LOGGER.warn("Data Hub Server is unavailable. The Data Hub Adapter will try again in {} seconds for retry attempt {} of {}.",
					getNextInterval(retryContext.getRetryCount()), retryContext.getRetryCount()+1, maxAttempts-1);
			LOGGER.warn(throwable.getMessage());
		}
	}

	private long getNextInterval(final int retryCount)
	{
		final long calculatedInterval = initialInterval * (long) Math.pow(multiplier, retryCount);
		return (maxInterval < calculatedInterval ? maxInterval : calculatedInterval) / 1000;
	}

	@Required
	public void setInitialInterval(final int initialInterval)
	{
		this.initialInterval = initialInterval;
	}

	@Required
	public void setMultiplier(final int multiplier)
	{
		this.multiplier = multiplier;
	}

	@Required
	public void setMaxAttempts(final int maxAttempts)
	{
		this.maxAttempts = maxAttempts;
	}

	@Required
	public void setMaxInterval(final int maxInterval)
	{
		this.maxInterval = maxInterval;
	}
}
