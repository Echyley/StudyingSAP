/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;

/**
 * A procedure to be executed after persisting an item model. Name of this procedure must be submitted with a POST request in
 * request header named Post-Persist-Hook.
 */
public interface PostPersistHook
{
	/**
	 * Executes this hook after persisting the given item.
	 * @param item an item to execute this hook with.
	 * @param context to provide information about the item to be persisted.
	 * @see PersistenceContext#getPostPersistHook()
	 */
	void execute(ItemModel item, PersistenceContext context);
}
