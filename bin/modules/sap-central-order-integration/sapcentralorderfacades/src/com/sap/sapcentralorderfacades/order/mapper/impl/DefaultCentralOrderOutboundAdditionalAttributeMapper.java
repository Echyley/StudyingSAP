/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import com.sap.sapcentralorderfacades.order.mapper.SapCpiOrderOutboundAdditionalAttributeMapper;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 * DefaultCentralOrderOutboundAdditionalAttributeMapper
 */
public class DefaultCentralOrderOutboundAdditionalAttributeMapper implements SapCpiOrderOutboundAdditionalAttributeMapper
{
	private CoConfigurationService configurationService;


	/**
	 * @param orderModel
	 * @param sapCpiOutboundOrderModel
	 */
	@Override
	public void mapAdditionalAttributes(final OrderModel orderModel, final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{
		if (getConfigurationService().isCoActiveFromBaseStore(orderModel))
		{
			sapCpiOutboundOrderModel.getSapCpiConfig()
					.setCentralOrderSourceSystemId(orderModel.getStore().getSAPConfiguration().getSapco_sourceSystemId());
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
