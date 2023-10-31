/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;

import java.util.Optional;

/**
 * A procedure to be executed before persisting an item model. Name of this procedure must be submitted with a POST request in
 * request header named Pre-Persist-Hook.
 */
public interface PrePersistHook
{
	/**
	 * Executes this hook before persisting the given item.
	 * @param item an item to execute this hook with.
	 * @param context to provide information about the item to be persisted
	 * @return result of processing/modification of the item by this hook to be persisted.
	 * If empty, that means there is no item to persist.
	 * @see PersistenceContext#getPrePersistHook()
	 */
	Optional<ItemModel> execute(ItemModel item, PersistenceContext context);
}
