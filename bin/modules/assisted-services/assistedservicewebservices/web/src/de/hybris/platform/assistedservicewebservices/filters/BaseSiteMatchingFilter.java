/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter that resolves base site id from the requested url and activates it.
 */
public class BaseSiteMatchingFilter extends OncePerRequestFilter
{
	private static final String BASE_SITE_PARAM = "baseSite";
	private BaseSiteService baseSiteService;

	private List<String> includedURLs;

	private String regexp;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (isFilterRequired(request.getServletPath().toLowerCase(Locale.ENGLISH)))
		{
			final String baseSite = getBaseSiteFromRequest(request);
			if (StringUtils.isBlank(baseSite))
			{
				throw new RequestParameterException("Request must have a baseSite", RequestParameterException.MISSING, BASE_SITE_PARAM);
			}
			else
			{
				setCurrentBaseSite(baseSite);
			}
		}
		filterChain.doFilter(request, response);
	}

	private boolean isFilterRequired(final String url)
	{
		return url != null && includedURLs != null && includedURLs.stream()
				.anyMatch(includedURL -> includedURL != null && url.matches(includedURL));
	}

	// to avoid try to get baseSite from path, we have to judge it
	protected String getBaseSiteFromRequest(final HttpServletRequest request)
	{
		String baseSiteId = request.getParameter(BASE_SITE_PARAM);
		if(StringUtils.isBlank(baseSiteId)) {
			baseSiteId = getBaseSiteValue(request, regexp);
		}
		return baseSiteId;
	}

	protected String getBaseSiteValue(final HttpServletRequest request, final String regexp)
	{
		final Matcher matcher = getMatcher(request, regexp);
		if (matcher.find())
		{
			return matcher.group().split("/")[1];
		}
		return null;
	}

	protected String getPath(final HttpServletRequest request)
	{
		return StringUtils.defaultString(request.getServletPath());
	}

	protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
	{
		final Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		final String path = getPath(request);
		return pattern.matcher(path);
	}

	private void setCurrentBaseSite(final String baseSite) throws ServletException
	{
		final BaseSiteModel requestedBaseSite = getBaseSiteService().getBaseSiteForUID(baseSite);
		if (requestedBaseSite == null)
		{
			throw new RequestParameterException("The requested BaseSite: " + baseSite + " doesn't exist", RequestParameterException.INVALID, BASE_SITE_PARAM);
		}
		if (isDifferentThanCurrentSite(requestedBaseSite))
		{
			getBaseSiteService().setCurrentBaseSite(requestedBaseSite, true);
		}
	}

	private boolean isDifferentThanCurrentSite(final BaseSiteModel requestedBaseSite)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		return !requestedBaseSite.equals(currentBaseSite);
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected List<String> getIncludedURLs()
	{
		return includedURLs;
	}

	public void setIncludedURLs(List<String> urls)
	{
		this.includedURLs = urls;
	}

	protected String getRegexp()
	{
		return regexp;
	}

	public void setRegexp(final String regexp)
	{
		this.regexp = regexp;
	}

	/**
	 * @deprecated (since 2211.7, we use included list instead of excluded)
	 */
	@Deprecated(since = "2211.7", forRemoval = true)
	protected List<String> getExcludedUrls()
	{
		return Collections.emptyList();
	}

	/**
	 * @deprecated (since 2211.7, we use included list instead of excluded)
	 */
	@Deprecated(since = "2211.7", forRemoval = true)
	public void setExcludedUrls(final List<String> excludedUrls)
	{
		// Do nothing because deprecated case
	}

}
