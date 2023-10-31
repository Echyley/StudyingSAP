/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.store;

public class ReadTemplateFileException extends RuntimeException
{
	public ReadTemplateFileException(final Throwable th)
	{
		super(th);
	}

	public ReadTemplateFileException(final String ex)
	{
		super(ex);
	}
}
