/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.outboundservices.util.OutboundServicesEssentialData.outboundServicesEssentialData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.model.OutboundRequestMediaModel;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.outboundservices.util.OutboundServicesEssentialData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;

@IntegrationTest
public class OutboundRequestRetentionCleanupIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Log.getLogger(OutboundRequestRetentionCleanupIntegrationTest.class);
	private static final String TEST_NAME = "OutboundRequestRetentionCleanup";
	private static final String OUTBOUND_REQUEST_CLEANUP_CRON_JOB_NAME = "outboundRequestCleanupCronJob";
	private static final String OUTBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME = "outboundRequestMediaCleanupCronJob";
	private static final String INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME = "integrationApiMediaCleanupCronJob";
	private static final String OUTBOUND_REQ_INTEGRATION_KEY = TEST_NAME + "_OutboundRequest";
	private static final String OUTBOUND_REQ_MEDIA_CODE = TEST_NAME + "_OutboundRequestMedia";
	private static final String INTEGRATION_API_MEDIA_CODE = TEST_NAME + "_IntegrationApiMedia";

	@ClassRule
	public static OutboundServicesEssentialData essentialData = outboundServicesEssentialData().withDependencies();

	@Resource
	private CronJobService cronJobService;

	@After
	public void tearDown() {
		IntegrationTestUtil.remove(OutboundRequestModel.class, it -> it.getIntegrationKey().equals(OUTBOUND_REQ_INTEGRATION_KEY));
		IntegrationTestUtil.remove(OutboundRequestMediaModel.class, it -> it.getCode().equals(OUTBOUND_REQ_MEDIA_CODE));
		IntegrationTestUtil.remove(IntegrationApiMediaModel.class, it -> it.getCode().equals(INTEGRATION_API_MEDIA_CODE));
	}
	@Test
	public void testCleanupRuleCleansUpOutboundRequestOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldOutboundRequest = {
				"$key=" + OUTBOUND_REQ_INTEGRATION_KEY,
				"INSERT_UPDATE OutboundRequest; type   ; status(code); integrationKey[unique=true]; destination ; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"                             ; Product; SUCCESS     ; $key                       ; adestination;" + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldOutboundRequest);
		assertModelExists(outboundRequest(OUTBOUND_REQ_INTEGRATION_KEY));

		executeCronJob(OUTBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(outboundRequest(OUTBOUND_REQ_INTEGRATION_KEY));
	}

	@Test
	public void testCleanupRuleDoesNotCleanNewOutboundRequest() throws ImpExException
	{
		final String sixDaysAgo = LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] newOutboundRequest = {
				"$key=" + OUTBOUND_REQ_INTEGRATION_KEY,
				"INSERT_UPDATE OutboundRequest; type    ; status(code); integrationKey[unique=true]; destination ; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"                             ; Category; ERROR       ; $key                        ; adestination; " + sixDaysAgo
		};
		IntegrationTestUtil.importImpEx(newOutboundRequest);
		assertModelExists(outboundRequest(OUTBOUND_REQ_INTEGRATION_KEY));

		executeCronJob(OUTBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelExists(outboundRequest(OUTBOUND_REQ_INTEGRATION_KEY));
	}

	@Test
	public void testCleanupRuleCleansUpOutboundRequestMediaOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldOutboundRequestMedia = {
				"$code=" + OUTBOUND_REQ_MEDIA_CODE,
				"INSERT_UPDATE OutboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"                                  ; $code            ; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldOutboundRequestMedia);

		assertModelExists(outboundRequestMedia(OUTBOUND_REQ_MEDIA_CODE));

		executeCronJob(OUTBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(outboundRequestMedia(OUTBOUND_REQ_MEDIA_CODE));
	}

	@Test
	public void testCleanupRuleCleansUpIntegrationApiMediaOlderThanRetentionPeriod_notIncludingOutboundRequestMedia() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now().minusDays(7).minusHours(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldOutboundRequestMedia = {
				"$code=" + OUTBOUND_REQ_MEDIA_CODE,
				"INSERT_UPDATE OutboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"                                  ; $code            ; " + oneWeekAgo
		};
		final String[] oldIntegrationApiMedia = {
				"$code=" + INTEGRATION_API_MEDIA_CODE,
				"INSERT_UPDATE IntegrationApiMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"                                 ; $code            ; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldOutboundRequestMedia);
		IntegrationTestUtil.importImpEx(oldIntegrationApiMedia);

		assertModelExists(outboundRequestMedia(OUTBOUND_REQ_MEDIA_CODE));
		assertModelExists(integrationApiMedia(INTEGRATION_API_MEDIA_CODE));

		executeCronJob(INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelExists(outboundRequestMedia(OUTBOUND_REQ_MEDIA_CODE));
		assertModelDoesNotExist(integrationApiMedia(INTEGRATION_API_MEDIA_CODE));
	}

	private void executeCronJob(final String cronJobName)
	{
		final CronJobModel cronJob = cronJobService.getCronJob(cronJobName);
		LOG.info("Performing cronJob {} synchronously", cronJob.getCode());
		cronJobService.performCronJob(cronJob, true);
		LOG.info("CronJob completed with status {}", cronJob.getStatus());
	}

	private OutboundRequestModel outboundRequest(final String integrationKey)
	{
		final OutboundRequestModel outboundRequest = new OutboundRequestModel();
		outboundRequest.setIntegrationKey(integrationKey);
		return outboundRequest;
	}

	private IntegrationApiMediaModel integrationApiMedia(final String code)
	{
		final IntegrationApiMediaModel media = new IntegrationApiMediaModel();
		media.setCode(code);
		return media;
	}

	private OutboundRequestMediaModel outboundRequestMedia(final String code)
	{
		final OutboundRequestMediaModel media = new OutboundRequestMediaModel();
		media.setCode(code);
		return media;
	}
}
