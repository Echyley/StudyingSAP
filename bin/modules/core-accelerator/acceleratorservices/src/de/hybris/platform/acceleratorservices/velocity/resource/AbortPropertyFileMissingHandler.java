/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource;

import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;


public class AbortPropertyFileMissingHandler implements PropertyFileMissingHandler
{
	@Override
	public void handle(final ScriptUrlContext ctx, final Resource resource)
	{
		throw new ResourceNotFoundException("Missing property file " + resource.getFilename());
	}
}
