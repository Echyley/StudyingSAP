/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.runtime.cps.enums.SAPProductConfigOrderOutboundMappingMode;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultMappingDecisionStrategyTest
{

	DefaultMappingDecisionStrategy classUnderTest = new DefaultMappingDecisionStrategy();
	@Mock
	private AbstractOrderModel order;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private SAPConfigurationModel sapConfiguration;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		when(order.getStore()).thenReturn(baseStore);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfiguration);
		when(sapConfiguration.getSapproductconfig_order_outbound_mapping_mode())
				.thenReturn(SAPProductConfigOrderOutboundMappingMode.A2ASERVICEFORMAT);

	}

	@Test
	public void testIsA2AServiceMappingActive()
	{
		assertTrue(classUnderTest.isA2AServiceMappingActive(order));
	}

	@Test
	public void testIsA2AServiceMappingActiveNoModeSet()
	{
		when(sapConfiguration.getSapproductconfig_order_outbound_mapping_mode()).thenReturn(null);
		assertFalse(classUnderTest.isA2AServiceMappingActive(order));
	}

	@Test
	public void testIsA2AServiceMappingActiveWhenIdoc()
	{
		when(sapConfiguration.getSapproductconfig_order_outbound_mapping_mode())
				.thenReturn(SAPProductConfigOrderOutboundMappingMode.IDOCFORMAT);
		assertFalse(classUnderTest.isA2AServiceMappingActive(order));
	}

	@Test(expected = NullPointerException.class)
	public void testIsA2AServiceMappingActiveNoOrder()
	{
		classUnderTest.isA2AServiceMappingActive(null);
	}

	@Test(expected = NullPointerException.class)
	public void testIsA2AServiceMappingActiveNoBaseStore()
	{
		when(order.getStore()).thenReturn(null);
		classUnderTest.isA2AServiceMappingActive(order);
	}

	@Test(expected = NullPointerException.class)
	public void testIsA2AServiceMappingActiveNoSapConfig()
	{
		when(baseStore.getSAPConfiguration()).thenReturn(null);
		classUnderTest.isA2AServiceMappingActive(order);
	}
}
