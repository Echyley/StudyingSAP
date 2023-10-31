/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;

/**
 * Indicates a general problem while performing data persistence.
 */
public class PersistenceFailedException extends RuntimeException
{
	private static final long serialVersionUID = 7892407885650341452L;
	private static final String MESSAGE_TEMPLATE = "Failed to persist item model of [%s] type";

	private final transient PersistenceContext persistenceContext;

	/**
	 * Instantiates this exception
	 * @param context context for the persistence failure
	 * @param e original exception that was thrown during the persistence operation.
	 */
	public PersistenceFailedException(final PersistenceContext context, final Throwable e)
	{
		super(createMessage(context), e);
		persistenceContext = context;
	}

	/**
	 * Retrieves context for the failed persistence operation.
	 *
	 * @return context for the failed persistence operation
	 */
	public PersistenceContext getPersistenceContext()
	{
		return persistenceContext;
	}

	private static String createMessage(final PersistenceContext context)
	{
		final String type = context != null
				? context.getIntegrationItem().getItemType().getItemCode()
				: null;
		return String.format(MESSAGE_TEMPLATE, type);
	}
}
