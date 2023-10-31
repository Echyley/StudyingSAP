/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;

import java.util.Objects;

import javax.validation.constraints.NotNull;

/**
 * Common exception for persistence hook problems.
 */
public class PersistenceHookException extends RuntimeException
{
	private static final long serialVersionUID = 7345092509032934459L;
	private final transient PersistenceContext persistenceContext;
	private final PersistenceHookType hookType;
	private final String hookName;

	/**
	 * Instantiates this exception.
	 *
	 * @param message a message explaining the problem
	 * @param context context for the request being processed when this exception was thrown
	 * @param name    name of the hook that failed processing.
	 */
	public PersistenceHookException(final String message, @NotNull final PersistenceContext context, final String name)
	{
		this(message, context, name, null);
	}

	/**
	 * Instantiates this exception.
	 *
	 * @param message a message explaining the problem
	 * @param context context for the request being processed when this exception was thrown
	 * @param name    name of the hook that failed processing.
	 * @param e       exception that caused this exception to be thrown.
	 */
	public PersistenceHookException(final String message, @NotNull final PersistenceContext context, final String name,
	                                final RuntimeException e)
	{
		super(message, e);
		persistenceContext = context;
		hookName = name;
		hookType = deriveType(context, name);
	}

	/**
	 * Retrieves context for the request that failed processing by the persistence hook.
	 *
	 * @return request context that was processed when this exception was thrown.
	 */
	public PersistenceContext getPersistenceContext()
	{
		return persistenceContext;
	}

	/**
	 * Informs about what persistence hook failed processing.
	 *
	 * @return type of the hook that failed processing
	 */
	public PersistenceHookType getPersistenceHookType()
	{
		return hookType;
	}

	/**
	 * Retrieves name of the hook that caused this exception.
	 *
	 * @return name of the hook that failed processing.
	 */
	public String getHookName()
	{
		return hookName;
	}

	private static PersistenceHookType deriveType(final PersistenceContext context, final String hookName)
	{
		if (context == null)
		{
			return null;
		}
		return Objects.equals(context.getPrePersistHook(), hookName) ? PersistenceHookType.PRE : PersistenceHookType.POST;
	}
}
