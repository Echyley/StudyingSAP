/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiquoteexchange.service.outbound;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteEntryMapperService;


/**
 * Maps the product configuration id, if existing, from the quote entry into the out bound integration object.<br>
 * The receiving external system is responsible for reading the configuration from CPS service using the id, and for
 * deep-copying before modification to avoid conflicts.
 */
public class ProductConfigSCPIQuoteEntryMapperImpl
		implements SapCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCpiOutboundQuoteItemModel>
{

	@Override
	public void map(final AbstractOrderEntryModel source, final SAPCpiOutboundQuoteItemModel target)
	{
		if (null != source.getProductConfiguration())
		{
			target.setConfigId(source.getProductConfiguration().getConfigurationId());
		}
	}
}
