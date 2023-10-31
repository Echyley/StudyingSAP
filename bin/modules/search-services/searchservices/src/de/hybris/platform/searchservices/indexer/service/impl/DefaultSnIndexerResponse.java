/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.core.service.impl.AbstractSnResponse;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse;


/**
 * Default implementation for {@link SnIndexerResponse}.
 */
public class DefaultSnIndexerResponse extends AbstractSnResponse implements SnIndexerResponse
{
	private Integer totalItems;
	private Integer processedItems;
	private SnIndexerOperationStatus status;

	public DefaultSnIndexerResponse(final SnIndexConfiguration indexConfiguration, final SnIndexType indexType)
	{
		super(indexConfiguration, indexType);
	}

	@Override
	public Integer getTotalItems()
	{
		return totalItems;
	}

	public void setTotalItems(final Integer totalItems)
	{
		this.totalItems = totalItems;
	}

	@Override
	public Integer getProcessedItems()
	{
		return processedItems;
	}

	public void setProcessedItems(final Integer processedItems)
	{
		this.processedItems = processedItems;
	}

	@Override
	public SnIndexerOperationStatus getStatus()
	{
		return status;
	}

	public void setStatus(final SnIndexerOperationStatus status)
	{
		this.status = status;
	}
}
