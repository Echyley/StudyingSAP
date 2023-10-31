/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Adapter class to use old pre persist hooks in the new persistence service
 *
 * @deprecated used temporary while old hooks are beeing reworked to use the new
 * {@link de.hybris.platform.inboundservices.persistence.hook.PrePersistHook} interface
 */
@Deprecated(since = "2205", forRemoval = true)
public class PrePersistHookAdapter implements de.hybris.platform.inboundservices.persistence.hook.PrePersistHook
{
	private final PrePersistHook oldHook;

	/**
	 * Instantiates this adapter
	 *
	 * @param hook a legacy hook implementation to adapt to the new hook interface
	 */
	public PrePersistHookAdapter(@NotNull final PrePersistHook hook)
	{
		Preconditions.checkArgument(hook != null, "PrePersistHook is required");
		oldHook = hook;
	}

	@Override
	public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
	{
		return oldHook.execute(item);
	}
}
