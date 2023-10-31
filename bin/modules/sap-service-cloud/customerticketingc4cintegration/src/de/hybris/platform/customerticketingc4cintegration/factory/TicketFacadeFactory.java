/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.factory;

import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeImpl;
import de.hybris.platform.customerticketingc4cintegration.facade.C4CTicketFacadeMock;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;


/**
 * Factory decides which facade will be used mock/real.
 */
public class TicketFacadeFactory
{
	private C4CTicketFacadeImpl c4cTicketFacadeImpl;
	private C4CTicketFacadeMock c4cTicketFacadeMock;
	private DefaultCustomerTicketingFacade defaultCustomerTicketingFacade;

	/**
	 * @return chosen facade
	 */
	public TicketFacade getTicketFacade()
	{
		final boolean mockEnabled = Config.getBoolean("customerticketingc4cintegration.facade.mock", true);
		final String url = Config.getParameter("customerticketingc4cintegration.c4c-url");

		if (StringUtils.isBlank(url)) 
		{
		    return defaultCustomerTicketingFacade;
		} 
		else if (mockEnabled)
		{
		    return c4cTicketFacadeMock;
		}
		else
		{
		    return c4cTicketFacadeImpl;
		}
	}

	protected C4CTicketFacadeImpl getC4cTicketFacadeImpl()
	{
		return c4cTicketFacadeImpl;
	}

	@Resource
	public void setC4cTicketFacadeImpl(final C4CTicketFacadeImpl c4cTicketFacadeImpl)
	{
		this.c4cTicketFacadeImpl = c4cTicketFacadeImpl;
	}

	protected C4CTicketFacadeMock getC4cTicketFacadeMock()
	{
		return c4cTicketFacadeMock;
	}

	@Resource
	public void setC4cTicketFacadeMock(final C4CTicketFacadeMock c4cTicketFacadeMock)
	{
		this.c4cTicketFacadeMock = c4cTicketFacadeMock;
	}

	public DefaultCustomerTicketingFacade getDefaultCustomerTicketingFacade() {
	    return defaultCustomerTicketingFacade;
	}

	public void setDefaultCustomerTicketingFacade(DefaultCustomerTicketingFacade defaultCustomerTicketingFacade) {
	    this.defaultCustomerTicketingFacade = defaultCustomerTicketingFacade;
	}
}
