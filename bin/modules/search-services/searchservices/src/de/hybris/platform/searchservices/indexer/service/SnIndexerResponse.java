/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import de.hybris.platform.searchservices.core.service.SnResponse;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;


/**
 * Represents an indexer response.
 */
public interface SnIndexerResponse extends SnResponse
{
	Integer getTotalItems();

	Integer getProcessedItems();

	SnIndexerOperationStatus getStatus();
}
