/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;

/**
 * This exception is thrown when a hook fails to execute during runtime
 */
public class PersistenceHookExecutionException extends PersistenceHookException
{
	private static final long serialVersionUID = 40576310898280932L;
	private static final String MESSAGE_TEMPLATE = "Exception occurred during the execution of hook [%s].";

	/**
	 * Instantiates this exception
	 *
	 * @param context  a context for the persistence hook execution
	 * @param hookName name of the hook that failed execution
	 * @param e        exception thrown by the hook.
	 */
	public PersistenceHookExecutionException(final PersistenceContext context, final String hookName, final RuntimeException e)
	{
		super(deriveMessage(hookName), context, hookName, e);
	}

	private static String deriveMessage(final String hookName)
	{
		return String.format(MESSAGE_TEMPLATE, hookName);
	}
}
