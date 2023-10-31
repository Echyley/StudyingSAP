/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.servicelayer.cronjob;

import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_CRONJOB;
import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_CRONJOB_RESULT;
import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_CRONJOB_STATUS;
import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_JOB;
import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_NAME_ABORT_CRONJOB;
import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_NAME_FINISH_CRONJOB;
import static de.hybris.platform.audit.actions.AuditableActionsConstants.AUDIT_NAME_START_CRONJOB;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.AuditableActionsUtil;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.internal.model.ScriptingJobModel;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@IntegrationTest
public class CronJobAuditTest extends ServicelayerBaseTest
{
	@Resource
	private CronJobService cronJobService;

	@Resource
	private ModelService modelService;

	private ServicelayerJobModel abortableJob;

	private ScriptingJobModel testJob, testJobWithException;
	private ScriptModel testScript, testScriptWithException;

	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;

	@Before
	public void setup() throws Exception
	{
		testAuditableActionHandler = mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActionsUtil.getAuditableActionHandlerFactory();
		AuditableActionsUtil.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);

		testScript = modelService.create(ScriptModel.class);
		testScript.setCode("myGroovyScript");
		final StringBuilder content = new StringBuilder();
		content.append("import de.hybris.platform.core.model.user.TitleModel\n");
		content.append("println 'hello groovy! '+ new Date()+ ' Cronjob='+cronjob\n");
		content.append("def title = modelService.create(TitleModel.class)\n");
		content.append("title.code='groovyTitle_'+System.currentTimeMillis()\n");
		content.append("modelService.save(title)\n");
		content.append("title.code");
		testScript.setContent(content.toString());

		testScriptWithException = modelService.create(ScriptModel.class);
		testScriptWithException.setCode("myGroovyScriptWithException");
		testScriptWithException.setContent("throw new RuntimeException('Broken intentionally');");

		modelService.saveAll(testScript, testScriptWithException);

		testJob = modelService.create(ScriptingJobModel.class);
		testJob.setCode("myGroovyJob");
		testJob.setScriptURI("model://myGroovyScript");
		testJobWithException = modelService.create(ScriptingJobModel.class);
		testJobWithException.setCode("myGroovyJobWithException");
		testJobWithException.setScriptURI("model://myGroovyScriptWithException");
		modelService.saveAll(testJob, testJobWithException);

		abortableJob = modelService.create(ServicelayerJobModel.class);
		abortableJob.setCode("abortableJob");
		abortableJob.setSpringId("testAbortableAdvancedJobPerformable");
		modelService.save(abortableJob);
	}

	@After
	public void tearDown()
	{
		AuditableActionsUtil.setAuditableActionHandlerFactory(originalActionHandler);
	}

	@Test
	public void shouldAuditCronJobStartAndFinish()
	{
		final String cronJobCode = "testCJ";
		final CronJobModel testCronJob = prepareCronjob(cronJobCode, testJob);
		modelService.save(testCronJob);

		//when
		cronJobService.performCronJob(testCronJob, true);

		//then
		assertThat(cronJobService.isSuccessful(testCronJob)).isTrue();

		Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_NAME_START_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, testJob.getCode()));


		optionalAction = getAuditableAction(AUDIT_NAME_FINISH_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, testJob.getCode()), entry(AUDIT_CRONJOB_STATUS,
						CronJobStatus.FINISHED.getCode()), entry(AUDIT_CRONJOB_RESULT, CronJobResult.SUCCESS.getCode()));

	}

	@Test
	public void shouldAuditCronJobStartAndAbortAndFinish() throws InterruptedException
	{
		final String cronJobCode = "testAbortableCJ";
		final CronJobModel testCronJob = prepareCronjob(cronJobCode, abortableJob);
		modelService.save(testCronJob);

		//when
		cronJobService.performCronJob(testCronJob, false);
		assertThat(cronJobService.isAbortable(testCronJob)).isTrue();
		waitTillCronJobStarted(testCronJob);
		modelService.refresh(testCronJob);

		cronJobService.requestAbortCronJob(testCronJob);

		//then
		waitTillCronJobFinished(testCronJob);
		modelService.refresh(testCronJob);
		assertThat(testCronJob.getStatus()).isEqualTo(CronJobStatus.ABORTED);
		Thread.sleep(3000);

		Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_NAME_START_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, abortableJob.getCode()));

		optionalAction = getAuditableAction(AUDIT_NAME_ABORT_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, abortableJob.getCode()));

		optionalAction = getAuditableAction(AUDIT_NAME_FINISH_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, abortableJob.getCode()), entry(AUDIT_CRONJOB_STATUS,
						CronJobStatus.ABORTED.getCode()), entry(AUDIT_CRONJOB_RESULT, CronJobResult.UNKNOWN.getCode()));

	}

	@Test
	public void shouldAuditCronJobStartAndFinishForJobThrowingRuntimeException()
	{
		final String cronJobCode = "testCJWithExc";
		final CronJobModel testCronJob = prepareCronjob(cronJobCode, testJobWithException);
		modelService.save(testCronJob);

		//when
		cronJobService.performCronJob(testCronJob, true);

		//then
		assertThat(cronJobService.isSuccessful(testCronJob)).isFalse();

		Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_NAME_START_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, testJobWithException.getCode()));


		optionalAction = getAuditableAction(AUDIT_NAME_FINISH_CRONJOB);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).contains(entry(AUDIT_CRONJOB, cronJobCode),
				entry(AUDIT_JOB, testJobWithException.getCode()), entry(AUDIT_CRONJOB_STATUS,
						CronJobStatus.ABORTED.getCode()), entry(AUDIT_CRONJOB_RESULT, CronJobResult.ERROR.getCode()));

	}

	private Optional<AuditableActions.Action> getAuditableAction(final String actionName)
	{
		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		return argCaptor.getAllValues().stream().filter(action -> actionName.equals(action.getActionName())).findFirst();
	}

	private CronJobModel prepareCronjob(final String code, final JobModel job)
	{
		final CronJobModel testCJ = modelService.create(CronJobModel.class);
		testCJ.setCode(code);
		testCJ.setJob(job);
		return testCJ;
	}

	private void waitTillCronJobFinished(final CronJobModel testCronJob)
	{
		try
		{
			Awaitility.await().atMost(Duration.ofSeconds(50)).pollInSameThread().until(() -> isTestCronjobFinished(testCronJob));
		}
		catch (final ConditionTimeoutException exc)
		{
			Assert.fail("Cronjob could not finish his job for some reason. Current cronjob status: " + testCronJob.getStatus());
		}
	}

	private void waitTillCronJobStarted(final CronJobModel testCronJob)
	{
		try
		{
			Awaitility.await().atMost(Duration.ofSeconds(5)).pollInSameThread().until(() -> isTestCronjobStarted(testCronJob));
		}
		catch (final ConditionTimeoutException exc)
		{
			Assert.fail("Cronjob could not start his job for some reason. Current cronjob status: " + testCronJob.getStatus());
		}
	}

	private boolean isTestCronjobFinished(final CronJobModel testCronJob)
	{
		modelService.refresh(testCronJob);
		return cronJobService.isFinished(testCronJob);
	}

	private boolean isTestCronjobStarted(final CronJobModel testCronJob)
	{
		modelService.refresh(testCronJob);
		return cronJobService.isRunning(testCronJob);
	}
}
