/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service;

import de.hybris.platform.cronjob.model.CronJobModel;


/**
 * Implementations of this interface are responsible for creating progress trackers fot corn jobs.
 */
public interface SnCronJobProgressTrackerFactory
{
	/**
	 * Creates a new progress tracker for a cron job.
	 *
	 * @param cronJob
	 *           - the cron job
	 *
	 * @return the progress tracker
	 */
	SnCloseableProgressTracker createProgressTracker(final CronJobModel cronJob);
}
