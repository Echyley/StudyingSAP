/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task.impl;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import java.util.concurrent.CountDownLatch;

/**
 * Test watchdog that spies on the watchdog task.
 */
public class TestSchedulerAuxiliaryTablesWatchdog extends SchedulerAuxiliaryTablesWatchdog
{
	private AuxiliaryTablesWatchdogTask spyTask;
	private int startTaskCounter = 0;
	private int runTaskCounter = 0;
	private CountDownLatch stopLatch;

	/**
	 * Creates a new test watchdog instance
	 */
	public TestSchedulerAuxiliaryTablesWatchdog(final AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory)
	{
		super("scheduler-test", auxiliaryTablesGatewayFactory);
		spyTask = spy(NO_OP);
	}

	@Override
	protected AuxiliaryTablesWatchdogTask createTask(final RuntimeConfigHolder runtimeConfigHolder,
	                                                 final TaskEngineParameters taskEngineParameters)
	{
		spyTask = spy(super.createTask(runtimeConfigHolder, taskEngineParameters));
		doAnswer((invocation) -> {
			runTaskCounter++;
			invocation.callRealMethod();
			if (stopLatch != null)
			{
				stopLatch.countDown();
			}
			return null;
		}).when(spyTask).run();
		return spyTask;
	}

	@Override
	public AuxiliaryTablesWatchdogTask startWatchdog(final RuntimeConfigHolder runtimeConfigHolder,
	                                                 final TaskEngineParameters taskEngineParameters)
	{
		startTaskCounter++;
		return super.startWatchdog(runtimeConfigHolder, taskEngineParameters);
	}

	public AuxiliaryTablesWatchdogTask getSpyTask()
	{
		return spyTask;
	}

	public int getStartTaskCounter()
	{
		return startTaskCounter;
	}

	public int getRunTaskCounter()
	{
		return runTaskCounter;
	}

	public void setStopLatch(final CountDownLatch stopLatch)
	{
		this.stopLatch = stopLatch;
	}
}
