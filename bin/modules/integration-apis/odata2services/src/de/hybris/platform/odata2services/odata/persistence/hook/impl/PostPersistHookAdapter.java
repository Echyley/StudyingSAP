/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Adapter class to use old post persist hooks in the new persistence service
 *
 * @deprecated used temporary while old hooks are beeing reworked to use the new
 * {@link de.hybris.platform.inboundservices.persistence.hook.PostPersistHook} interface
 */
@Deprecated(since = "2205", forRemoval = true)
public class PostPersistHookAdapter implements de.hybris.platform.inboundservices.persistence.hook.PostPersistHook
{
	private final PostPersistHook oldHook;

	/**
	 * Instantiates this adapter
	 *
	 * @param hook a legacy hook implementation to adapt to the new hook interface
	 */
	public PostPersistHookAdapter(@NotNull final PostPersistHook hook)
	{
		Preconditions.checkArgument(hook != null, "PostPersistHook is required");
		oldHook = hook;
	}

	@Override
	public void execute(final ItemModel item, final PersistenceContext context)
	{
		oldHook.execute(item);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " for " + oldHook;
	}
}
