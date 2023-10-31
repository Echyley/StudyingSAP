/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpqquoteexchange.service.outbound;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteEntryMapperService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * Maps the product configuration id, if existing, from the quote entry into the out bound integration object.<br>
 * The receiving external system is responsible for reading the configuration from CPS service using the id, and for
 * deep-copying before modification to avoid conflicts.
 */
public class DefaultProductConfigCpqCpiQuoteEntryMapperService  implements SapCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>
{
	@Override
	public void map(final AbstractOrderEntryModel quoteEntry, final SAPCPQOutboundQuoteItemModel sapCpiQuoteItem)
	{
		if (null != quoteEntry.getProductConfiguration())
		{
			sapCpiQuoteItem.setExternalConfigurationId(quoteEntry.getProductConfiguration().getConfigurationId());
		}
	}
}
