/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Default implementation for {@link SnIndexerItemSourceOperation}.
 */
public class DefaultSnIndexerItemSourceOperation implements SnIndexerItemSourceOperation
{
	private final SnDocumentOperationType documentOperationType;
	private final SnIndexerItemSource indexerItemSource;
	private Set<String> fieldIds;

	public DefaultSnIndexerItemSourceOperation(final SnDocumentOperationType documentOperationType,
			final SnIndexerItemSource indexerItemSource)
	{
		this(documentOperationType, indexerItemSource, null);
	}

	public DefaultSnIndexerItemSourceOperation(final SnDocumentOperationType documentOperationType,
			final SnIndexerItemSource indexerItemSource, final Collection<String> fieldIds)
	{
		this.documentOperationType = documentOperationType;
		this.indexerItemSource = indexerItemSource;
		this.fieldIds = fieldIds == null ? Collections.emptySet() : Set.copyOf(fieldIds);
	}

	@Override
	public SnDocumentOperationType getDocumentOperationType()
	{
		return documentOperationType;
	}

	@Override
	public SnIndexerItemSource getIndexerItemSource()
	{
		return indexerItemSource;
	}

	@Override
	public Set<String> getFieldIds()
	{
		return fieldIds;
	}
}
