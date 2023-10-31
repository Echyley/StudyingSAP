/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.processengine.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.transaction.support.TransactionTemplate;

@IntegrationTest
public class ProcessEngineTaskRunnerTest extends HybrisJUnit4Test
{
	private MockitoSession mockito;

	@Mock
	private TransactionTemplate transactionTemplate;

	@Before
	public void setUp()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
	}

	@After
	public void tearDown()
	{
		mockito.finishMocking();
	}

	@Test
	public void runTest() throws RetryLaterException
	{
		final ProcessTaskModel testTask;

		testTask = new ProcessTaskModel();

		testTask.setAction("someAction");
		final BusinessProcessModel processEngineProcessModel = new BusinessProcessModel();
		processEngineProcessModel.setCode("code");
		processEngineProcessModel.setState(ProcessState.CREATED);
		processEngineProcessModel.setProcessDefinitionName("name");
		testTask.setProcess(processEngineProcessModel);
		final ProcessengineTaskRunner runner = new ProcessengineTaskRunner()
		{
			@Override
			public void run(final TaskService taskManager, final ProcessTaskModel task) throws RetryLaterException
			{
				super.run(taskManager, task);
				assertEquals("testTask is not equal to task", testTask, task);
			}
		};
		runner.setTransactionTemplate(transactionTemplate);

		final ModelService modelService = mock(ModelService.class);

		runner.setModelService(modelService);

		final TaskService taskService = mock(TaskService.class);
		runner.run(taskService, testTask);
	}

	@Test
	public void handleErrorTest()
	{
		final Logger logger = Logger.getLogger(ProcessengineTaskRunner.class);
		final Level level = logger.getLevel();
		try
		{
			logger.setLevel(Level.OFF);
			final ProcessengineTaskRunner taskRunner = new ProcessengineTaskRunner();
			taskRunner.setProcessParameterHelper(
					(ProcessParameterHelper) Registry.getApplicationContext().getBean("processParameterHelper"));
			assertNotNull("taskRunner can't be null", taskRunner);
			final Exception exception = new Exception("test");
			final BusinessProcessModel businessProcess = new BusinessProcessModel();
			businessProcess.setCode("code");
			businessProcess.setState(ProcessState.CREATED);
			businessProcess.setProcessDefinitionName("name");
			final ProcessTaskModel task = new ProcessTaskModel();
			task.setRunnerBean("foo");
			task.setAction("ss");
			task.setProcess(businessProcess);

			task.setExecutionDate(new Date());
			final ModelService modelService = mock(ModelService.class);
			taskRunner.setModelService(modelService);

			taskRunner.handleError(null, task, exception);
		}
		finally
		{
			logger.setLevel(level);
		}

	}
}
