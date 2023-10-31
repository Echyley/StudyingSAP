/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.inventoryevent.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.inventoryevent.dao.InventoryEventDao;
import de.hybris.platform.warehousing.model.AllocationEventModel;
import de.hybris.platform.warehousing.model.CancellationEventModel;
import de.hybris.platform.warehousing.model.IncreaseEventModel;
import de.hybris.platform.warehousing.model.ShrinkageEventModel;
import de.hybris.platform.warehousing.model.WastageEventModel;
import de.hybris.platform.warehousing.stock.strategies.StockLevelSelectionStrategy;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultInventoryEventServiceTest
{
	@InjectMocks
	private DefaultInventoryEventService inventoryEventService;

	@Mock
	private IncreaseEventModel increaseEventModel;
	@Mock
	private ShrinkageEventModel shrinkageEventModel;
	@Mock
	private WastageEventModel wastageEventModel;
	@Mock
	private ProductModel productModel;
	@Mock
	private WarehouseModel warehouseModel;
	@Mock
	private StockLevelModel stockLevelModel1;
	@Mock
	private StockLevelModel stockLevelModel2;
	@Mock
	private StockLevelModel stockLevelModel3;
	@Mock
	private ModelService modelService;
	@Mock
	private StockService stockService;
	@Mock
	private StockLevelSelectionStrategy stockLevelSelectionStrategy;
	@Mock
	private InventoryEventDao inventoryEventDao;
	@Mock
	private TimeService timeService;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private ConsignmentEntryModel consignmentEntryModel1;
	@Mock
	private ConsignmentEntryModel consignmentEntryModel2;
	@Mock
	private DeclineEntry declineEntry;
	@Spy
	private AllocationEventModel allocationEventModel1;
	@Spy
	private AllocationEventModel allocationEventModel2;
	@Spy
	private AllocationEventModel allocationEventModel3;
	@Mock
	private OrderEntryModel orderEntryModel1;
	@Mock
	private OrderEntryModel orderEntryModel2;
	@Spy
	private CancellationEventModel cancellationEventModel1;

	private long quantity = 5;
	private Map<StockLevelModel, Long> stockMap = new HashMap<>();
	MockitoSession mockito;

	@Before
	public void setUp()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
		inventoryEventService.setModelService(modelService);
		inventoryEventService.setStockService(stockService);
		inventoryEventService.setStockLevelSelectionStrategy(stockLevelSelectionStrategy);
		inventoryEventService.setInventoryEventDao(inventoryEventDao);

		Mockito.lenient().when(shrinkageEventModel.getStockLevel()).thenReturn(stockLevelModel1);
		Mockito.lenient().when(shrinkageEventModel.getQuantity()).thenReturn(quantity);

		Mockito.lenient().when(wastageEventModel.getStockLevel()).thenReturn(stockLevelModel1);
		Mockito.lenient().when(wastageEventModel.getQuantity()).thenReturn(quantity);

		Mockito.lenient().when(increaseEventModel.getStockLevel()).thenReturn(stockLevelModel1);
		Mockito.lenient().when(increaseEventModel.getQuantity()).thenReturn(quantity);

		Mockito.lenient().when(stockLevelModel1.getWarehouse()).thenReturn(warehouseModel);
		Mockito.lenient().when(stockLevelModel1.getProduct()).thenReturn(productModel);

		Mockito.lenient().when(stockLevelModel2.getWarehouse()).thenReturn(warehouseModel);
		Mockito.lenient().when(stockLevelModel2.getProduct()).thenReturn(productModel);

		Mockito.lenient().when(stockLevelModel3.getWarehouse()).thenReturn(warehouseModel);
		Mockito.lenient().when(stockLevelModel3.getProduct()).thenReturn(productModel);

		Mockito.lenient().when(consignmentModel.getConsignmentEntries())
				.thenReturn(new HashSet<>(Arrays.asList(consignmentEntryModel1, consignmentEntryModel2)));

		Mockito.lenient().when(consignmentEntryModel1.getOrderEntry()).thenReturn(orderEntryModel1);
		Mockito.lenient().when(consignmentEntryModel2.getOrderEntry()).thenReturn(orderEntryModel2);
		Mockito.lenient().when(consignmentEntryModel1.getConsignment()).thenReturn(consignmentModel);
		Mockito.lenient().when(consignmentEntryModel2.getConsignment()).thenReturn(consignmentModel);
		Mockito.lenient().when(consignmentEntryModel1.getQuantity()).thenReturn(1L);
		Mockito.lenient().when(consignmentEntryModel2.getQuantity()).thenReturn(1L);

		Mockito.lenient().when(orderEntryModel1.getProduct()).thenReturn(productModel);
		Mockito.lenient().when(orderEntryModel2.getProduct()).thenReturn(productModel);

		Mockito.lenient().when(warehouseModel.getCode()).thenReturn("WAREHOUSE_CODE");
		Mockito.lenient().when(productModel.getCode()).thenReturn("PRODUCT_CODE");
		Mockito.lenient().when(consignmentModel.getWarehouse()).thenReturn(warehouseModel);
		Mockito.lenient().when(warehouseModel.isExternal()).thenReturn(false);

		Mockito.lenient().when(allocationEventModel1.getStockLevel()).thenReturn(stockLevelModel1);
		Mockito.lenient().when(allocationEventModel2.getStockLevel()).thenReturn(stockLevelModel2);
		Mockito.lenient().when(allocationEventModel3.getStockLevel()).thenReturn(stockLevelModel3);
		Mockito.lenient().when(stockLevelModel1.getReleaseDate()).thenReturn(new Date());
		Mockito.lenient().when(stockLevelModel2.getReleaseDate()).thenReturn(new Date());
		Mockito.lenient().when(stockLevelModel3.getReleaseDate()).thenReturn(new Date());

		Mockito.lenient().when(stockService.getStockLevels(any(), any())).thenReturn(Arrays.asList(stockLevelModel1, stockLevelModel2));
		Mockito.lenient().when(stockLevelSelectionStrategy.getStockLevelsForAllocation(any(), any())).thenReturn(stockMap);
		Mockito.lenient().when(stockLevelSelectionStrategy.getStockLevelsForCancellation(any(), any())).thenReturn(stockMap);

		Mockito.lenient().when(modelService.create(AllocationEventModel.class)).thenReturn(allocationEventModel1);
		Mockito.lenient().when(modelService.create(CancellationEventModel.class)).thenReturn(cancellationEventModel1);
	}

	@After
	public void cleanUp() throws Exception
	{
		mockito.finishMocking();
	}

	@Test
	public void shouldCreateIncreaseEvent()
	{
		when(modelService.create(IncreaseEventModel.class)).thenReturn(increaseEventModel);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		IncreaseEventModel resultEvent = inventoryEventService.createIncreaseEvent(increaseEventModel);
		verify(modelService, times(1)).save(increaseEventModel);
		assertEquals(quantity, resultEvent.getQuantity());
	}

	@Test
	public void shouldCreateShrinkageEvent()
	{
		when(modelService.create(ShrinkageEventModel.class)).thenReturn(shrinkageEventModel);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		ShrinkageEventModel resultEvent = inventoryEventService.createShrinkageEvent(shrinkageEventModel);
		verify(modelService, times(1)).save(shrinkageEventModel);
		assertEquals(quantity, resultEvent.getQuantity());
	}

	@Test
	public void shouldCreateWastageEvent()
	{
		when(modelService.create(WastageEventModel.class)).thenReturn(wastageEventModel);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		WastageEventModel resultEvent = inventoryEventService.createWastageEvent(wastageEventModel);
		verify(modelService, times(1)).save(wastageEventModel);
		assertEquals(quantity, resultEvent.getQuantity());
	}

	@Test
	public void shouldCreateAllocationEvents()
	{
		stockMap.put(stockLevelModel1, 100L);
		final Collection<AllocationEventModel> allocationEvents = inventoryEventService.createAllocationEvents(consignmentModel);

		assertEquals(2, allocationEvents.size());
		verify(modelService, times(1)).saveAll(allocationEvents);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreateAllocationEvents_externalWarehouse()
	{
		when(warehouseModel.isExternal()).thenReturn(true);
		stockMap.put(stockLevelModel1, 1L);
		inventoryEventService.createAllocationEvents(consignmentModel);
	}

	@Test
	public void shouldCreateAllocationEventsForEntry()
	{
		stockMap.put(stockLevelModel1, 5L);
		final AllocationEventModel firstAllocationEvent = inventoryEventService
				.createAllocationEventsForConsignmentEntry(consignmentEntryModel1).iterator().next();

		assertEquals(5L, firstAllocationEvent.getQuantity());
		assertEquals(stockLevelModel1, firstAllocationEvent.getStockLevel());
		verify(modelService, times(1)).save(firstAllocationEvent);
	}

	@Test
	public void shouldCreateCancellationEvents()
	{
		stockMap.put(stockLevelModel1, 5L);
		when(cancellationEventModel1.getConsignmentEntry()).thenReturn(consignmentEntryModel1);
		when(inventoryEventDao.getAllocationEventsForConsignmentEntry(consignmentEntryModel1))
				.thenReturn(Arrays.asList(allocationEventModel1));
		final CancellationEventModel firstCancellationEvent = inventoryEventService
				.createCancellationEvents(cancellationEventModel1).iterator().next();

		assertEquals(5L, firstCancellationEvent.getQuantity());
		assertEquals(stockLevelModel1, firstCancellationEvent.getStockLevel());
		verify(modelService, times(1)).save(firstCancellationEvent);
	}

	@Test
	public void shouldReallocateAllocationEvent_Complete()
	{
		when(declineEntry.getConsignmentEntry()).thenReturn(consignmentEntryModel1);
		when(consignmentEntryModel1.getQuantity()).thenReturn(1L);
		when(inventoryEventDao.getAllocationEventsForConsignmentEntry(consignmentEntryModel1))
				.thenReturn(Arrays.asList(allocationEventModel1));

		inventoryEventService.reallocateAllocationEvent(declineEntry, 1L);

		verify(modelService, times(1)).removeAll(Arrays.asList(allocationEventModel1));
	}

	@Test
	public void shouldReallocateAllocationEvent_Partial()
	{
		when(declineEntry.getConsignmentEntry()).thenReturn(consignmentEntryModel1);
		when(consignmentEntryModel1.getQuantity()).thenReturn(2L);
		when(inventoryEventDao.getAllocationEventsForConsignmentEntry(consignmentEntryModel1))
				.thenReturn(Arrays.asList(allocationEventModel1));
		when(allocationEventModel1.getQuantity()).thenReturn(1L);
		inventoryEventService.reallocateAllocationEvent(declineEntry, 1L);
		verify(modelService, times(1)).remove(allocationEventModel1);
	}

	@Test
	public void shouldReallocateAllocationEvent_Partial_TwoStocks_OneAdjustQuantity()
	{
		when(declineEntry.getConsignmentEntry()).thenReturn(consignmentEntryModel1);
		when(consignmentEntryModel1.getQuantity()).thenReturn(5L);
		when(inventoryEventDao.getAllocationEventsForConsignmentEntry(consignmentEntryModel1))
				.thenReturn(Arrays.asList(allocationEventModel1, allocationEventModel2));

		when(allocationEventModel1.getQuantity()).thenReturn(1L);
		when(allocationEventModel2.getQuantity()).thenReturn(4L);

		final Calendar calendar = Calendar.getInstance();
		calendar.set(2000, 1, 1);
		when(stockLevelModel1.getReleaseDate()).thenReturn(calendar.getTime());

		when(stockLevelModel2.getReleaseDate()).thenReturn(null);

		inventoryEventService.reallocateAllocationEvent(declineEntry, 2L);

		verify(modelService, times(1)).remove(allocationEventModel1);
		verify(modelService, times(0)).remove(allocationEventModel2);
		verify(allocationEventModel1, times(0)).setQuantity(anyLong());
		verify(allocationEventModel2, times(1)).setQuantity(3L);
	}

	@Test
	public void shouldGetAllocationEventsForReallocation()
	{
		// Should sort by nulls last, reverse order.
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2000, 1, 1);
		when(stockLevelModel1.getReleaseDate()).thenReturn(calendar.getTime());

		when(stockLevelModel2.getReleaseDate()).thenReturn(null);

		calendar.set(2020, 1, 1);
		when(stockLevelModel3.getReleaseDate()).thenReturn(calendar.getTime());

		when(allocationEventModel1.getQuantity()).thenReturn(2L);
		when(allocationEventModel2.getQuantity()).thenReturn(2L);
		when(allocationEventModel3.getQuantity()).thenReturn(1L);

		final Map<AllocationEventModel, Long> allocationMap = inventoryEventService.getAllocationEventsForReallocation(
				Arrays.asList(allocationEventModel1, allocationEventModel2, allocationEventModel3), 4L);

		assertEquals(2L, allocationMap.get(allocationEventModel1).longValue());
		assertEquals(1L, allocationMap.get(allocationEventModel2).longValue());
		assertEquals(1L, allocationMap.get(allocationEventModel3).longValue());
	}

	@Test
	public void shouldGetAllocationEventsForReallocation_NotEnoughQuantity()
	{
		when(allocationEventModel1.getQuantity()).thenReturn(1L);
		when(allocationEventModel2.getQuantity()).thenReturn(1L);
		when(allocationEventModel3.getQuantity()).thenReturn(1L);

		// Make sure allocationEvent1 is the first after sort.
		when(stockLevelModel2.getReleaseDate()).thenReturn(null);
		when(stockLevelModel3.getReleaseDate()).thenReturn(null);

		final Map<AllocationEventModel, Long> allocationMap = inventoryEventService.getAllocationEventsForReallocation(
				Arrays.asList(allocationEventModel1, allocationEventModel2, allocationEventModel3), 5L);

		assertEquals(3L, allocationMap.get(allocationEventModel1).longValue());
		assertEquals(1L, allocationMap.get(allocationEventModel2).longValue());
		assertEquals(1L, allocationMap.get(allocationEventModel3).longValue());
	}
}
