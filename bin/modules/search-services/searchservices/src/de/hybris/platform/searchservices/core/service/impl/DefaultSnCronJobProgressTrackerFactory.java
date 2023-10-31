/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;

import de.hybris.platform.cronjob.jalo.CronJobProgressTracker;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.searchservices.constants.SearchservicesConstants;
import de.hybris.platform.searchservices.core.service.SnCloseableProgressTracker;
import de.hybris.platform.searchservices.core.service.SnCronJobProgressTrackerFactory;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SnCronJobProgressTrackerFactory}.
 */
public class DefaultSnCronJobProgressTrackerFactory implements SnCronJobProgressTrackerFactory
{
	private ModelService modelService;
	private CronJobService cronJobService;

	@Override
	public SnCloseableProgressTracker createProgressTracker(final CronJobModel cronJob)
	{
		return new SnCronJobProgressTracker(cronJob);
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	@Required
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	protected class SnCronJobProgressTracker implements SnCloseableProgressTracker
	{
		protected final String cronJobCode;
		protected final CronJobProgressTracker cronJobProgressTracker;

		protected SnCronJobProgressTracker(final CronJobModel cronJob)
		{
			this.cronJobCode = cronJob.getCode();

			this.cronJobProgressTracker = new CronJobProgressTracker(modelService.getSource(cronJob));
			this.cronJobProgressTracker.setProgress(SearchservicesConstants.MIN_PROGRESS_VALUE);
		}

		@Override
		public Double getProgress()
		{
			final Double value = cronJobProgressTracker.getProgress();
			return value != null ? value : 0;
		}

		@Override
		public void setProgress(final Double value)
		{
			cronJobProgressTracker.setProgress(value);
		}

		@Override
		public void requestCancellation()
		{
			final CronJobModel cronJob = loadCronJob();
			cronJobService.requestAbortCronJob(cronJob);
		}

		@Override
		public boolean isCancellationRequested()
		{
			final CronJobModel cronJob = loadCronJob();
			return BooleanUtils.isTrue(cronJob.getRequestAbort());
		}

		@Override
		public void close() throws IOException
		{
			cronJobProgressTracker.setProgress(SearchservicesConstants.MAX_PROGRESS_VALUE);
			cronJobProgressTracker.close();
		}

		protected CronJobModel loadCronJob()
		{
			final CronJobModel cronJob = cronJobService.getCronJob(cronJobCode);
			modelService.refresh(cronJob);

			return cronJob;
		}
	}
}
