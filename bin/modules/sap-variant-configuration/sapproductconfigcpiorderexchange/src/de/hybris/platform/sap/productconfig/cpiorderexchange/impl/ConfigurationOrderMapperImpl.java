/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ConfigurationOrderEntryMapper;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;

import java.util.HashSet;
import java.util.List;


public class ConfigurationOrderMapperImpl implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>
{

	private final ConfigurationOrderEntryMapper orderEntryMapper;

	public ConfigurationOrderMapperImpl(final ConfigurationOrderEntryMapper orderEntryMapper)
	{
		this.orderEntryMapper = orderEntryMapper;
	}

	@Override
	public void map(final OrderModel source, final SAPCpiOutboundOrderModel target)
	{
		final List<AbstractOrderEntryModel> orderEntries = source.getEntries();

		if (isConfigurationMappingNeeded(orderEntries))
		{
			for (final AbstractOrderEntryModel entry : orderEntries)
			{
				if (orderEntryMapper.isMapperApplicable(entry))
				{
					initProductConfigSets(target);
					final SAPCpiOutboundOrderItemModel outboundItem = findOutboundItem(target, entry);
					orderEntryMapper.mapConfiguration(entry, target, outboundItem.getEntryNumber());
				}
			}
		}
	}

	protected boolean isConfigurationMappingNeeded(final List<AbstractOrderEntryModel> orderEntries)
	{
		return orderEntries.stream().anyMatch(entry -> orderEntryMapper.isMapperApplicable(entry));
	}

	protected void initProductConfigSets(final SAPCpiOutboundOrderModel target)
	{
		if (target.getProductConfigHeaders() == null)
		{
			target.setProductConfigHeaders(new HashSet<>());
			target.setProductConfigInstances(new HashSet<>());
			target.setProductConfigHierarchies(new HashSet<>());
			target.setProductConfigValues(new HashSet<>());
			target.setProductConfigConditions(new HashSet<>());
		}
	}

	protected SAPCpiOutboundOrderItemModel findOutboundItem(final SAPCpiOutboundOrderModel target,
			final AbstractOrderEntryModel entry)
	{
		final String outboundItemEntryNumber = entry.getEntryNumber().toString();
		final SAPCpiOutboundOrderItemModel outboundItemMatch = target.getSapCpiOutboundOrderItems().stream()
				.filter(e -> e.getEntryNumber() != null && e.getEntryNumber().equals(outboundItemEntryNumber)).findFirst()
				.orElse(null);
		if (outboundItemMatch == null)
		{
			final StringBuilder availableEntryNumbers = new StringBuilder();
			target.getSapCpiOutboundOrderItems().forEach(e -> availableEntryNumbers.append(e.getEntryNumber()).append(","));

			throw new IllegalStateException("No matching outboundItem found for entryNumber " + entry.getEntryNumber()
					+ ". Available numbers: " + availableEntryNumbers.toString());
		}
		return outboundItemMatch;
	}

}
