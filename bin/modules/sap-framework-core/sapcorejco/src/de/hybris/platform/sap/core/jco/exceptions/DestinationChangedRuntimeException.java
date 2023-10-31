/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.exceptions;

/**
 * Exception is thrown in case of administrator changes destination data in hmc/backoffice. <br>
 * Jco runtime invalidates all current used destinations. Subsequent access to an invalidated destination throws a
 * specific jco exception, which is transformed in a DestinationChangedRuntimeException.
 */
public class DestinationChangedRuntimeException extends BackendRuntimeException
{

	private static final long serialVersionUID = 8371796871878845045L;

	@SuppressWarnings("javadoc")
	public DestinationChangedRuntimeException()
	{
		super();
	}

	@SuppressWarnings("javadoc")
	public DestinationChangedRuntimeException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}

	@SuppressWarnings("javadoc")
	public DestinationChangedRuntimeException(final String msg)
	{
		super(msg);
	}

}
