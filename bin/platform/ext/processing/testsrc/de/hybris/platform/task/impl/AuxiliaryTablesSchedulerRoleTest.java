/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.AdditionalAnswers.answersWithDelay;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.task.impl.AuxiliaryTablesBasedTaskProvider.Params;
import de.hybris.platform.task.impl.gateways.BaseGateway;
import de.hybris.platform.task.impl.gateways.SchedulerState;
import de.hybris.platform.task.impl.gateways.SchedulerStateGateway;
import de.hybris.platform.task.impl.gateways.TasksQueueGateway;
import de.hybris.platform.task.impl.gateways.WorkerStateGateway;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;

import com.codahale.metrics.MetricRegistry;

@IntegrationTest
public class AuxiliaryTablesSchedulerRoleTest extends ServicelayerBaseTest
{
	private static final Duration SCHEDULER_INTERVAL = Duration.ofMillis(10L);
	private static final String TASK_AUXILIARY_TABLES_SCHEDULER_WATCHDOG_ENABLED = "task.auxiliaryTables.scheduler.watchdog.enabled";

	private final PropertyConfigSwitcher schedulerWatchdogEnabledSwitch = new PropertyConfigSwitcher(
			TASK_AUXILIARY_TABLES_SCHEDULER_WATCHDOG_ENABLED);
	MockitoSession mockito;
	private TimeZone defaultTimeZone;
	@Spy
	private MockRuntimeConfigHolder runtimeConfigHolder = new MockRuntimeConfigHolder();
	@Resource
	private AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory;
	@Resource
	private MetricRegistry metricRegistry;
	@Resource
	private TypeService typeService;
	@Resource
	private TaskService taskService;
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private ModelService modelService;
	private AuxiliaryTablesTaskProviderTestHelper testHelper;

	@Before
	public void setUp() throws Exception
	{
		mockito = Mockito.mockitoSession().initMocks(this).strictness(Strictness.LENIENT).startMocking();
		testHelper = new AuxiliaryTablesTaskProviderTestHelper(taskService, jdbcTemplate, typeService,
				auxiliaryTablesGatewayFactory, modelService);
		testHelper.disableTaskEngine();
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		defaultTimeZone = TimeZone.getDefault();
		recreateTables();
	}

	@After
	public void tearDown() throws Exception
	{
		mockito.finishMocking();
		schedulerWatchdogEnabledSwitch.switchBackToDefault();
		if (defaultTimeZone != null)
		{
			TimeZone.setDefault(defaultTimeZone);
		}
		recreateTables();
		testHelper.enableTaskEngine();
	}

	@Test
	public void tryToPerformSchedulerJob()
	{
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway gateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		when(spyGatewayFactory.getSchedulerStateGateway()).thenReturn(gateway);

		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);
		final Instant now = gateway.getSchedulerTimestamp().get().getDbNow().minusSeconds(200);

		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/Montreal")));
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);

		final ArgumentCaptor<Duration> argCaptor = ArgumentCaptor.forClass(Duration.class);

		verify(gateway, times(2)).insertSchedulerRow(any(Instant.class), eq(AuxiliaryTablesBasedTaskProvider.VERSION));
		verify(gateway).updateSchedulerRow(argCaptor.capture());
		assertThat(argCaptor.getValue()).isEqualTo(runtimeConfigHolder.getProperty(Params.SCHEDULER_INTERVAL));
	}

	@Test
	public void shouldRecreateSchedulerTableAfterDeleteOfSchedulerRow()
	{
		//given
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway gateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		when(spyGatewayFactory.getSchedulerStateGateway()).thenReturn(gateway);

		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);
		verify(gateway, times(2)).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		verifyRecreationOfSchedulerTable(gateway, 1);

		//when
		spyGatewayFactory.getSchedulerStateGateway().deleteSchedulerRow();
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);

		//then
		verify(gateway, times(4)).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		verifyRecreationOfSchedulerTable(gateway, 2);
	}

	@Test
	public void shouldHaveSchedulerWatchdog()
	{
		// given
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spyStateGateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		when(spyGatewayFactory.getSchedulerStateGateway()).thenReturn(spyStateGateway);
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		// when
		final SchedulerAuxiliaryTablesWatchdog schedulerWatchdog = schedulerRole.getSchedulerWatchdog();
		// then
		assertThat(schedulerWatchdog).isNotNull();
	}

	@Test
	public void shouldNotCallWatchdogIfSchedulerIntervalIsZero()
	{
		doReturn(Duration.ZERO).when(runtimeConfigHolder).getProperty(Params.SCHEDULER_INTERVAL);
		shouldNotCallWatchdog();
	}

	@Test
	public void shouldNotCallWatchdogIfSchedulerIntervalIsNegative()
	{
		doReturn(Duration.ofMillis(-1L)).when(runtimeConfigHolder).getProperty(Params.SCHEDULER_INTERVAL);
		shouldNotCallWatchdog();
	}

	@Test
	public void shouldNotCallWatchdogIfWatchdogIsDisabled()
	{
		schedulerWatchdogEnabledSwitch.switchToValue("false");
		shouldNotCallWatchdog();
	}

	private void shouldNotCallWatchdog()
	{
		// given
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spySchedulerStateGateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		doReturn(spySchedulerStateGateway).when(spyGatewayFactory).getSchedulerStateGateway();
		final WorkerStateGateway spyWorkersStateGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersStateGateway).when(spyGatewayFactory).getWorkerStateGateway();
		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		final TestSchedulerAuxiliaryTablesWatchdog testSchedulerWatchdog = new TestSchedulerAuxiliaryTablesWatchdog(
				spyGatewayFactory);

		schedulerRole.setSchedulerWatchdog(testSchedulerWatchdog);
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		// when
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);

		//then
		verify(spySchedulerStateGateway, times(2)).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		// check if scheduler performed
		verify(spyWorkersStateGateway).getWorkers();
		verify(spySchedulerStateGateway, never()).updateSchedulerRow(any(Duration.class));
		assertThat(testSchedulerWatchdog.getStartTaskCounter()).isEqualTo(1);
		assertThat(testSchedulerWatchdog.getRunTaskCounter()).as("task should not run").isZero();
		verify(testSchedulerWatchdog.getSpyTask(), never()).run();
	}

	@Test
	public void shouldNotCallWatchdogIfSchedulerIsNotLocked()
	{
		// given
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spySchedulerStateGateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		doReturn(spySchedulerStateGateway).when(spyGatewayFactory).getSchedulerStateGateway();
		// do not allow to lock scheduler
		doReturn(false).when(spySchedulerStateGateway).insertSchedulerRow(anyInt());

		final WorkerStateGateway spyWorkersStateGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersStateGateway).when(spyGatewayFactory).getWorkerStateGateway();

		final SchedulerAuxiliaryTablesWatchdog mockSchedulerWatchdog = mock(SchedulerAuxiliaryTablesWatchdog.class);
		schedulerRole.setSchedulerWatchdog(mockSchedulerWatchdog);
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		// when
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);

		//then
		verify(spySchedulerStateGateway).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		verify(spySchedulerStateGateway, never()).updateSchedulerRow(SCHEDULER_INTERVAL);
		verify(mockSchedulerWatchdog, never()).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		// check if scheduler was not performed
		verify(spyWorkersStateGateway, never()).getWorkers();
	}

	@Test
	public void shouldCallWatchdogIfSchedulerIsLocked()
	{
		// given
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spyStateGateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());

		doReturn(spyStateGateway).when(spyGatewayFactory).getSchedulerStateGateway();

		final WorkerStateGateway spyWorkersStateGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersStateGateway).when(spyGatewayFactory).getWorkerStateGateway();

		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		final SchedulerAuxiliaryTablesWatchdog mockSchedulerWatchdog = mock(SchedulerAuxiliaryTablesWatchdog.class);
		final AuxiliaryTablesWatchdogTask mockWatchdogTask = mock(AuxiliaryTablesWatchdogTask.class);
		doReturn(mockWatchdogTask).when(mockSchedulerWatchdog).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		schedulerRole.setSchedulerWatchdog(mockSchedulerWatchdog);
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		// when
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);

		//then
		verify(spyStateGateway, times(2)).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		verify(spyStateGateway, never()).updateSchedulerRow(SCHEDULER_INTERVAL);
		verify(mockSchedulerWatchdog).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		verify(mockWatchdogTask).close();
		// check if scheduler performed
		verify(spyWorkersStateGateway).getWorkers();
	}

	@Test
	public void shouldCallWatchdogOnlyIfSchedulerIsLockedWhileMultipleAttempts()
	{
		// given
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spySchedulerStateGateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		doReturn(spySchedulerStateGateway).when(spyGatewayFactory).getSchedulerStateGateway();
		// allow to lock on first update
		doReturn(true, false).when(spySchedulerStateGateway).updateSchedulerRow(SCHEDULER_INTERVAL);

		final WorkerStateGateway spyWorkersGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersGateway).when(spyGatewayFactory).getWorkerStateGateway();

		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		final SchedulerAuxiliaryTablesWatchdog mockSchedulerWatchdog = mock(SchedulerAuxiliaryTablesWatchdog.class);
		final AuxiliaryTablesWatchdogTask mockWatchdogTask = mock(AuxiliaryTablesWatchdogTask.class);
		doReturn(mockWatchdogTask).when(mockSchedulerWatchdog).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		schedulerRole.setSchedulerWatchdog(mockSchedulerWatchdog);
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		// when
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);
		IntStream.range(0, 9)
		         .forEach(i -> schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10));

		//then
		verify(spySchedulerStateGateway, times(2)).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		verify(spySchedulerStateGateway, times(10)).updateSchedulerRow(SCHEDULER_INTERVAL);
		verify(mockSchedulerWatchdog, times(2)).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		verify(mockWatchdogTask, times(2)).close();
		// check if scheduler performed
		verify(spyWorkersGateway, times(2)).getWorkers();
	}

	@Test
	public void shouldExecuteWatchdogTaskForLongSchedulerOperation() throws InterruptedException
	{
		// given
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		final Duration schedulerInterval = Duration.ofMillis(100L);
		doReturn(schedulerInterval).when(runtimeConfigHolder).getProperty(Params.SCHEDULER_INTERVAL);

		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();

		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spySchedulerStateGateway = spy(this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		doReturn(spySchedulerStateGateway).when(spyGatewayFactory).getSchedulerStateGateway();

		final WorkerStateGateway spyWorkersStateGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersStateGateway).when(spyGatewayFactory).getWorkerStateGateway();

		final TestSchedulerAuxiliaryTablesWatchdog testSchedulerWatchdog = new TestSchedulerAuxiliaryTablesWatchdog(
				spyGatewayFactory);
		final CountDownLatch stopLatch = new CountDownLatch(1);
		testSchedulerWatchdog.setStopLatch(stopLatch);
		schedulerRole.setSchedulerWatchdog(testSchedulerWatchdog);

		// simulate long scheduler operation
		final int longOperationCounter = 3;
		final long longSchedulerOperationTime = schedulerInterval.multipliedBy(longOperationCounter).toMillis();
		doAnswer(answersWithDelay(longSchedulerOperationTime, invocation -> Collections.emptyList()))
				.when(spyWorkersStateGateway).getWorkers();

		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		setSchedulingDelayToForceUnlock(taskEngineParameters, schedulerInterval);

		// when
		schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10);
		final Optional<SchedulerState> updatedSchedulerTimestamp = spySchedulerStateGateway.getSchedulerTimestamp();

		// then
		verify(spySchedulerStateGateway, times(2)).insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(testSchedulerWatchdog.getStartTaskCounter()).isEqualTo(1);
		assertThat(testSchedulerWatchdog.getRunTaskCounter()).isEqualTo(1);
		verify(testSchedulerWatchdog.getSpyTask()).close();

		assertThat(stopLatch).as("Watchdog task should be invoked").isNotNull();
		assertThat(stopLatch.await(1000L, TimeUnit.MILLISECONDS)).as("Watchdog task should stop").isTrue();

		final ArgumentCaptor<Duration> checkIntervalCaptor = ArgumentCaptor.forClass(Duration.class);
		verify(spySchedulerStateGateway, atLeast(longOperationCounter)).updateSchedulerRow(checkIntervalCaptor.capture());

		assertThat(checkIntervalCaptor.getAllValues())
				.as("check intervals of watchdog update operation should be less than scheduler interval")
				.allMatch(interval -> interval.compareTo(schedulerInterval) < 0);

		assertThat(updatedSchedulerTimestamp).isPresent();
		assertThat(updatedSchedulerTimestamp.get().getLastActiveTs()).isNotNull();
		assertThat(updatedSchedulerTimestamp.get().getDbNow()).isNotNull();
		final Instant lastActiveTs = updatedSchedulerTimestamp.get().getLastActiveTs();
		final Instant lockBeginning = updatedSchedulerTimestamp.get().getDbNow().minus(schedulerInterval);
		assertThat(lastActiveTs).as(
				"scheduler should be locked - last activity timestamp (%s) should be greater than lock beginning (%s)",
				lastActiveTs, lockBeginning).isGreaterThan(lockBeginning);
		verify(spyWorkersStateGateway).getWorkers();
	}

	@Test
	public void shouldThrowExceptionWhenWatchdogRejectsTaskExecution()
	{
		// given
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);

		final WorkerStateGateway spyWorkersStateGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersStateGateway).when(spyGatewayFactory).getWorkerStateGateway();

		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		final SchedulerAuxiliaryTablesWatchdog mockSchedulerWatchdog = mock(SchedulerAuxiliaryTablesWatchdog.class);
		doThrow(new IllegalStateException("watchdog rejected next task")).when(mockSchedulerWatchdog)
		                                                                 .startWatchdog(runtimeConfigHolder,
				                                                                 taskEngineParameters);
		schedulerRole.setSchedulerWatchdog(mockSchedulerWatchdog);
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		// when
		final Throwable watchdogException = catchThrowable(
				() -> schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10));

		// then
		assertThat(watchdogException).as("scheduler should throw exception when watchdog rejects task execution")
		                             .isInstanceOf(IllegalStateException.class)
		                             .hasMessageEndingWith("watchdog rejected next task");

		verify(mockSchedulerWatchdog).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		verify(spyWorkersStateGateway, never()).getWorkers();
	}

	@Test
	public void shouldThrowExceptionIfNoTenantInParameters()
	{
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final SchedulerStateGateway spySchedulerStateGateway = spy(
				this.auxiliaryTablesGatewayFactory.getSchedulerStateGateway());
		doReturn(spySchedulerStateGateway).when(spyGatewayFactory).getSchedulerStateGateway();

		doReturn(true).when(spySchedulerStateGateway).insertSchedulerRow(anyInt());
		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		final TaskEngineParameters taskEngineParameters = mock(TaskEngineParameters.class);
		doReturn(null).when(taskEngineParameters).getTenant();
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		assertThatThrownBy(() -> schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("non-null tenant must be provided");
	}

	@Test
	public void shouldThrowExceptionIfNoTaskEngineParameters()
	{
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		assertThatThrownBy(() -> schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, null, 10))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("non-null taskEngineParameters must be provided");
	}

	@Test
	public void shouldThrowExceptionIfNoRuntimeConfigHolder()
	{
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();
		final TaskEngineParameters taskEngineParameters = mock(TaskEngineParameters.class);
		assertThatThrownBy(() -> schedulerRole.tryToPerformSchedulerJob(null, taskEngineParameters, 10))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("non-null runtimeConfigHolder must be provided");
	}

	@Test
	public void shouldStopWatchdogTaskWhenExceptionForSchedulerOperation()
	{
		// given
		schedulerWatchdogEnabledSwitch.switchToValue("true");
		final AuxiliaryTablesSchedulerRole schedulerRole = new AuxiliaryTablesSchedulerRole();

		final TaskEngineParameters taskEngineParameters = spy(createTaskEngineParameters());
		final SchedulerAuxiliaryTablesWatchdog mockSchedulerWatchdog = mock(SchedulerAuxiliaryTablesWatchdog.class);
		final AuxiliaryTablesWatchdogTask mockWatchdogTask = mock(AuxiliaryTablesWatchdogTask.class);
		doReturn(mockWatchdogTask).when(mockSchedulerWatchdog).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		schedulerRole.setSchedulerWatchdog(mockSchedulerWatchdog);

		final AuxiliaryTablesGatewayFactory spyGatewayFactory = spy(this.auxiliaryTablesGatewayFactory);
		final WorkerStateGateway spyWorkersStateGateway = spy(this.auxiliaryTablesGatewayFactory.getWorkerStateGateway());
		doReturn(spyWorkersStateGateway).when(spyGatewayFactory).getWorkerStateGateway();
		doThrow(RuntimeException.class).when(spyWorkersStateGateway).getWorkers();

		prepareSchedulerRole(schedulerRole, spyGatewayFactory);
		setSchedulingDelayToForceUnlock(taskEngineParameters, SCHEDULER_INTERVAL);

		// when
		final Throwable error = catchThrowable(
				() -> schedulerRole.tryToPerformSchedulerJob(runtimeConfigHolder, taskEngineParameters, 10));

		// then watchdog task should stop
		assertThat(error).isInstanceOf(RuntimeException.class);
		verify(mockSchedulerWatchdog).startWatchdog(runtimeConfigHolder, taskEngineParameters);
		verify(mockWatchdogTask).close();
	}

	private void setSchedulingDelayToForceUnlock(final TaskEngineParameters taskEngineParameters,
	                                             final Duration schedulerInterval)
	{
		// prolong scheduler operation to unblock memory lock
		doAnswer(answersWithDelay(schedulerInterval.plusMillis(1).toMillis(), InvocationOnMock::callRealMethod)).when(
				taskEngineParameters).getClusterNodeID();
	}

	private TaskEngineParameters createTaskEngineParameters()
	{
		return new TaskEngineParameters.ParametersBuilder()
				.withClusterNodeID(500)
				.withTenant(Registry.getCurrentTenantNoFallback())
				.build();
	}

	private void prepareSchedulerRole(final AuxiliaryTablesSchedulerRole schedulerRole,
	                                  final AuxiliaryTablesGatewayFactory spyGatewayFactory)
	{
		schedulerRole.setGatewayFactory(spyGatewayFactory);
		schedulerRole.setMetricRegistry(metricRegistry);
		schedulerRole.setTypeService(typeService);
		schedulerRole.setWorkerHelper(new DefaultWorkerHelper());
		schedulerRole.afterPropertiesSet();
	}

	private void verifyRecreationOfSchedulerTable(final SchedulerStateGateway gateway, final int i)
	{
		verify(gateway, times(i)).dropTable();
		verify(gateway, times(i)).createTable();
	}

	private void recreateTables()
	{
		final TasksQueueGateway tasksQueueGateway = auxiliaryTablesGatewayFactory.getTasksQueueGateway();
		final WorkerStateGateway workerStateGateway = auxiliaryTablesGatewayFactory.getWorkerStateGateway();
		final SchedulerStateGateway schedulerStateGateway = auxiliaryTablesGatewayFactory.getSchedulerStateGateway();

		final List<BaseGateway> gateways = List.of(tasksQueueGateway, workerStateGateway, schedulerStateGateway);

		for (final BaseGateway gateway : gateways)
		{
			try
			{
				gateway.dropTable();
			}
			catch (final Exception ignored)
			{
			}
		}

		gateways.forEach(BaseGateway::createTable);

		gateways.forEach(gateway -> testHelper.assertTableExists(gateway.getTableName()));
	}

	public static class MockRuntimeConfigHolder extends RuntimeConfigHolder
	{
		@Override
		public <T> T getProperty(final TaskEngineProperty<T> parameter)
		{
			if (Params.SCHEDULER_INTERVAL.equals(parameter))
			{
				return (T) SCHEDULER_INTERVAL;
			}
			if (Params.TASKS_RANGE_START.equals(parameter))
			{
				return (T) Integer.valueOf(0);
			}
			if (Params.TASKS_RANGE_END.equals(parameter))
			{
				return (T) Integer.valueOf(100);
			}
			if (Params.SCHEDULER_CLEAN_QUEUE_OLD_TASKS_THRESHOLD.equals(parameter))
			{
				return (T) Duration.ofSeconds(10);
			}
			return super.getProperty(parameter);
		}
	}
}
