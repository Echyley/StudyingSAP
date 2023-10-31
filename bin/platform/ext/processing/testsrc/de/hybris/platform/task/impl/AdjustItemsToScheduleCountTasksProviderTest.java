/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl;

import static de.hybris.platform.task.impl.RuntimeConfigHolder.intProperty;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.task.impl.RuntimeConfigHolder.IntTaskEngineProperty;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class AdjustItemsToScheduleCountTasksProviderTest
{
	private final IntTaskEngineProperty multiplierProperty = intProperty(UUID.randomUUID().toString(), 10);

	private AdjustItemsToScheduleCountTasksProvider provider;

	@Mock
	private TasksProvider mockDelegate;

	@Spy
	private MockRuntimeConfigHolder mockConfigHolder = new MockRuntimeConfigHolder();

	@Before
	public void setUp() throws Exception
	{
		provider = new AdjustItemsToScheduleCountTasksProvider(mockDelegate, multiplierProperty);
	}

	@Test
	public void shouldMultiplyTheNumberOfItemsToPoll()
	{
		final int itemsToSchedule = 10;
		final int multiplier = 20;

		when(mockConfigHolder.getProperty(multiplierProperty)).thenReturn(multiplier);

		provider.getTasksToSchedule(mockConfigHolder, null, itemsToSchedule);

		verify(mockDelegate).getTasksToSchedule(any(), isNull(), eq(itemsToSchedule * multiplier));
	}

	@Test
	public void shouldMultiplyTheNumberOfItemsToPollWithNegativeMultiplier()
	{
		final int itemsToSchedule = 10;
		final int multiplier = -1;

		when(mockConfigHolder.getProperty(multiplierProperty)).thenReturn(multiplier);

		provider.getTasksToSchedule(mockConfigHolder, null, itemsToSchedule);

		verify(mockDelegate).getTasksToSchedule(any(), isNull(), eq(itemsToSchedule));
	}

	@Test
	public void shouldMultiplyTheNumberOfItemsToPollWithZeroMultiplier()
	{
		final int itemsToSchedule = 10;
		final int multiplier = 0;

		when(mockConfigHolder.getProperty(multiplierProperty)).thenReturn(multiplier);

		provider.getTasksToSchedule(mockConfigHolder, null, itemsToSchedule);

		verify(mockDelegate).getTasksToSchedule(any(), isNull(), eq(itemsToSchedule));
	}

	public static class MockRuntimeConfigHolder extends RuntimeConfigHolder
	{
		@Override
		public <T> T getProperty(final TaskEngineProperty<T> parameter)
		{
			return super.getProperty(parameter);
		}
	}
}
