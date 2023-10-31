/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.warehousingfacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.ConsignmentEntryPopulator;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;


public class WarehousingConsignmentEntrySourceOrderEntryPopulator extends ConsignmentEntryPopulator
{
	@Override
	public void populate(final ConsignmentEntryModel source, final ConsignmentEntryData target)
	{
		if (source.getSourceOrderEntry() != null)
		{
			target.setOrderEntry(getOrderEntryConverter().convert(source.getSourceOrderEntry()));
		}
	}
}
