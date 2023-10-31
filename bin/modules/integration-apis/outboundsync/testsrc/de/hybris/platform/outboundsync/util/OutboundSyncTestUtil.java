/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.util;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder;
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncJobModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel;

import java.util.Collection;

public class OutboundSyncTestUtil
{
	public static OutboundChannelConfigurationModel outboundChannelConfigurationExists(final String channelCode,
	                                                                                   final String integrationObjectCode)
			throws ImpExException
	{
		return outboundChannelConfigurationBuilder()
				.withCode(channelCode)
				.withIntegrationObjectCode(integrationObjectCode)
				.withConsumedDestination(consumedDestinationBuilder().withId("destination_" + channelCode))
				.build();
	}

	public static void cleanup()
	{
		IntegrationTestUtil.removeAll(OutboundSyncJobModel.class);
		IntegrationTestUtil.removeAll(OutboundSyncCronJobModel.class);
		IntegrationTestUtil.removeAll(OutboundSyncStreamConfigurationContainerModel.class);
		IntegrationTestUtil.removeAll(CronJobModel.class);
	}

	public static void outboundSyncRetryExists(final Long itemPk, final String channelConfigurationCode) throws ImpExException
	{
		importImpEx(
				"INSERT_UPDATE OutboundSyncRetry; itemPk[unique = true]; syncAttempts; channel(code)",
				"                               ; " + itemPk + "       ; 3           ;" + channelConfigurationCode);
	}

	static ProductModel getProductByCode(final String code)
	{
		return IntegrationTestUtil.findAny(ProductModel.class, it -> it.getCode().equals(code)).orElse(null);
	}

	public static OutboundChannelConfigurationModel getChannelConfigurationByAttributes(final String code,
	                                                                                    final IntegrationObjectModel integrationObject)
	{

		return IntegrationTestUtil.findAny(OutboundChannelConfigurationModel.class,
				it -> it.getCode().equals(code) && it.getIntegrationObject().equals(integrationObject)).orElse(null);
	}

	/**
	 * Retrieves the outbound sync job configured in the persistence storage.
	 *
	 * @return the job model, if there is only one job configured in the storage; {@code null}, if there are no outbound
	 * sync jobs in the persistent storage.
	 * @throws IllegalStateException when there is more than one job exists in the persistent storage. This method is not
	 *                               suitable for such cases and the ambiguity needs to be resolved by the test.
	 */
	public static CronJobModel outboundCronJob()
	{
		final Collection<OutboundSyncCronJobModel> jobs = IntegrationTestUtil.findAll(OutboundSyncCronJobModel.class);
		if (jobs.size() > 1)
		{
			throw new IllegalStateException("More than one context jobs found: " + jobs);
		}
		return jobs.isEmpty() ? null : jobs.stream().findFirst().get();
	}

	/**
	 * Creates an outbound sync cron job model with the corresponding job model and the stream container model
	 *
	 * @param id ID for the jobs to create. The ID will be appended by {@code "Job"} for the job model creation, by
	 *           {@code "CronJob"} for the cron job model and by {@code "Streams"} for the stream container model creation.
	 * @return the created cron job model, which has reference to the corresponding job model that was also created.
	 */
	public static OutboundSyncCronJobModel createOutboundSyncJob(final String id) throws ImpExException
	{
		final String containerId = id + "Streams";
		final String jobId = id + "Job";
		final String cronJobId = id + "CronJob";
		importImpEx(
				"INSERT_UPDATE OutboundSyncStreamConfigurationContainer; id[unique = true]",
				"                                                      ;" + containerId,
				"INSERT_UPDATE OutboundSyncJob; code[unique = true]; streamConfigurationContainer(id)",
				"                             ; " + jobId + "      ;" + containerId,
				"INSERT_UPDATE OutboundSyncCronJob; code[unique = true]; job(code)    ; sessionLanguage(isocode)",
				"                                 ; " + cronJobId + "  ; " + jobId + "; en");
		return IntegrationTestUtil.findAny(OutboundSyncCronJobModel.class, it -> it.getCode().equals(cronJobId))
		                          .orElse(null);
	}

	/**
	 * Creates a cron job model with the corresponding job model.
	 *
	 * @param id              ID for the jobs to create. The ID will be appended by {@code "Job"} for the job model creation and by
	 *                        {@code "CronJob"} for the cron job model.
	 * @param performableBean name or ID of the spring bean implementing the job logic
	 * @return the created cron job model, which has reference to the corresponding job model that was also created.
	 */
	public static CronJobModel createCronJob(final String id, final String performableBean) throws ImpExException
	{
		final String jobId = id + "Job";
		final String cronJobId = id + "CronJob";
		importImpEx(
				"INSERT_UPDATE ServicelayerJob; code[unique = true]; springId              ; sessionLanguage(isocode)",
				"                             ;" + jobId + "       ;" + performableBean + "; en",
				"INSERT_UPDATE CronJob; code[unique = true]; job(code)    ; sessionLanguage(isocode)",
				"                     ; " + cronJobId + "  ; " + jobId + "; en");
		return IntegrationTestUtil.findAny(CronJobModel.class, it -> it.getCode().equals(cronJobId)).orElse(null);
	}

	/**
	 * Updates result of performing a job.
	 *
	 * @param model  a model of the job to be updated.
	 * @param result result to update the job with
	 * @param status status to update the job with
	 */
	public static void updateCronJobResult(final CronJobModel model, final CronJobResult result, final CronJobStatus status)
			throws ImpExException
	{
		importImpEx(
				"UPDATE CronJob; pk[unique = true]   ; result(code) ; status(code)",
				"              ;" + model.getPk() + ";" + result + ";" + status);
	}
}
