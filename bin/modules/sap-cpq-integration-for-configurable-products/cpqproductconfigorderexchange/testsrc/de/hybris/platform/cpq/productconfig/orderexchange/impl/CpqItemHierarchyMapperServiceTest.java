/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.orderexchange.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cpq.productconfig.orderexchange.CpqOrderEntryMapper;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link CpqItemHierarchyMapperService}
 */
@UnitTest
public class CpqItemHierarchyMapperServiceTest
{
	private static final String ENTRY_NUMBER = "10";
	private static final String ENTRY_NUMBER_2 = "20";
	private static final String OUTBOUND_ENTRY_NUMBER = "10";
	private static final String OUTBOUND_ENTRY_NUMBER_2 = "20";
	@Mock
	private CpqOrderEntryMapper orderEntryMapper;
	private CpqItemHierarchyMapperService classUnderTest;

	@Mock
	private OrderModel orderModel;
	private final List<AbstractOrderEntryModel> orderEntries = new ArrayList();
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModel2;
	private SAPCpiOutboundOrderModel outboundOrder;
	private SAPCpiOutboundOrderItemModel outboundOrderItem;
	private SAPCpiOutboundOrderItemModel outboundOrderItem2;
	private final Set<SAPCpiOutboundOrderItemModel> outboundOrderItems = new HashSet();
	private final List<SAPCpiOutboundOrderItemModel> additionalLineItems = new ArrayList<SAPCpiOutboundOrderItemModel>();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CpqItemHierarchyMapperService(orderEntryMapper, 10, 1);
		orderEntries.add(orderEntryModel);
		orderEntries.add(orderEntryModel2);
		when(orderModel.getEntries()).thenReturn(orderEntries);
		when(orderEntryModel.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUMBER));
		when(orderEntryModel2.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUMBER_2));
		outboundOrder = new SAPCpiOutboundOrderModel();
		outboundOrderItem = new SAPCpiOutboundOrderItemModel();
		outboundOrderItem.setEntryNumber(OUTBOUND_ENTRY_NUMBER);
		outboundOrderItem2 = new SAPCpiOutboundOrderItemModel();
		outboundOrderItem2.setEntryNumber(OUTBOUND_ENTRY_NUMBER_2);
		outboundOrderItems.add(outboundOrderItem);
		outboundOrderItems.add(outboundOrderItem2);
		outboundOrder.setSapCpiOutboundOrderItems(outboundOrderItems);
		when(orderEntryMapper.isMapperApplicable(orderEntryModel)).thenReturn(true);

		additionalLineItems.add(new SAPCpiOutboundOrderItemModel());
		additionalLineItems.add(new SAPCpiOutboundOrderItemModel());
		additionalLineItems.add(new SAPCpiOutboundOrderItemModel());
		additionalLineItems.get(0).setEntryNumber("11");
		additionalLineItems.get(1).setEntryNumber("12");
		additionalLineItems.get(2).setEntryNumber("13");
		when(orderEntryMapper.mapCPQLineItems(orderEntryModel, outboundOrderItem)).thenReturn(additionalLineItems);
	}

	@Test
	public void testFindOutboundItem1()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel);
		assertEquals(outboundOrderItem, result);
	}

	@Test
	public void testFindOutboundItem2()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel2);
		assertEquals(outboundOrderItem2, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testFindOutboundItemNotExisting()
	{
		final AbstractOrderEntryModel orderEntryModelNotExisting = mock(AbstractOrderEntryModel.class);
		when(orderEntryModelNotExisting.getEntryNumber()).thenReturn(Integer.valueOf("1"));
		classUnderTest.findOutboundItem(outboundOrder, orderEntryModelNotExisting);
	}

	@Test
	public void testIsCPQMappingNeeded()
	{
		assertTrue(classUnderTest.isCPQMappingNeeded(orderEntries));
	}

	@Test
	public void testIsCPQMappingNeededNoCPQProducts()
	{
		when(orderEntryMapper.isMapperApplicable(orderEntryModel)).thenReturn(false);
		assertFalse(classUnderTest.isCPQMappingNeeded(orderEntries));
	}

	@Test
	public void testEntryNumbersNoCPQProducts()
	{
		when(orderEntryMapper.isMapperApplicable(orderEntryModel)).thenReturn(false);
		classUnderTest.map(orderModel, outboundOrder);
		assertEquals(OUTBOUND_ENTRY_NUMBER, outboundOrderItem.getEntryNumber());
		assertEquals(OUTBOUND_ENTRY_NUMBER_2, outboundOrderItem2.getEntryNumber());
	}

	@Test
	public void testEntryNumbersCPQProductFirstPosition()
	{
		classUnderTest.map(orderModel, outboundOrder);
		assertEquals(OUTBOUND_ENTRY_NUMBER, outboundOrderItem.getEntryNumber());
		assertEquals(OUTBOUND_ENTRY_NUMBER_2, outboundOrderItem2.getEntryNumber());
		assertEquals(5, outboundOrderItems.size());
	}

	@Test
	public void testItemComparator()
	{
		final Comparator<SAPCpiOutboundOrderItemModel> itemComparator = classUnderTest.getItemComparator();
		final SAPCpiOutboundOrderItemModel item1 = new SAPCpiOutboundOrderItemModel();
		final SAPCpiOutboundOrderItemModel item2 = new SAPCpiOutboundOrderItemModel();
		item1.setEntryNumber("5");
		item2.setEntryNumber("3");
		final int result = itemComparator.compare(item1, item2);
		assertEquals(2, result);
	}

	@Test
	public void testItemComparatorList()
	{
		final Comparator<SAPCpiOutboundOrderItemModel> itemComparator = classUnderTest.getItemComparator();
		final List<SAPCpiOutboundOrderItemModel> list = new ArrayList<SAPCpiOutboundOrderItemModel>();
		final SAPCpiOutboundOrderItemModel item1 = new SAPCpiOutboundOrderItemModel();
		final SAPCpiOutboundOrderItemModel item2 = new SAPCpiOutboundOrderItemModel();
		item1.setEntryNumber("5");
		item2.setEntryNumber("3");
		list.add(item1);
		list.add(item2);
		Collections.sort(list, (i, j) -> itemComparator.compare(i, j));

		assertEquals(item2.getEntryNumber(), list.get(0).getEntryNumber());
		assertEquals(item1.getEntryNumber(), list.get(1).getEntryNumber());
	}

	@Test
	public void testEntryNumbersOutboundItemSorted()
	{
		classUnderTest.map(orderModel, outboundOrder);
		assertEquals(5, outboundOrder.getSapCpiOutboundOrderItems().size());
		final Iterator<SAPCpiOutboundOrderItemModel> iterator = outboundOrder.getSapCpiOutboundOrderItems().iterator();
		final int[] targetEntryNumbers =
		{ 10, 11, 12, 13, 20 };
		for (final int targetEntryNumber : targetEntryNumbers)
		{
			assertEquals(String.valueOf(targetEntryNumber), iterator.next().getEntryNumber());
		}
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testCheckSpacing()
	{
		classUnderTest.checkSpacing(5);
	}

	@Test(expected = IllegalStateException.class)
	public void testCheckSpacingFails()
	{
		classUnderTest.checkSpacing(10);
	}

	@Test
	public void testCheckSpacingOk()
	{
		final CpqItemHierarchyMapperService clausUnderTest = new CpqItemHierarchyMapperService(orderEntryMapper, 11, 1);
		clausUnderTest.checkSpacing(10);
	}

	@Test(expected = IllegalStateException.class)
	public void testCheckSpacingNotOkDueToSubItems()
	{
		final CpqItemHierarchyMapperService clausUnderTest = new CpqItemHierarchyMapperService(orderEntryMapper, 10, 2);
		clausUnderTest.checkSpacing(5);
	}

	@Test(expected = IllegalStateException.class)
	public void testMapTooManyAdditionalItems()
	{
		final List<SAPCpiOutboundOrderItemModel> additionalLineItems2 = new ArrayList<SAPCpiOutboundOrderItemModel>();
		for (int i = 0; i < 10; i++)
		{
			final SAPCpiOutboundOrderItemModel lineItem = new SAPCpiOutboundOrderItemModel();
			lineItem.setEntryNumber(String.valueOf(i));
			additionalLineItems2.add(lineItem);
		}
		when(orderEntryMapper.mapCPQLineItems(orderEntryModel, outboundOrderItem)).thenReturn(additionalLineItems2);
		classUnderTest.map(orderModel, outboundOrder);
	}

}
