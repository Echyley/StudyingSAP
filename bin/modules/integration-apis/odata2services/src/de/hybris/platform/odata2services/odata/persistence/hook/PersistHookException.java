/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.hook;

import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

public abstract class PersistHookException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.BAD_REQUEST;

	protected PersistHookException(final String hookName, final String hookType, final String errorCode, final Throwable cause, final String integrationKey)
	{
		super(deriveMessage(hookName, hookType, cause), STATUS_CODE, errorCode, cause, integrationKey);
	}

	protected PersistHookException(final String message, final String errorCode, final String integrationKey)
	{
		super(message, STATUS_CODE, errorCode, integrationKey);
	}

	protected static String deriveMessage(final String hookName, final String hookType, final Throwable e)
	{
		final var messageBuilder = new StringBuilder();
		messageBuilder.append(String.format("Exception occurred during the execution of %s: [%s].", hookType, hookName));

		if (e != null)
		{
			messageBuilder.append(String.format(" Caused by %s", e.toString()));
		}
		return messageBuilder.toString();
	}
}
