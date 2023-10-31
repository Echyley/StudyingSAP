/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.core.service.impl.AbstractSnRequest;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.suggest.service.SnSuggestRequest;

import java.util.List;


/**
 * Default implementation for {@link SnSuggestRequest}.
 */
public class DefaultSnIndexerRequest extends AbstractSnRequest implements SnIndexerRequest
{
	private final SnIndexerOperationType indexerOperationType;
	private final List<SnIndexerItemSourceOperation> indexerItemSourceOperations;
	private final SnProgressTracker progressTracker;

	public DefaultSnIndexerRequest(final String indexTypeId, final SnIndexerOperationType indexerOperationType,
			final List<SnIndexerItemSourceOperation> indexerItemSourceOperations)
	{
		this(indexTypeId, indexerOperationType, indexerItemSourceOperations, null);
	}

	public DefaultSnIndexerRequest(final String indexTypeId, final SnIndexerOperationType indexerOperationType,
			final List<SnIndexerItemSourceOperation> indexerItemSourceOperations, final SnProgressTracker progressTracker)
	{
		super(indexTypeId);
		this.indexerOperationType = indexerOperationType;
		this.indexerItemSourceOperations = indexerItemSourceOperations;
		this.progressTracker = progressTracker;
	}

	@Override
	public SnIndexerOperationType getIndexerOperationType()
	{
		return indexerOperationType;
	}

	@Override
	public List<SnIndexerItemSourceOperation> getIndexerItemSourceOperations()
	{
		return indexerItemSourceOperations;
	}

	@Override
	public SnProgressTracker getProgressTracker()
	{
		return progressTracker;
	}
}
