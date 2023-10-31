/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;

import java.util.Optional;

/**
 * A service for executing persistence hooks.
 */
public interface PersistenceHookExecutor
{
	/**
	 * Executes a {@link PrePersistHook} on an item model
	 *
	 * @param item    an item to execute the {@code PrePersistHook} with.
	 * @param context for the current item that is being created or updated
	 * @return item modified by the hook or empty. If {@code Optional.empty()} is returned the item should not be persisted.
	 * This way pre-persist hooks can filter certain items out.
	 * @see PrePersistHook#execute(ItemModel, PersistenceContext)
	 */
	Optional<ItemModel> runPrePersistHook(ItemModel item, PersistenceContext context);

	/**
	 * Executes a {@link PostPersistHook} on an item model
	 *
	 * @param item    an item to execute the {@code PostPersistHook} with.
	 * @param context for the current item that is being created or updated
	 * @see PostPersistHook#execute(ItemModel, PersistenceContext)
	 */
	void runPostPersistHook(ItemModel item, PersistenceContext context);
}
