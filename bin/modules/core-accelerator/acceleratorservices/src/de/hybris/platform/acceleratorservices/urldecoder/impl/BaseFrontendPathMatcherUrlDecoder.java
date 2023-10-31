/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.urldecoder.impl;

import de.hybris.platform.acceleratorservices.urldecoder.FrontendUrlDecoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.PathMatcher;


/**
 * Match url pattern using a path Matcher pattern.
 * 
 * @param <T>
 */
public abstract class BaseFrontendPathMatcherUrlDecoder<T> implements FrontendUrlDecoder<T>
{

	private static final Logger LOG = LoggerFactory.getLogger(BaseFrontendPathMatcherUrlDecoder.class);

	private PathMatcher pathMatcher;
	private String pathMatchPattern;


	@Override
	public T decode(final String urlIn)
	{
		final URL url;
		try
		{
			url = new URL(urlIn);
		}
		catch (final MalformedURLException e)
		{
			LOG.warn("unable to parse url [{}] as it was malformed", urlIn);
			return null;
		}
		final int paramsIndex = StringUtils.indexOfAny(url.getPath(), ";?&");
		final String cleanedUrl = paramsIndex > -1 ? url.getPath().substring(0, paramsIndex) : url.getPath();
		if (getPathMatcher().match(getPathMatchPattern(), cleanedUrl))
		{
			final Map<String, String> pathParams = getPathMatcher().extractUriTemplateVariables(getPathMatchPattern(), cleanedUrl);

			if (pathParams.size() > 1)
			{
				LOG.warn("unable to extract id from path {} and pattern {}" , url.getPath(), getPathMatchPattern());
				return null;
			}
			return translateId(pathParams.get(pathParams.keySet().iterator().next()));
		}
		return null;
	}

	protected abstract T translateId(String id);


	/**
	 * @param pathMatcher
	 *           the pathMatcher to set
	 */
	public void setPathMatcher(final PathMatcher pathMatcher)
	{
		this.pathMatcher = pathMatcher;
	}

	/**
	 * @return the pathMatcher
	 */
	public PathMatcher getPathMatcher()
	{
		return pathMatcher;
	}

	/**
	 * @param pathMatchPattern
	 *           the pathMatchPattern to set
	 */
	public void setPathMatchPattern(final String pathMatchPattern)
	{
		this.pathMatchPattern = pathMatchPattern;
	}

	/**
	 * @return the pathMatchPattern
	 */
	public String getPathMatchPattern()
	{
		return pathMatchPattern;
	}
}
