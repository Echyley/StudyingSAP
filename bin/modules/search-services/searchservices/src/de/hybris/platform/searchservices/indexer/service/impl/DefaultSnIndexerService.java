/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceFactory;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerService;
import de.hybris.platform.searchservices.indexer.service.SnIndexerStrategy;
import de.hybris.platform.searchservices.indexer.service.SnIndexerStrategyFactory;
import de.hybris.platform.searchservices.model.AbstractSnIndexerItemSourceModel;
import de.hybris.platform.searchservices.model.SnFieldModel;
import de.hybris.platform.searchservices.model.SnIndexerItemSourceOperationModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link SnIndexerService}.
 */
public class DefaultSnIndexerService implements SnIndexerService
{
	private static final String ITEM_SOURCE_PARAM = "itemSource";
	private static final String ITEM_SOURCE_OPERATION_PARAM = "itemSourceOperation";
	private static final String INDEX_TYPE_ID_PARAM = "indexTypeId";
	private static final String INDEXER_ITEM_SOURCE_PARAM = "indexerItemSource";
	private static final String INDEXER_ITEM_SOURCE_OPERATIONS_PARAM = "indexerItemSourceOperations";

	private SnIndexerItemSourceFactory snIndexerItemSourceFactory;
	private SnIndexerStrategyFactory snIndexerStrategyFactory;

	@Override
	public SnIndexerItemSource createItemSource(final AbstractSnIndexerItemSourceModel itemSource,
			final Map<String, Object> parameters) throws SnIndexerException
	{
		ServicesUtil.validateParameterNotNullStandardMessage(ITEM_SOURCE_PARAM, itemSource);

		return snIndexerItemSourceFactory.createItemSource(itemSource, parameters);
	}

	@Override
	public SnIndexerItemSourceOperation createItemSourceOperation(final SnIndexerItemSourceOperationModel itemSourceOperation,
			final Map<String, Object> parameters) throws SnIndexerException
	{
		ServicesUtil.validateParameterNotNullStandardMessage(ITEM_SOURCE_OPERATION_PARAM, itemSourceOperation);

		final SnIndexerItemSource itemSource = snIndexerItemSourceFactory
				.createItemSource(itemSourceOperation.getIndexerItemSource(), parameters);
		final Set<String> fieldIds = CollectionUtils.emptyIfNull(itemSourceOperation.getFields()).stream().map(SnFieldModel::getId)
				.collect(Collectors.toSet());
		return new DefaultSnIndexerItemSourceOperation(itemSourceOperation.getDocumentOperationType(), itemSource, fieldIds);
	}

	@Override
	public SnIndexerRequest createFullIndexerRequest(final String indexTypeId, final SnIndexerItemSource indexerItemSource)
			throws SnIndexerException
	{
		return createFullIndexerRequest(indexTypeId, indexerItemSource, null);
	}

	@Override
	public SnIndexerRequest createFullIndexerRequest(final String indexTypeId, final SnIndexerItemSource indexerItemSource,
			final SnProgressTracker progressTracker) throws SnIndexerException
	{
		ServicesUtil.validateParameterNotNullStandardMessage(INDEX_TYPE_ID_PARAM, indexTypeId);
		ServicesUtil.validateParameterNotNullStandardMessage(INDEXER_ITEM_SOURCE_PARAM, indexerItemSource);

		final List<SnIndexerItemSourceOperation> indexerItemSourceOperations = List
				.of(new DefaultSnIndexerItemSourceOperation(SnDocumentOperationType.CREATE, indexerItemSource));

		return new DefaultSnIndexerRequest(indexTypeId, SnIndexerOperationType.FULL, indexerItemSourceOperations, progressTracker);
	}

	@Override
	public SnIndexerRequest createIncrementalIndexerRequest(final String indexTypeId,
			final List<SnIndexerItemSourceOperation> indexerItemSourceOperations) throws SnIndexerException
	{
		return createIncrementalIndexerRequest(indexTypeId, indexerItemSourceOperations, null);
	}

	@Override
	public SnIndexerRequest createIncrementalIndexerRequest(final String indexTypeId,
			final List<SnIndexerItemSourceOperation> indexerItemSourceOperations, final SnProgressTracker progressTracker)
			throws SnIndexerException
	{
		ServicesUtil.validateParameterNotNullStandardMessage(INDEX_TYPE_ID_PARAM, indexTypeId);
		ServicesUtil.validateParameterNotNullStandardMessage(INDEXER_ITEM_SOURCE_OPERATIONS_PARAM, indexerItemSourceOperations);

		return new DefaultSnIndexerRequest(indexTypeId, SnIndexerOperationType.INCREMENTAL, indexerItemSourceOperations,
				progressTracker);
	}

	@Override
	public SnIndexerResponse index(final SnIndexerRequest indexerRequest) throws SnIndexerException
	{
		ServicesUtil.validateParameterNotNullStandardMessage(INDEX_TYPE_ID_PARAM, indexerRequest);

		final SnIndexerStrategy indexerStrategy = snIndexerStrategyFactory.getIndexerStrategy(indexerRequest);
		return indexerStrategy.execute(indexerRequest);
	}

	public SnIndexerItemSourceFactory getSnIndexerItemSourceFactory()
	{
		return snIndexerItemSourceFactory;
	}

	@Required
	public void setSnIndexerItemSourceFactory(final SnIndexerItemSourceFactory snIndexerItemSourceFactory)
	{
		this.snIndexerItemSourceFactory = snIndexerItemSourceFactory;
	}

	public SnIndexerStrategyFactory getSnIndexerStrategyFactory()
	{
		return snIndexerStrategyFactory;
	}

	@Required
	public void setSnIndexerStrategyFactory(final SnIndexerStrategyFactory snIndexerStrategyFactory)
	{
		this.snIndexerStrategyFactory = snIndexerStrategyFactory;
	}
}
