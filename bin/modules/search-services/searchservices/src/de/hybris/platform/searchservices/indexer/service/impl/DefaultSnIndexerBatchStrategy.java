/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.SnRuntimeException;
import de.hybris.platform.searchservices.core.service.SnIdentityProvider;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.core.service.SnQualifierType;
import de.hybris.platform.searchservices.core.service.SnQualifierTypeFactory;
import de.hybris.platform.searchservices.document.data.SnDocument;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationResponse;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse;
import de.hybris.platform.searchservices.enums.SnDocumentOperationStatus;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchStrategy;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;
import de.hybris.platform.searchservices.spi.data.AbstractSnSearchProviderConfiguration;
import de.hybris.platform.searchservices.spi.service.SnSearchProvider;
import de.hybris.platform.searchservices.spi.service.SnSearchProviderFactory;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Default implementation for {@link SnIndexerBatchStrategy}.
 */
public class DefaultSnIndexerBatchStrategy extends AbstractSnIndexerBatchStrategy implements ApplicationContextAware
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSnIndexerBatchStrategy.class);

	private FlexibleSearchService flexibleSearchService;
	private SnSearchProviderFactory snSearchProviderFactory;
	private SnQualifierTypeFactory snQualifierTypeFactory;

	private ApplicationContext applicationContext;

	@Override
	protected SnIndexerBatchResponse doExecute(final SnIndexerBatchContext indexerBatchContext, final String indexerBatchId)
			throws SnException, InterruptedException
	{
		int totalItems = 0;
		int processedItems = 0;

		final List<SnIndexerItemSourceOperation> indexerItemSourceOperations = indexerBatchContext.getIndexerItemSourceOperations();
		final SnIdentityProvider<ItemModel> identityProvider = createIdentityProvider(indexerBatchContext);
		final List<SnDocumentBatchOperationRequest> documentBatchOperationRequests = new ArrayList<>();

		for (final SnIndexerItemSourceOperation indexerItemSourceOperation : indexerItemSourceOperations)
		{
			if (indexerItemSourceOperation.getIndexerItemSource() != null)
			{
				final List<PK> pks = indexerItemSourceOperation.getIndexerItemSource().getPks(indexerBatchContext);
				if (CollectionUtils.isNotEmpty(pks))
				{
					totalItems += pks.size();
					addIndexDocumentBatchOperationRequests(indexerBatchContext, identityProvider, documentBatchOperationRequests,
							indexerItemSourceOperation, pks);
				}
			}
		}

		if (documentBatchOperationRequests.isEmpty())
		{
			LOG.warn("Skipping indexer batch, no document operations");
			return createIndexerBatchResponse(indexerBatchContext, totalItems, processedItems, SnIndexerOperationStatus.COMPLETED,
					null, null);
		}
		else
		{
			final SnDocumentBatchRequest documentBatchRequest = new SnDocumentBatchRequest();
			documentBatchRequest.setId(indexerBatchId);
			documentBatchRequest.setRequests(documentBatchOperationRequests);

			final SnSearchProvider<AbstractSnSearchProviderConfiguration> searchProvider = snSearchProviderFactory
					.getSearchProviderForContext(indexerBatchContext);
			final SnDocumentBatchResponse documentBatchResponse = searchProvider.executeDocumentBatch(indexerBatchContext,
					indexerBatchContext.getIndexId(), documentBatchRequest, indexerBatchContext.getIndexerOperationId());

			for (final SnDocumentBatchOperationResponse documentBatchOperationResponse : documentBatchResponse.getResponses())
			{
				if (!Objects.equals(documentBatchOperationResponse.getStatus(), SnDocumentOperationStatus.FAILED))
				{
					processedItems++;
				}
			}

			final SnIndexerOperationStatus status = totalItems == processedItems ? SnIndexerOperationStatus.COMPLETED
					: SnIndexerOperationStatus.FAILED;

			return createIndexerBatchResponse(indexerBatchContext, totalItems, processedItems, status, documentBatchRequest,
					documentBatchResponse);
		}
	}

	private void addIndexDocumentBatchOperationRequests(final SnIndexerBatchContext indexerBatchContext,
			final SnIdentityProvider<ItemModel> identityProvider,
			final List<SnDocumentBatchOperationRequest> documentBatchOperationRequests,
			final SnIndexerItemSourceOperation indexerItemSourceOperation, final List<PK> pks)
			throws SnException, InterruptedException
	{
		if (SnDocumentOperationType.CREATE == indexerItemSourceOperation.getDocumentOperationType()
				|| SnDocumentOperationType.CREATE_UPDATE == indexerItemSourceOperation.getDocumentOperationType()
				|| SnDocumentOperationType.PARTIAL_UPDATE == indexerItemSourceOperation.getDocumentOperationType())
		{
			addIndexDocumentBatchOperationRequests(indexerBatchContext, documentBatchOperationRequests, pks, identityProvider,
					indexerItemSourceOperation);
		}
		else if (SnDocumentOperationType.DELETE == indexerItemSourceOperation.getDocumentOperationType())
		{
			addDeleteDocumentBatchOperationRequests(indexerBatchContext, documentBatchOperationRequests, pks, identityProvider);
		}
		else
		{
			throw new SnRuntimeException(MessageFormat.format("Cannot process document operation type ''{0}''",
					indexerItemSourceOperation.getDocumentOperationType()));
		}
	}

	protected void addIndexDocumentBatchOperationRequests(final SnIndexerContext indexerContext,
			final List<SnDocumentBatchOperationRequest> documentBatchOperationRequests, final List<PK> pks,
			final SnIdentityProvider<ItemModel> identityProvider, final SnIndexerItemSourceOperation indexeritemSourceOperation)
			throws SnException, InterruptedException
	{
		final List<ItemModel> items = collectItems(indexerContext, pks);
		final Collection<ValueProviderWrapper> valueProviderWrappers = createValueProviders(indexerContext,
				indexeritemSourceOperation);

		for (final ItemModel item : items)
		{
			if (Thread.interrupted())
			{
				throw new InterruptedException();
			}

			final String documentId = identityProvider.getIdentifier(indexerContext, item);

			final SnDocument document = new SnDocument();
			document.setId(documentId);

			for (final ValueProviderWrapper valueProviderWrapper : valueProviderWrappers)
			{
				final SnIndexerValueProvider<ItemModel> valueProvider = valueProviderWrapper.getValueProvider();
				final List<SnIndexerFieldWrapper> fieldWrappers = valueProviderWrapper.getFieldWrappers();
				valueProvider.provide(indexerContext, fieldWrappers, item, document);
			}

			final SnDocumentBatchOperationRequest documentBatchOperationRequest = new SnDocumentBatchOperationRequest();
			documentBatchOperationRequest.setId(documentId);
			documentBatchOperationRequest.setOperationType(indexeritemSourceOperation.getDocumentOperationType());
			documentBatchOperationRequest.setDocument(document);

			documentBatchOperationRequests.add(documentBatchOperationRequest);
		}
	}

	protected void addDeleteDocumentBatchOperationRequests(final SnIndexerContext indexerContext,
			final List<SnDocumentBatchOperationRequest> documentBatchOperationRequests, final List<PK> pks,
			final SnIdentityProvider<ItemModel> identityProvider) throws SnException, InterruptedException
	{
		for (final PK pk : pks)
		{
			if (Thread.interrupted())
			{
				throw new InterruptedException();
			}

			final String documentId = identityProvider.getIdentifier(indexerContext, pk);

			final SnDocumentBatchOperationRequest documentBatchOperationRequest = new SnDocumentBatchOperationRequest();
			documentBatchOperationRequest.setId(documentId);
			documentBatchOperationRequest.setOperationType(SnDocumentOperationType.DELETE);

			documentBatchOperationRequests.add(documentBatchOperationRequest);
		}
	}

	protected SnIdentityProvider<ItemModel> createIdentityProvider(final SnIndexerBatchContext indexerBatchContext)
			throws SnIndexerException
	{
		final SnIndexType indexType = indexerBatchContext.getIndexType();

		try
		{
			return applicationContext.getBean(indexType.getIdentityProvider(), SnIdentityProvider.class);
		}
		catch (final BeansException e)
		{
			throw new SnIndexerException("Cannot create identity provider [" + indexType.getIdentityProvider() + "]", e);
		}
	}

	protected List<ItemModel> collectItems(final SnIndexerContext indexerContext, final List<PK> pks)
	{
		final SnIndexType indexType = indexerContext.getIndexType();

		final String query = "SELECT {pk} FROM {" + indexType.getItemComposedType() + "} where {pk} in (?pks)";
		final Map<String, Object> queryParameters = Collections.<String, Object> singletonMap("pks", pks);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query, queryParameters);
		final SearchResult<ItemModel> flexibleSearchResult = flexibleSearchService.search(flexibleSearchQuery);

		return flexibleSearchResult.getResult();
	}

	protected Collection<ValueProviderWrapper> createValueProviders(final SnIndexerContext indexerContext,
			final SnIndexerItemSourceOperation indexerItemSourceOperation) throws SnIndexerException
	{
		final SnIndexType indexType = indexerContext.getIndexType();

		if (MapUtils.isEmpty(indexType.getFields()))
		{
			return Collections.emptyList();
		}

		final Map<String, List<SnQualifier>> availableQualifiers = collectAvailableQualifiers(indexerContext, indexType);
		final Map<String, ValueProviderWrapper> valueProviderWrappers = new HashedMap();
		final Collection<SnField> fields = CollectionUtils.isEmpty(indexerItemSourceOperation.getFieldIds())
				? indexType.getFields().values()
				: indexerItemSourceOperation.getFieldIds().stream().map(id -> indexType.getFields().get(id)).filter(Objects::nonNull)
						.collect(Collectors.toList());
		for (final SnField field : fields)
		{
			String valueProviderId = null;
			Map<String, String> valueProviderParameters = null;

			if (field.getValueProvider() != null)
			{
				valueProviderId = field.getValueProvider();
				valueProviderParameters = field.getValueProviderParameters();
			}
			else if (indexType.getDefaultValueProvider() != null)
			{
				valueProviderId = indexType.getDefaultValueProvider();
				valueProviderParameters = indexType.getDefaultValueProviderParameters();
			}

			if (StringUtils.isNotBlank(valueProviderId))
			{
				ValueProviderWrapper valueProviderWrapper = valueProviderWrappers.get(valueProviderId);
				if (valueProviderWrapper == null)
				{
					valueProviderWrapper = new ValueProviderWrapper(createValueProvider(valueProviderId));
					valueProviderWrappers.put(valueProviderId, valueProviderWrapper);
				}

				final DefaultSnIndexerFieldWrapper fieldWrapper = new DefaultSnIndexerFieldWrapper();
				fieldWrapper.setField(field);
				fieldWrapper.setValueProviderId(valueProviderId);
				fieldWrapper.setValueProviderParameters(valueProviderParameters);

				final Optional<SnQualifierType> qualifierTypeOptional = snQualifierTypeFactory.getQualifierType(indexerContext,
						field);
				if (qualifierTypeOptional.isPresent())
				{
					final SnQualifierType qualifierType = qualifierTypeOptional.get();
					fieldWrapper.setQualifiers(availableQualifiers.get(qualifierType.getId()));
				}

				valueProviderWrapper.getFieldWrappers().add(fieldWrapper);
			}
		}

		return valueProviderWrappers.values();
	}

	protected Map<String, List<SnQualifier>> collectAvailableQualifiers(final SnIndexerContext indexerContext,
			final SnIndexType indexType)
	{
		if (MapUtils.isEmpty(indexType.getFields()))
		{
			return Collections.emptyMap();
		}

		final Map<String, List<SnQualifier>> availableQualifiers = new HashMap<>();

		for (final SnField field : indexType.getFields().values())
		{
			final Optional<SnQualifierType> qualifierTypeOptional = snQualifierTypeFactory.getQualifierType(indexerContext, field);
			if (qualifierTypeOptional.isPresent())
			{
				final SnQualifierType qualifierType = qualifierTypeOptional.get();
				availableQualifiers.computeIfAbsent(qualifierType.getId(),
						key -> qualifierType.getQualifierProvider().getAvailableQualifiers(indexerContext));
			}
		}

		return availableQualifiers;
	}

	protected SnIndexerValueProvider<ItemModel> createValueProvider(final String valueProviderId) throws SnIndexerException
	{
		try
		{
			return applicationContext.getBean(valueProviderId, SnIndexerValueProvider.class);
		}
		catch (final BeansException e)
		{
			throw new SnIndexerException("Cannot create value provider [" + valueProviderId + "]", e);
		}
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public SnSearchProviderFactory getSnSearchProviderFactory()
	{
		return snSearchProviderFactory;
	}

	@Required
	public void setSnSearchProviderFactory(final SnSearchProviderFactory snSearchProviderFactory)
	{
		this.snSearchProviderFactory = snSearchProviderFactory;
	}

	public SnQualifierTypeFactory getSnQualifierTypeFactory()
	{
		return snQualifierTypeFactory;
	}

	@Required
	public void setSnQualifierTypeFactory(final SnQualifierTypeFactory snQualifierTypeFactory)
	{
		this.snQualifierTypeFactory = snQualifierTypeFactory;
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	protected static class ValueProviderWrapper
	{
		private final SnIndexerValueProvider<ItemModel> valueProvider;
		private final List<SnIndexerFieldWrapper> fieldWrappers = new ArrayList<>();

		public ValueProviderWrapper(final SnIndexerValueProvider<ItemModel> valueProvider)
		{
			this.valueProvider = valueProvider;
		}

		public SnIndexerValueProvider<ItemModel> getValueProvider()
		{
			return valueProvider;
		}

		public List<SnIndexerFieldWrapper> getFieldWrappers()
		{
			return fieldWrappers;
		}
	}
}
