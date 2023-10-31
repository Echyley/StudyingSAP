/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task.impl;

import static de.hybris.platform.task.constants.TaskConstants.Params.POLLING_INTERVAL;
import static de.hybris.platform.task.constants.TaskConstants.Params.WORKERS_MAX;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for {@link DefaultTaskService}
 * Determines whether the poll method is working correctly, depending on the scheduling state of the repoll task.
 */
@IntegrationTest
public class DefaultTaskServicePollTest extends DefaultTaskServiceBaseTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTaskServicePollTest.class);
	private static final Duration LATCH_WAIT_TIMEOUT = Duration.ofMinutes(2);
	private static final int POLL_INTERVAL = 2;
	private static final int MAX_WORKERS = 4;
	private static final int MAX_ITEMS_TO_SCHEDULE = 10;
	private static final int NODE_ID = 0;
	private static final boolean DISABLED_EXCLUSIVE_MODE = false;
	private static final boolean ENABLED_PROCESS_TRIGGER_TASK = true;
	private static final String REFRESH_PROVIDER_STATE_CONTEXT = "refreshProviderState-testContext";
	private static final String GET_TASKS_TO_SCHEDULE_CONTEXT = "getTasksToSchedule-testContext";

	private final PropertyConfigSwitcher taskPollIntervalSwitcher = new PropertyConfigSwitcher(POLLING_INTERVAL);
	private final PropertyConfigSwitcher taskWorkersMaxSwitcher = new PropertyConfigSwitcher(WORKERS_MAX);
	private TasksProvider tasksProvider;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		taskPollIntervalSwitcher.switchToValue(String.valueOf(POLL_INTERVAL));
		taskWorkersMaxSwitcher.switchToValue(String.valueOf(MAX_WORKERS));
		tasksProvider = mock(TasksProvider.class);
		when(tasksProvider.getMaxItemsToSchedule(any(DefaultTaskService.TaskEngineRunningState.class),
				any(RuntimeConfigHolder.class))).thenReturn(MAX_ITEMS_TO_SCHEDULE);
	}

	@Override
	@After
	public void tearDown()
	{
		taskPollIntervalSwitcher.switchBackToDefault();
		taskWorkersMaxSwitcher.switchBackToDefault();
		super.tearDown();
	}

	@Override
	protected TasksProvider getTasksProvider()
	{
		return tasksProvider;
	}

	@Test
	public void shouldGetTasksToSchedule() throws Exception
	{
		assureTaskEngineStopped();

		final int expectedTaskCount = MAX_ITEMS_TO_SCHEDULE - 1;

		final List<TasksProvider.VersionPK> tasksVersions = createTasksVersions(expectedTaskCount, GET_TASKS_TO_SCHEDULE_CONTEXT);
		when(this.tasksProvider.getTasksToSchedule(any(RuntimeConfigHolder.class),
				any(TaskEngineParameters.class), eq(MAX_ITEMS_TO_SCHEDULE)))
				.thenReturn(tasksVersions)
				.thenReturn(emptyList());

		final CountDownLatch latch = new CountDownLatch(expectedTaskCount);
		final CountDownLatch deleteLatch = new CountDownLatch(expectedTaskCount);

		final TestTaskService taskService = new TestTaskService(NODE_ID, emptyList(),
				t -> t.getContext().equals(GET_TASKS_TO_SCHEDULE_CONTEXT), latch, deleteLatch, DISABLED_EXCLUSIVE_MODE);

		try
		{
			taskService.getEngine().start();

			assertThat(latch.await(LATCH_WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS)).isTrue();
			assertThat(taskService.getExecutedTasks()).hasSize(expectedTaskCount);
			assertThat(deleteLatch.await(LATCH_WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS)).isTrue();

			verify(tasksProvider, atLeastOnce()).getTasksToSchedule(any(RuntimeConfigHolder.class),
					any(TaskEngineParameters.class), eq(MAX_ITEMS_TO_SCHEDULE));
		}
		finally
		{
			taskService.getEngine().stop();
		}
	}

	@Test
	public void shouldRefreshProviderStateWhenRepollIsScheduled() throws Exception
	{
		assureTaskEngineStopped();

		/*
		 * When number of scheduled tasks is equal MAX_ITEMS_TO_SCHEDULE and is grater that MAX_WORKERS then
		 * repoll task is scheduled, and it will be waiting for execution because waitingExecutionStrategy will block all workers
		 * until waitingLatch countDown.
		 */
		final int expectedTaskCount = MAX_ITEMS_TO_SCHEDULE;

		final List<TasksProvider.VersionPK> tasksVersions = createTasksVersions(expectedTaskCount,
				REFRESH_PROVIDER_STATE_CONTEXT);
		when(this.tasksProvider.getTasksToSchedule(any(RuntimeConfigHolder.class),
				any(TaskEngineParameters.class), eq(MAX_ITEMS_TO_SCHEDULE)))
				.thenReturn(tasksVersions)
				.thenReturn(emptyList());

		final CountDownLatch executedTaskCountLatch = new CountDownLatch(expectedTaskCount);
		final CountDownLatch deleteLatch = new CountDownLatch(expectedTaskCount);

		final BlockingExecutionStrategy blockingExecutionStrategy = new BlockingExecutionStrategy(
				t -> t.getContext().equals(REFRESH_PROVIDER_STATE_CONTEXT), executedTaskCountLatch, deleteLatch, MAX_WORKERS,
				LATCH_WAIT_TIMEOUT);

		final TestTaskService taskService = new TestTaskService(NODE_ID, emptyList(), DISABLED_EXCLUSIVE_MODE,
				ENABLED_PROCESS_TRIGGER_TASK, blockingExecutionStrategy, null);

		try
		{
			taskService.getEngine().start();
			// wait until all workers are blocked
			blockingExecutionStrategy.waitForLock(LATCH_WAIT_TIMEOUT);
			taskService.poll();
			taskService.poll();
			blockingExecutionStrategy.unlock();

			assertThat(executedTaskCountLatch.await(LATCH_WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS)).isTrue();
			assertThat(taskService.getExecutedTasks()).hasSize(expectedTaskCount);
			assertThat(deleteLatch.await(LATCH_WAIT_TIMEOUT.toSeconds(), TimeUnit.SECONDS)).isTrue();

			verify(tasksProvider, atLeastOnce()).getTasksToSchedule(any(RuntimeConfigHolder.class),
					any(TaskEngineParameters.class), eq(MAX_ITEMS_TO_SCHEDULE));
			verify(tasksProvider, atLeast(2)).refreshState(any(RuntimeConfigHolder.class), any(TaskEngineParameters.class));
		}
		finally
		{
			taskService.getEngine().stop();
		}
	}

	protected List<TasksProvider.VersionPK> createTasksVersions(final int taskCount, final String context)
	{
		final List<TaskModel> tasks = new ArrayList<>();
		int i = 0;
		while (i++ < taskCount)
		{
			createTask(null, task -> prepareTask(task, context, tasks));
		}
		return tasks.stream()
		            .map(this::mapToVersionPK)
		            .collect(toList());
	}

	protected TaskModel prepareTask(final TaskModel task, final String context, final List<TaskModel> tasks)
	{
		tasks.add(task);
		task.setContext(context);
		return task;
	}

	protected TasksProvider.VersionPK mapToVersionPK(final TaskModel task)
	{
		return new TasksProvider.VersionPK(task.getPk(),
				task.getItemModelContext().getPersistenceVersion());
	}

	/**
	 * Execution strategy that allows to block a specified number of threads that run this strategy
	 * and unlock them on request.
	 */
	class BlockingExecutionStrategy extends TestExecutionStrategy
	{
		private final CountDownLatch lockLatch;
		private final CountDownLatch unlockLatch;
		private final Duration unlockTimeLimit;

		/**
		 * Constructs a strategy initialized with the parameters.
		 *
		 * @param shouldProcess          filter that specifies whether this strategy should process the task
		 * @param countDownLatch         latch that is used to count tasks that met the filter and were executed by the strategy
		 * @param deletionCountDownLatch latch that is used to count all finished tasks
		 * @param lockCount              number of threads that can be blocked simultaneously
		 * @param unlockTimeLimit        the maximum time to wait for the specified number of threads to be blocked
		 */
		public BlockingExecutionStrategy(final Predicate<TaskModel> shouldProcess, final CountDownLatch countDownLatch,
		                                 final CountDownLatch deletionCountDownLatch, final int lockCount,
		                                 final Duration unlockTimeLimit)
		{
			super(shouldProcess, countDownLatch, deletionCountDownLatch);
			this.lockLatch = new CountDownLatch(lockCount);
			this.unlockLatch = new CountDownLatch(1);
			this.unlockTimeLimit = unlockTimeLimit;
		}

		/**
		 * Blocks the current thread, which has to wait until a specified number of the other threads
		 * will run the strategy unless the thread is interrupted or the specified waiting time elapses.
		 * <p>The first call block the current thread. For subsequent calls, this method returns immediately
		 * with the value {@code true} and the current thread will no longer be blocked.
		 * <p>
		 * If the specified number of threads reaches the strategy then the method returns with the value {@code true}.
		 * If the specified waiting time elapses then the value {@code false} is returned.
		 * <p>
		 *
		 * @param timeout the maximum time to wait
		 * @return {@code true} if the specified number of threads ran the strategy before timeout.<br>
		 * {@code false} if the waiting time elapsed before the specified number of threads ran the strategy<br>
		 * @throws InterruptedException if the current thread is interrupted while waiting
		 */
		public boolean waitForLock(final Duration timeout) throws InterruptedException
		{
			return lockLatch.await(timeout.toSeconds(), TimeUnit.SECONDS);
		}

		/**
		 * Removes the lock, releasing all waiting threads that ran the strategy.<br>
		 * Only first call terminates the blockade. After calling this method, the strategy remains unlocked forever.
		 */
		public void unlock()
		{
			unlockLatch.countDown();
		}

		@Override
		public void run(final TaskService taskService, final TaskRunner<TaskModel> runner, final TaskModel model)
				throws RetryLaterException
		{
			lockLatch.countDown();
			try
			{
				if (this.unlockLatch.await(unlockTimeLimit.toSeconds(), TimeUnit.SECONDS))
				{
					LOGGER.info("task {} unlocked", model.getPk());
					super.run(taskService, runner, model);
				}
				else
				{
					LOGGER.info("task {} unlocked due to lock timeout", model.getPk());
				}
			}
			catch (final InterruptedException e)
			{
				LOGGER.info("task {} interrupted", model.getPk());
			}
		}
	}
}
