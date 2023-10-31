/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task;

import static de.hybris.platform.task.StrandedTasksTest.DbOutageTestTaskRunner.DB_OUTAGE_TASK_RUNNER_BEAN_ID;
import static de.hybris.platform.task.impl.DefaultTaskExecutionStrategy.TASK_RETRY_DELAY_CONFIG_KEY;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.DBOutageSimulator;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.jdbcwrapper.JUnitJDBCConnectionPool;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.task.impl.DefaultTaskExecutionStrategy;
import de.hybris.platform.task.impl.StrandedTasksLogic;
import de.hybris.platform.task.impl.StrandedTasksResolutionHandler;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert;
import de.hybris.platform.testframework.log.TestLogListener;
import de.hybris.platform.tx.TransactionConnectionBrokenException;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

@IntegrationTest
public class StrandedTasksTest extends ServicelayerBaseTest
{

	private static final Logger LOG = Logger.getLogger(StrandedTasksTest.class);

	private final TestLogListener testLogListener = new TestLogListener();
	@Resource
	private ModelService modelService;
	@Resource
	private TaskService taskService;
	@Resource
	private StrandedTasksLogic strandedTasksLogic;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	private DBOutageSimulator dbOutageSimulator;
	private JUnitJDBCConnectionPool connectionPool;

	private final PropertyConfigSwitcher triggerRetryIntervalSeconds = new PropertyConfigSwitcher(
			TASK_RETRY_DELAY_CONFIG_KEY);

	@Before
	public void setUp()
	{
		final HybrisDataSource dataSource = getDataSource();
		assertThat(dataSource.getConnectionPool()).isInstanceOf(JUnitJDBCConnectionPool.class);
		connectionPool = (JUnitJDBCConnectionPool) dataSource.getConnectionPool();
		dbOutageSimulator = new DBOutageSimulator(connectionPool);
		testLogListener.attach();
	}

	@After
	public void tearDown()
	{
		connectionPool.resetTestMode();
		cleanUpDbOutageTaskBeenIfNeeded();
		testLogListener.detach();
	}

	@Test
	public void shouldMarkAsFailedStrandedTaskAfterDbOutage() throws InterruptedException
	{
		final TaskModel processedTaskModel = runScenarioAndReturnProcessedTask(ErrorType.NONE, 1, "Marked as failed task with pk: ");

		// TASK SHOULD BE PRESENT ON DB AND MARKED AS FAILED
		final Optional<TaskModel> taskOnDb = findTaskOnDb(processedTaskModel.getPk());
		assertThat(taskOnDb).isPresent();
		assertThat(taskOnDb.get().getFailed()).isTrue();
	}

	@Test
	public void shouldUnlockStrandedTaskAfterDbOutage() throws InterruptedException
	{
		runTestScenario(ErrorType.FAIL, "Unlocked task with pk: ");
	}

	@Test
	public void shouldRetryStrandedTaskAfterDbOutage() throws InterruptedException
	{
		runTestScenario(ErrorType.RETRY, "Marked to retry task with pk: ","Unlocked task with pk: ");
	}

	@Test
	public void shouldRetryTaskIfTransactionExceptionThrown() throws InterruptedException
	{
		runTestScenario(ErrorType.TX_EXCEPTION, "Database connection has been marked as errored. Retrying task. ");
	}

	private void runTestScenario(final ErrorType errorType, final String... errorMessage) throws InterruptedException
	{
		final TaskModel processedTaskModel = runScenarioAndReturnProcessedTask(errorType, 2, errorMessage);
		// TASK AFTER RETRY SHOULD NOT BE AVAILABLE ON DB
		final Optional<TaskModel> taskAfterUnlock = findTaskOnDb(processedTaskModel.getPk());
		assertThat(taskAfterUnlock).isNotPresent();
	}

	private TaskModel runScenarioAndReturnProcessedTask(final ErrorType errorType, final int expectedTaskRunNumber,
	                                                    final String ... strandedTaskHandlerLogMsg) throws InterruptedException
	{
		//stranded tasks registry should be empty
		final AtomicInteger runCounter = registerDbOutageTestTaskBeanAndReturnTaskRunCounter(errorType);
		Class expectedLoggingClass = StrandedTasksResolutionHandler.class;
		if(errorType.equals(ErrorType.TX_EXCEPTION))
		{
			triggerRetryIntervalSeconds.switchToValue("10");
			expectedLoggingClass = DefaultTaskExecutionStrategy.class;
		}
		assertThat(runCounter.get()).isZero();

		// create task
		final TaskModel task = prepareTask();

		return validateTaskErrorHandling(task, runCounter, expectedTaskRunNumber, expectedLoggingClass,
				strandedTaskHandlerLogMsg);
	}

	private TaskModel validateTaskErrorHandling(final TaskModel task, final AtomicInteger runCounter, final int expectedTaskRunNumber,
												final Class expectedLoggingClass, final String ... strandedTaskHandlerLogMsg)
			throws InterruptedException
	{
		// task should be at this moment on DB
		final Optional<TaskModel> taskOnDb = findTaskOnDb(task.getPk());
		assertThat(taskOnDb).isPresent();
		assertThat(taskOnDb.get().getPk()).isEqualTo(task.getPk());

		// task should not be marked as failed
		assertThat(taskOnDb.get().getFailed()).isFalse();

		// run task instantly
		taskService.scheduleTask(task);

		// let the task execute
		await().until(() -> runCounter.get() != 0);
		assertThat(runCounter.get()).isEqualTo(1);

		// simulate db outage
		dbOutageSimulator.simulateDBOutage(Duration.ofSeconds(15));

		// wait for fail during state change on db
		waitFor(30, TimeUnit.SECONDS);

		// stranded task should be handled by the resolution handler
		for(final String strandedLogMsg: strandedTaskHandlerLogMsg)
		{
			TestLogListenerAssert.assertThat(testLogListener)
			                     .hasLog()
			                     .withMessageContaining(strandedLogMsg + task.getPk())
			                     .loggedFrom(expectedLoggingClass)
			                     .withLogLevel(TestLogListenerAssert.LogLevel.INFO)
			                     .occurrences(1);
		}

		// task should be invoked expectedTaskRunNumber times
		assertThat(runCounter.get()).isEqualTo(expectedTaskRunNumber);

		// finally stranded tasks registry should be empty
		assertThat(strandedTasksLogic.getStrandedItemsRegistry().getStrandedItems()).isEmpty();
		return task;
	}

	private Optional<TaskModel> findTaskOnDb(final PK pk)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {PK} FROM {Task} WHERE {pk}=?pk_value");
		query.addQueryParameter("pk_value", pk.getLong());
		try
		{
			final TaskModel searchResult = flexibleSearchService.searchUnique(query);
			return Optional.of(searchResult);
		}
		catch (final ModelNotFoundException exception)
		{
			return Optional.empty();
		}
	}

	private AtomicInteger registerDbOutageTestTaskBeanAndReturnTaskRunCounter(final ErrorType errorType)
	{
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Registry.getCoreApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		destroyBeanIfPresent(beanFactory);

		final AtomicInteger runCounter = new AtomicInteger(0);
		beanFactory.registerSingleton(DB_OUTAGE_TASK_RUNNER_BEAN_ID, new DbOutageTestTaskRunner(runCounter, errorType));
		return runCounter;
	}

	private HybrisDataSource getDataSource()
	{
		return Registry.getCurrentTenant().getDataSource();
	}

	private void cleanUpDbOutageTaskBeenIfNeeded()
	{
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Registry.getCoreApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		destroyBeanIfPresent(beanFactory);
	}

	private void destroyBeanIfPresent(final DefaultListableBeanFactory beanFactory)
	{
		try
		{
			beanFactory.destroySingleton(DB_OUTAGE_TASK_RUNNER_BEAN_ID);
		}
		catch (final Exception ignored)
		{
			// NO BEAN TO DESTROY
		}
	}

	private TaskModel prepareTask()
	{
		final TaskModel task = modelService.create(TaskModel.class);

		task.setRunnerBean(DB_OUTAGE_TASK_RUNNER_BEAN_ID); // the action bean name
		task.setExecutionDate(new Date());  // the execution time - here asap
		modelService.save(task);
		return task;
	}

	private void waitFor(final long timeout, final TimeUnit timeUnit) throws InterruptedException
	{
		try
		{
			timeUnit.sleep(timeout);
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw e;
		}
	}

	class DbOutageTestTaskRunner implements TaskRunner<TaskModel>
	{
		static String DB_OUTAGE_TASK_RUNNER_BEAN_ID = "dbOutageTaskRunner";
		private final AtomicInteger runCounter;
		private final ErrorType errorType;
		private boolean retryExceptionWasThrownOnce = false;
		public DbOutageTestTaskRunner(final AtomicInteger runCounter, final ErrorType errorType)
		{
			this.runCounter = runCounter;
			this.errorType = errorType;
		}

		@Override
		public void run(final TaskService taskService, final TaskModel task) throws RetryLaterException
		{
			runCounter.incrementAndGet();
			try
			{
				waitFor(2, TimeUnit.SECONDS);
			}
			catch (final InterruptedException e)
			{
				throw new RuntimeException(e);
			}
			if (errorType.equals(ErrorType.FAIL))
			{
				throw new RuntimeException("Task failed internally");
			}
			if (errorType.equals(ErrorType.RETRY))
			{
				if (!retryExceptionWasThrownOnce)
				{
					LOG.info("Throwing RetryLaterException");
					retryExceptionWasThrownOnce = true;
					final RetryLaterException reLater = new RetryLaterException();
					reLater.setDelay(1000);
					throw reLater;
				}
				else
				{
					LOG.info("Executing empty action");
				}
			}
			if(errorType.equals(ErrorType.TX_EXCEPTION))
			{
				if(runCounter.get() == 1)
				{
					LOG.info("First time DBOutageTransactionException test has executed so throwing exception to simulate" +
							"DB failure: " + task.getPk());
					throw new TransactionConnectionBrokenException("A test transaction connection broken exception");
				}
				else
				{
					LOG.info("Retrying DBOutageTransactionException test so not throwing exception");
				}
			}
		}

		@Override
		public void handleError(final TaskService taskService, final TaskModel task, final Throwable error)
		{
			// EMPTY FOR TEST CASE
		}

	}

	enum ErrorType
	{
		FAIL, RETRY, NONE, TX_EXCEPTION
	}
}
