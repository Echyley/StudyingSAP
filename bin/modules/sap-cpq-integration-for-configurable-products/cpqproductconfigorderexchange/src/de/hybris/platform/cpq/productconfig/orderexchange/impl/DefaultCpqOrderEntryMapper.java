/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.orderexchange.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.orderexchange.CpqOrderEntryMapper;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;


/**
 * Default implementation of CpqOrderEntryMapper.
 */
public class DefaultCpqOrderEntryMapper implements CpqOrderEntryMapper
{
	private final ConfigurationService cpqService;
	private final ConfigurationServiceLayerHelper serviceLayerHelper;
	private final int subItemsSpacing;


	/**
	 *
	 * Constructor that gets the mandatory beans injected
	 *
	 * @param cpqService
	 *                                             configuration service
	 * @param serviceLayerHelper
	 *                                             service layer helper
	 * @param subItemsSpacing
	 *                                             spacing for subItems
	 */
	public DefaultCpqOrderEntryMapper(final ConfigurationService cpqService,
			final ConfigurationServiceLayerHelper serviceLayerHelper, final int subItemsSpacing)
	{
		this.cpqService = cpqService;
		this.serviceLayerHelper = serviceLayerHelper;
		this.subItemsSpacing = subItemsSpacing;
	}

	@Override
	public List<SAPCpiOutboundOrderItemModel> mapCPQLineItems(final AbstractOrderEntryModel entry,
			final SAPCpiOutboundOrderItemModel outboundItem)
	{
		final List<SAPCpiOutboundOrderItemModel> additionalLineItems = new ArrayList<>();
		getServiceLayerHelper().ensureBaseSiteSetAndExecuteConfigurationAction(entry.getOrder(),
				baseSiteModel -> retrieveCPQLineItems(entry, outboundItem, additionalLineItems));
		return additionalLineItems;
	}

	protected String incrementEntryNumber(final String rootEntryNumber, final int currentIncrement)
	{
		final int rootEntryNumberParsed = Integer.parseInt(rootEntryNumber);
		return String.valueOf(rootEntryNumberParsed + currentIncrement);
	}

	@Override
	public boolean isMapperApplicable(final AbstractOrderEntryModel entry)
	{
		return getServiceLayerHelper().getCPQInfo(entry) != null;
	}

	@Override
	public void retrieveCPQLineItems(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderItemModel outboundItem,
			final List<SAPCpiOutboundOrderItemModel> cpqLineItems)
	{
		final CloudCPQOrderEntryProductInfoModel cpqInfo = getServiceLayerHelper().getCPQInfo(entry);
		if (cpqInfo != null)
		{
			final ConfigurationSummaryData configurationSummary = getCpqService()
					.getConfigurationSummary(cpqInfo.getConfigurationId(), entry);
			mapSummaryLineItems(cpqLineItems, configurationSummary, outboundItem);
		}
	}

	protected void mapSummaryLineItems(final List<SAPCpiOutboundOrderItemModel> cpqLineItems,
			final ConfigurationSummaryData configurationSummary, final SAPCpiOutboundOrderItemModel outboundItem)
	{
		final List<ConfigurationSummaryLineItemData> summaryLineItems = configurationSummary.getConfiguration().getLineItems();
		Preconditions.checkNotNull(summaryLineItems);
		int entryNumberIncrement = subItemsSpacing;
		for (final ConfigurationSummaryLineItemData summaryLineItem : summaryLineItems)

		{
			final SAPCpiOutboundOrderItemModel outboundLineItem = mapSummaryLineItem(summaryLineItem, outboundItem,
					entryNumberIncrement);
			if (outboundLineItem != null)
			{
				cpqLineItems.add(outboundLineItem);
				entryNumberIncrement += subItemsSpacing;
			}
		}
	}

	protected SAPCpiOutboundOrderItemModel mapSummaryLineItem(final ConfigurationSummaryLineItemData summaryLineItem,
			final SAPCpiOutboundOrderItemModel outboundItem, final int currentIncrement)
	{
		if (!isMappingNeeded(summaryLineItem))
		{
			return null;
		}
		final SAPCpiOutboundOrderItemModel lineItem = new SAPCpiOutboundOrderItemModel();
		lineItem.setHigherLevelEntryNumber(outboundItem.getEntryNumber());
		lineItem.setEntryNumber(incrementEntryNumber(outboundItem.getEntryNumber(), currentIncrement));
		lineItem.setProductCode(summaryLineItem.getProductSystemId().toUpperCase(Locale.US));
		lineItem.setProductName(summaryLineItem.getDescription());
		lineItem.setQuantity(calculateLineItemQuantity(outboundItem, summaryLineItem));
		lineItem.setOrderId(outboundItem.getOrderId());
		return lineItem;
	}

	protected String calculateLineItemQuantity(final SAPCpiOutboundOrderItemModel outboundItem,
			final ConfigurationSummaryLineItemData summaryLineItem)
	{
		final BigDecimal rootItemQty = new BigDecimal(outboundItem.getQuantity());
		return rootItemQty.multiply(summaryLineItem.getQuantity()).toString();
	}

	protected boolean isMappingNeeded(final ConfigurationSummaryLineItemData summaryLineItem)
	{
		return StringUtils.isNotEmpty(summaryLineItem.getProductSystemId());
	}

	protected ConfigurationService getCpqService()
	{
		return cpqService;
	}

	protected ConfigurationServiceLayerHelper getServiceLayerHelper()
	{
		return serviceLayerHelper;
	}

}
