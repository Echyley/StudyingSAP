/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.quote.impl;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.saprevenuecloudorder.facade.quote.SapSbQuoteFacade;
import de.hybris.platform.sap.saprevenuecloudorder.service.quote.SapSbQuoteApiClientService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestClientException;


/**
 *
 */
public class DefaultSapSbQuoteFacadeImpl implements SapSbQuoteFacade
{

	private static final Logger logger = Logger.getLogger(DefaultSapSbQuoteFacadeImpl.class.getName());

	private QuoteService quoteService;

	private UserService userService;
	
	private SapSbQuoteApiClientService sapSbQuoteApiClientService;


	@Override
	public byte[] downloadQuoteProposalDocument(final String quoteCode)
	{
		byte[] dataFromMedia = null;
		try
		{
			final QuoteModel currentQuote = getQuoteService().getCurrentQuoteForCode(quoteCode);

			final UserModel currentUser = userService.getCurrentUser();


			if (null != currentQuote && CollectionUtils.isNotEmpty(currentUser.getQuotes())
					&& currentUser.getQuotes().contains(currentQuote))
			{

				dataFromMedia = sapSbQuoteApiClientService.fetchProposalDocument(quoteCode);
			}

		}
		catch (final RestClientException exp)
		{
			logger.error("Unable to Retrieve the PDF from backend");

		}
		return dataFromMedia;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
	
	/**
	 * @return the quoteService
	 */
	public QuoteService getQuoteService()
	{
		return quoteService;
	}

	/**
	 * @param quoteService
	 *           the quoteService to set
	 */
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	public SapSbQuoteApiClientService getSapSbQuoteApiClientService() {
		return sapSbQuoteApiClientService;
	}

	public void setSapSbQuoteApiClientService(SapSbQuoteApiClientService sapSbQuoteApiClientService) {
		this.sapSbQuoteApiClientService = sapSbQuoteApiClientService;
	}

	


}
