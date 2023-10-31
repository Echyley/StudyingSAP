/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.task.impl.gateways.AuxTaskGatewayDeadlockTest;
import de.hybris.platform.task.impl.gateways.TasksQueueGateway;
import de.hybris.platform.task.impl.gateways.WorkerStateGateway;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.MessageFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Iterables;

public class AuxiliaryTablesTaskProviderTestHelper
{
	private final TaskService taskService;

	private final JdbcTemplate jdbcTemplate;
	private final Set<PK> tasks = new HashSet<>();
	private boolean taskEngineWasRunningBefore;
	private final TypeService typeService;
	private final AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory;

	/**
	 * @deprecated since 2205, use {@link #AuxiliaryTablesTaskProviderTestHelper(TaskService, JdbcTemplate, TypeService, AuxiliaryTablesGatewayFactory, ModelService)} instead
	 */
	@Deprecated
	public AuxiliaryTablesTaskProviderTestHelper(final TaskService taskService,
	                                             final JdbcTemplate jdbcTemplate)
	{
		this(taskService, jdbcTemplate, null, null, null);
	}

	public AuxiliaryTablesTaskProviderTestHelper(final TaskService taskService,
	                                             final JdbcTemplate jdbcTemplate,
	                                             final TypeService typeService,
	                                             final AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory,
	                                             final ModelService modelService)
	{
		this.taskService = taskService;
		this.jdbcTemplate = jdbcTemplate;
		this.typeService = typeService;
		this.auxiliaryTablesGatewayFactory = auxiliaryTablesGatewayFactory;
		this.modelService = modelService;
	}

	public void disableTaskEngine()
	{
		if (taskService.getEngine().isRunning())
		{
			taskEngineWasRunningBefore = true;
			taskService.getEngine().stop();
		}

		Assertions.assertThat(taskService.getEngine().isRunning()).isFalse();
	}

	public void enableTaskEngine()
	{
		if (taskEngineWasRunningBefore)
		{
			taskService.getEngine().start();
		}
	}

	public void assertTableExists(final String tableName)
	{
		final Pair<Integer, Integer> testInt = jdbcTemplate.queryForObject(
				MessageFormatUtils.format("SELECT 1, COUNT(*) FROM {0}", tableName),
				(resultSet, i) -> Pair.of(resultSet.getInt(1), resultSet.getInt(2)));

		Assertions.assertThat(testInt).isNotNull();
		Assertions.assertThat(testInt.getLeft()).isEqualTo(1);
	}

	public void assertTableNotExists(final String tableName)
	{
		LoggerFactory.getLogger(AuxiliaryTablesTaskProviderTestHelper.class)
		             .info("table {} should not be present in DB", tableName);
		Assertions.assertThatThrownBy(() -> jdbcTemplate.execute(MessageFormatUtils.format("SELECT * FROM {0}", tableName)))
		          .as("table %s should not exist", tableName)
		          .isInstanceOf(BadSqlGrammarException.class);
	}

	public void prepareTasks(final int numberOfTasks, final String nodeGroup)
	{
		final int batchSize = 1000;
		for (int i = 0; i < numberOfTasks; i = i + batchSize)
		{
			prepareTasksInTx(numberOfTasks, batchSize, i, nodeGroup);
		}
	}

	private final ModelService modelService;

	private void prepareTasksInTx(final int numberOfTasks, final int batchSize, final int batchNumber, final String nodeGroup)
	{
		try
		{
			Transaction.current().execute(() -> {
				AuxTaskGatewayDeadlockTest.LOGGER.info("Preparing tasks, batch {}", batchNumber);
				for (int j = 0; j < batchSize; j++)
				{
					if (j + batchNumber >= numberOfTasks)
					{
						break;
					}

					final TaskModel task = modelService.create(TaskModel.class);
					task.setRunnerBean("passthroughRunner");
					task.setNodeGroup(nodeGroup);
					modelService.save(task);
					tasks.add(task.getPk());

				}
				AuxTaskGatewayDeadlockTest.LOGGER.info("Prepared tasks, batch {}", batchNumber);
				return null;
			});
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void cleanUpTasks()
	{
		Iterables.partition(tasks, 1000).forEach(pks -> {
			try
			{
				AuxTaskGatewayDeadlockTest.LOGGER.info("removing tasks, {}", pks.size());
				Transaction.current().execute(() ->
				{

					pks.forEach(pk -> {
						try
						{
							modelService.remove(pk);
						}
						catch (final Exception e)
						{
							AuxTaskGatewayDeadlockTest.LOGGER.warn(e.getMessage(), e);
						}
					});
					return null;
				});
			}
			catch (final Exception e)
			{
				AuxTaskGatewayDeadlockTest.LOGGER.warn(e.getMessage(), e);
			}
		});
	}

	public long copyTasksToAuxQueue(final TasksQueueGateway tasksQueueGateway, final int rangeStart, final int rangeEnd)
	{
		final TestAuxiliarySchedulerRole testAuxiliarySchedulerRole = new TestAuxiliarySchedulerRole();
		testAuxiliarySchedulerRole.setGatewayFactory(auxiliaryTablesGatewayFactory);
		testAuxiliarySchedulerRole.setTypeService(typeService);
		final String tasksQuery = testAuxiliarySchedulerRole.getTasksQuery(rangeStart, rangeEnd);
		final String expiredTasksQuery = testAuxiliarySchedulerRole.getExpiredTasksQuery(rangeStart, rangeEnd);

		return tasksQueueGateway.addTasks(tasksQuery, expiredTasksQuery, Instant.now(), rangeStart, rangeEnd);
	}

	public WorkerStateGateway.WorkerState getWorkerState(final int nodeId)
	{
		return new WorkerStateGateway.WorkerState(nodeId, Duration.ZERO,
				false, Set.of());
	}

	public WorkerStateGateway.WorkerState getWorkerState(final int nodeId, final String ... nodeGroup)
	{
		return new WorkerStateGateway.WorkerState(nodeId, Duration.ZERO,
				false, Set.of(nodeGroup));
	}

	private static final class TestAuxiliarySchedulerRole extends AuxiliaryTablesSchedulerRole
	{
		@Override
		protected String getTasksQuery(final int rangeStart, final int rangeEnd)
		{
			return super.getTasksQuery(rangeStart, rangeEnd);
		}

		@Override
		protected String getExpiredTasksQuery(final int rangeStart, final int rangeEnd)
		{
			return super.getExpiredTasksQuery(rangeStart, rangeEnd);
		}
	}
}
