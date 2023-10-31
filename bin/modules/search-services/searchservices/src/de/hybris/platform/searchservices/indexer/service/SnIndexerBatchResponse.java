/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service;

import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse;


/**
 * Represents an indexer batch response.
 */
public interface SnIndexerBatchResponse extends SnIndexerResponse
{
	/**
	 * Returns the document batch request.
	 *
	 * @return the the document batch request
	 */
	SnDocumentBatchRequest getDocumentBatchRequest();

	/**
	 * Returns the document batch response.
	 *
	 * @return the the document batch response
	 */
	SnDocumentBatchResponse getDocumentBatchResponse();
}
