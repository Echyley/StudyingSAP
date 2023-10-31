/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.order.sourcing.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.sourcing.impl.DefaultSourcingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.sapcentralorderservices.dao.CentralOrderConfigurationDao;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 * DefaultCentralOrderSourcingService
 */
public class DefaultCentralOrderSourcingService extends DefaultSourcingService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCentralOrderSourcingService.class);

	private CentralOrderConfigurationDao centralOrderConfigurationDao;
	private CoConfigurationService configurationService;

	@Override
	public SourcingResults sourceOrder(final AbstractOrderModel abstractOrderModel)
	{
		final OrderModel order = (OrderModel) abstractOrderModel;

		if (getConfigurationService().isCoActiveFromBaseStore(order))
		{
			LOGGER.info("Central Order in use");
			setOriginalOrderNumber(order);
			final SourcingResults results = new SourcingResults();
			final Set<SourcingResult> sourcingResults = new HashSet();
			constructSourcingResult(order, sourcingResults);

			results.setResults(sourcingResults);
			results.setComplete(true);

			return results;
		}
		else
		{
			LOGGER.info("Central Order not in use");
			return super.sourceOrder(abstractOrderModel);
		}

	}

	protected void constructSourcingResult(final OrderModel order, final Set<SourcingResult> sourcingResults)
	{
		final List<AbstractOrderEntryModel> abstractOrderEntryModels = new ArrayList();
		final SourcingResult result = new SourcingResult();
		final Map<AbstractOrderEntryModel, Long> allocation = new HashMap();

		for (final AbstractOrderEntryModel abstractOrderEntryModel : order.getEntries())
		{
			allocation.put(abstractOrderEntryModel, abstractOrderEntryModel.getQuantity());
			abstractOrderEntryModels.add(abstractOrderEntryModel);
		}
		result.setAllocation(allocation);
		final Optional<WarehouseModel> warehouse = getCentralOrderConfigurationDao().findSapCentralOrderConfiguration()
				.getWarehouses().stream().findFirst();

		if (warehouse.isPresent())
		{
			result.setWarehouse(warehouse.get());
			LOGGER.info("Warehouse added in sourcing result : '{}' ", result.getWarehouse().getCode());
		}
		else
		{
			LOGGER.warn("No warehouse found in SAPCentralOrderConfigurationModel");
		}

		sourcingResults.add(result);
	}

	protected void setOriginalOrderNumber(final OrderModel order)
	{
		order.setOriginalOrderNumber(order.getCode());
	}

	public CentralOrderConfigurationDao getCentralOrderConfigurationDao()
	{
		return centralOrderConfigurationDao;
	}

	public void setCentralOrderConfigurationDao(final CentralOrderConfigurationDao centralOrderConfigurationDao)
	{
		this.centralOrderConfigurationDao = centralOrderConfigurationDao;
	}

	public CoConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final CoConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


}

