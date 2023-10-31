/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service;

/**
 * Tracks progress for long running operations. Implementations of this class must be thread-safe.
 */
public interface SnProgressTracker
{
	/**
	 * Returns the current progress.
	 *
	 * @return the current progress
	 */
	Double getProgress();

	/**
	 * Sets the current progress.
	 *
	 * @param progress
	 *           - the current progress
	 */

	void setProgress(final Double value);

	/**
	 * Requests cancellation.
	 */
	void requestCancellation();

	/**
	 * Checks whether cancellation was requested or not.
	 *
	 * @return <code>true</code> if cancellation was requested, <code>false</code> otherwise
	 */
	boolean isCancellationRequested();
}
