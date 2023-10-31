/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cronjob.jalo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.jdbcwrapper.DBOutageSimulator;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.jdbcwrapper.JUnitJDBCConnectionPool;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.scripting.util.TestScriptLogicHolder;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.TriggerService;
import de.hybris.platform.servicelayer.internal.model.ScriptingJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.utils.NeedsTaskEngine;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.StrandedItemsRegistry;
import de.hybris.platform.util.Utilities;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;


@IntegrationTest
@NeedsTaskEngine
public class CronJobRetryTest extends ServicelayerBaseTest
{
	@Resource
	private CronJobService cronJobService;

	@Resource
	private ModelService modelService;

	@Resource
	private TypeService typeService;

	@Resource
	private TriggerService triggerService;

	private DBOutageSimulator dbOutageSimulator;
	private JUnitJDBCConnectionPool connectionPool;
	private TestScriptLogicHolder testScripts;
	private final PropertyConfigSwitcher cronJobRepetitionSwitcher = new PropertyConfigSwitcher("cronjob.enableRepeat");
	private final PropertyConfigSwitcher cronjobEnabledConfigSwitcher = new PropertyConfigSwitcher(
			"cronjob.timertask.loadonstartup");

	@Before
	public void setUp()
	{
		final HybrisDataSource dataSource = getDataSource();
		assertThat(dataSource.getConnectionPool()).isInstanceOf(JUnitJDBCConnectionPool.class);

		connectionPool = (JUnitJDBCConnectionPool) dataSource.getConnectionPool();
		dbOutageSimulator = new DBOutageSimulator(connectionPool);
		testScripts = new TestScriptLogicHolder();
		cronJobRepetitionSwitcher.switchToValue("true");
		TestUtils.disableFileAnalyzer("testing cronjobs during db outage - there will be a lot of errors");
	}

	@After
	public void tearDown()
	{
		TestUtils.enableFileAnalyzer();
		connectionPool.resetTestMode();
		testScripts.clear();
		cronJobRepetitionSwitcher.switchBackToDefault();
		cronjobEnabledConfigSwitcher.switchBackToDefault();
	}

	private static StrandedItemsRegistry strandedCronJobRegistry()
	{
		return CronJobManager.getInstance().getStrandedCronJobsRegistry();
	}

	@Test
	public void testConfiguredCronJobGetsRepeatedAndFinishedAfterConnectionErrorOccursDuringItsExecution()
			throws InterruptedException, TimeoutException, ExecutionException
	{
		final String titleCode = "groovyTitle";
		//create cronjob that will run for at least 6s
		final CronJobModel cronjob = createScriptedCronJob(titleCode, Duration.ofSeconds(4));
		cronjob.setLogToFile(true);
		cronjob.setNumberOfRetries(1);
		modelService.save(cronjob);

		cronJobService.performCronJob(cronjob, false);

		//wait for cronjob to start
		waitForCronJobRunning(cronjob, Duration.ofSeconds(30));

		//after 2s start the db outage that will last 30s
		Thread.sleep(TimeUnit.SECONDS.toMillis(2));
		final DBOutageSimulator.DBOutage dbOutage = dbOutageSimulator.simulateDBOutage(Duration.ofSeconds(30));

		//the cronjob should fail the logic and will never finish
		assertThatThrownBy(() -> waitForCronJobFinished(cronjob, Duration.ofSeconds(30))).isNotNull();

		//wait for the db outage to finish
		dbOutage.waitTillEnd(40, TimeUnit.SECONDS);

		//get fresh cronjob data from db
		modelService.detach(cronjob);
		Utilities.invalidateCache(cronjob.getPk());
		final CronJobModel cronJobDb = modelService.get(cronjob.getPk());

		//the cronjob on db is marked as running
		assertThat(cronJobService.isFinished(cronJobDb)).isFalse();

		//the cronjob should be marked for checking
		assertThat(strandedCronJobRegistry().getStrandedItems()).contains(cronJobDb.getPk());
		strandedCronJobRegistry().checkStrandedItems(0);
		assertThat(strandedCronJobRegistry().getStrandedItems()).doesNotContain(cronJobDb.getPk());

		//wait for cron job to restart and finish
		waitForCronJobRunning(cronJobDb, Duration.ofSeconds(30));
		modelService.refresh(cronJobDb);
		assertThat(cronJobDb.getStatus()).isEqualTo(CronJobStatus.RUNNINGRESTART);
		assertThat(cronJobDb.getCurrentRetry()).isEqualTo(1);
		waitForCronJobFinished(cronJobDb, Duration.ofSeconds(30));

		modelService.refresh(cronJobDb);
		assertThat(cronJobDb.getStatus()).isEqualTo(CronJobStatus.FINISHED);
		assertThat(cronJobDb.getCurrentRetry()).isZero();
	}

	@Test
	public void testCronJobGetsAbortedWhenConnectionFailureOccursDuringTheRepeatedExecution()
			throws InterruptedException, TimeoutException, ExecutionException
	{
		final String titleCode = "groovyTitle";
		//create cronjob that will run for at least 6s
		final CronJobModel cronjob = createScriptedCronJob(titleCode, Duration.ofSeconds(4));
		cronjob.setLogToFile(true);
		cronjob.setNumberOfRetries(1);
		modelService.save(cronjob);

		cronJobService.performCronJob(cronjob, false);

		for (int i = 0; i < 2; ++i)
		{
			//wait for cronjob to start
			waitForCronJobRunning(cronjob, Duration.ofSeconds(30));

			//after 2s start the db outage that will last 30s
			Thread.sleep(TimeUnit.SECONDS.toMillis(2));
			final DBOutageSimulator.DBOutage dbOutage = dbOutageSimulator.simulateDBOutage(Duration.ofSeconds(30));

			//the cronjob should fail the logic and will never finish
			assertThatThrownBy(() -> waitForCronJobFinished(cronjob, Duration.ofSeconds(30))).isNotNull();

			//wait for the db outage to finish
			dbOutage.waitTillEnd(40, TimeUnit.SECONDS);

			//get fresh cronjob data from db
			modelService.detach(cronjob);
			Utilities.invalidateCache(cronjob.getPk());

			final CronJobModel cronJobDb = modelService.get(cronjob.getPk());

			//the cronjob on db is marked as running
			assertThat(cronJobService.isFinished(cronJobDb)).isFalse();

			//the cronjob should be marked for checking
			assertThat(strandedCronJobRegistry().getStrandedItems()).contains(cronJobDb.getPk());
			strandedCronJobRegistry().checkStrandedItems(0);
			assertThat(strandedCronJobRegistry().getStrandedItems()).doesNotContain(cronJobDb.getPk());
		}
		modelService.refresh(cronjob);
		assertThat(cronjob.getStatus()).isEqualTo(CronJobStatus.ABORTED);
	}

	@Test
	public void shouldRestartAndFinishCronJobCreatedFromTriggeredJobAfterConnectionErrorOccursDuringItsExecution()
			throws InterruptedException, TimeoutException, ExecutionException
	{

		final String titleCode = "groovyTitle";
		//create job that will will run for at least 4s
		final ScriptingJobModel scriptingJob = prepareScriptingJob(titleCode, Duration.ofSeconds(4));
		scriptingJob.setNumberOfRetries(1);
		modelService.save(scriptingJob);

		//create trigger that will activate the job
		final TriggerModel trigger = prepareTriggerForJob(scriptingJob);
		adjustTriggerActivationTimeToMakeItPossibleToRun(trigger);

		//run job and deactivate the trigger in order to prevent it from activating again
		runJobAndDeactivateTheTrigger(trigger);

		//get cronjob which has been created after trigger activation
		final CronJobModel cronjob = getCronJobFromJob(scriptingJob);
		assertThat(cronjob).isNotNull();

		//wait for cronjob to start
		waitForCronJobRunning(cronjob, Duration.ofSeconds(30));

		//after 2s start the db outage that will last 30s
		Thread.sleep(TimeUnit.SECONDS.toMillis(2));
		final DBOutageSimulator.DBOutage dbOutage = dbOutageSimulator.simulateDBOutage(Duration.ofSeconds(30));

		//the cronjob should fail the logic and will never finish
		assertThatThrownBy(() -> waitForCronJobFinished(cronjob, Duration.ofSeconds(30))).isNotNull();

		//wait for the db outage to finish
		dbOutage.waitTillEnd(40, TimeUnit.SECONDS);

		//get fresh cronjob data from db
		modelService.detach(cronjob);
		Utilities.invalidateCache(cronjob.getPk());
		final CronJobModel cronJobDb = modelService.get(cronjob.getPk());

		//the cronjob on db is marked as running
		assertThat(cronJobService.isFinished(cronJobDb)).isFalse();

		//the cronjob should be marked for checking
		assertThat(strandedCronJobRegistry().getStrandedItems()).contains(cronJobDb.getPk());
		strandedCronJobRegistry().checkStrandedItems(0);
		assertThat(strandedCronJobRegistry().getStrandedItems()).doesNotContain(cronJobDb.getPk());

		//wait for cron job to restart and finish
		waitForCronJobRunning(cronJobDb, Duration.ofSeconds(30));
		modelService.refresh(cronJobDb);
		assertThat(cronJobDb.getStatus()).isEqualTo(CronJobStatus.RUNNINGRESTART);
		assertThat(cronJobDb.getCurrentRetry()).isEqualTo(1);
		waitForCronJobFinished(cronJobDb, Duration.ofSeconds(30));

		modelService.refresh(cronJobDb);
		assertThat(cronJobDb.getStatus()).isEqualTo(CronJobStatus.FINISHED);
		assertThat(cronJobDb.getCurrentRetry()).isZero();
	}

	@Test
	public void shouldTakeNumberOfRetriesFromJobAsTheHighestValueAndRepeatCronJobAsManyTimes()
			throws ExecutionException, InterruptedException, TimeoutException
	{
		final int jobNumberOfRetries = 3;
		final int cronJobNumberOfRetries = 1;

		shouldTakeTheHighestNumberOfRetriesFromCronJobAndJobAndRepeatCronJobAsManyTimes(jobNumberOfRetries,
				cronJobNumberOfRetries, jobNumberOfRetries);
	}

	@Test
	public void shouldTakeNumberOfRetriesFromCronJobAsTheHighestValueAndRepeatCronJobAsManyTimes()
			throws InterruptedException, TimeoutException, ExecutionException
	{
		final int jobNumberOfRetries = 1;
		final int cronJobNumberOfRetries = 3;

		shouldTakeTheHighestNumberOfRetriesFromCronJobAndJobAndRepeatCronJobAsManyTimes(jobNumberOfRetries,
				cronJobNumberOfRetries, cronJobNumberOfRetries);
	}

	@Test
	public void shouldRepeatCronJobAppropriateNumberOfTimesIfNumberOfRetriesIsEqualInJobAndCronJob()
			throws ExecutionException, InterruptedException, TimeoutException
	{
		final int numberOfRetries = 2;

		shouldTakeTheHighestNumberOfRetriesFromCronJobAndJobAndRepeatCronJobAsManyTimes(numberOfRetries, numberOfRetries,
				numberOfRetries);
	}

	public void shouldTakeTheHighestNumberOfRetriesFromCronJobAndJobAndRepeatCronJobAsManyTimes(final int jobNumberOfRetries,
	                                                                                            final int cronJobNumberOfRetries,
	                                                                                            final int expectedNumberOfRetries)
			throws InterruptedException, TimeoutException, ExecutionException
	{
		final String titleCode = "groovyTitle";
		//create cronjob that will run for at least 4s
		final CronJobModel cronjob = createScriptedCronJob(titleCode, Duration.ofSeconds(4));
		cronjob.setLogToFile(true);
		cronjob.setNumberOfRetries(cronJobNumberOfRetries);
		cronjob.getJob().setNumberOfRetries(jobNumberOfRetries);
		modelService.save(cronjob);

		cronJobService.performCronJob(cronjob, false);

		//numberOfRepeats is negative to skip first run of cron job which is not repeat
		int numberOfRepeats = -1;

		do
		{
			//Wait for cronjob to start/restart. If it doesn't start, it means that it has repeated the maximum number of times
			try
			{
				waitForCronJobRunning(cronjob, Duration.ofSeconds(30));
				numberOfRepeats++;
			}
			catch (final TimeoutException e)
			{
				break;
			}

			//after 2s start the db outage that will last 30s
			Thread.sleep(TimeUnit.SECONDS.toMillis(2));
			final DBOutageSimulator.DBOutage dbOutage = dbOutageSimulator.simulateDBOutage(Duration.ofSeconds(30));

			//the cronjob should fail the logic and will never finish
			assertThatThrownBy(() -> waitForCronJobFinished(cronjob, Duration.ofSeconds(30))).isNotNull();

			//wait for the db outage to finish
			dbOutage.waitTillEnd(40, TimeUnit.SECONDS);

			//get fresh cronjob data from db
			modelService.detach(cronjob);
			Utilities.invalidateCache(cronjob.getPk());

			final CronJobModel cronJobDb = modelService.get(cronjob.getPk());

			//the cronjob on db is marked as running
			assertThat(cronJobService.isFinished(cronJobDb)).isFalse();

			//the cronjob should be marked for checking
			assertThat(strandedCronJobRegistry().getStrandedItems()).contains(cronJobDb.getPk());
			strandedCronJobRegistry().checkStrandedItems(0);
			assertThat(strandedCronJobRegistry().getStrandedItems()).doesNotContain(cronJobDb.getPk());

		} while (true);

		assertThat(numberOfRepeats).isEqualTo(expectedNumberOfRetries);
	}

	public AttributeDescriptorModel createAttributeDescriptor(final String qualifier,
	                                                          final Class<? extends ItemModel> enclosingType,
	                                                          final TypeModel attributeType)
	{
		final AttributeDescriptorModel runtimeAttribute = modelService.create(AttributeDescriptorModel.class);
		runtimeAttribute.setAttributeType(attributeType);
		runtimeAttribute.setEnclosingType(typeService.getComposedTypeForClass(enclosingType));
		runtimeAttribute.setGenerate(false);
		runtimeAttribute.setPartOf(false);
		runtimeAttribute.setQualifier(qualifier);
		modelService.save(runtimeAttribute);
		modelService.detachAll();
		return runtimeAttribute;
	}

	private void waitForCronJobState(final CronJobModel cronJob, final Duration timeout,
	                                 final Predicate<CronJobModel> statePredicate, final boolean throwTimeoutException)
			throws InterruptedException, TimeoutException
	{

		final Instant start = Instant.now();
		while (!statePredicate.test(cronJob))
		{
			Thread.sleep(50);
			modelService.refresh(cronJob);
			if (timeout.minus(Duration.between(start, Instant.now())).isNegative())
			{
				if (throwTimeoutException)
				{
					throw new TimeoutException();
				}
				else
				{
					return;
				}
			}
		}
	}

	private void waitForCronJobRunning(final CronJobModel cronJob, final Duration timeout)
			throws InterruptedException, TimeoutException
	{
		waitForCronJobRunning(cronJob, timeout, true);
	}

	private void waitForCronJobRunning(final CronJobModel cronJob,
	                                   final Duration timeout,
	                                   final boolean throwTimeoutException)
			throws InterruptedException, TimeoutException
	{
		waitForCronJobState(cronJob, timeout, cj -> cronJobService.isRunning(cj), throwTimeoutException);
	}

	private void waitForCronJobFinished(final CronJobModel cronJob, final Duration timeout)
			throws InterruptedException, TimeoutException
	{
		waitForCronJobFinished(cronJob, timeout, true);
	}

	private void waitForCronJobFinished(final CronJobModel cronJob,
	                                    final Duration timeout,
	                                    final boolean throwTimeoutException)
			throws InterruptedException, TimeoutException
	{
		waitForCronJobState(cronJob, timeout, cj -> cronJobService.isFinished(cj), throwTimeoutException);
	}

	private HybrisDataSource getDataSource()
	{
		return Registry.getCurrentTenant().getDataSource();
	}

	private CronJobModel createScriptedCronJob(final String titleCode,
	                                           final Duration scriptMinDuration)
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
			Thread.sleep(Math.max(10, scriptMinDuration.toMillis()));
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

	private ScriptingJobModel prepareScriptingJob(final String scriptCode, final Duration scriptMinDuration)
	{
		return prepareScriptingJob(prepareScript(scriptCode + UUID.randomUUID(), scriptMinDuration).getCode());
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

	private void adjustTriggerActivationTimeToMakeItPossibleToRun(final TriggerModel triggerModel)
	{
		//it should not be persisted in db as there is an interceptor which automatically changes activation time to CronJob expression
		triggerModel.setActivationTime(new Date(System.currentTimeMillis()));
	}

	private void runJobAndDeactivateTheTrigger(final TriggerModel triggerModel)
	{
		triggerService.activate(triggerModel);
		triggerModel.setActive(false);

		modelService.save(triggerModel);
	}

	private CronJobModel getCronJobFromJob(final JobModel jobModel)
	{
		return jobModel.getCronJobs().stream().findFirst().orElse(null);
	}
}
