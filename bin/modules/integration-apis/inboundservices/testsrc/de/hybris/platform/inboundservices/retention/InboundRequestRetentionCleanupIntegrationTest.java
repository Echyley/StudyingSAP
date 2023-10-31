/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.retention;

import static de.hybris.platform.inboundservices.util.InboundServicesEssentialData.inboundServicesEssentialData;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;

@IntegrationTest
public class InboundRequestRetentionCleanupIntegrationTest extends ServicelayerTest
{
	private static final String TEST_NAME = "InboundRequestRetentionCleanup";
	private static final Logger LOG = Log.getLogger(InboundRequestRetentionCleanupIntegrationTest.class);
	private static final String INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME = "inboundRequestCleanupCronJob";
	private static final String INBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME = "inboundRequestMediaCleanupCronJob";
	private static final String INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME = "integrationApiMediaCleanupCronJob";
	private static final String PRODUCT = TEST_NAME + "_Product";
	private static final String CATEGORY = TEST_NAME + "_Category";
	private static final String UNIT = TEST_NAME + "_B2B_Unit";
	private static final String MEDIA1 = TEST_NAME + "_InboundRequestMedia";
	private static final String MEDIA2 = TEST_NAME + "_IntegrationApiMedia";
	private static final String ERROR = TEST_NAME + "_Error";

	@ClassRule
	public static ModuleEssentialData essentialData = inboundServicesEssentialData().withDependencies();

	@Resource
	private CronJobService cronJobService;

	@Test
	public void testCleanupRuleCleansUpInboundRequestOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now()
		                                       .minusDays(7)
		                                       .minusHours(1)
		                                       .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequest = {
				"$product=" + PRODUCT,
				"INSERT_UPDATE InboundRequest; type; status(code); integrationKey[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; $product; SUCCESS; old; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldInboundRequest);
		assertModelExists(inboundRequest("old"));

		executeCronJob(INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(inboundRequest("old"));
	}

	@Test
	public void testCleanupRuleDoesNotCleanNewInboundRequest() throws ImpExException
	{
		final String sixDaysAgo = LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] newInboundRequest = {
				"$category=" + CATEGORY,
				"INSERT_UPDATE InboundRequest; type; status(code); integrationKey[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; $category; ERROR; new; " + sixDaysAgo
		};
		IntegrationTestUtil.importImpEx(newInboundRequest);
		assertModelExists(inboundRequest("new"));

		executeCronJob(INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelExists(inboundRequest("new"));
	}

	@Test
	public void testInboundRequestCleanupAlsoCleansInboundRequestErrors() throws ImpExException
	{
		final String olderThanAWeek = LocalDateTime.now()
		                                           .minusDays(7)
		                                           .minusHours(1)
		                                           .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequest = {
				"$unit=" + UNIT,
				"$error=" + ERROR,
				"INSERT_UPDATE InboundRequest; type; status(code); integrationKey[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; $unit; ERROR; old_with_error; " + olderThanAWeek,
				"INSERT_UPDATE InboundRequestError; code[unique=true]; message; inboundRequest(integrationKey)",
				"; $error; A detailed error message; old_with_error"
		};
		IntegrationTestUtil.importImpEx(oldInboundRequest);
		assertModelExists(inboundRequest("old_with_error"));
		assertModelExists(requestError(ERROR));

		executeCronJob(INBOUND_REQUEST_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(inboundRequest("old_with_error"));
		assertModelDoesNotExist(requestError(ERROR));
	}

	@Test
	public void testCleanupRuleCleansUpInboundRequestMediaOlderThanRetentionPeriod() throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now()
		                                       .minusDays(7)
		                                       .minusHours(1)
		                                       .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequestMedia = {
				"$media=" + MEDIA1,
				"INSERT_UPDATE InboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; $media; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldInboundRequestMedia);

		assertModelExists(inboundRequestMedia(MEDIA1));

		executeCronJob(INBOUND_REQUEST_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelDoesNotExist(inboundRequestMedia(MEDIA1));
	}

	@Test
	public void testCleanupRuleCleansUpIntegrationApiMediaOlderThanRetentionPeriod_notIncludingInboundRequestMedia()
			throws ImpExException
	{
		final String oneWeekAgo = LocalDateTime.now()
		                                       .minusDays(7)
		                                       .minusHours(1)
		                                       .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		final String[] oldInboundRequestMedia = {
				"$media=" + MEDIA1,
				"INSERT_UPDATE InboundRequestMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; $media; " + oneWeekAgo
		};
		final String[] oldIntegrationApiMedia = {
				"$media=" + MEDIA2,
				"INSERT_UPDATE IntegrationApiMedia; code[unique=true]; creationtime[dateformat=dd.MM.yyyy HH:mm]",
				"; $media; " + oneWeekAgo
		};
		IntegrationTestUtil.importImpEx(oldInboundRequestMedia);
		IntegrationTestUtil.importImpEx(oldIntegrationApiMedia);

		assertModelExists(inboundRequestMedia(MEDIA1));
		assertModelExists(integrationApiMedia(MEDIA2));

		executeCronJob(INTEGRATION_API_MEDIA_CLEANUP_CRON_JOB_NAME);

		assertModelExists(inboundRequestMedia(MEDIA1));
		assertModelDoesNotExist(integrationApiMedia(MEDIA2));
	}

	private void executeCronJob(final String cronJobName)
	{
		final CronJobModel cronJob = cronJobService.getCronJob(cronJobName);
		LOG.info("Performing cronJob {} synchronously", cronJob.getCode());
		cronJobService.performCronJob(cronJob, true);
		LOG.info("CronJob completed with status {}", cronJob.getStatus());
	}

	private InboundRequestModel inboundRequest(final String integrationKey)
	{
		final InboundRequestModel inboundRequest = new InboundRequestModel();
		inboundRequest.setIntegrationKey(integrationKey);
		return inboundRequest;
	}

	private InboundRequestMediaModel inboundRequestMedia(final String code)
	{
		final InboundRequestMediaModel media = new InboundRequestMediaModel();
		media.setCode(code);
		return media;
	}

	private IntegrationApiMediaModel integrationApiMedia(final String code)
	{
		final IntegrationApiMediaModel media = new IntegrationApiMediaModel();
		media.setCode(code);
		return media;
	}

	private InboundRequestErrorModel requestError(final String errorCode)
	{
		final InboundRequestErrorModel requestError = new InboundRequestErrorModel();
		requestError.setCode(errorCode);
		return requestError;
	}
}
