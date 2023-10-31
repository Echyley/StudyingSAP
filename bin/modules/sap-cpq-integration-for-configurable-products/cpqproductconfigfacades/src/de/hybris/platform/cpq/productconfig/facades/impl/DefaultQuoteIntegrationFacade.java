/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cpq.productconfig.facades.QuoteIntegrationFacade;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;


/**
 * Default implementation of {@link QuoteIntegrationFacade}
 */
public class DefaultQuoteIntegrationFacade implements QuoteIntegrationFacade
{

	private final QuoteService quoteService;
	private final AbstractOrderIntegrationService abstractOrderIntegrationService;

	/**
	 * Default constructor for dependency injection.
	 *
	 * @param abstractOrderIntegrationService
	 *           for retrieving the configuration id from the abstract order entry
	 * @param quoteService
	 *           to read the quoteModel
	 */
	public DefaultQuoteIntegrationFacade(final AbstractOrderIntegrationService abstractOrderIntegrationService,
			final QuoteService quoteService)
	{
		super();
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
		this.quoteService = quoteService;
	}


	@Override
	public String getConfigurationId(final String quoteCode, final int entryNumber)
	{
		final AbstractOrderEntryModel quoteEntryModel = findQuoteEntry(quoteCode, entryNumber);
		return getAbstractOrderIntegrationService().getConfigIdForAbstractOrderEntry(quoteEntryModel);
	}


	@Override
	public String getProductCode(final String quoteCode, final int entryNumber)
	{
		final AbstractOrderEntryModel quoteEntry = findQuoteEntry(quoteCode, entryNumber);
		return quoteEntry.getProduct().getCode();
	}

	protected AbstractOrderEntryModel findQuoteEntry(final String quoteCode, final int entryNumber)
	{
		final QuoteModel quoteModel = getQuoteService().getCurrentQuoteForCode(quoteCode);
		return quoteModel.getEntries().stream()
				.filter(entry -> entryNumber == entry.getEntryNumber().intValue()).findFirst()
				.orElseThrow(() -> new ModelNotFoundException(
						String.format("QuoteEntry with entry number '%d' not found for quote with code '%s'", entryNumber, quoteCode)));
	}


	protected QuoteService getQuoteService()
	{
		return quoteService;
	}


	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return abstractOrderIntegrationService;
	}

}
