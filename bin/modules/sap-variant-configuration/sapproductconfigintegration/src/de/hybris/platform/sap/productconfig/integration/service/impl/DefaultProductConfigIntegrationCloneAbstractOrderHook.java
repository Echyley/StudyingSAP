/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderHook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;


public class DefaultProductConfigIntegrationCloneAbstractOrderHook implements CloneAbstractOrderHook
{
	private final int itemsStart;
	private final int itemsIncrement;

	public DefaultProductConfigIntegrationCloneAbstractOrderHook(final int itemsStart, final int itemsIncrement)
	{
		super();
		this.itemsStart = itemsStart;
		this.itemsIncrement = itemsIncrement;
	}


	@Override
	public void beforeClone(final AbstractOrderModel original, final Class abstractOrderClassResult)
	{
		// nothing to do here

	}

	@Override
	public <T extends AbstractOrderModel> void afterClone(final AbstractOrderModel original, final T clone,
			final Class abstractOrderClassResult)
	{
		// nothing to do here

	}

	@Override
	public void beforeCloneEntries(final AbstractOrderModel original)
	{
		// nothing to do here

	}

	@Override
	public <T extends AbstractOrderEntryModel> void afterCloneEntries(final AbstractOrderModel original,
			final List<T> clonedEntries)
	{
		// nothing to do here
	}

	@Override
	public void adjustEntryNumbers(final Map<AbstractOrderEntryModel, Integer> entryNumberMappings)
	{
		if (!checkCorrectEntryNumberShift(entryNumberMappings.values()))
		{
			updateEntryNumbers(entryNumberMappings);
		}
	}

	protected void updateEntryNumbers(final Map<AbstractOrderEntryModel, Integer> entryNumberMappings)
	{
		final List<Entry<AbstractOrderEntryModel, Integer>> mappingAsList = new ArrayList<>(entryNumberMappings.entrySet());
		mappingAsList.sort(Entry.comparingByValue());
		for (int j = 0; j < mappingAsList.size(); j++)
		{
			mappingAsList.get(j).setValue(Integer.valueOf(j * itemsIncrement + itemsStart));
		}
		mappingAsList.forEach(entry -> entryNumberMappings.put(entry.getKey(), entry.getValue()));
	}

	protected boolean checkCorrectEntryNumberShift(final Collection<Integer> values)
	{
		if (CollectionUtils.isNotEmpty(values))
		{
			final List<Integer> entryNumbers = new ArrayList<>(values);
			entryNumbers.sort(null);
			final int size = entryNumbers.size();
			return entryNumbers.get(size - 1) == (size - 1) * itemsIncrement + itemsStart;
		}
		return true;
	}
}
