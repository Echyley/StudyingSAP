/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cronjob.jalo;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.scripting.util.TestScriptLogicHolder;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.TriggerService;
import de.hybris.platform.servicelayer.cronjob.impl.DefaultTriggerService;
import de.hybris.platform.servicelayer.internal.model.ScriptingJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class RetryingCronJobsIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;
	private CronJobManager testCronJobManager;
	private TestScriptLogicHolder testScripts;
	@Resource
	private TriggerService triggerService;
	private int currentNodeId;
	private final PropertyConfigSwitcher cronJobRepetitionSwitcher = new PropertyConfigSwitcher("cronjob.enableRepeat");

	@Before
	public void setUp()
	{
		cronJobRepetitionSwitcher.switchToValue("true");
		CronJobManager.getInstance().stopConjobEngine();
		testScripts = new TestScriptLogicHolder();
		testCronJobManager = new CronJobManager();
		currentNodeId = Registry.getClusterID();
	}

	@After
	public void teardown()
	{
		cronJobRepetitionSwitcher.switchBackToDefault();
		CronJobManager.getInstance().startupCronjobEngine();
		testScripts.clear();
	}

	@Test
	public void testCronJobWithRepeatsHigherThenZeroStartsUpAgainAfterBeingAborted() throws InterruptedException
	{
		final CronJobModel cronJob = createRetryingRunningCronJob(currentNodeId + 1);
		cronJob.setNumberOfRetries(1);
		modelService.save(cronJob);

		testCronJobManager.abortRunningCronJobsForClusterNodes(Set.of(currentNodeId + 1), 100);

		assertTrueOneOfProvidedCronJobStatus(cronJob, CronJobStatus.ABORTED, CronJobStatus.RUNNINGRESTART);
		assertTrueCronJobStatus(cronJob, CronJobStatus.FINISHED);
	}

	@Test
	public void testCronJobWithoutRepeatsStatusDoesntChangeAgainAfterBeingAborted() throws InterruptedException
	{
		final CronJobModel cronJob = createRetryingRunningCronJob(currentNodeId + 1);

		testCronJobManager.abortRunningCronJobsForClusterNodes(Set.of(currentNodeId + 1), 100);

		assertTrueCronJobStatus(cronJob, CronJobStatus.ABORTED);
		assertFalseCronJobStatus(cronJob, CronJobStatus.RUNNINGRESTART);
	}

	@Test
	public void shouldCreateCronJobWithSetDefaultNumberOfRetriesFromTriggeredJob()
	{
		//given
		final ScriptingJobModel scriptingJob = prepareScriptingJob(Duration.ofSeconds(10));
		modelService.save(scriptingJob);

		final TriggerModel trigger = prepareTriggerForJob(scriptingJob);

		assertThat(scriptingJob.getCronJobs()).isEmpty();

		//when
		//run job and deactivate the trigger in order to prevent it from activating again
		runJobAndDeactivateTheTrigger(trigger);
		modelService.refresh(scriptingJob);

		//then
		final CronJobModel cronJobFromJob = getCronJobFromJob(scriptingJob);
		assertThat(cronJobFromJob).isNotNull();
		assertThat(cronJobFromJob.getNumberOfRetries()).isZero();
	}

	@Test
	public void shouldCreateCronJobWithSetNumberOfRetriesFromTriggeredJob()
	{
		//given
		final ScriptingJobModel scriptingJob = prepareScriptingJob(Duration.ofSeconds(10));
		final int numberOfRetries = 5;
		scriptingJob.setNumberOfRetries(numberOfRetries);
		modelService.save(scriptingJob);

		final TriggerModel trigger = prepareTriggerForJob(scriptingJob);

		assertThat(scriptingJob.getCronJobs()).isEmpty();

		//when
		//run job and deactivate the trigger in order to prevent it from activating again
		runJobAndDeactivateTheTrigger(trigger);
		modelService.refresh(scriptingJob);

		//then
		final CronJobModel cronJobFromJob = getCronJobFromJob(scriptingJob);
		assertThat(cronJobFromJob).isNotNull();
		assertThat(cronJobFromJob.getNumberOfRetries()).isEqualTo(numberOfRetries);
	}

	@Test
	public void shouldRestartCronjobWhichIsCreatedFromTriggeredJobAfterBeingAborted() throws InterruptedException
	{
		//given
		final ScriptingJobModel scriptingJob = prepareScriptingJob(Duration.ofSeconds(10));
		final int numberOfRetries = 1;
		scriptingJob.setNumberOfRetries(numberOfRetries);
		modelService.save(scriptingJob);

		final TriggerModel trigger = prepareTriggerForJob(scriptingJob);

		//when
		//run job and deactivate the trigger in order to prevent it from activating again
		runJobAndDeactivateTheTrigger(trigger);
		final CronJobModel cronJobFromJob = getCronJobFromJobAndRunOnClusterNode(scriptingJob, currentNodeId + 1);
		testCronJobManager.abortRunningCronJobsForClusterNodes(Set.of(currentNodeId + 1), 100);

		//then
		assertTrueOneOfProvidedCronJobStatus(cronJobFromJob, CronJobStatus.ABORTED, CronJobStatus.RUNNINGRESTART);
		assertTrueCronJobStatus(cronJobFromJob, CronJobStatus.FINISHED);
	}

	@Test
	public void shouldNotRestartCronjobWhichIsCreatedFromTriggeredJobAfterBeingAborted() throws InterruptedException
	{
		//given
		final ScriptingJobModel scriptingJob = prepareScriptingJob(Duration.ofSeconds(10));
		final int numberOfRetries = 0;
		scriptingJob.setNumberOfRetries(numberOfRetries);
		modelService.save(scriptingJob);

		final TriggerModel trigger = prepareTriggerForJob(scriptingJob);

		//when
		//run job and deactivate the trigger in order to prevent it from activating again
		runJobAndDeactivateTheTrigger(trigger);
		final CronJobModel cronJobFromJob = getCronJobFromJobAndRunOnClusterNode(scriptingJob, currentNodeId + 1);
		testCronJobManager.abortRunningCronJobsForClusterNodes(Set.of(currentNodeId + 1), 100);

		//then
		assertTrueCronJobStatus(cronJobFromJob, CronJobStatus.ABORTED);
		assertFalseCronJobStatus(cronJobFromJob, CronJobStatus.RUNNINGRESTART);
	}

	private void assertTrueCronJobStatus(final CronJobModel cronJob, final CronJobStatus cronJobStatus)
			throws InterruptedException
	{
		assertThat(waitForStatusChange(cronJob, cronJobStatus)).withFailMessage("The status was %s, instead %s",
				cronJob.getStatus().getCode(), cronJobStatus.getCode()).isTrue();
	}

	private void assertTrueOneOfProvidedCronJobStatus(final CronJobModel cronjob, final CronJobStatus cronJobStatus1,
	                                                  final CronJobStatus cronJobStatus2) throws InterruptedException
	{
		assertThat(waitForStatusChange(cronjob, cronJobStatus1, cronJobStatus2)).withFailMessage(
				"The status was %s, instead %s or %s", cronjob.getStatus().getCode(), cronJobStatus1.getCode(),
				cronJobStatus2.getCode()).isTrue();
	}

	private void assertFalseCronJobStatus(final CronJobModel cronJob, final CronJobStatus cronJobStatus)
			throws InterruptedException
	{
		assertThat(waitForStatusChange(cronJob, cronJobStatus)).withFailMessage("The status was %s, however it shouldn't be",
				cronJobStatus.getCode()).isFalse();
	}

	private boolean waitForStatusChange(final CronJobModel cronJob, final CronJobStatus expectedStatus)
			throws InterruptedException
	{
		return waitForStatusChange(cronJob, cronJobStatus -> cronJobStatus == expectedStatus);
	}

	private boolean waitForStatusChange(final CronJobModel cronJob, final CronJobStatus expectedStatus1,
	                                    final CronJobStatus expectedStatus2)
			throws InterruptedException
	{
		return waitForStatusChange(cronJob,
				cronJobStatus -> cronJobStatus == expectedStatus1 || cronJobStatus == expectedStatus2);
	}

	private boolean waitForStatusChange(final CronJobModel cronJob, final Predicate<CronJobStatus> checkCronJobStatus)
			throws InterruptedException
	{
		int i = 0;
		while (!checkCronJobStatus.test(cronJob.getStatus()))
		{
			modelService.refresh(cronJob);
			Thread.sleep(TimeUnit.SECONDS.toMillis(2));

			if (++i == 15)
			{
				return false;
			}
		}

		return true;
	}

	private CronJobModel createRetryingRunningCronJob(final int runningOnClusterNodeId)
	{
		final String titleCode = "groovyTitle";
		final CronJobModel cronJob = createScriptedCronJob(titleCode, Duration.ofSeconds(10));
		cronJob.setLogToFile(false);
		cronJob.setStatus(CronJobStatus.RUNNING);
		cronJob.setRunningOnClusterNode(runningOnClusterNodeId);
		modelService.save(cronJob);

		return cronJob;
	}

	private CronJobModel createScriptedCronJob(final String titleCode, final Duration scriptMinDuration)
	{
		return createScriptedCronJob(prepareScript(titleCode + UUID.randomUUID(), scriptMinDuration));
	}

	private CronJobModel createScriptedCronJob(final ScriptModel script)
	{
		final ScriptingJobModel scriptingJobModel = prepareScriptingJob(script.getCode());
		return prepareCronJob("testCronJob-" + UUID.randomUUID(), scriptingJobModel, JobLogLevel.INFO);
	}

	private ScriptModel prepareScript(final String testGroovyTitle, final Duration scriptMinDuration)
	{
		return testScripts.scriptWithLogic(modelService, () -> {
			Thread.sleep(scriptMinDuration.toMillis());
			LoggerFactory.getLogger("testName-" + testGroovyTitle).info("Script has finished");
		});
	}

	private ScriptingJobModel prepareScriptingJob(final String scriptCode)
	{
		final ScriptingJobModel scriptingJob = modelService.create(ScriptingJobModel.class);
		scriptingJob.setCode(scriptCode);
		scriptingJob.setScriptURI("model://" + scriptCode);

		return scriptingJob;
	}

	private ScriptingJobModel prepareScriptingJob(final Duration scriptMinDuration)
	{
		final String titleCode = "groovyTitle";
		return prepareScriptingJob(prepareScript(titleCode + UUID.randomUUID(), scriptMinDuration).getCode());
	}

	private CronJobModel prepareCronJob(final String code, final JobModel job, final JobLogLevel dbLogLevel)
	{
		final CronJobModel cronjob = modelService.create(CronJobModel.class);
		cronjob.setCode(code);
		cronjob.setSingleExecutable(Boolean.TRUE);
		cronjob.setJob(job);
		cronjob.setLogToDatabase(Boolean.TRUE);
		cronjob.setLogLevelDatabase(dbLogLevel);
		cronjob.setNodeID(Registry.getClusterID());
		modelService.save(cronjob);
		return cronjob;
	}

	private TriggerModel prepareTriggerForJob(final JobModel jobModel)
	{
		final TriggerModel triggerModel = modelService.create(TriggerModel.class);
		triggerModel.setJob(jobModel);
		triggerModel.setCronExpression("0 0 0 1 JAN ? 2099");
		modelService.save(triggerModel);

		return triggerModel;
	}

	private void runJobAndDeactivateTheTrigger(final TriggerModel triggerModel)
	{
		((DefaultTriggerService) triggerService).activateForTest(triggerModel, getActivationTimeForTrigger(triggerModel));
		triggerModel.setActive(false);

		modelService.save(triggerModel);
	}

	private CronJobModel getCronJobFromJobAndRunOnClusterNode(final JobModel job, final int clusterNodeId)
	{
		final CronJobModel cronJob = getCronJobFromJob(job);
		cronJob.setRunningOnClusterNode(clusterNodeId);
		cronJob.setStatus(CronJobStatus.RUNNING);
		modelService.save(cronJob);

		return cronJob;
	}

	private CronJobModel getCronJobFromJob(final JobModel jobModel)
	{
		return jobModel.getCronJobs().stream().findFirst().orElse(null);
	}

	private long getActivationTimeForTrigger(final TriggerModel triggerModel)
	{
		return triggerModel.getActivationTime().getTime();
	}
}
