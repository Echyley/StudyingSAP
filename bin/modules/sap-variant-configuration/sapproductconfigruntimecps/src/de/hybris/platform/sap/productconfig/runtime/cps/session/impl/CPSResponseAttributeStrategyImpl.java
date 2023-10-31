/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.session.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CPSResponseAttributeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.NewCookie;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.RawResponse;


/**
 * Default implementation of {@link CPSResponseAttributeStrategy}
 */
public class CPSResponseAttributeStrategyImpl implements CPSResponseAttributeStrategy
{
	private static final Logger LOG = Logger.getLogger(CPSResponseAttributeStrategyImpl.class);
	public static final int EXPECTED_NUMBER_OF_COOKIES = 2;
	private CPSCache cpsCache;

	@Override
	public void setCookies(final String configId, final List<NewCookie> cookies)
	{
		if (cookies == null)
		{
			throw new IllegalArgumentException("We expect cookies at this point");
		}
		getCpsCache().setCookies(configId, convertToStringArray(cookies));
	}


	@Override
	public List<String> getCookiesAsString(final String configId)
	{
		final List<String> cookies = getCpsCache().getCookies(configId);
		if (cookies == null)
		{
			LOG.info("No cookies available for configuration: " + configId);
		}
		//We expect 2 cookies thus we need to check the size of the list that we got
		//Cookies can be null in case we didn't find cookies for an existing config in the cache
		if (cookies != null && cookies.size() != EXPECTED_NUMBER_OF_COOKIES)
		{
			LOG.error("Exactly 2 cookies are expected, but there was " + cookies.size()
					+ ". The reason for this could be a changed CPS engine behaivior.");
		}
		return cookies;
	}

	/**
	 * @param cookies
	 * @return List of cookies as string, name and value separated by '='
	 */
	public List<String> convertToStringArray(final List<NewCookie> cookies)
	{
		final List<String> cookiesAsString = new ArrayList<>();
		cookies.stream().forEach(cookie -> cookiesAsString.add(cookie2String(cookie)));
		LOG.debug("Cookies to be cached: " + cookiesAsString);
		return cookiesAsString;
	}


	protected String cookie2String(final NewCookie cookie)
	{
		final StringBuilder cookieAsString = new StringBuilder(cookie.getName());
		return cookieAsString.append("=").append(cookie.getValue()).toString();
	}


	@Override
	public void removeCookies(final String configId)
	{
		getCpsCache().removeCookies(configId);

	}


	@Override
	public void setCookiesAsString(final String newConfigId, final List<String> cookieList)
	{
		getCpsCache().setCookies(newConfigId, cookieList);
	}

	@Override
	public void extractAndSaveCookies(final RawResponse response, final String configId)
	{
		final List<NewCookie> cookies = response.getSetCookies();
		if (CollectionUtils.isNotEmpty(cookies))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("extractAndSaveCookies, we will store new cookies for configuration: " + configId);
				LOG.debug("Cookies from response: " + cookies);
			}

			if (cookies.size() >= EXPECTED_NUMBER_OF_COOKIES)
			{
				setCookies(configId, cookies);
			}
			else
			{
				final String cookieName = cookies.get(0).getName();
				final String cookieValue = cookies.get(0).getValue();
				final List<String> cachedCookies = getCpsCache().getCookies(configId);
				final String cachedCookieValue = getCachedCoockieValue(cookieName, cachedCookies);
				if (!cookieValue.equals(cachedCookieValue))
				{
					removeCookies(configId);
				}
			}
		}
	}

	protected String getCachedCoockieValue(final String cookieName, final List<String> cachedCookies)
	{
		if (CollectionUtils.isNotEmpty(cachedCookies))
		{
			for (final String cachedCookie : cachedCookies)
			{
				final String[] cachedCookieAsArray = cachedCookie.split("=");
				if (cachedCookieAsArray[0] != null && cachedCookieAsArray[0].equals(cookieName))
				{
					return cachedCookieAsArray[1];
				}
			}
		}
		return null;
	}


	protected String retrieveETag(final RawResponse response, final String configId)
	{
		final Optional<String> eTag = response.eTag();
		if (eTag.isPresent())
		{
			final String eTagValue = eTag.get();
			LOG.debug("ETag: " + eTagValue);
			return eTagValue;
		}
		throw new IllegalStateException("No eTag returned after update/get for configuration: " + configId);
	}

	@Override
	public String retrieveETagAndSaveResponseAttributes(final RawResponse rawResponse, final String cfgId)
	{
		extractAndSaveCookies(rawResponse, cfgId);
		return retrieveETag(rawResponse, cfgId);
	}


	protected CPSCache getCpsCache()
	{
		return cpsCache;
	}

	@Required
	public void setCpsCache(final CPSCache cpsCache)
	{
		this.cpsCache = cpsCache;
	}
}
