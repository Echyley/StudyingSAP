/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import org.apache.log4j.Logger;


/**
 * Takes care of populating product variant relevant attributes for cart
 */
public class CartProductVariantPopulator extends AbstractOrderProductVariantPopulator implements Populator<CartModel, CartData>
{


	private static final Logger LOG = Logger.getLogger(CartProductVariantPopulator.class);


	@Override
	public void populate(final CartModel source, final CartData target)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}

		for (final AbstractOrderEntryModel entry : source.getEntries())
		{
			populateAbstractOrderData(entry, target.getEntries());
		}

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("CPQ Variant Populating for cart took " + duration + " ms");
		}
	}
}
