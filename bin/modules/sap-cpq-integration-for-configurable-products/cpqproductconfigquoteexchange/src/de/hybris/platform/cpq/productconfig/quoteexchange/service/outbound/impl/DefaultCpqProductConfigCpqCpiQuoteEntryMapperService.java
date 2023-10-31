/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.quoteexchange.service.outbound.impl;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteEntryMapperService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;


/**
 * Maps the product configuration id, if existing, from the quote entry into the outbound integration object.<br>
 * The receiving external system is responsible for reading the configuration using the id, and for deep-copying before
 * modification to avoid conflicts.
 */
public class DefaultCpqProductConfigCpqCpiQuoteEntryMapperService  implements SapCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>
{
	private final ConfigurationServiceLayerHelper configServiceLayerHelper;

	/**
	 * Constructor. Expects all required dependencies as constructor arguments
	 *
	 * @param configServiceLayerhelper
	 *           config service layer helper         
	 */
	public DefaultCpqProductConfigCpqCpiQuoteEntryMapperService(final ConfigurationServiceLayerHelper configServiceLayerhelper)
	{
		super();
		this.configServiceLayerHelper = configServiceLayerhelper;
	}

	@Override
	public void map(final AbstractOrderEntryModel quoteEntry, final SAPCPQOutboundQuoteItemModel sapCpiQuoteItem)
	{
		final CloudCPQOrderEntryProductInfoModel productInfo = configServiceLayerHelper.getCPQInfo(quoteEntry);
		if (null != productInfo)
		{
			sapCpiQuoteItem.setConfigurationId(productInfo.getConfigurationId());
		}
	}
}
