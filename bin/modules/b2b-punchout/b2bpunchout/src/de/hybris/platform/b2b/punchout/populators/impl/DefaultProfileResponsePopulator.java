/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.util.CXmlDateUtil;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.time.ZonedDateTime;
import java.util.Map;

import org.cxml.CXML;
import org.cxml.ProfileResponse;
import org.cxml.Transaction;
import org.cxml.URL;


/**
 * Default implementation of a {@link Populator} that fills in all {@link ProfileResponse} details.
 */
public class DefaultProfileResponsePopulator implements Populator<CXML, ProfileResponse>
{
	private CXmlDateUtil cXmlDateUtil;
	private SessionService sessionService;

	private ZonedDateTime lastRefreshTime = ZonedDateTime.now();

	/**
	 *
	 */
	@Override
	public void populate(final CXML source, final ProfileResponse target) throws ConversionException
	{
		target.setEffectiveDate(getcXmlDateUtil().formatDate(ZonedDateTime.now()));
		target.setLastRefresh(getcXmlDateUtil().formatDate(getLastRefreshTime()));
		final var requestProvidedURLPaths = (Map<String, String>) sessionService.getAttribute(
				PunchOutUtils.SUPPORTED_TRANSACTION_URL_PATHS);
		if (requestProvidedURLPaths != null)
		{
			for (final Map.Entry<String, String> entry : requestProvidedURLPaths.entrySet())
			{
				final Transaction transaction = new Transaction();
				transaction.setRequestName(entry.getKey());
				final URL url = new URL();
				url.setvalue(entry.getValue());
				transaction.setURL(url);
				target.getTransaction().add(transaction);
			}
		}
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CXmlDateUtil getcXmlDateUtil()
	{
		return cXmlDateUtil;
	}

	public void setcXmlDateUtil(final CXmlDateUtil cXmlDateUtil)
	{
		this.cXmlDateUtil = cXmlDateUtil;
	}

	public ZonedDateTime getLastRefreshTime()
	{
		return lastRefreshTime;
	}

	public void setLastRefreshTime(final ZonedDateTime lastRefreshTime)
	{
		this.lastRefreshTime = lastRefreshTime;
	}

}
