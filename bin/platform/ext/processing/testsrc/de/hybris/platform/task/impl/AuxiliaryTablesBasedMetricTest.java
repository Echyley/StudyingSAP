/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl;

import static de.hybris.platform.task.impl.DefaultTaskService.PoolingQueueSizeGauge.getPoolingQueueSizeGauge;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskConditionModel;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.task.impl.gateways.BaseGateway;
import de.hybris.platform.task.impl.gateways.WorkerStateGateway;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.codahale.metrics.MetricRegistry;

@IntegrationTest
public class AuxiliaryTablesBasedMetricTest extends ServicelayerBaseTest
{
	private static final String TASKS_QUEUE_SIZE_METRIC = DefaultTaskService.TASKS_QUEUE_SIZE_METRIC;
	private static final String CONDITIONS_QUEUE_SIZE_METRIC = DefaultTaskService.CONDITIONS_QUEUE_SIZE_METRIC;
	private static final String POOLING_QUEUE_SIZE_METRIC = DefaultTaskService.POOLING_QUEUE_SIZE_METRIC;

	private static final int NUM_OF_TASKS = 2;

	@Resource
	private AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory;

	@Resource
	private AuxiliaryTablesSchedulerRole auxiliaryTablesSchedulerRole;

	@Resource
	MetricRegistry metricRegistry;

	@Resource
	private ModelService modelService;

	@Resource
	private TaskService taskService;

	@Resource
	private JdbcTemplate jdbcTemplate;

	private final RuntimeConfigHolder runtimeConfigHolder = new RuntimeConfigHolder();

	private final PropertyConfigSwitcher workerActivationInterval = new PropertyConfigSwitcher(
			AuxiliaryTablesBasedTaskProvider.Params.WORKER_ACTIVATION_INTERVAL.getName());

	private final PropertyConfigSwitcher workerRemovalInterval = new PropertyConfigSwitcher(
			AuxiliaryTablesBasedTaskProvider.Params.WORKER_REMOVAL_INTERVAL.getName());

	private final TaskEngineParameters parameters = new TaskEngineParameters.ParametersBuilder()
			.withClusterNodeID(500)
			.withTenant(Registry.getCurrentTenantNoFallback())
			.build();

	private AuxiliaryTablesTaskProviderTestHelper testHelper;

	@Before
	public void setUp()
	{
		testHelper = new AuxiliaryTablesTaskProviderTestHelper(taskService, jdbcTemplate);
		testHelper.disableTaskEngine();
		recreateAuxiliaryTasksTable();
		resetQueueMetrics();
		registerWorker();

		workerActivationInterval.switchToValue(String.valueOf(Integer.MAX_VALUE));
		workerRemovalInterval.switchToValue(String.valueOf(Integer.MAX_VALUE));
	}

	@After
	public void tearDown()
	{
		resetQueueMetrics();
		recreateAuxiliaryTasksTable();
		testHelper.enableTaskEngine();

		workerActivationInterval.switchBackToDefault();
		workerRemovalInterval.switchBackToDefault();
	}

	@Test
	public void shouldRetrieveEmptyMetricDataWhenNoPendingProcessableTasks()
	{
		final var conditions = auxiliaryTablesSchedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, parameters, 4);
		assertThat(conditions).isEmpty();
		assertQueueMetricsSize(0);
	}

	@Test
	public void shouldRetrieveValidMetricDataWhenPendingProcessableTasksChange()
	{
		populateTaskEngineWithProcessableTasks();
		assertQueueMetricsSize(0);

		var conditions = auxiliaryTablesSchedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, parameters, 4);
		assertThat(conditions).hasSize(NUM_OF_TASKS);
		assertQueueMetricsSize(NUM_OF_TASKS);

		populateTaskEngineWithProcessableTasks();

		conditions = auxiliaryTablesSchedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, parameters, 4);
		assertThat(conditions).hasSize(2 * NUM_OF_TASKS);
		assertQueueMetricsSize(2 * NUM_OF_TASKS);
	}

	private void assertQueueMetricsSize(final int size)
	{
		assertThat(getPoolingQueueSizeGauge(TASKS_QUEUE_SIZE_METRIC, metricRegistry).getValue()).isEqualTo(size);
		assertThat(getPoolingQueueSizeGauge(CONDITIONS_QUEUE_SIZE_METRIC, metricRegistry).getValue()).isEqualTo(size);
		assertThat(getPoolingQueueSizeGauge(POOLING_QUEUE_SIZE_METRIC, metricRegistry).getValue()).isEqualTo(2 * size);
	}

	private void populateTaskEngineWithProcessableTasks()
	{
		for (int i = 0; i < NUM_OF_TASKS; i++)
		{
			final TaskModel task = modelService.create(TaskModel.class);
			task.setExecutionDate(new Date());
			task.setRunnerBean("notRequired");

			final TaskConditionModel condition = modelService.create(TaskConditionModel.class);
			condition.setUniqueID(UUID.randomUUID().toString());
			condition.setExpirationDate(new Date());

			modelService.saveAll();
		}
	}

	private void recreateAuxiliaryTasksTable()
	{
		final BaseGateway tasksQueueGateway = auxiliaryTablesGatewayFactory.getTasksQueueGateway();
		tasksQueueGateway.dropTable();
		tasksQueueGateway.createTable();
		testHelper.assertTableExists(tasksQueueGateway.getTableName());
	}

	private void registerWorker()
	{
		final WorkerStateGateway workerStateGateway = auxiliaryTablesGatewayFactory.getWorkerStateGateway();
		workerStateGateway.registerAsWorker(new WorkerStateGateway.WorkerState(500, null, false, null));
	}

	private void resetQueueMetrics()
	{
		getPoolingQueueSizeGauge(TASKS_QUEUE_SIZE_METRIC, metricRegistry).setSize(0);
		getPoolingQueueSizeGauge(CONDITIONS_QUEUE_SIZE_METRIC, metricRegistry).setSize(0);
		getPoolingQueueSizeGauge(POOLING_QUEUE_SIZE_METRIC, metricRegistry).setSize(0);
	}
}
