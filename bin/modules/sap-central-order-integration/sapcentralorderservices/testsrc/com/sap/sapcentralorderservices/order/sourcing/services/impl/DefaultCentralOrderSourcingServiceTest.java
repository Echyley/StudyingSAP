/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.order.sourcing.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.sapcentralorderservices.dao.CentralOrderConfigurationDao;
import com.sap.sapcentralorderservices.model.SAPCentralOrderConfigurationModel;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderSourcingServiceTest
{

	/**
	 *
	 */
	private static final String CO_WAREHOUSE = "co_warehouse";


	private final DefaultCentralOrderSourcingService defaultCentralOrderSourcingService = new DefaultCentralOrderSourcingService();


	private final OrderModel orderModel = mock(OrderModel.class);


	private final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);


	private final SAPConfigurationModel configurationModel = mock(SAPConfigurationModel.class);


	private final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);


	private final CentralOrderConfigurationDao centralOrderConfigurationDao = mock(CentralOrderConfigurationDao.class);


	private final SAPCentralOrderConfigurationModel centralOrderConfigurationModel = mock(SAPCentralOrderConfigurationModel.class);


	private final WarehouseModel warehouseModel = mock(WarehouseModel.class);


	private final CoConfigurationService configurationService = mock(CoConfigurationService.class);

	private List<AbstractOrderEntryModel> abstractOrderEntryModels;

	private List<WarehouseModel> warehouseModels;


	@Before
	public void setUp()
	{
		defaultCentralOrderSourcingService.setCentralOrderConfigurationDao(centralOrderConfigurationDao);
		defaultCentralOrderSourcingService.setConfigurationService(configurationService);



		abstractOrderEntryModels = new ArrayList<AbstractOrderEntryModel>();
		abstractOrderEntryModels.add(abstractOrderEntryModel);

		warehouseModels = new ArrayList<WarehouseModel>();
		warehouseModels.add(warehouseModel);

		Mockito.lenient().when(orderModel.getStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(configurationModel);
		Mockito.lenient().when(configurationModel.getSapco_active()).thenReturn(true);

		Mockito.lenient().when(orderModel.getEntries()).thenReturn(abstractOrderEntryModels);
		Mockito.lenient().when(abstractOrderEntryModel.getQuantity()).thenReturn(1l);

		Mockito.lenient().when(centralOrderConfigurationDao.findSapCentralOrderConfiguration())
				.thenReturn(centralOrderConfigurationModel);
		Mockito.lenient().when(centralOrderConfigurationModel.getWarehouses()).thenReturn(warehouseModels);

		Mockito.lenient().when(warehouseModel.getCode()).thenReturn(CO_WAREHOUSE);
		Mockito.lenient().when(configurationService.isCoActiveFromBaseStore(orderModel)).thenReturn(true);
	}

	@Test
	public void sourceOrderTest()
	{
		//test
		final SourcingResults sourcingResults = defaultCentralOrderSourcingService.sourceOrder(orderModel);

		//assertions
		assertTrue(sourcingResults.getResults().size() == 1);
		for (final SourcingResult sourcingResult : sourcingResults.getResults())
		{
			assertEquals(sourcingResult.getWarehouse().getCode(), CO_WAREHOUSE);
		}
	}

	@Test
	public void sourceOrderTest_WarehouseNotPresent()
	{
		//setUp
		warehouseModels.clear();

		//test
		final SourcingResults sourcingResults = defaultCentralOrderSourcingService.sourceOrder(orderModel);

		//assertions
		assertTrue(sourcingResults.getResults().size() == 1);
		for (final SourcingResult sourcingResult : sourcingResults.getResults())
		{
			assertEquals(sourcingResult.getWarehouse(), null);
		}
	}

}
