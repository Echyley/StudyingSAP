/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;

/**
 * This exception is thrown when a hook with name matching {@code hookName} is not found
 */
public class PersistenceHookNotFoundException extends PersistenceHookException
{
	private static final long serialVersionUID = -3710786452052408377L;
	private static final String MESSAGE_TEMPLATE = "[%s] was not found. Payload will not be persisted.";

	/**
	 * Instantiates this exception
	 *
	 * @param context  a context for the hook execution
	 * @param hookName name of the hook that is requested by the context, but which does not exist in the system.
	 */
	public PersistenceHookNotFoundException(final PersistenceContext context, final String hookName)
	{
		super(deriveMessage(hookName), context, hookName);
	}

	private static String deriveMessage(final String hook)
	{
		return String.format(MESSAGE_TEMPLATE, hook);
	}
}
