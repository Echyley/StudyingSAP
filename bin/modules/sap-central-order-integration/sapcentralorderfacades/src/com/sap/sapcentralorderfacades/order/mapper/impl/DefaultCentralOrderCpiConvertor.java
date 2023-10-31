/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper.impl;


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiorderexchangeoms.service.impl.SapCpiOmsOrderConversionService;

import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 * DefaultCentralOrderCpiConvertor
 */
public class DefaultCentralOrderCpiConvertor extends SapCpiOmsOrderConversionService
{
	private CoConfigurationService configurationService;

	/**
	 * @param orderModel
	 * @return SapcpiOrder
	 */
	@Override
	public SapCpiOrder convertOrderToSapCpiOrder(final OrderModel orderModel)
	{
		final SapCpiOrder sapCpiOmsOrder = super.convertOrderToSapCpiOrder(orderModel);
		if (getConfigurationService().isCoActiveFromBaseStore(orderModel))
		{
			//This ensures that the original order number generated at the
			//time of placing the order is also sent to the external system
			sapCpiOmsOrder.setOrderId(orderModel.getOriginalOrderNumber());

		}
		return sapCpiOmsOrder;

	}

	/**
	 * @param orderModel
	 * @return SapCpiConfig
	 */
	@Override
	protected SapCpiConfig mapOrderConfigInfo(final OrderModel orderModel)
	{
		if (getConfigurationService().isCoActiveFromBaseStore(orderModel))
		{
			return new SapCpiConfig();
		}
		else
		{
			return super.mapOrderConfigInfo(orderModel);
		}

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
