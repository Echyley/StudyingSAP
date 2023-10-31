/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cronjob.jalo;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.internal.model.ScriptingJobModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.awaitility.Awaitility;
import org.junit.Test;


@IntegrationTest
public class CronJobHistoryAsyncTest extends ServicelayerBaseTest
{
	@Resource
	private CronJobService cronJobService;
	@Resource
	private ModelService modelService;

	@Test
	public void testMultipleExecutions()
	{
		final CronJobModel cronJob = createScriptableCronJob();

		cronJobService.performCronJob(cronJob, false);

		waitForCronJobState(cronJob, Duration.ofSeconds(1), cj -> cj.getActiveCronJobHistory() != null);

		final CronJobHistoryModel cronJobHistory = cronJob.getActiveCronJobHistory();

		cronJobService.performCronJob(cronJob, false);
		cronJobService.performCronJob(cronJob, false);

		waitForCronJobState(cronJob, Duration.ofSeconds(30), cj -> cronJobService.isFinished(cj));
		waitForCronJobHistoryUpdate(cronJobHistory, Duration.ofSeconds(5));

		assertThat(cronJobHistory.getStatus()).isEqualTo(CronJobStatus.FINISHED);
		assertThat(cronJobHistory.getResult()).isEqualTo(CronJobResult.SUCCESS);

		final List<CronJobHistoryModel> entries = cronJob.getCronJobHistoryEntries();
		assertThat(entries).isNotNull().hasSize(3).contains(cronJobHistory);

		assertThat(entries).extracting(e -> e.getResult()).containsExactlyInAnyOrder(CronJobResult.SUCCESS, CronJobResult.ERROR,
				CronJobResult.ERROR);
		assertThat(entries).extracting(e -> e.getStatus()).containsExactlyInAnyOrder(CronJobStatus.FINISHED, CronJobStatus.ABORTED,
				CronJobStatus.ABORTED);
	}

	private CronJobModel createScriptableCronJob()
	{
		final String scriptCode = UUID.randomUUID().toString();
		final ScriptModel groovyScript = modelService.create(ScriptModel.class);
		groovyScript.setCode(scriptCode);
		groovyScript.setContent("Thread.sleep(2000);");

		final String jobCode = UUID.randomUUID().toString();
		final ScriptingJobModel job = modelService.create(ScriptingJobModel.class);
		job.setCode(jobCode);
		job.setScriptURI("model://" + scriptCode);

		final String cronJobCode = UUID.randomUUID().toString();
		final CronJobModel cronjob = modelService.create(CronJobModel.class);
		cronjob.setCode(cronJobCode);
		cronjob.setSingleExecutable(Boolean.FALSE);
		cronjob.setJob(job);

		modelService.saveAll(groovyScript, job, cronjob);

		return cronjob;
	}

	private void waitForCronJobState(final CronJobModel cronJob, final Duration timeout,
	                                 final Predicate<CronJobModel> statePredicate)
	{
		Awaitility.await().atMost(timeout).pollInSameThread().until(() -> {
			modelService.refresh(cronJob);
			return statePredicate.test(cronJob);
		});
	}

	private void waitForCronJobHistoryUpdate(final CronJobHistoryModel cronJobHistory, final Duration timeout)
	{
		Awaitility.await().atMost(timeout).pollInSameThread()
		          .until(() -> cronJobHistory.getStatus().equals(CronJobStatus.FINISHED));
	}
}
