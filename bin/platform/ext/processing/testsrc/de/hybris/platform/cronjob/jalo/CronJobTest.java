/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cronjob.jalo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.constants.CronJobConstants;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.jalo.CronJob.CronJobResult;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.servicelayer.internal.jalo.ServicelayerJob;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for extension Cronjob
 */
@IntegrationTest
public class CronJobTest extends HybrisJUnit4Test
{
	private static final Logger log = Logger.getLogger(CronJobTest.class);
	private CronJobManager manager;
	private EnumerationValue running, finished, paused;
	//	private EnumerationValue fail, pause, ignore;

	private static final String CRONJOB_11 = "CRONJOB_11";
	private static final String CRONJOB_12 = "CRONJOB_12";
	private static final String CRONJOB_13 = "CRONJOB_13";
	private static final String CRONJOB_14 = "CRONJOB_14";
	private static final String CRONJOB_21 = "CRONJOB_21";
	private static int node1 = 1;
	private static int node2 = 2;

	private static final Collection RUNNING_CRONJOBS = Arrays.asList(new String[]
			{ CRONJOB_11, CRONJOB_12 });

	private CronJob cronJob;

	private final PropertyConfigSwitcher filteredCtxAttributesInImportMode = new PropertyConfigSwitcher(
			"cronjob.ctx.filtered.attributes.in.impex.import.mode");

	private final PropertyConfigSwitcher filteredCtxAttributes = new PropertyConfigSwitcher("cronjob.ctx.filtered.attributes");

	@Before
	public void setUp() throws Exception
	{
		manager = (CronJobManager) jaloSession.getExtensionManager().getExtension(CronJobConstants.EXTENSIONNAME);
		running = jaloSession.getEnumerationManager().getEnumerationValue(CronJobConstants.TC.CRONJOBSTATUS,
				CronJobConstants.Enumerations.CronJobStatus.RUNNING);
		paused = jaloSession.getEnumerationManager().getEnumerationValue(CronJobConstants.TC.CRONJOBSTATUS,
				CronJobConstants.Enumerations.CronJobStatus.PAUSED);
		finished = jaloSession.getEnumerationManager().getEnumerationValue(CronJobConstants.TC.CRONJOBSTATUS,
				CronJobConstants.Enumerations.CronJobStatus.FINISHED);
		//		fail = jaloSession.getEnumerationManager().getEnumerationValue( CronJobConstants.TC.ERRORMODE, CronJobConstants.Enumerations.ErrorMode.FAIL );
		//		pause = jaloSession.getEnumerationManager().getEnumerationValue( CronJobConstants.TC.ERRORMODE, CronJobConstants.Enumerations.ErrorMode.PAUSE );
		//		ignore = jaloSession.getEnumerationManager().getEnumerationValue( CronJobConstants.TC.ERRORMODE, CronJobConstants.Enumerations.ErrorMode.IGNORE );

		// JobRestriction Test
		MediaManager.getInstance().createMedia(MyRestricionTestPerformable.TESTMEDIA1);
		MediaManager.getInstance().createMedia(MyRestricionTestPerformable.TESTMEDIA2);
		cronJob = new CronJob();
	}

	@After
	public void tearDown()
	{
		filteredCtxAttributesInImportMode.switchBackToDefault();
		filteredCtxAttributes.switchBackToDefault();
	}

	@Test
	public void testCronjob()
	{
		// sample tests
		//assertTrue( manager.isTestOK() );
		final BatchJob batchJob = manager.createBatchJob("batchjob");
		assertEquals("batchjob", batchJob.getCode());
		final CronJob cronJob = manager.createCronJob(batchJob, "cronjob", true);
		assertEquals(batchJob, cronJob.getJob());
		assertEquals("cronjob", cronJob.getCode());
		assertEquals(Boolean.TRUE, cronJob.isActive());
		assertNotNull(batchJob.getCronJobs());
		assertEquals(1, batchJob.getCronJobs().size());
		assertEquals(cronJob, batchJob.getCronJobs().iterator().next());
		assertNotNull(batchJob.getCronJobsByCode("cronjob"));
		assertEquals(1, batchJob.getCronJobsByCode("cronjob").size());
		assertEquals(cronJob, batchJob.getCronJobsByCode("cronjob").iterator().next());
	}

	private CronJob createCronJob(final Job job, final String code, final boolean active, final Integer nodeID,
	                              final EnumerationValue status) throws Exception
	{
		final ComposedType type = jaloSession.getTypeManager().getComposedType(CronJob.class);
		final Map values = new HashMap();
		values.put(CronJob.JOB, job);
		values.put(CronJob.CODE, code);
		values.put(CronJob.ACTIVE, Boolean.valueOf(active));
		values.put(CronJob.RUNNINGONCLUSTERNODE, nodeID);

		// values.put( CronJob.STATUS, status);
		final CronJob jobDetail = (CronJob) type.newInstance(values);
		jobDetail.setProperty(CronJob.STATUS, status);
		return jobDetail;
	}

	@Test
	public void testResetRunningCronJobs() throws Exception
	{
		final BatchJob job = manager.createBatchJob("testBatch");
		createCronJob(job, CRONJOB_11, true, node1, running);
		createCronJob(job, CRONJOB_12, true, node1, running);
		createCronJob(job, CRONJOB_13, true, node1, paused);
		createCronJob(job, CRONJOB_14, true, node1, finished);
		final CronJob cronJob21 = createCronJob(job, CRONJOB_21, true, node2, running);
		assertNotNull("job details is null!", manager.getRunningOrRestartedCronJobsForNode(node1));
		assertEquals(2, manager.getRunningOrRestartedCronJobsForNode(node1).size());
		for (final Iterator iter = manager.getRunningOrRestartedCronJobsForNode(node1).iterator(); iter.hasNext(); )
		{
			final String code = ((CronJob) iter.next()).getCode();
			assertTrue("CronJob " + code + " exists!", RUNNING_CRONJOBS.contains(code));
		}
		assertNotNull("cronjobs is null!", manager.getRunningOrRestartedCronJobsForNode(node2));
		assertEquals(1, manager.getRunningOrRestartedCronJobsForNode(node2).size());
		assertEquals(cronJob21, manager.getRunningOrRestartedCronJobsForNode(node2).iterator().next());
		assertTrue(manager.abortRunningCronJobsForClusterNode(node1));
		assertNotNull("cronjobs is null!", manager.getRunningOrRestartedCronJobsForNode(node1));
		assertEquals(0, manager.getRunningOrRestartedCronJobsForNode(node1).size());
		assertTrue(manager.abortRunningCronJobsForClusterNode(node2));
		assertNotNull("cronjobs is null!", manager.getRunningOrRestartedCronJobsForNode(node2));
		assertEquals(0, manager.getRunningOrRestartedCronJobsForNode(node2).size());
	}

	@Test
	public void testResetRunningCronJobAndItsHistory() throws Exception
	{
		final BatchJob job = manager.createBatchJob("testBatch");
		final CronJob cronJob = createCronJob(job, CRONJOB_11, true, node1, running);
		cronJob.createCronJobHistory();

		assertEquals(1, cronJob.getCronJobHistoryEntries().size());
		assertEquals(CronJobStatus.RUNNING.getCode(), cronJob.getActiveCronJobHistory().getStatus().getCode());

		assertTrue(manager.abortRunningCronJobsForClusterNode(node1));

		assertEquals(1, cronJob.getCronJobHistoryEntries().size());
		assertEquals(CronJobStatus.ABORTED.getCode(), cronJob.getActiveCronJobHistory().getStatus().getCode());
	}

	@Test
	public void testResetAllCronJobHistoryEntries() throws Exception
	{
		final PropertyConfigSwitcher switcher = new PropertyConfigSwitcher(CronJobManager.CRON_JOB_HISTORY_RESET_THRESHOLD_HOURS);
		switcher.switchToValue("0");
		final BatchJob job = manager.createBatchJob("testBatch");
		final CronJob cronJob = createCronJob(job, CRONJOB_11, true, node1, running);
		cronJob.createCronJobHistory();
		cronJob.createCronJobHistory();
		cronJob.createCronJobHistory();
		cronJob.createCronJobHistory();

		assertEquals(CronJobStatus.RUNNING.getCode(), cronJob.getActiveCronJobHistory().getStatus().getCode());
		assertTrue(CronJobManager.getInstance().findAllCronJobHistoryEntries().size() > 1);

		CronJobManager.getInstance().findAndFixAllCronJobHistoryEntries();

		assertEquals(CronJobStatus.ABORTED.getCode(), cronJob.getActiveCronJobHistory().getStatus().getCode());
		assertEquals(0, CronJobManager.getInstance().findAllCronJobHistoryEntries().size());
		switcher.switchBackToDefault();
	}

	@Test
	public void testResetAllCronJobHistoryEntriesWithDefaultFlagValue() throws Exception
	{
		final BatchJob job = manager.createBatchJob("testBatch");
		final CronJob cronJob = createCronJob(job, CRONJOB_11, true, node1, running);
		cronJob.createCronJobHistory();

		assertEquals(CronJobStatus.RUNNING.getCode(), cronJob.getActiveCronJobHistory().getStatus().getCode());
		assertEquals(0, CronJobManager.getInstance().findAllCronJobHistoryEntries().size());

		CronJobManager.getInstance().findAndFixAllCronJobHistoryEntries();

		assertEquals(CronJobStatus.RUNNING.getCode(), cronJob.getActiveCronJobHistory().getStatus().getCode());
		assertEquals(0, CronJobManager.getInstance().findAllCronJobHistoryEntries().size());
	}

	@Test
	public void testResetAllCronJobHistoryEntriesWhenNoHistoryEntries() throws Exception
	{
		try
		{
			CronJobManager.getInstance().findAndFixAllCronJobHistoryEntries();
		}
		catch (final Exception e)
		{
			fail("No exception expected but found: " + e.getMessage());
		}
	}

	@Test
	public void testTrigger() throws Exception
	{
		final BatchJob job = manager.createBatchJob("testBatch");
		final CronJob cronJob11 = createCronJob(job, CRONJOB_11, true, node1, running);
		final CronJob cronJob12 = createCronJob(job, CRONJOB_12, true, node1, running);
		manager.createTrigger(cronJob11, 10, true);
		manager.createTrigger(cronJob12, 30, true);
		// whatever
	}

	@Test
	public void testEmailNotification() throws Exception
	{
		// create essential data for doing an import
		new CoreBasicDataCreator().createSupportedEncodings();
		getOrCreateLanguage("de");
		getOrCreateLanguage("en");

		// create essential data for cronjob to create default template
		CronJobManager.getInstance().createEssentialData(Collections.EMPTY_MAP, null);

		// create cronjob without setting a template
		final BatchJob job = manager.createBatchJob("testBatch");
		final CronJob cronJob11 = createCronJob(job, CRONJOB_11, true, node1, running);

		// check if default template was set
		assertNotNull("No default notification template set at cronjob", cronJob11.getEmailNotificationTemplate());
		assertEquals("Assigned notification template is not the default one", CronJobManager.getInstance()
		                                                                                    .getDefaultCronJobFinishNotificationTemplate(),
				cronJob11.getEmailNotificationTemplate());
	}

	@Test
	public void testSetSteps()
	{
		final CronJobManager cronJobManager = CronJobManager.getInstance();
		final BatchJob batchJob1 = cronJobManager.createBatchJob("batchjob1");
		final BatchJob batchJob2 = cronJobManager.createBatchJob("batchjob2");

		final Map values = new HashMap();
		values.put(Step.BATCHJOB, batchJob1);
		values.put(Step.CODE, "step1");
		values.put(Step.SEQUENCENUMBER, Integer.valueOf(1));
		final Step step1 = cronJobManager.createCSVExportStep(values);

		values.put(Step.CODE, "step2");
		values.put(Step.SEQUENCENUMBER, Integer.valueOf(2));
		cronJobManager.createCSVExportStep(values);

		values.put(Step.BATCHJOB, batchJob2);
		values.put(Step.CODE, "step3");
		values.put(Step.SEQUENCENUMBER, Integer.valueOf(1));
		final Step step3 = cronJobManager.createCSVExportStep(values);

		assertTrue(batchJob1.getSteps().size() == 2);
		assertTrue(batchJob2.getSteps().size() == 1);

		batchJob1.setSteps(Arrays.asList(new Step[]
				{ step1, step3 }));

		assertTrue(batchJob1.getSteps().size() == 1);
		assertTrue(batchJob2.getSteps().size() == 1);
	}

	@Test
	public void testJobRestrictions() throws Exception
	{
		ComposedType type = null;
		final TypeManager manager = TypeManager.getInstance();
		type = manager.createComposedType(manager.getComposedType(Job.class), "TestJobRestrictions");
		type.setJaloClass(TestJob.class);
		final Job testJob = (Job) type.newInstance(Collections.singletonMap(Job.CODE, "TestJobRestrictions"));

		((TestJob) testJob).setPerformable(new MyRestricionTestPerformable());

		final CronJob testCronJob = CronJobManager.getInstance().createCronJob(testJob, "TestJobRestrictionsCronJob", true);

		final HashMap attributeValues = new HashMap();
		attributeValues.put(JobSearchRestriction.CODE, "dynamic");
		attributeValues.put(JobSearchRestriction.TYPE, TypeManager.getInstance().getComposedType(Media.class));
		attributeValues.put(JobSearchRestriction.QUERY, "{Media.code} = '" + MyRestricionTestPerformable.TESTMEDIA1 + "'");

		final JobSearchRestriction dynamicRestriction = CronJobManager.getInstance().createJobSearchRestriction(attributeValues);
		testCronJob.getJob().setRestrictions(Arrays.asList(dynamicRestriction));


		testCronJob.getJob().perform(testCronJob, true);

		assertTrue(testCronJob.isFinished());

		assertEquals(
				testCronJob.getResult(),
				EnumerationManager.getInstance().getEnumerationValue(CronJobConstants.TC.CRONJOBRESULT,
						CronJobConstants.Enumerations.CronJobResult.SUCCESS));
	}

	@Test
	/*
	 * PLA-8368
	 */
	public void testTriggerCreationForTriggerableJobs() throws Exception
	{

		final TypeManager manager = TypeManager.getInstance();
		final ComposedType jobType = manager.createComposedType(manager.getComposedType(Job.class), "UnperformableJobType");
		jobType.setJaloClass(UnPerformableJob.class);

		final UnPerformableJob unperformable = (UnPerformableJob) jobType.newInstance(Collections.singletonMap(Job.CODE,
				"MyUnperformableJob"));

		if (ClassUtils.isAssignable(UnPerformableJob.class, TriggerableJob.class)
				|| ClassUtils.isAssignable(UnPerformableJob.class, ServicelayerJob.class))
		{
			final Map values = new HashMap();
			values.put("job", unperformable.getPK());

			// initially there should be no triggers for this triggerable job instance
			assertEquals(Collections.emptyList(), unperformable.getTriggers());
			// initially there should be no assigned cronjob for this triggerable job instance
			assertEquals(Collections.emptyList(), unperformable.getCronJobs());

			final Trigger trigger = createTriggerForJobNow(unperformable);
			trigger.activate();

			Thread.sleep(2000);

			final Collection<CronJob> cronJobs = unperformable.getCronJobs();
			assertEquals(1, cronJobs.size());
			final CronJob newlyCreateCronjob = cronJobs.iterator().next();

			final List<Trigger> cronjobTriggers = newlyCreateCronjob.getTriggers();
			assertEquals(1, cronjobTriggers.size());
		}
	}

	private Trigger createTriggerForJobNow(final Job job)
	{
		final Calendar cal = Utilities.getDefaultCalendar();
		cal.setTime(new Date());

		final Map<String, Object> attributes = new HashMap();
		attributes.put(Trigger.JOB, job);
		attributes.put(Trigger.SECOND, Integer.valueOf(cal.get(Calendar.SECOND)));
		attributes.put(Trigger.MINUTE, Integer.valueOf(cal.get(Calendar.MINUTE)));
		attributes.put(Trigger.HOUR, Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		attributes.put(Trigger.DAY, Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
		attributes.put(Trigger.MONTH, Integer.valueOf(cal.get(Calendar.MONTH)));
		attributes.put(Trigger.YEAR, Integer.valueOf(cal.get(Calendar.YEAR)));
		attributes.put(Trigger.RELATIVE, Boolean.FALSE);
		attributes.put(Trigger.DAYSOFWEEK, null);

		return CronJobManager.getInstance().createTrigger(attributes);
	}

	@Test
	public void testCompositeCronJob() throws Exception
	{
		// YTODO
		// deactivated, because this test fails in an undeterministic way

		final TypeManager manager = TypeManager.getInstance();
		final ComposedType compositeJobType = manager.getComposedType(CompositeJob.class);
		final Job compositeJob = (Job) compositeJobType.newInstance(Collections.singletonMap(Job.CODE, "MyCompositeJob"));

		final HashMap attributeValues = new HashMap();
		attributeValues.put(CronJob.JOB, compositeJob);
		attributeValues.put(CronJob.CODE, "CompositeCronJob");
		attributeValues.put(CronJob.ACTIVE, Boolean.TRUE);
		final CompositeCronJob compositeCronJob = CronJobManager.getInstance().createCompositeCronJob(attributeValues);

		final List<CompositeEntry> entries = new ArrayList<CompositeEntry>();

		//
		// TestJob generation
		//
		final ComposedType jobType = manager.createComposedType(manager.getComposedType(Job.class), "TestCompositeJob");
		jobType.setJaloClass(TestJob.class);

		// Cron Job

		// Job 1
		final Job testJob1 = (Job) jobType.newInstance(Collections.singletonMap(Job.CODE, "TestCompositeJob1"));
		((TestJob) testJob1).setPerformable(new MyCompositeTestPerformable());
		testJob1.createCronjob();
		testJob1.setTransientObject("value", Long.valueOf(1));

		attributeValues.clear();
		attributeValues.put(CompositeEntry.CODE, "job1");
		attributeValues.put(CompositeEntry.TRIGGERABLEJOB, testJob1);

		entries.add(CronJobManager.getInstance().createCompositeEntry(attributeValues));

		// Job 2
		final Job testJob2 = (Job) jobType.newInstance(Collections.singletonMap(Job.CODE, "TestCompositeJob2"));
		((TestJob) testJob2).setPerformable(new MyCompositeTestPerformable());
		testJob2.createCronjob();
		testJob2.setTransientObject("value", Long.valueOf(10));

		attributeValues.clear();
		attributeValues.put(CompositeEntry.CODE, "job2");
		attributeValues.put(CompositeEntry.TRIGGERABLEJOB, testJob2);

		entries.add(CronJobManager.getInstance().createCompositeEntry(attributeValues));

		// Job 3
		final Job testJob3 = (Job) jobType.newInstance(Collections.singletonMap(Job.CODE, "TestCompositeJob3"));
		((TestJob) testJob3).setPerformable(new MyCompositeTestPerformable());
		testJob3.createCronjob();
		testJob3.setTransientObject("value", Long.valueOf(100));

		attributeValues.clear();
		attributeValues.put(CompositeEntry.CODE, "job3");
		attributeValues.put(CompositeEntry.TRIGGERABLEJOB, testJob3);

		entries.add(CronJobManager.getInstance().createCompositeEntry(attributeValues));

		// Cron Job / Job 4
		final Job testJob4 = (Job) jobType.newInstance(Collections.singletonMap(Job.CODE, "TestCompositeJob4"));
		((TestJob) testJob4).setPerformable(new MyCompositeTestPerformable());
		testJob4.createCronjob();
		testJob4.setTransientObject("value", Long.valueOf(1000));
		final CronJob testCronJob = CronJobManager.getInstance().createCronJob(testJob4, "TestCompositeJob4", true);

		attributeValues.clear();
		attributeValues.put(CompositeEntry.CODE, "job4");
		attributeValues.put(CompositeEntry.EXECUTABLECRONJOB, testCronJob);

		entries.add(CronJobManager.getInstance().createCompositeEntry(attributeValues));

		compositeCronJob.setCompositeEntries(entries);

		compositeJob.perform(compositeCronJob);

		Thread.sleep(1000);

		while (compositeCronJob.isRunning()
				|| (!testJob1.getCronJobs().isEmpty() && testJob1.getCronJobs().iterator().next().isRunning())
				|| (!testJob2.getCronJobs().isEmpty() && testJob2.getCronJobs().iterator().next().isRunning())
				|| (!testJob3.getCronJobs().isEmpty() && testJob3.getCronJobs().iterator().next().isRunning())
				|| (!testJob4.getCronJobs().isEmpty() && testJob4.getCronJobs().iterator().next().isRunning())
				|| (testCronJob.isRunning()))
		{
			Thread.sleep(2000);
		}

		final long sum = ((Long) testJob1.getTransientObject("value")).longValue()
				+ ((Long) testJob2.getTransientObject("value")).longValue()
				+ ((Long) testJob3.getTransientObject("value")).longValue()
				+ ((Long) testCronJob.getJob().getTransientObject("value")).longValue();

		assertEquals(sum, 2222);
	}

	@Test
	public void testLogCronJob() throws Exception
	{
		final long count = 10000000;

		/*
		 * StopWatch w = new StopWatch("nano"); for( int i = 0; i < count; i++ ) { System.nanoTime(); } w.stop(); w = new
		 * StopWatch("milli"); for( int i = 0; i < count; i++ ) { System.currentTimeMillis(); } w.stop(); if(true)return;
		 */

		log.isDebugEnabled();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			log.isDebugEnabled();
		}
		final long endWithout = (System.currentTimeMillis() - start);
		log.info("Finished " + count + " calls of isDebugEnabled in " + endWithout + "ms");

		ComposedType type = null;
		final TypeManager manager = TypeManager.getInstance();
		type = manager.createComposedType(manager.getComposedType(Job.class), "TestLogCronJob");
		type.setJaloClass(TestJob.class);
		final Job testJob = (Job) type.newInstance(Collections.singletonMap(Job.CODE, "testLogCronJob"));
		final CronJob testCronJob = CronJobManager.getInstance().createCronJob(testJob, "testLogCronJob", true);
		testCronJob.setProperty("count", Long.valueOf(count));

		testCronJob.getJob().perform(testCronJob, true);

		assertTrue(testCronJob.isFinished());

		final long endWith = ((Long) testCronJob.getProperty("time")).longValue();
		log.info("Finished " + count + " calls of isDebugEnabled within Job in " + endWith + "ms");
		assertTrue("logging within cronJob does take " + endWith + "ms and without " + endWithout
						+ "ms, the difference is too big (>x30)\nExecution time difference " + endWith + " ms - " + endWithout + " ms = "
						+ (endWith - endWithout) + " ms (" + (((double) endWith) / endWithout) + ")",
				endWith < endWithout * 30);
	}

	// TODO: To be checked, ignored for now to make bamboo happy
	@Test
	public void testNestedExecution() throws Exception
	{
		final TypeManager manager = TypeManager.getInstance();
		ComposedType jobType1;
		jobType1 = manager.createComposedType(manager.getComposedType(Job.class), "TestNestedExecutionJob");
		jobType1.setJaloClass(TestJob.class);
		ComposedType jobType2;
		jobType2 = manager.createComposedType(manager.getComposedType(Job.class), "TestNestedExecutionJob2");
		jobType2.setJaloClass(SyncExcutionTestJob.class);

		Job nested;
		nested = (Job) jobType1.newInstance(Collections.singletonMap(Job.CODE, "nested"));
		CronJob nestedCJ;
		nestedCJ = CronJobManager.getInstance().createCronJob(nested, "nestedCJ", true);

		Job outer;
		outer = (Job) jobType2.newInstance(Collections.singletonMap(Job.CODE, "outer"));
		CronJob outerCJ;
		outerCJ = CronJobManager.getInstance().createCronJob(nested, "outerCJ", true);

		outerCJ.setProperty("nested", nestedCJ);
		outerCJ.setLogToDatabase(true);
		outerCJ.setLogLevelDatabase(outerCJ.getWarnLogLevel());
		nestedCJ.setLogToDatabase(true);
		nestedCJ.setLogLevelDatabase(outerCJ.getWarnLogLevel());
		nestedCJ.setProperty("count", Long.valueOf(30000l));

		/*
		 * 1) perform asynchronously
		 */
		outer.perform(outerCJ, false);

		while (!outerCJ.isFinished())
		{
			Thread.sleep(1000);
		}

		assertFalse(CronJob.hasCurrentlyExecutingCronJob());
		assertFalse(Job.jobIsCurrentlyRunning());

		assertTrue(outerCJ.isFinished());
		assertTrue(nestedCJ.isFinished());
		assertEquals(outerCJ.getSuccessResult(), outerCJ.getResult());
		assertEquals(outerCJ.getSuccessResult(), nestedCJ.getResult());
		assertNotNull(outerCJ.getStartTime());
		assertNotNull(outerCJ.getEndTime());
		assertTrue(outerCJ.getEndTime().getTime() >= outerCJ.getStartTime().getTime());

		boolean foundOuter = false;
		boolean foundNested = false;
		for (final JobLog log : outerCJ.getLogs())
		{
			if ("outer".equals(log.getMessage()))
			{
				foundOuter = true;
			}
			if ("nested".equals(log.getMessage()))
			{
				foundNested = true;
			}
			log.remove();
		}
		assertTrue(foundOuter);
		assertFalse(foundNested);

		foundOuter = false;
		foundNested = false;
		for (final JobLog log : nestedCJ.getLogs())
		{
			if ("outer".equals(log.getMessage()))
			{
				foundOuter = true;
			}
			if ("nested".equals(log.getMessage()))
			{
				foundNested = true;
			}
			log.remove();
		}
		assertFalse(foundOuter);
		assertTrue(foundNested);

		outerCJ.setResult(null);
		nestedCJ.setResult(null);

		/*
		 * 2) perform synchronously
		 */
		outer.perform(outerCJ, true);

		assertFalse(CronJob.hasCurrentlyExecutingCronJob());
		assertFalse(Job.jobIsCurrentlyRunning());

		assertTrue(outerCJ.isFinished());
		assertTrue(nestedCJ.isFinished());
		assertEquals(outerCJ.getSuccessResult(), outerCJ.getResult());
		assertEquals(outerCJ.getSuccessResult(), nestedCJ.getResult());
		assertNotNull(outerCJ.getStartTime());
		assertNotNull(outerCJ.getEndTime());
		assertTrue(outerCJ.getEndTime().getTime() >= outerCJ.getStartTime().getTime());

		foundOuter = false;
		foundNested = false;
		for (final JobLog log : outerCJ.getLogs())
		{
			if ("outer".equals(log.getMessage()))
			{
				foundOuter = true;
			}
			if ("nested".equals(log.getMessage()))
			{
				foundNested = true;
			}
			log.remove();
		}
		assertTrue(foundOuter);
		assertFalse(foundNested);

		foundOuter = false;
		foundNested = false;
		for (final JobLog log : nestedCJ.getLogs())
		{
			if ("outer".equals(log.getMessage()))
			{
				foundOuter = true;
			}
			if ("nested".equals(log.getMessage()))
			{
				foundNested = true;
			}
			log.remove();
		}
		assertFalse(foundOuter);
		assertTrue(foundNested);

		outerCJ.setResult(null);
		nestedCJ.setResult(null);

	}

	class MyRestricionTestPerformable implements TestJob.Performable
	{
		public static final String TESTMEDIA1 = "DYNAMIC_RESTRICTION_1.jpg";
		public static final String TESTMEDIA2 = "DYNAMIC_RESTRICTION_2.jpg";

		@Override
		public CronJobResult perform(final CronJob cronJob)
		{
			if (MediaManager.getInstance().getAllMedia().size() == 1)
			{
				return cronJob.getFinishedResult(true);
			}
			return cronJob.getFinishedResult(false);
		}
	}

	class MyCompositeTestPerformable implements TestJob.Performable
	{
		@Override
		public CronJobResult perform(final CronJob cronJob)
		{
			{
				final long count = ((Long) cronJob.getJob().getTransientObject("value")).longValue();
				try
				{
					Thread.sleep(5000);
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				cronJob.getJob().setTransientObject("value", Long.valueOf(2 * count));
				return cronJob.getFinishedResult(true);
			}
		}
	}

	// see PLA-13735 - should filter
	// dont.change.existing.links, disable.attribute.check, transaction_in_create_disabled, core.types.creation.initial, catalog.sync.active
	@Test
	public void testSessionContextAttributeFiltering() throws JaloInvalidParameterException, JaloSecurityException,
			JaloBusinessException
	{
		final Job job = CronJobManager.getInstance().createBatchJob("DummyJob");
		final CronJob cronJob = CronJobManager.getInstance().createCronJob(job, "testCronJob", false);
		cronJob.setAttribute(CronJob.SESSIONCONTEXTVALUES, null);

		assertEquals(jaloSession.getUser(), cronJob.getSessionUser());
		assertEquals(jaloSession.getSessionContext().getLanguage(), cronJob.getSessionLanguage());
		assertNull(cronJob.getAttribute(CronJob.SESSIONCONTEXTVALUES));

		{
			final JaloSession newSession = JaloConnection.getInstance().createAnonymousCustomerSession();
			applyToSession(cronJob, newSession);
			assertEquals(jaloSession.getUser(), newSession.getUser());
			assertEquals(jaloSession.getSessionContext().getLanguage(), newSession.getSessionContext().getLanguage());
			assertNull(newSession.getSessionContext().getAttribute("dont.change.existing.links"));
			assertNull(newSession.getSessionContext().getAttribute("disable.attribute.check"));
			assertNull(newSession.getSessionContext().getAttribute("transaction_in_create_disabled"));
			assertNull(newSession.getSessionContext().getAttribute("core.types.creation.initial"));
			assertNull(newSession.getSessionContext().getAttribute("catalog.sync.active"));
			newSession.close();
		}


		{
			final Map<String, Object> evilContext = new HashMap<>();
			evilContext.put("dont.change.existing.links", Boolean.TRUE);
			evilContext.put("disable.attribute.check", Boolean.TRUE);
			evilContext.put("transaction_in_create_disabled", Boolean.TRUE);
			evilContext.put("core.types.creation.initial", Boolean.TRUE);
			evilContext.put("catalog.sync.active", Boolean.TRUE);
			evilContext.put("useful.param", "hello");
			cronJob.setAttribute(CronJob.SESSIONCONTEXTVALUES, evilContext);

			final JaloSession newSession = JaloConnection.getInstance().createAnonymousCustomerSession();
			applyToSession(cronJob, newSession);
			assertEquals(jaloSession.getUser(), newSession.getUser());
			assertEquals(jaloSession.getSessionContext().getLanguage(), newSession.getSessionContext().getLanguage());
			assertNull(newSession.getSessionContext().getAttribute("dont.change.existing.links"));
			assertNull(newSession.getSessionContext().getAttribute("disable.attribute.check"));
			assertNull(newSession.getSessionContext().getAttribute("transaction_in_create_disabled"));
			assertNull(newSession.getSessionContext().getAttribute("core.types.creation.initial"));
			assertNull(newSession.getSessionContext().getAttribute("catalog.sync.active"));
			assertEquals("hello", newSession.getSessionContext().getAttribute("useful.param"));
			newSession.close();
		}

		final String backup = Config.getParameter(CronJob.CFG_FILTERED_CTX_ATTRIBUTES);
		try
		{
			Config.setParameter(CronJob.CFG_FILTERED_CTX_ATTRIBUTES, "foo,bar,,,blubb");

			final Map<String, Object> evilContext = new HashMap<>();
			evilContext.put("dont.change.existing.links", Boolean.TRUE);
			evilContext.put("foo", Boolean.TRUE);
			evilContext.put("bar", Boolean.TRUE);
			evilContext.put("blubb", Boolean.TRUE);
			evilContext.put("useful.param", "hello");
			cronJob.setAttribute(CronJob.SESSIONCONTEXTVALUES, evilContext);

			final JaloSession newSession = JaloConnection.getInstance().createAnonymousCustomerSession();
			applyToSession(cronJob, newSession);
			assertEquals(jaloSession.getUser(), newSession.getUser());
			assertEquals(jaloSession.getSessionContext().getLanguage(), newSession.getSessionContext().getLanguage());
			assertEquals(newSession.getSessionContext().getAttribute("dont.change.existing.links"), Boolean.TRUE);
			assertNull(newSession.getSessionContext().getAttribute("foo"));
			assertNull(newSession.getSessionContext().getAttribute("bar"));
			assertNull(newSession.getSessionContext().getAttribute("blubb"));
			assertEquals("hello", newSession.getSessionContext().getAttribute("useful.param"));
			newSession.close();
		}
		finally
		{
			Config.setParameter(CronJob.CFG_FILTERED_CTX_ATTRIBUTES, backup);
		}
	}

	static void applyToSession(final CronJob cronJob, final JaloSession session)
	{
		// Hint: only works due to being in the same package *and* class loader!
		cronJob.createSessionForCronJob(session);
	}

	@Test
	public void shouldFilterRedundantSessionCtxAttributesIfImportModeIsTrue()
	{
		//given
		filteredCtxAttributesInImportMode.switchToValue(convertMapToKeysString(createRedundantSessionCtxAttributes()));
		final Map<String, Object> sessionContextAttributes = getValidAndRedundantSessionCtxAttributes();
		sessionContextAttributes.put(CronJob.IMPORT_MODE, Boolean.TRUE);

		//when
		final Map<String, Object> filteredSessionContextAttributes = cronJob.filterSessionCtxAttributesAddedDuringImportCronJobByImpex(
				sessionContextAttributes);

		//then
		assertThat(filteredSessionContextAttributes).containsKeys(convertMapToKeysArray(createValidSessionCtxAttributes()))
		                                            .doesNotContainKeys(
				                                            convertMapToKeysArray(createRedundantSessionCtxAttributes()));
	}

	@Test
	public void shouldNotFilterSessionCtxAttributesIfImportModeIsFalse()
	{
		//given
		filteredCtxAttributesInImportMode.switchToValue(convertMapToKeysString(createRedundantSessionCtxAttributes()));
		final Map<String, Object> sessionContextAttributes = getValidAndRedundantSessionCtxAttributes();
		sessionContextAttributes.put(CronJob.IMPORT_MODE, Boolean.FALSE);

		//when, then
		shouldNotFilterSessionCtxAttributes(sessionContextAttributes);
	}

	@Test
	public void shouldNotFilterSessionCtxAttributesIfThereIsNoImportModeAttribute()
	{
		//given
		filteredCtxAttributesInImportMode.switchToValue(convertMapToKeysString(createRedundantSessionCtxAttributes()));
		final Map<String, Object> sessionContextAttributes = getValidAndRedundantSessionCtxAttributes();
		sessionContextAttributes.remove(CronJob.IMPORT_MODE);

		//when, then
		shouldNotFilterSessionCtxAttributes(sessionContextAttributes);
	}

	@Test
	public void shouldNotFilterSessionCtxAttributesIfPropertyIsEmpty()
	{
		//given
		filteredCtxAttributesInImportMode.switchToValue(StringUtils.EMPTY);
		final Map<String, Object> sessionContextAttributes = getValidAndRedundantSessionCtxAttributes();
		sessionContextAttributes.put(CronJob.IMPORT_MODE, Boolean.TRUE);

		//when, then
		shouldNotFilterSessionCtxAttributes(sessionContextAttributes);
	}

	@Test
	public void shouldCreateCronJobInImportModeWithoutRedundantSessionCtxAttributes()
	{
		//given
		filteredCtxAttributesInImportMode.switchToValue(convertMapToKeysString(createRedundantSessionCtxAttributes()));
		final SessionContext sessionContext = jaloSession.getSessionContext();
		sessionContext.addAllAttributes(createRedundantSessionCtxAttributes());

		//when
		sessionContext.setAttribute(CronJob.IMPORT_MODE, Boolean.FALSE);
		final CronJob withoutFilteredCtxAttributes = createCronJob();

		sessionContext.setAttribute(CronJob.IMPORT_MODE, Boolean.TRUE);
		final CronJob withFilteredCtxAttributes = createCronJob();

		//then
		final String[] redundantCtxAttributesKeys = convertMapToKeysArray(createRedundantSessionCtxAttributes());
		assertThat(withoutFilteredCtxAttributes.getSessionContextValues()).containsKeys(redundantCtxAttributesKeys);
		assertThat(withFilteredCtxAttributes.getSessionContextValues()).doesNotContainKeys(redundantCtxAttributesKeys);
	}

	@Test
	public void shouldFilterRedundantSessionContextValuesAndNotAddThemToSessionContext()
	{
		//given
		final Map<String, Object> redundantAttributes = createRedundantSessionCtxAttributes();
		redundantAttributes.put(CronJob.IMPORT_MODE, Boolean.TRUE);
		final String redundantAttributesAddedDuringImport = String.format("redundantAttribute1,redundantAttribute2,%s", CronJob.IMPORT_MODE);
		final String otherRedundantAttributes = "redundantAttribute3,redundantAttribute4";
		filteredCtxAttributesInImportMode.switchToValue(redundantAttributesAddedDuringImport);
		filteredCtxAttributes.switchToValue(otherRedundantAttributes);

		final CronJob cronJob = createCronJob();
		cronJob.setSessionContextValues(redundantAttributes);

		//when
		final JaloSession sessionForCronJob = cronJob.createSessionForCronJob(jaloSession);

		//then
		assertThat(sessionForCronJob.getSessionContext().getAttributes()).doesNotContainKeys(convertMapToKeysArray(redundantAttributes));
	}

	@Test
	public void shouldNotFilterSessionContextValuesIfHaveNotBeenAddedDuringImport()
	{
		//given
		final Map<String, Object> sessionCtxAttributes = getValidAndRedundantSessionCtxAttributes();
		filteredCtxAttributesInImportMode.switchToValue(convertMapToKeysString(createValidSessionCtxAttributes()));
		filteredCtxAttributes.switchToValue(convertMapToKeysString(createRedundantSessionCtxAttributes()));

		final CronJob cronJob = createCronJob();
		cronJob.setSessionContextValues(sessionCtxAttributes);

		//when
		final JaloSession sessionForCronJob = cronJob.createSessionForCronJob(jaloSession);

		//then
		final Map<String, Object> attributesAddedToSession = sessionForCronJob.getSessionContext().getAttributes();
		assertThat(attributesAddedToSession).containsKeys(convertMapToKeysArray(createValidSessionCtxAttributes()))
		                                    .doesNotContainKeys(convertMapToKeysArray(createRedundantSessionCtxAttributes()));
	}

	private CronJob createCronJob()
	{
		final Job job = manager.createBatchJob("testJob" + UUID.randomUUID());
		return manager.createCronJob(job, "testCronJob" + UUID.randomUUID(), false);
	}

	private void shouldNotFilterSessionCtxAttributes(final Map<String, Object> sessionContextAttributes)
	{
		//when
		final Map<String, Object> filteredSessionContextAttributes = cronJob.filterSessionCtxAttributesAddedDuringImportCronJobByImpex(
				sessionContextAttributes);

		//then
		assertThat(filteredSessionContextAttributes).containsKeys(convertMapToKeysArray(sessionContextAttributes));
	}

	private String[] convertMapToKeysArray(final Map<String, Object> map)
	{
		return map.keySet().toArray(new String[0]);
	}

	private String convertMapToKeysString(final Map<String, Object> map)
	{
		return String.join(",", convertMapToKeysArray(map));
	}

	private Map<String, Object> getValidAndRedundantSessionCtxAttributes()
	{
		final Map<String, Object> sessionContextAttributes = new HashMap<>();
		sessionContextAttributes.putAll(createValidSessionCtxAttributes());
		sessionContextAttributes.putAll(createRedundantSessionCtxAttributes());

		return sessionContextAttributes;
	}

	private Map<String, Object> createValidSessionCtxAttributes()
	{
		final Map<String, Object> validSessionCtxAttributes = new HashMap<>();
		validSessionCtxAttributes.put("validAttribute1", "test");
		validSessionCtxAttributes.put("validAttribute2", "test");
		validSessionCtxAttributes.put("validAttribute3", "test");

		return validSessionCtxAttributes;
	}

	private Map<String, Object> createRedundantSessionCtxAttributes()
	{
		final Map<String, Object> redundantSessionCtxAttributes = new HashMap<>();
		redundantSessionCtxAttributes.put("redundantAttribute1", "test");
		redundantSessionCtxAttributes.put("redundantAttribute2", "test");
		redundantSessionCtxAttributes.put("redundantAttribute3", "test");
		redundantSessionCtxAttributes.put("redundantAttribute4", "test");

		return redundantSessionCtxAttributes;
	}
}
