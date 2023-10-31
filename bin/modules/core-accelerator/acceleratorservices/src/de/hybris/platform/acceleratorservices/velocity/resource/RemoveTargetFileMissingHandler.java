/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource;

import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import org.springframework.core.io.Resource;

public class RemoveTargetFileMissingHandler implements PropertyFileMissingHandler
{
	@Override
	public void handle(final ScriptUrlContext ctx, final Resource resource)
	{
		throw new PropertyResourceNotFoundException(resource, "Missing property file [" + resource.getFilename()
				+ "] , avoiding to generate target impex script [" + ctx.getTargetScriptEffectivePath() + "]");
	}
}
