/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.hook;

import de.hybris.platform.core.model.ItemModel;

/**
 * A procedure to be executed after persisting an item model. Name of this procedure must be submitted with a POST request in
 * request header named Post-Persist-Hook.
 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.hook.PostPersistHook} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public interface PostPersistHook
{
	/**
	 * Executes this hook after persisting the given item.
	 * @param item an item to execute this hook with
	 */
	void execute(ItemModel item);
}
