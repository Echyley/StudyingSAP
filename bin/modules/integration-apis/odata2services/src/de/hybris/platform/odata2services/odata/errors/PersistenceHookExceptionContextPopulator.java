/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.errors;

import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookException;
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookNotFoundException;
import de.hybris.platform.inboundservices.persistence.hook.impl.PersistenceHookType;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;

import com.google.common.base.Preconditions;

/**
 * Context populator for persistence hook exceptions
 */
public class PersistenceHookExceptionContextPopulator implements ErrorContextPopulator
{
	private static final String HOOK_NOT_FOUND_MSG_TEMPLATE = "%s [%s] does not exist. Payload will not be persisted.";
	private static final String HOOK_EXECUTION_MSG_TEMPLATE = "Exception occurred during the execution of %s: [%s].";
	private static final String ERROR_CODE_PRE = "pre_persist_error";
	private static final String ERROR_CODE_POST = "post_persist_error";
	private static final String ERROR_CODE_NOT_FOUND = "hook_not_found";
	private static final String PRE_PERSIST_HOOK = "PrePersistHook";
	private static final String POST_PERSIST_HOOK = "PostPersistHook";

	@Override
	public void populate(@NotNull final ODataErrorContext context)
	{
		Preconditions.checkArgument(context != null, "ODataErrorContext cannot be null");

		final var contextException = context.getException();

		if (contextException instanceof PersistenceHookException)
		{
			final var ex = (PersistenceHookException) context.getException();
			context.setHttpStatus(HttpStatusCodes.BAD_REQUEST);
			context.setMessage(getMessage(ex));
			context.setInnerError(getIntegrationKey(ex));
			context.setErrorCode(selectErrorCode(ex));
		}
	}

	private String getMessage(final PersistenceHookException ex)
	{
		final String hookType = getHookType(ex);
		return ex instanceof PersistenceHookNotFoundException
				? String.format(HOOK_NOT_FOUND_MSG_TEMPLATE, hookType, ex.getHookName())
				: String.format(HOOK_EXECUTION_MSG_TEMPLATE, hookType, ex.getHookName());
	}

	private String getHookType(final PersistenceHookException ex)
	{
		if (ex.getPersistenceHookType() == PersistenceHookType.PRE)
		{
			return PRE_PERSIST_HOOK;
		}
		if (ex.getPersistenceHookType() == PersistenceHookType.POST)
		{
			return POST_PERSIST_HOOK;
		}
		return "PersistenceHook";
	}

	private String getIntegrationKey(final PersistenceHookException ex)
	{
		return ex.getPersistenceContext() != null
				? ex.getPersistenceContext().getIntegrationItem().getIntegrationKey() : null;
	}

	private String selectErrorCode(final PersistenceHookException ex) {
		if (ex instanceof PersistenceHookNotFoundException)
		{
			return ERROR_CODE_NOT_FOUND;
		}
		return ex.getPersistenceHookType() == PersistenceHookType.POST
				? ERROR_CODE_POST : ERROR_CODE_PRE;
	}

	@Override
	public Class<PersistenceHookException> getExceptionClass()
	{
		return PersistenceHookException.class;
	}
}
