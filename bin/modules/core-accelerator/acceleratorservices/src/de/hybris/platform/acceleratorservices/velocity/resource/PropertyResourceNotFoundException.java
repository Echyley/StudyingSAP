/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource;

import de.hybris.platform.acceleratorservices.velocity.VelocityExecutor;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;


/**
 * Specific exception, if thrown handled in the
 * {@link VelocityExecutor#execute(ScriptUrlContext, VelocityContext) and
 * threaten especially so no impex is not copied over to target folder
 */
public class PropertyResourceNotFoundException extends ResourceNotFoundException
{
	private final transient Resource resource;

	public PropertyResourceNotFoundException(final Resource resource, final String message)
	{
		super(message);
		this.resource = resource;
	}

	public Resource getResource()
	{
		return resource;
	}
}
