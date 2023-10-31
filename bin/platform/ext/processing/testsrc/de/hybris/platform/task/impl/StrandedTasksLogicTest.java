/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.task.impl;

import static de.hybris.platform.task.impl.StrandedTaskContext.StrandedTaskFailureRootCause.DELETE;
import static de.hybris.platform.task.impl.StrandedTaskContext.StrandedTaskFailureRootCause.UNLOCK;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert;
import de.hybris.platform.testframework.log.TestLogListener;
import de.hybris.platform.util.StrandedItemsRegistry;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

@IntegrationTest
public class StrandedTasksLogicTest extends ServicelayerBaseTest
{
	private final static int ITEMS_IN_STRANDED_TASKS_REGISTRY = 10;
	private final TestLogListener testLogListener = new TestLogListener();

	@Before
	public void setUp()
	{
		testLogListener.attach();
	}

	@After
	public void tearDown()
	{
		testLogListener.detach();
	}

	@Test
	public void shouldStrandedItemsRegistryHaveSameAmountOfItemsAfterUnlockFail()
	{
		// given
		final StrandedTasksLogic strandedTasksLogic = prepareStrandedTasksLogicWithFailingTaskDao();
		populateStrandedTasksRegistry(strandedTasksLogic.getStrandedItemsRegistry(), ITEMS_IN_STRANDED_TASKS_REGISTRY);

		// when
		strandedTasksLogic.unlockStrandedTasks();

		//then
		assertThat(strandedTasksLogic.getStrandedItemsRegistry().getStrandedItems()).hasSize(ITEMS_IN_STRANDED_TASKS_REGISTRY);
		assertThatStrandedTasksLogicLogged("Failed to unlock stranded tasks", 1);
	}

	@Test
	public void shouldStrandedItemsRegistryHaveSameAmountOfItemsWithConstantlyFailingUnlock()
	{
		// given
		final StrandedTasksLogic strandedTasksLogic = prepareStrandedTasksLogicWithFailingTaskDao();
		populateStrandedTasksRegistry(strandedTasksLogic.getStrandedItemsRegistry(), ITEMS_IN_STRANDED_TASKS_REGISTRY);

		// when
		IntStream.range(0, 100).forEach(a -> strandedTasksLogic.unlockStrandedTasks());

		//then
		assertThat(strandedTasksLogic.getStrandedItemsRegistry().getStrandedItems()).hasSize(ITEMS_IN_STRANDED_TASKS_REGISTRY);
		assertThatStrandedTasksLogicLogged("Failed to unlock stranded tasks", 100);
	}

	@Test
	public void shouldNotRemoveItemFromStrandedTasksRegistryWhenUnlockThrowsException()
	{
		// given
		final List<PK> pks = createPks(ITEMS_IN_STRANDED_TASKS_REGISTRY);

		final StrandedTasksResolutionHandler strandedTasksResolutionHandler = prepareStrandedTasksResolutionHandlerWithFailingUnlockAndMarkAsFailed(pks);
		final StrandedTasksLogic strandedTasksLogic = new StrandedTasksLogic(strandedTasksResolutionHandler);
		populateStrandedTasksRegistry(strandedTasksLogic.getStrandedItemsRegistry(), pks, UNLOCK);

		// when
		IntStream.range(0, 10).forEach(a -> strandedTasksLogic.unlockStrandedTasks());

		// then
		assertThat(strandedTasksLogic.getStrandedItemsRegistry().getStrandedItems()).hasSize(ITEMS_IN_STRANDED_TASKS_REGISTRY);
		assertThatStrandedTasksResolutionHandlerLogged("Failed to unlock task with pk:", 100);
	}


	@Test
	public void shouldNotRemoveItemFromStrandedTasksRegistryWhenMarkAsFailedThrowsException()
	{
		// given
		final List<PK> pks = createPks(ITEMS_IN_STRANDED_TASKS_REGISTRY);

		final StrandedTasksResolutionHandler strandedTasksResolutionHandler = prepareStrandedTasksResolutionHandlerWithFailingUnlockAndMarkAsFailed(pks);
		final StrandedTasksLogic strandedTasksLogic = new StrandedTasksLogic(strandedTasksResolutionHandler);
		populateStrandedTasksRegistry(strandedTasksLogic.getStrandedItemsRegistry(), pks, DELETE);

		// when
		IntStream.range(0, 10).forEach(a -> strandedTasksLogic.unlockStrandedTasks());

		// then
		assertThat(strandedTasksLogic.getStrandedItemsRegistry().getStrandedItems()).hasSize(ITEMS_IN_STRANDED_TASKS_REGISTRY);
		assertThatStrandedTasksResolutionHandlerLogged("Failed to mark as failed task with pk:", 100);
	}

	StrandedTasksLogic prepareStrandedTasksLogicWithFailingTaskDao()
	{
		final TaskDAO errorThrowingTaskDao = Mockito.mock(TaskDAO.class);
		Mockito.doThrow(new TestRuntimeException())
		       .when(errorThrowingTaskDao)
		       .fetchAllRunningProcessTask(any());
		return new StrandedTasksLogic(errorThrowingTaskDao, null);
	}

	private List<PK> createPks(final int pkNumber)
	{
		return IntStream.range(0, pkNumber).mapToObj(a -> PK.createUUIDPK(5)).collect(Collectors.toList());
	}

	private void populateStrandedTasksRegistry(final StrandedItemsRegistry strandedItemsRegistry, final int itemsNumber)
	{
		IntStream.range(0, itemsNumber)
		         .mapToObj(a -> PK.createUUIDPK(5))
		         .forEach(strandedItemsRegistry::markStrandedItem);
		assertThat(strandedItemsRegistry.getStrandedItems()).hasSize(itemsNumber);
	}

	private void assertThatStrandedTasksLogicLogged(final String msg, final int occurrences)
	{
		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(StrandedTasksLogic.class)
		                     .withMessageContaining(msg)
		                     .withLogLevel(TestLogListenerAssert.LogLevel.WARN)
		                     .occurrences(occurrences);
	}

	private void assertThatStrandedTasksResolutionHandlerLogged(final String msg, final int occurrences)
	{
		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(StrandedTasksResolutionHandler.class)
		                     .withMessageContaining(msg)
		                     .withLogLevel(TestLogListenerAssert.LogLevel.WARN)
		                     .withThrowable(TestRuntimeException.class)
		                     .occurrences(occurrences);
	}

	private StrandedTasksResolutionHandler prepareStrandedTasksResolutionHandlerWithFailingUnlockAndMarkAsFailed(
			final List<PK> pks)
	{
		final StrandedTasksResolutionHandler strandedTasksResolutionHandler = Mockito.spy(
				new StrandedTasksResolutionHandler(null, null));
		Mockito.doThrow(new TestRuntimeException()).when(strandedTasksResolutionHandler).unlockTask(any(), any());
		Mockito.doThrow(new TestRuntimeException()).when(strandedTasksResolutionHandler).markTaskFailed(any());
		Mockito.doReturn(pks).when(strandedTasksResolutionHandler).filterItemsToResolve(any(), any());
		return strandedTasksResolutionHandler;
	}

	private void populateStrandedTasksRegistry(final StrandedItemsRegistry strandedItemsRegistry, final List<PK> pks,
	                                           final StrandedTaskContext.StrandedTaskFailureRootCause failureRootCause)
	{
		pks.forEach(pk -> strandedItemsRegistry.markStrandedItem(pk, strandedTaskContext(failureRootCause)));
		assertThat(strandedItemsRegistry.getStrandedItems()).hasSize(pks.size());
	}

	private StrandedTaskContext strandedTaskContext(final StrandedTaskContext.StrandedTaskFailureRootCause failureRootCause)
	{
		return StrandedTaskContext
				.builder()
				.withFailureRootCause(failureRootCause).build();
	}

	private static class TestRuntimeException extends RuntimeException
	{
	}
}
