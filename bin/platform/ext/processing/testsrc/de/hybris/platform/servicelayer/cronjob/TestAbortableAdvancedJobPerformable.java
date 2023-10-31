/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.servicelayer.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abortable job for testing, which waits for a while for an abort request.
 */
public class TestAbortableAdvancedJobPerformable extends TestAbortableJobPerformable
{

	private final static Logger LOG = LoggerFactory.getLogger(TestAbortableAdvancedJobPerformable.class);

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		try
		{
			LOG.info("### Starting executing cronjob, waiting for abort request : {} ###", cronJob.getCode());
			for (int i = 0; i < 10; i++)
			{
				LOG.info("checking abort request... :");
				Thread.sleep(3000);
				modelService.refresh(cronJob);
				if (Boolean.TRUE.equals(cronJob.getRequestAbort()))
				{
					cronJob.setRequestAbort(null);
					modelService.save(cronJob);
					LOG.info("Cronjob set to aborted.");
					return new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.ABORTED);
				}
			}
			LOG.warn("### Abort request didn't come for cronjob: {} ###", cronJob.getCode());
		}
		catch (final InterruptedException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}
}
