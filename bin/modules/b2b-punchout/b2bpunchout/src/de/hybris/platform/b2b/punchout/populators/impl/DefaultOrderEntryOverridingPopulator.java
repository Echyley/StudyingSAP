/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;

import org.cxml.ItemDetail;
import org.cxml.ItemOut;
import org.cxml.Tax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This populator overrides certain properties on the given {@link AbstractOrderEntryModel}.
 */
public class DefaultOrderEntryOverridingPopulator implements Populator<ItemOut, AbstractOrderEntryModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultOrderEntryOverridingPopulator.class);

	private Populator<Tax, Collection<TaxValue>> taxValuePopulator;

	@Override
	public void populate(final ItemOut itemOut, final AbstractOrderEntryModel orderEntryModel)
	{
		final ItemDetail itemDetail = (ItemDetail) itemOut.getItemDetailOrBlanketItemDetail().iterator().next();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Overriding price and tax details for entry number: {} with base price: {} and total price: {}",
					orderEntryModel.getEntryNumber(), orderEntryModel.getBasePrice(), orderEntryModel.getTotalPrice());
		}

		final Double basePrice = Double.valueOf(itemDetail.getUnitPrice().getMoney().getvalue());
		final Double quantity = Double.valueOf(itemOut.getQuantity());
		orderEntryModel.setBasePrice(basePrice);
		orderEntryModel.setTotalPrice(basePrice * quantity);
		getTaxValuePopulator().populate(itemOut.getTax(), orderEntryModel.getTaxValues());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Overriding complete for entry number: {} with new base price: {} and new total price: {}",
					orderEntryModel.getEntryNumber(), orderEntryModel.getBasePrice(), orderEntryModel.getTotalPrice());
		}
	}

	protected Populator<Tax, Collection<TaxValue>> getTaxValuePopulator()
	{
		return taxValuePopulator;
	}

	public void setTaxValuePopulator(final Populator<Tax, Collection<TaxValue>> taxValuePopulator)
	{
		this.taxValuePopulator = taxValuePopulator;
	}
}
