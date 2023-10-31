/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ConfigurationOrderEntryMapper;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;


@UnitTest
public class ConfigurationOrderMapperImplTest
{
	private static final String ENTRY_NUMBER = "1";
	private static final String ENTRY_NUMBER_2 = "2";
	private static final String OUTBOUND_ENTRY_NUMBER = "1";
	private static final String OUTBOUND_ENTRY_NUMBER_2 = "2";
	private ConfigurationOrderMapperImpl classUnderTest;

	@Mock
	private OrderModel orderModel;
	private final List<AbstractOrderEntryModel> orderEntries = new ArrayList();
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModel2;
	@Mock
	private ConfigurationOrderEntryMapper orderEntryMapper;

	private SAPCpiOutboundOrderModel outboundOrder;
	private SAPCpiOutboundOrderItemModel outboundOrderItem;
	private SAPCpiOutboundOrderItemModel outboundOrderItem2;
	private final Set<SAPCpiOutboundOrderItemModel> outboundOrderItems = new HashSet();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationOrderMapperImpl(orderEntryMapper);

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

		when(orderEntryMapper.isMapperApplicable(Mockito.any())).thenReturn(true);
		when(orderEntryMapper.mapConfiguration(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
	}

	@Test
	public void testInitProductConfigSets()
	{
		final SAPCpiOutboundOrderModel outboundOrder2 = new SAPCpiOutboundOrderModel();
		assertNull(outboundOrder2.getProductConfigHeaders());
		classUnderTest.initProductConfigSets(outboundOrder2);
		assertNotNull(outboundOrder2.getProductConfigHeaders());
		assertEquals(0, outboundOrder2.getProductConfigHeaders().size());
		final SAPCpiOutboundOrderItemConfigHeaderModel header = new SAPCpiOutboundOrderItemConfigHeaderModel();
		outboundOrder2.getProductConfigHeaders().add(header);
		assertEquals(1, outboundOrder2.getProductConfigHeaders().size());
		classUnderTest.initProductConfigSets(outboundOrder2);
		assertEquals(1, outboundOrder2.getProductConfigHeaders().size());
	}

	@Test
	public void testIsConfigurationMappingNeeded()
	{
		assertTrue(classUnderTest.isConfigurationMappingNeeded(orderModel.getEntries()));
	}

	@Test
	public void testIsConfigurationMappingNeededFalse()
	{
		when(orderEntryMapper.isMapperApplicable(Mockito.any())).thenReturn(false);
		assertFalse(classUnderTest.isConfigurationMappingNeeded(orderModel.getEntries()));
	}

	@Test
	public void testFindOutboundItem1()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel);
		assertNotNull(result);
		assertEquals(outboundOrderItem, result);
	}

	@Test
	public void testFindOutboundItem2()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel2);
		assertEquals(outboundOrderItem2, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testFindOutboundItemEntryNumberNotExisting()
	{
		when(orderEntryModel.getEntryNumber()).thenReturn(Integer.valueOf("99"));
		final SAPCpiOutboundOrderItemModel outboundItem = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel);
	}
}
