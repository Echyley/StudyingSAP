/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.orderexchange.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cpq.productconfig.orderexchange.CpqOrderEntryMapper;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service for mapping CPQ items in an order
 */
public class CpqItemHierarchyMapperService implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>
{
	private final CpqOrderEntryMapper orderEntryMapper;
	private final int itemsSpacing;
	private final int subItemsSpacing;

	/**
	 * Constructor for dependency injection
	 *
	 * @param orderEntryMapper
	 *           mapper for individual entries
	 * @param itemsSpacing
	 *           spacing between items
	 * @param subItemsSpacing
	 *           spacing between sub items
	 */
	public CpqItemHierarchyMapperService(final CpqOrderEntryMapper orderEntryMapper, final int itemsSpacing,
			final int subItemsSpacing)
	{
		this.orderEntryMapper = orderEntryMapper;
		this.itemsSpacing = itemsSpacing;
		this.subItemsSpacing = subItemsSpacing;
	}


	@Override
	public void map(final OrderModel source, final SAPCpiOutboundOrderModel target)
	{
		final List<AbstractOrderEntryModel> orderEntries = source.getEntries();

		if (isCPQMappingNeeded(orderEntries))
		{

			for (int i = 0; i < orderEntries.size(); i++)
			{
				final AbstractOrderEntryModel entry = orderEntries.get(i);
				final SAPCpiOutboundOrderItemModel outboundItem = findOutboundItem(target, entry);
				if (getOrderEntryMapper().isMapperApplicable(entry))
				{
					final List<SAPCpiOutboundOrderItemModel> additionalLineItems = getOrderEntryMapper().mapCPQLineItems(entry,
							outboundItem);
					checkSpacing(additionalLineItems.size());
					target.getSapCpiOutboundOrderItems().addAll(additionalLineItems);
				}
			}
			sortByEntryNumber(target);
		}
	}


	protected void sortByEntryNumber(final SAPCpiOutboundOrderModel target)
	{
		final List<SAPCpiOutboundOrderItemModel> sortedList = target.getSapCpiOutboundOrderItems().stream()
				.sorted(getItemComparator()).collect(Collectors.toList());
		final LinkedHashSet<SAPCpiOutboundOrderItemModel> sortedItems = new LinkedHashSet<>(sortedList);
		target.setSapCpiOutboundOrderItems(sortedItems);
	}

	protected Comparator<SAPCpiOutboundOrderItemModel> getItemComparator()
	{
		return (item1, item2) -> Integer.parseInt(item1.getEntryNumber()) - Integer.parseInt(item2.getEntryNumber());
	}

	protected boolean isCPQMappingNeeded(final List<AbstractOrderEntryModel> orderEntries)
	{
		return orderEntries.stream().anyMatch(entry -> getOrderEntryMapper().isMapperApplicable(entry));
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
			throw new IllegalStateException("No matching outboundItem found for entryNumber " + entry.getEntryNumber()
					+ ". Expected entryNumber in outboundItem: " + outboundItemEntryNumber);
		}
		return outboundItemMatch;
	}


	protected void checkSpacing(final int numberOfAdditionalItems)
	{
		if (numberOfAdditionalItems * subItemsSpacing >= itemsSpacing)
		{
			throw new IllegalStateException("Number of additional line items (" + numberOfAdditionalItems
					+ ") exceeds the item spacing (" + itemsSpacing + ") in combination with the subItemsSpacing (" + subItemsSpacing
					+ "). Consider increasing cpqproductconfigservices.items.increment or if possible decreasing cpqproductconfigorderexchange.spacing.subitems.");
		}
	}

	protected CpqOrderEntryMapper getOrderEntryMapper()
	{
		return orderEntryMapper;
	}

}
