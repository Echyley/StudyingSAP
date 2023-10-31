/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource;

import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import org.springframework.core.io.Resource;


public interface PropertyFileMissingHandler
{
	void handle(ScriptUrlContext ctx, Resource resource);
}
