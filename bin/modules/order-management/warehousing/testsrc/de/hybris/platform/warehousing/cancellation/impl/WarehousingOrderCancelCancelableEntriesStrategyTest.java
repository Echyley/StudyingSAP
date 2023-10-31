/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.warehousing.cancellation.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousingOrderCancelCancelableEntriesStrategyTest
{
	private final WarehousingOrderCancelCancelableEntriesStrategy strategy = new WarehousingOrderCancelCancelableEntriesStrategy();
	
	@Test
	public void testGetAllCancelableEntries() {
		final OrderModel order = new OrderModel();
		final List<AbstractOrderEntryModel> entryList = new ArrayList<>();
		final OrderEntryModel orderEntry1 = mock(OrderEntryModel.class);
		final OrderEntryModel orderEntry2 = mock(OrderEntryModel.class);
		final OrderEntryModel orderEntry3 = mock(OrderEntryModel.class);
		
		entryList.add(orderEntry1);
		entryList.add(orderEntry2);
		entryList.add(orderEntry3);
		order.setEntries(entryList);
		
		when(orderEntry1.getQuantityPending()).thenReturn(4L);
		when(orderEntry2.getQuantityPending()).thenReturn(null);
		when(orderEntry3.getQuantityPending()).thenReturn(-2L);
		
		final Map<AbstractOrderEntryModel, Long> result = strategy.getAllCancelableEntries(order, null);
		assert(result.size() == 1);
	}
}