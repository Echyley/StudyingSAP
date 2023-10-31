/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;


/**
 * Ant UTF-8 based reader util for loading properties.
 */
public final class LocalizedPropertyUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(LocalizedPropertyUtil.class);

	private LocalizedPropertyUtil()
	{
		// not instantiable
	}

	public static Properties loadProperties(final Resource resource) throws IOException
	{
		final Properties props = new Properties();
		fillProperties(props, resource);
		return props;
	}

	/**
	 * Fill the given properties from the given resource.
	 *
	 * @param props
	 *           the Properties instance to fill
	 * @param resource
	 *           the resource to load from
	 * @throws IOException
	 *            if loading failed
	 */
	public static void fillProperties(final Properties props, final Resource resource) throws IOException
	{
		final Reader is = new InputStreamReader(new BOMInputStream(resource.getInputStream()), StandardCharsets.UTF_8);
		try
		{
			props.load(is);
		}
		catch (final IOException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Resource not found.", e);
			}
			throw new ResourceNotFoundException(e);
		}
		catch (final IllegalArgumentException ile)//case when not supported encoding given
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("No BOMs specified.", ile);
			}
			throw new ParseErrorException(ile.getMessage());
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}
}
