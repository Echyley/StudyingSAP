/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.store;

public class WriteScriptException extends RuntimeException
{
	public WriteScriptException(final Throwable th)
	{
		super(th);
	}

	public WriteScriptException(final String ex)
	{
		super(ex);
	}
}
