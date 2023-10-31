/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.outboundsync.events.OutboundSyncEvent;

import javax.validation.constraints.NotNull;

/**
 * An object controlling and representing state of a single outbound sync job. Implementations make decisions about whether the
 * job is finished and what's the result of the job execution.
 */
public interface OutboundSyncJob
{
	/**
	 * Retrieves identifier of this outbound sync job. The default implementation in this interface always returns
	 * {@code PK.fromLong(0)}, so this method must be overridden by the implementation to provide correct behavior.
	 *
	 * @return identifier of this job, i.e. a value that differs one outbound sync job from the other: for example, a customer
	 * synchronization from product synchronization. Therefore, this ID should persist for different executions of the same job.
	 */
	default @NotNull PK getId()
	{
		return PK.fromLong(0);
	}

	/**
	 * Applies an event and recalculates state of the context outbound sync job.
	 *
	 * @param event a job state changing event.
	 */
	<T extends OutboundSyncEvent> void applyEvent(@NotNull T event);

	/**
	 * Retrieves the current state of the context job.
	 *
	 * @return current state of the context outbound sync job.
	 */
	@NotNull OutboundSyncState getCurrentState();

	/**
	 * Registers a state observer.
	 *
	 * @param observer an observer that is going to be notified whenever the job state changes.
	 */
	void addStateObserver(@NotNull OutboundSyncStateObserver observer);

	/**
	 * Retrieves state of the context job after the last event was applied.
	 *
	 * @return final state of the context outbound sync job.
	 */
	default @NotNull OutboundSyncState getFinalState()
	{
		return OutboundSyncStateBuilder.initialOutboundSyncState().build();
	}

	/**
	 * Forces this job to stop, so that it changes its status to FINISHED, even if its state does not show that all expected
	 * items have been processed. This means that changes processed by this job may continue being synchronized, but result of
	 * their synchronization may be ignored and not reported in the job's {@link de.hybris.platform.servicelayer.cronjob.PerformResult}
	 * <p>Default implementation in this interface does nothing, meaning the job won't be stopped by default. Implementations of
	 * this interface must implement the {@code stop()} logic.</p>
	 */
	default void stop()
	{
	}
}
