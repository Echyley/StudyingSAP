package de.hybris.platform.task.impl.gateways;

import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.impl.gateways.WorkerStateGateway.WorkerState;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;

import java.time.Duration;
import java.util.Optional;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
public class HanaTasksQueueGatewayTest extends BaseGatewayTest
{

	public static final int NUMBER_OF_TASKS = 50;
	public static final int WORKER_RANGE_START = 0;
	public static final int WORKER_RANGE_END = 1;

	public static final int HANA_1 = 1;
	private static final int HANA_2 = 2;

	public static final boolean EXPECTING_NO_LOCK = false;
	public static final boolean EXPECTING_LOCK = true;
	public static final String NODE_GROUP = "nodeGroup1";

	private final PropertyConfigSwitcher disableTableLockOnGettingTasksFromAuxQueue = new PropertyConfigSwitcher(
			HanaTasksQueueGateway.PROPERTY_DISABLE_LOCK_ON_GET_TASKS);

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private TypeService typeService;

	@Before
	public void setUp() throws Exception
	{
		assumeThat(Config.getDatabaseName(), is(Config.DatabaseName.HANA));

		disableTaskEngine();

		disableTableLockOnGettingTasksFromAuxQueue.switchToValue("false");
	}

	@After
	public void tearDown() throws Exception
	{
		disableTableLockOnGettingTasksFromAuxQueue.switchBackToDefault();
		testHelper.cleanUpTasks();
		enableTaskEngine();
	}

	@Test
	public void shouldLockTableOnGettingTasksWhenHana1IsUsedWithNodeGroups()
	{
		runTest(HANA_1, workerStateWithNodeGroup(), EXPECTING_LOCK);
	}

	@Test
	public void shouldNotLockTableOnGettingTasksWhenHana2IsUsedWithNodeGroups()
	{
		runTest(HANA_2, workerStateWithNodeGroup(), EXPECTING_NO_LOCK);
	}

	@Test
	public void shouldNotLockTableOnGettingTasksWhenHana1IsUsedWithoutNodeGroups()
	{
		runTest(HANA_1, workerStateWithoutNodeGroup(), EXPECTING_NO_LOCK);
	}

	@Test
	public void shouldNotLockTableOnGettingTasksWhenHana2IsUsedWithoutNodeGroups()
	{
		runTest(HANA_2, workerStateWithoutNodeGroup(), EXPECTING_NO_LOCK);
	}

	@Test
	public void shouldNotLockTableOnGettingTasksWhenHana1IsUsedWithNodeGroupsAndDisabledByProperty()
	{
		disableTableLockOnGettingTasksFromAuxQueue.switchToValue("true");
		runTest(HANA_1, workerStateWithNodeGroup(), EXPECTING_NO_LOCK);
	}

	private void runTest(final int hanaVersion, final WorkerState workerState, final boolean expectingLock)
	{
		final HanaTasksQueueGateway hanaTasksQueueGateway = createHanaTasksQueueGateway(hanaVersion);

		prepareTasks(hanaTasksQueueGateway);
		final HanaTasksQueueGateway spy = spy(hanaTasksQueueGateway);
		spy.getTasksForWorkerAndMarkForProcessing(getWorkerRange(), NUMBER_OF_TASKS, workerState, Duration.ofSeconds(2));

		if (expectingLock)
		{
			verify(spy).lockTable();
		}
		else
		{
			verify(spy, never()).lockTable();
		}
	}

	private HanaTasksQueueGateway createHanaTasksQueueGateway(final int hanaVersion)
	{
		final HanaTasksQueueGateway hanaTasksQueueGateway = new HanaTasksQueueGateway(jdbcTemplate, typeService,
				() -> Optional.of(AdditionalDatabaseData.builder()
				                                        .majorDbVersion(hanaVersion)
				                                        .minorDbVersion(0)
				                                        .build()));
		return hanaTasksQueueGateway;
	}

	private void prepareTasks(final HanaTasksQueueGateway hanaTasksQueueGateway)
	{
		testHelper.prepareTasks(NUMBER_OF_TASKS, NODE_GROUP);
		final long l = testHelper.copyTasksToAuxQueue(hanaTasksQueueGateway, WORKER_RANGE_START, WORKER_RANGE_END);
		Assertions.assertThat(l).isGreaterThanOrEqualTo(NUMBER_OF_TASKS);
	}

	private WorkerStateGateway.WorkerRange getWorkerRange()
	{
		return new WorkerStateGateway.WorkerRange(WORKER_RANGE_START, WORKER_RANGE_END);
	}

	private WorkerState workerStateWithoutNodeGroup()
	{
		return testHelper.getWorkerState(0);
	}

	private WorkerState workerStateWithNodeGroup()
	{
		return testHelper.getWorkerState(0, NODE_GROUP);
	}
}
