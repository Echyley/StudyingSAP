/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.exception;

public class PageTemplateNotFoundException extends RuntimeException
{

	private static final long serialVersionUID = 5951489885466889543L;

	public PageTemplateNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public PageTemplateNotFoundException(final String message)
	{
		super(message);
	}

	public PageTemplateNotFoundException(final Throwable cause)
	{
		super(cause);
	}
}
