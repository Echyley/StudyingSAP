/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.searchservices.core.service.SnCloseableProgressTracker;
import de.hybris.platform.searchservices.core.service.SnCronJobProgressTrackerFactory;
import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.dao.SnIndexerCronJobDao;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerService;
import de.hybris.platform.searchservices.model.AbstractSnIndexerCronJobModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link JobPerformable} for running indexer operations.
 */
public abstract class AbstractSnIndexerJob<T extends AbstractSnIndexerCronJobModel> extends AbstractJobPerformable<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSnIndexerJob.class);

	private SnIndexerService snIndexerService;
	private SnIndexerCronJobDao snIndexerCronJobDao;
	private SnCronJobProgressTrackerFactory snCronJobProgressTrackerFactory;

	@Override
	public boolean isAbortable()
	{
		return true;
	}

	@Override
	public boolean isPerformable()
	{
		return true;
	}

	@Override
	public PerformResult perform(final T cronJob)
	{
		LOG.info("Started indexer job for cron job '{}'", cronJob.getCode());

		final SnIndexTypeModel indexType = cronJob.getIndexType();
		if (indexType == null || StringUtils.isBlank(indexType.getId()))
		{
			LOG.error("Error running indexer job, index type not found for cron job '{}'", cronJob.getCode());
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}

		try (final SnCloseableProgressTracker progressTracker = getSnCronJobProgressTrackerFactory().createProgressTracker(cronJob))
		{
			final SnIndexerRequest indexerRequest = createIndexerRequest(cronJob, progressTracker);
			final SnIndexerResponse indexerResponse = getSnIndexerService().index(indexerRequest);

			if (indexerResponse.getStatus() == SnIndexerOperationStatus.COMPLETED)
			{
				saveLastSuccessfulStartTime(cronJob);

				LOG.info("Indexer job for cron job '{}' finished with status '{}'", cronJob.getCode(), indexerResponse.getStatus());

				return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
			}
			else
			{
				LOG.info("Indexer job for cron job '{}' finished with status '{}'", cronJob.getCode(), indexerResponse.getStatus());

				return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
			}
		}
		catch (final SnIndexerException | IOException | RuntimeException e)
		{
			LOG.error(MessageFormat.format("Indexer job for cron job ''{0}'' failed: ''{1}''", cronJob.getCode(), e.getMessage()),
					e);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
	}

	protected abstract SnIndexerRequest createIndexerRequest(T cronJob, SnProgressTracker progressTracker)
			throws SnIndexerException;

	/**
	 * Retrieve the lastSuccessfulStartTime from the cronjob. If the cronjob didn't finish successfully before, retrieve the
	 * maximum of the lastSuccessfulStartTime from the full indexing cronjobs for the same {@link SnIndexTypeModel}. If both
	 * are not available, return an empty result.
	 *
	 * @param cronJob
	 *           the cronjob
	 * @return the last successful start time, or empty
	 */
	protected Optional<Date> getLastSuccessfulStartTime(final T cronJob)
	{
		return Optional.ofNullable(cronJob.getLastSuccessfulStartTime())
				.or(() -> snIndexerCronJobDao.getMaxFullLastSuccessfulStartTime(cronJob.getIndexType()));
	}

	/**
	 * Persist the start time of the cronjob as last successful start time.
	 *
	 * @param cronJob
	 *           the cronjob
	 */
	protected void saveLastSuccessfulStartTime(final T cronJob)
	{
		cronJob.setLastSuccessfulStartTime(cronJob.getStartTime());
		modelService.save(cronJob);
	}

	public SnIndexerService getSnIndexerService()
	{
		return snIndexerService;
	}

	@Required
	public void setSnIndexerService(final SnIndexerService snIndexerService)
	{
		this.snIndexerService = snIndexerService;
	}

	public SnIndexerCronJobDao getSnIndexerCronJobDao()
	{
		return snIndexerCronJobDao;
	}

	@Required
	public void setSnIndexerCronJobDao(final SnIndexerCronJobDao snIndexerCronJobDao)
	{
		this.snIndexerCronJobDao = snIndexerCronJobDao;
	}

	public SnCronJobProgressTrackerFactory getSnCronJobProgressTrackerFactory()
	{
		return snCronJobProgressTrackerFactory;
	}

	@Required
	public void setSnCronJobProgressTrackerFactory(final SnCronJobProgressTrackerFactory snCronJobProgressTrackerFactory)
	{
		this.snCronJobProgressTrackerFactory = snCronJobProgressTrackerFactory;
	}
}
