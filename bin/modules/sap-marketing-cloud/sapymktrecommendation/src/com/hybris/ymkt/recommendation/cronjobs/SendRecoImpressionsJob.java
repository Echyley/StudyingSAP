/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.cronjobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendation.services.ImpressionService;


/**
 *
 */
public class SendRecoImpressionsJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SendRecoImpressionsJob.class);
	private ImpressionService impressionService;

	@Override
	public PerformResult perform(final CronJobModel cronJobModel)
	{
		LOGGER.info("{} started at {}", cronJobModel.getCode(), cronJobModel.getStartTime());

		try
		{
			this.impressionService.sendAggregatedImpressions();
			LOGGER.info("{} completed in {}ms", cronJobModel.getCode(),
					System.currentTimeMillis() - cronJobModel.getStartTime().getTime());
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("Error occurred when sending impressions.", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	@Required
	public void setImpressionService(final ImpressionService impressionService)
	{
		this.impressionService = impressionService;
	}

}
