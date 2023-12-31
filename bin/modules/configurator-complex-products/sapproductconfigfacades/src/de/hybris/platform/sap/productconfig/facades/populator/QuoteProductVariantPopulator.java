/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

import org.apache.log4j.Logger;


/**
 * Takes care of populating product variant relevant attributes for quotes
 */
public class QuoteProductVariantPopulator extends AbstractOrderProductVariantPopulator implements Populator<QuoteModel, QuoteData>
{
	private static final Logger LOG = Logger.getLogger(QuoteProductVariantPopulator.class);

	@Override
	public void populate(final QuoteModel source, final QuoteData target)
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
