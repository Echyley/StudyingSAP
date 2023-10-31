/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence;

import de.hybris.platform.integrationservices.search.ItemSearchRequest;

/**
 * Service to delete platform items.
 */
public interface ItemDeletionService {
	/**
	 * Deletes the item matching the conditions of the specified request
	 *
	 * @param searchRequest Parameter object that holds values for searching the item to be deleted.
	 */
	void deleteItem(final ItemSearchRequest searchRequest);
}
