/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource;

import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class IgnorePropertyFileMissingHandler implements PropertyFileMissingHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(IgnorePropertyFileMissingHandler.class);

	@Override
	public void handle(final ScriptUrlContext ctx, final Resource resource)
	{
		if (LOG.isDebugEnabled())
		{
			try
			{
				LOG.debug("Missing property file [{}]", resource.getFile().getAbsolutePath());
			}
			catch (final IOException e)
			{
				LOG.debug("Can not access property file [{}]", resource.getFilename());
			}
		}
	}
}
