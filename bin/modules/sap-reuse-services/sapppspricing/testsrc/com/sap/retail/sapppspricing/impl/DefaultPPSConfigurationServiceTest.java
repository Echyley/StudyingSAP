/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.*;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.retail.sapppspricing.impl.DefaultPPSConfigService;


@SuppressWarnings("javadoc")
public class DefaultPPSConfigurationServiceTest
{
	DefaultPPSConfigService cut;

	@Mock
	private BaseStoreService baseStoreService;
	private BaseStoreModel baseStoreModel;
	private SAPConfigurationModel sapConfig;
	private CartModel order;
	private ProductModel product;

	@Before
	public void setUp()
	{
		cut = new DefaultPPSConfigService();
		baseStoreModel = new BaseStoreModel();
		sapConfig = new SAPConfigurationModel();
		order = new CartModel();
		product = new ProductModel();
		MockitoAnnotations.initMocks(this);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		baseStoreModel.setSAPConfiguration(sapConfig);
		sapConfig.setSappps_active(Boolean.TRUE);
		cut.setBaseStoreService(baseStoreService);
		order.setStore(baseStoreModel);
	}

	@Test
	public void testSetGetBaseStoreService()
	{
		cut = new DefaultPPSConfigService();
		assertNull(cut.getBaseStoreService());
		cut.setBaseStoreService(baseStoreService);
		assertSame(baseStoreService, cut.getBaseStoreService());
	}

	@Test
	public void testNoSapConfigActive()
	{
		sapConfig.setSappps_active(Boolean.FALSE);
		assertFalse(cut.isPpsActive(order));
	}

	@Test
	public void testNoSapConfigAssigned()
	{
		baseStoreModel.setSAPConfiguration(null);
		assertFalse(cut.isPpsActive(order));
	}

	@Test
	public void testNoSapConfigActiveProd()
	{
		sapConfig.setSappps_active(Boolean.FALSE);
		assertFalse(cut.isPpsActive(product));
	}

	@Test
	public void testNoSapConfigAssignedProd()
	{
		baseStoreModel.setSAPConfiguration(null);
		assertFalse(cut.isPpsActive(product));
	}

	@Test
	public void testGetBuIdOrder()
	{
		sapConfig.setSappps_businessUnitId("BU1");
		assertEquals("BU1", cut.getBusinessUnitId(order));
	}

	@Test
	public void testGetBuIdProd()
	{
		sapConfig.setSappps_businessUnitId("BU1");
		assertEquals("BU1", cut.getBusinessUnitId(product));
	}

}
