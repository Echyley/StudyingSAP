/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;


/**
 * Default implementation for {@link SnIndexerBatchResponse}.
 */
public class DefaultSnIndexerBatchResponse extends DefaultSnIndexerResponse implements SnIndexerBatchResponse
{
	private SnDocumentBatchRequest documentBatchRequest;
	private SnDocumentBatchResponse documentBatchResponse;

	public DefaultSnIndexerBatchResponse(final SnIndexConfiguration indexConfiguration, final SnIndexType indexType)
	{
		super(indexConfiguration, indexType);
	}

	@Override
	public SnDocumentBatchRequest getDocumentBatchRequest()
	{
		return documentBatchRequest;
	}

	public void setDocumentBatchRequest(final SnDocumentBatchRequest documentBatchRequest)
	{
		this.documentBatchRequest = documentBatchRequest;
	}

	@Override
	public SnDocumentBatchResponse getDocumentBatchResponse()
	{
		return documentBatchResponse;
	}

	public void setDocumentBatchResponse(final SnDocumentBatchResponse documentBatchResponse)
	{
		this.documentBatchResponse = documentBatchResponse;
	}
}
