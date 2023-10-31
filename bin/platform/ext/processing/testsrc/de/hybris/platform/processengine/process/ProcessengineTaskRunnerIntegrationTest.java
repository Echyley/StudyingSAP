package de.hybris.platform.processengine.process;

import static de.hybris.platform.processengine.process.ProcessengineTaskRunner.MARK_AS_DONE_ENABLED;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.persistence.property.JDBCValueMappings;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.definition.ProcessDefinitionFactory;
import de.hybris.platform.processengine.jalo.ProcessTask;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskConditionModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.task.impl.DefaultTaskExecutionStrategy;
import de.hybris.platform.task.impl.DefaultTaskService;
import de.hybris.platform.task.impl.TaskExecutionStrategy;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.MessageFormatUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
public class ProcessengineTaskRunnerIntegrationTest extends ServicelayerBaseTest
{
	private static final String PROCESS_DEFINITION_NAME = "simpleTestProcessName";
	private static final String PROCESS_THAT_FAILS_DEFINITION_NAME = "simpleTestProcessThatFailsName";

	private final ClassPathResource processDefinition = new ClassPathResource("processengine/test/simpleTestAction.xml");
	private final ClassPathResource processThatFailsDefinition = new ClassPathResource("processengine/test/simpleTestActionThatFails.xml");

	@Resource
	private ModelService modelService;

	@Resource
	private ProcessDefinitionFactory processDefinitionFactory;

	@Resource
	private ProcessengineTaskRunner taskRunner;

	@Resource
	private TaskService taskService;

	@Resource
	private BusinessProcessService businessProcessService;

	private Collection<? extends TaskExecutionStrategy> taskServiceExecutionStrategies;

	private final AtomicReference<ProcessTaskModel> taskToCheck = new AtomicReference<ProcessTaskModel>();

	final PropertyConfigSwitcher switcher = new PropertyConfigSwitcher(MARK_AS_DONE_ENABLED);

	@Before
	public void setUp() throws Exception
	{
		adjustDefaultStrategyInTaskService();
	}

	@After
	public void tearDown()
	{
		cleanUpTaskService();
	}

	@Test
	public void shouldMarkProcessTaskAsDoneTest() throws IOException, TimeoutException
	{
		taskService.getEngine().stop();
		processDefinitionFactory.add(processDefinition);
		switcher.switchToValue("true");

		final BusinessProcessModel businessProcessModel = modelService.create(BusinessProcessModel.class);
		businessProcessModel.setCode(UUID.randomUUID().toString());
		businessProcessModel.setProcessDefinitionName(PROCESS_DEFINITION_NAME);

		final ProcessTaskModel processTaskModel = modelService.create(ProcessTaskModel.class);
		processTaskModel.setProcess(businessProcessModel);
		processTaskModel.setRunnerBean("taskRunner");
		processTaskModel.setAction("start");

		final TaskConditionModel condition = modelService.create(TaskConditionModel.class);
		condition.setUniqueID(UUID.randomUUID().toString());
		condition.setTask(processTaskModel);
		modelService.saveAll();
		final PK pk = processTaskModel.getPk();

		assertThat(modelService.isRemoved(condition)).isFalse();
		assertThat(processTaskModel.getConditions()).hasSize(1);
		assertThat(processTaskModel.getProcess()).isEqualTo(businessProcessModel);
		assertThat(processTaskModel.getRunnerBean()).isEqualTo("taskRunner");

		taskRunner.run(taskService, processTaskModel);

		final ProcessTaskModel processTaskModelAfterRun = modelService.get(pk);
		assertThat(processTaskModelAfterRun.getRunnerBean()).isEqualTo("passthroughRunner");
		assertThat(processTaskModelAfterRun.getProcess()).isNull();
		assertThat(modelService.isRemoved(processTaskModelAfterRun)).isFalse();
		assertThat(modelService.isRemoved(condition)).isTrue();
		assertThat(processTaskModel.getConditions()).isEmpty();

		taskService.getEngine().start();

		waitForProcessTask(processTaskModel);
		assertThat(modelService.isRemoved(processTaskModelAfterRun)).isTrue();
		switcher.switchBackToDefault();
	}

	@Test
	public void shouldNotMarkProcessTaskAsDoneWithFlagDisabledTest() throws IOException, TimeoutException
	{
		taskService.getEngine().stop();
		processDefinitionFactory.add(processDefinition);
		switcher.switchToValue("false");

		final BusinessProcessModel businessProcessModel = modelService.create(BusinessProcessModel.class);
		businessProcessModel.setCode(UUID.randomUUID().toString());
		businessProcessModel.setProcessDefinitionName(PROCESS_DEFINITION_NAME);

		final ProcessTaskModel processTaskModel = modelService.create(ProcessTaskModel.class);
		processTaskModel.setProcess(businessProcessModel);
		processTaskModel.setRunnerBean("taskRunner");
		processTaskModel.setAction("start");

		final TaskConditionModel condition = modelService.create(TaskConditionModel.class);
		condition.setUniqueID(UUID.randomUUID().toString());
		condition.setTask(processTaskModel);
		modelService.saveAll();
		final PK pk = processTaskModel.getPk();

		assertThat(modelService.isRemoved(condition)).isFalse();
		assertThat(processTaskModel.getConditions()).hasSize(1);
		assertThat(processTaskModel.getProcess()).isEqualTo(businessProcessModel);
		assertThat(processTaskModel.getRunnerBean()).isEqualTo("taskRunner");

		taskRunner.run(taskService, processTaskModel);

		final ProcessTaskModel processTaskModelAfterRun = modelService.get(pk);

		assertThat(modelService.isRemoved(condition)).isFalse();
		assertThat(processTaskModelAfterRun.getConditions()).hasSize(1);
		assertThat(processTaskModelAfterRun.getProcess()).isEqualTo(businessProcessModel);
		assertThat(processTaskModelAfterRun.getRunnerBean()).isEqualTo("taskRunner");

		taskService.getEngine().start();
		switcher.switchBackToDefault();
	}

	@Test
	public void shouldProcessBusinessProcessCorrectlyWhenCannotDeleteProcessTaskTest()
			throws IOException, TimeoutException
	{
		final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Registry.getCoreApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
				.getBeanFactory();
		beanFactory.registerSingleton("SimpleActionThatFailsTestBean", new ActionThatFails());
		beanFactory.destroySingleton("passthroughRunner");
		beanFactory.registerSingleton("passthroughRunner", new TestPassthroughTaskRunner());
		processDefinitionFactory.add(processThatFailsDefinition);
		switcher.switchToValue("true");

		try
		{
			final BusinessProcessModel businessProcessModel = modelService.create(BusinessProcessModel.class);
			businessProcessModel.setCode(UUID.randomUUID().toString());
			businessProcessModel.setProcessDefinitionName(PROCESS_THAT_FAILS_DEFINITION_NAME);
			modelService.saveAll();

			businessProcessService.startProcess(businessProcessModel);

			waitForTask();
			assertThat(modelService.isRemoved(taskToCheck.get())).isTrue();
		}
		finally
		{
			beanFactory.destroySingleton("SimpleActionThatFailsTestBean");
			beanFactory.destroySingleton("passthroughRunner");
			beanFactory.registerSingleton("passthroughRunner", new PassthroughTaskRunner());
			switcher.switchBackToDefault();
		}
	}

	private void updateTaskDirectly(final String value)
	{
		final JDBCValueMappings.ValueWriter<PK, ?> pkWriter = JDBCValueMappings.getInstance().PK_WRITER;
		final String updateTaskStatement = "UPDATE {0} SET sealed={1} WHERE pk=?";
		final JdbcTemplate jdbcTemplate = Registry.getApplicationContext().getBean("jdbcTemplate", JdbcTemplate.class);
		jdbcTemplate.update(
				MessageFormatUtils.format(updateTaskStatement, getTableName(ProcessTask.class), value), preparedStatement -> {
					pkWriter.setValue(preparedStatement, 1, taskToCheck.get().getPk());
				});
	}

	private String getTableName(final Class<?> clazz)
	{
		final ComposedType type = TypeManager.getInstance().getComposedType(clazz);
		return type.getTable();
	}

	private class ActionThatFails extends AbstractProceduralAction
	{
		@Override
		public void executeAction(final BusinessProcessModel process)
		{
			taskToCheck.set(process.getCurrentTasks().stream().findAny().get());
			updateTaskDirectly("1");
		}
	}

	private class TestPassthroughTaskRunner implements TaskRunner<ProcessTaskModel>
	{

		@Override
		public void run(final TaskService taskService, final ProcessTaskModel task) throws RetryLaterException
		{
			updateTaskDirectly("0");
		}

		@Override
		public void handleError(final TaskService taskService, final ProcessTaskModel task, final Throwable error)
		{
			// nothing to do here
		}
	}


	private void waitForTask() throws TimeoutException
	{
		final long maxWaitTime = System.currentTimeMillis() + (60 * 1000);
		do
		{
			try
			{
				if (System.currentTimeMillis() > maxWaitTime)
				{
					throw new TimeoutException("Wait time exceeded for task:" + taskToCheck.get());
				}

				if (taskToCheck.get() != null && modelService.isRemoved(taskToCheck.get()))
				{
					return;
				}
				Thread.sleep(500);
			}
			catch (final InterruptedException e)
			{
				throw new SystemException(e);
			}
		}
		while (true);
	}

	private void waitForProcessTask(final ProcessTaskModel task) throws TimeoutException
	{
		final long maxWaitTime = System.currentTimeMillis() + (60 * 1000);
		do
		{
			try
			{
				if (System.currentTimeMillis() > maxWaitTime)
				{
					throw new TimeoutException("Wait time exceeded for task:" + taskToCheck.get());
				}

				if (modelService.isRemoved(task))
				{
					return;
				}
				Thread.sleep(500);
			}
			catch (final InterruptedException e)
			{
				throw new SystemException(e);
			}
		}
		while (true);
	}

	private void adjustDefaultStrategyInTaskService()
	{
		if (taskService instanceof DefaultTaskService)
		{
			final DefaultTaskService defaultTaskService = (DefaultTaskService) taskService;
			taskServiceExecutionStrategies = defaultTaskService.getTaskExecutionStrategies().values();

			final TaskExecutionStrategy taskExecutionStrategy = defaultTaskService.getExecutionStrategy(taskRunner);

			if (taskExecutionStrategy instanceof DefaultTaskExecutionStrategy)
			{
				adjustDefaultExecutionStrategyToNotDealWithStrandedTasksLogic(defaultTaskService, (DefaultTaskExecutionStrategy) taskExecutionStrategy);
			}
		}
	}

	private void cleanUpTaskService()
	{
		if (taskService instanceof DefaultTaskService)
		{
			((DefaultTaskService) taskService).setTaskExecutionStrategies(taskServiceExecutionStrategies);
		}
	}

	private void adjustDefaultExecutionStrategyToNotDealWithStrandedTasksLogic(final DefaultTaskService defaultTaskService, final DefaultTaskExecutionStrategy taskExecutionStrategy)
	{
		final DefaultTaskExecutionStrategy defaultTaskExecutionStrategy = taskExecutionStrategy;
		final DefaultTaskExecutionStrategy spyDefaultTaskExecutionStrategy = Mockito.spy(defaultTaskExecutionStrategy);
		Mockito.doReturn(Boolean.FALSE).when(spyDefaultTaskExecutionStrategy).wrapWithStrandedTasksLogic(taskService);

		final List mockStrategies = new ArrayList(taskServiceExecutionStrategies);
		mockStrategies.remove(defaultTaskExecutionStrategy);
		mockStrategies.add(spyDefaultTaskExecutionStrategy);

		defaultTaskService.setTaskExecutionStrategies(mockStrategies);
	}
}
