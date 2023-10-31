/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import de.hybris.platform.searchservices.enums.SnDocumentOperationType;

import java.util.Set;

/**
 * Defines the operation to be applied on an indexer item source.
 */
public interface SnIndexerItemSourceOperation
{
	/**
	 * Returns the document operation type.
	 *
	 * @return the document operation type.
	 */
	SnDocumentOperationType getDocumentOperationType();

	/**
	 * Returns the indexer item source.
	 *
	 * @return the the indexer item source.
	 */
	SnIndexerItemSource getIndexerItemSource();

	/**
	 * Returns the optional set of field Ids for partial updates.
	 *
	 * @return optional set of fields, or empty list
	 */
	Set<String> getFieldIds();
}
