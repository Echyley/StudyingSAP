/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.processengine.process;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.DBOutageSimulator;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.jdbcwrapper.JUnitJDBCConnectionPool;
import de.hybris.platform.processengine.definition.ProcessDefinitionFactory;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.processengine.task.impl.ProcessengineTaskExecutionStrategy;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.tx.TransactionConnectionBrokenException;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.transaction.support.TransactionTemplate;

@IntegrationTest
public class ProcessEngineTaskRunnerRetryOnConnectionFailureTest extends ServicelayerBaseTest
{
	final PropertyConfigSwitcher switchRetry = new PropertyConfigSwitcher(
			"processengine.process.retry.for.connection.failure.during.transaction.enabled");

	private MockitoSession mockito;

	@Mock
	private TransactionTemplate transactionTemplate;
	@Mock
	private ProcessDefinitionFactory processDefinitionFactory;

	@Resource
	private TaskService taskService;

	private DBOutageSimulator dbOutageSimulator;
	private JUnitJDBCConnectionPool connectionPool;

	@Before
	public void setUp()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
		final HybrisDataSource dataSource = getDataSource();
		assertThat(dataSource.getConnectionPool()).isInstanceOf(JUnitJDBCConnectionPool.class);
		connectionPool = (JUnitJDBCConnectionPool) dataSource.getConnectionPool();
		dbOutageSimulator = new DBOutageSimulator(connectionPool);
	}

	@After
	public void tearDown()
	{
		mockito.finishMocking();
		connectionPool.resetTestMode();
		switchRetry.switchBackToDefault();
	}

	private HybrisDataSource getDataSource()
	{
		return Registry.getCurrentTenant().getDataSource();
	}

	@Test
	public void shouldThrowRetryLaterExceptionWhenEnabled() throws RetryLaterException
	{
		taskService.getEngine().stop();
		//given
		switchRetry.switchToValue("true");
		final ProcessTaskModel testTask = new ProcessTaskModel();
		testTask.setAction("someAction");
		final BusinessProcessModel processEngineProcessModel = new BusinessProcessModel();
		processEngineProcessModel.setCode("code");
		processEngineProcessModel.setState(ProcessState.CREATED);
		processEngineProcessModel.setProcessDefinitionName("name");
		testTask.setProcess(processEngineProcessModel);

		final ProcessengineTaskRunner runner = Mockito.spy(new ProcessengineTaskRunner()
		{
			@Override
			public void run(final TaskService taskManager, final ProcessTaskModel task) throws RetryLaterException
			{
				dbOutageSimulator.enable();
				try
				{
					super.run(taskManager, task);
					Assert.fail("RetryLaterException should be thrown");
				}
				finally
				{
					dbOutageSimulator.disable();
				}
			}
		});
		when(transactionTemplate.execute(
				Mockito.any())).thenThrow(
				new RuntimeException("Exception", new TransactionConnectionBrokenException("Connection is closed")));

		runner.setTransactionTemplate(transactionTemplate);
		final ModelService modelService = mock(ModelService.class);
		runner.setModelService(modelService);
		final TaskService taskServiceMock = mock(TaskService.class);

		//when, then
		Assert.assertThrows(RetryLaterException.class, () -> runner.run(taskServiceMock, testTask));
		Mockito.verify(runner, Mockito.never()).processRunError(Mockito.any(), Mockito.any(), Mockito.any());

		taskService.getEngine().start();
	}


	@Test
	public void shouldNotThrowRetryLaterExceptionWhenDisabled() throws RetryLaterException
	{
		taskService.getEngine().stop();
		//given
		switchRetry.switchToValue("false");
		final ProcessTaskModel testTask;
		testTask = new ProcessTaskModel();
		testTask.setAction("someAction");
		final BusinessProcessModel processEngineProcessModel = new BusinessProcessModel();
		processEngineProcessModel.setCode("code");
		processEngineProcessModel.setState(ProcessState.CREATED);
		processEngineProcessModel.setProcessDefinitionName("name");
		testTask.setProcess(processEngineProcessModel);

		final ProcessengineTaskRunner runner = Mockito.spy(new ProcessengineTaskRunner()
		{
			@Override
			public void run(final TaskService taskManager, final ProcessTaskModel task) throws RetryLaterException
			{
				dbOutageSimulator.enable();
				try
				{
					super.run(taskManager, task);
				}
				catch (final RetryLaterException re)
				{
					Assertions.fail("Should not throw RetryLaterException");
				}
				finally
				{
					dbOutageSimulator.disable();
				}
			}
		});
		when(transactionTemplate.execute(Mockito.any())).thenThrow(
				new RuntimeException("Exception", new TransactionConnectionBrokenException("Connection is closed")));
		Mockito.doReturn(null).when(runner).processRunError(Mockito.any(), Mockito.any(), Mockito.any());
		runner.setTransactionTemplate(transactionTemplate);
		runner.setProcessDefinitionFactory(processDefinitionFactory);
		final ModelService modelService = mock(ModelService.class);
		runner.setModelService(modelService);
		final TaskService taskServiceMock = mock(TaskService.class);

		//when
		runner.run(taskServiceMock, testTask);
		//then
		Mockito.verify(runner, Mockito.times(1)).processRunError(Mockito.any(), Mockito.any(), Mockito.any());
		taskService.getEngine().start();
	}

	@Test
	public void shouldRetryTaskWhenTransactionConnectionBrokenExceptionThrown()
	{
		final ProcessengineTaskExecutionStrategy strategy = new ProcessengineTaskExecutionStrategy();
		final TaskService mockTaskService = mock(TaskService.class);
		final TaskRunner<TaskModel> mockTaskRunner = mock(TaskRunner.class);
		final TaskModel taskModel = mock(TaskModel.class);
		doThrow(new TransactionConnectionBrokenException("test exception"))
		       .when(mockTaskRunner).run(mockTaskService, taskModel);
		assertThatThrownBy(() -> strategy.run(mockTaskService, mockTaskRunner, taskModel))
				.isInstanceOf(RetryLaterException.class);
	}

}
