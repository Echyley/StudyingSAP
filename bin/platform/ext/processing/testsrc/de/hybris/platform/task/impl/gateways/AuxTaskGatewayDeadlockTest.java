package de.hybris.platform.task.impl.gateways;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.task.impl.TasksProvider;
import de.hybris.platform.util.Config;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

@IntegrationTest
public class AuxTaskGatewayDeadlockTest extends BaseGatewayTest
{
	public static final Logger LOGGER = LoggerFactory.getLogger(AuxTaskGatewayDeadlockTest.class);
	public static final int NUMBER_OF_TASKS = 500;
	public static final int NO_OF_THREADS = 30;
	public static final boolean NOT_EXCLUSIVE_MODE = false;
	public static final boolean EXCLUSIVE_MODE = true;
	public static final String NODE_GROUP = "nodeGroup";
	private final AtomicBoolean run = new AtomicBoolean(true);
	private final MetricRegistry metricRegistry = new MetricRegistry();
	private final Histogram returnedTasksNumberHistogram = metricRegistry.histogram("histogram");
	private final Meter getTasksMeter = metricRegistry.meter("meter");

	@Resource
	private JdbcTemplate jdbcTemplate;
	private ExecutorService executorService;

	private TasksQueueGateway tasksQueueGateway;

	@Before
	public void setUp()
	{
		setupBaseGatewayTest();
		disableTaskEngine();

		tasksQueueGateway = getTasksQueueGateway();

		prepareTable(tasksQueueGateway);

		final ThreadGroup taskEnginePerfTest = new ThreadGroup("taskEnginePerfTest");
		executorService = Executors.newCachedThreadPool(
				runnable -> new RegistrableThread(taskEnginePerfTest, runnable));

	}

	@After
	public void tearDown()
	{
		executorService.shutdownNow();
		testHelper.cleanUpTasks();
		cleanUpTable(tasksQueueGateway);

		enableTaskEngine();
	}

	@Test
	public void shouldCorrectlyConcurrentlyGetTasksWithNodeGroups() throws Exception
	{
		//for HSQLDB this test does not make any sens?
		assumeThat(Config.getDatabaseName(), not(Config.DatabaseName.HSQLDB));

		//query sanity check to fail fast when there is a SQL syntax problem
		runTheTest(NOT_EXCLUSIVE_MODE);
	}

	@Test
	public void shouldCorrectlyConcurrentlyGetTasksWithNodeGroupsInExclusiveMode() throws Exception
	{
		//for HSQLDB this test does not make any sens?
		assumeThat(Config.getDatabaseName(), not(Config.DatabaseName.HSQLDB));

		runTheTest(EXCLUSIVE_MODE);
	}

	private void runTheTest(final boolean exclusiveMode) throws InterruptedException
	{
		//query sanity check to fail fast when there is a SQL syntax problem
		tasksQueueGateway.getTasksForWorkerAndMarkForProcessing(getWorkerRange(1), 10, getWorkerState(1, exclusiveMode), Duration.ofSeconds(1));

		prepareTasks();

		final List<Future<List<Throwable>>> futures = startThreads(exclusiveMode);
		waitForThreads(futures);

		final double meanRate = getTasksMeter.getMeanRate();
		final double meanTasksReturned = returnedTasksNumberHistogram.getSnapshot().getMean();

		LOGGER.info("rate: {}; mean tasks returned: {}", meanRate, meanTasksReturned);

		assertNoErrorsWhereThrown(futures);
		assertThat(meanTasksReturned).isNotZero();
		assertThat(meanRate).isNotZero();
	}

	private List<Future<List<Throwable>>> startThreads(final boolean exclusiveMode)
	{
		final AtomicInteger i = new AtomicInteger(0);

		final List<Future<List<Throwable>>> futures;
		futures = Stream.generate(
				                () -> singleThreadLogic(tasksQueueGateway, i.getAndIncrement(),
						                Registry.getCurrentTenantNoFallback().getTenantID(),
						                run, exclusiveMode))
		                .limit(NO_OF_THREADS)
		                .map(executorService::submit)
		                .collect(Collectors.toList());
		return futures;
	}

	private void prepareTasks()
	{
		testHelper.prepareTasks(NUMBER_OF_TASKS, NODE_GROUP);

		final long addTasks = testHelper.copyTasksToAuxQueue(tasksQueueGateway, 0, NO_OF_THREADS);
		assertThat(addTasks).isNotZero();
	}

	private void waitForThreads(final List<? extends Future<List<Throwable>>> futures)
			throws InterruptedException
	{
		final Instant timeout = Instant.now().plus(1, ChronoUnit.MINUTES);
		while (Instant.now().isBefore(timeout))
		{
			final long done = futures.stream().filter(Future::isDone).count();
			LOGGER.info("Done threads: {}/{}", done, NO_OF_THREADS);

			TimeUnit.SECONDS.sleep(1);
		}

		this.run.set(false);
		Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> futures.stream().allMatch(Future::isDone));
	}

	private void logLocks()
	{
		if (Config.getDatabaseName() == Config.DatabaseName.HANA)
		{
			jdbcTemplate.queryForList("select * from SYS.M_BLOCKED_TRANSACTIONS").forEach(m -> LOGGER.info("1: {}", m));
			jdbcTemplate.queryForList("select * from SYS.M_RECORD_LOCKS").forEach(m -> LOGGER.info("2: {}", m));
		}

	}

	private void assertNoErrorsWhereThrown(final List<? extends Future<List<Throwable>>> futures)
	{
		assertThat(futures).flatExtracting(listFuture -> {
			try
			{
				return listFuture.get();
			}
			catch (final ExecutionException | InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}).isEmpty();
	}

	private Callable<List<Throwable>> singleThreadLogic(final TasksQueueGateway tasksQueueGateway, final int nodeId,
	                                                    final String tenantId, final AtomicBoolean run,
	                                                    final boolean exclusiveMode)
	{
		return () -> {
			Registry.setCurrentTenantByID(tenantId);

			final List<Throwable> errors = new LinkedList<>();
			try
			{
				final WorkerStateGateway.WorkerRange range = getWorkerRange(nodeId);
				final WorkerStateGateway.WorkerState workerState = getWorkerState(nodeId, exclusiveMode);
				while (run.get())
				{
					final int expected = Math.min(10, NUMBER_OF_TASKS/NO_OF_THREADS + 10);


					try
					{
						final List<TasksProvider.VersionPK> tasksForWorkerAndMarkForProcessing = tasksQueueGateway.getTasksForWorkerAndMarkForProcessing(
								range, expected, workerState, Duration.ofSeconds(2));

						getTasksMeter.mark();
						returnedTasksNumberHistogram.update(tasksForWorkerAndMarkForProcessing.size());
						LOGGER.info("getting {}", tasksForWorkerAndMarkForProcessing.size());
					}
					catch (final Exception e)
					{
						logLocks();
						errors.add(e);
						LOGGER.error(e.getMessage(), e);

					}
					TimeUnit.SECONDS.sleep(1);
				}
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			finally
			{
				Registry.unsetCurrentTenant();
			}
			return errors;
		};
	}

	private WorkerStateGateway.WorkerState getWorkerState(final int nodeId, final boolean exclusiveMode)
	{
		return new WorkerStateGateway.WorkerState(nodeId, Duration.ZERO,
				exclusiveMode, Set.of(NODE_GROUP));
	}

	private WorkerStateGateway.WorkerRange getWorkerRange(final int nodeId)
	{
		return new WorkerStateGateway.WorkerRange(nodeId, nodeId + 1);
	}

	private TasksQueueGateway getTasksQueueGateway()
	{
		final TasksQueueGateway tasksQueueGateway = auxiliaryTablesGatewayFactory.getTasksQueueGateway();
		assertThat(tasksQueueGateway).isInstanceOf(DefaultTasksQueueGateway.class);

		final DefaultTasksQueueGateway spy = Mockito.spy(((DefaultTasksQueueGateway) tasksQueueGateway));
		Mockito.doAnswer(call -> {
			       final List<?> list = call.getArgument(1, List.class);
			       Collections.shuffle(list);
			       return call.callRealMethod();
		       })
		       .when(spy).markTasksForProcessingInternal(Mockito.anyString(), Mockito.anyList());
		return spy;
	}

}
