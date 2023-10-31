/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.impl;

import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.ContextItemModelService;
import de.hybris.platform.inboundservices.persistence.ItemPersistenceService;
import de.hybris.platform.integrationservices.search.ItemNotFoundException;
import de.hybris.platform.integrationservices.search.ItemSearchResult;
import de.hybris.platform.integrationservices.search.ItemSearchService;
import de.hybris.platform.integrationservices.search.validation.ItemSearchRequestValidator;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookExecutor;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.processor.RetrievalErrorRuntimeException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Default implementation for {@link PersistenceService}
 */
public class DefaultPersistenceService implements PersistenceService
{
	private ModelEntityService modelEntityService;
	private ItemSearchService itemSearchService;
	private SessionService sessionService;
	private ModelService modelService;
	private ItemPersistenceService persistenceService;
	private PersistHookExecutor persistHookRegistry;
	private TransactionTemplate transactionTemplate;
	private ContextItemModelService itemModelService;
	private List<ItemSearchRequestValidator> deleteItemValidators = Collections.emptyList();

	@Override
	public ODataEntry createEntityData(final StorageRequest storageRequest) throws EdmException
	{
		getPersistenceService().persist(storageRequest);
		return getODataEntry(storageRequest);
	}

	private ODataEntry getODataEntry(final StorageRequest storageRequest) throws EdmException
	{
		final Optional<ItemModel> contextItem = storageRequest.getContextItem();
		return contextItem.isPresent()
				? toODataEntry(storageRequest, contextItem.get())
				: storageRequest.getODataEntry();
	}

	@Override
	public ODataEntry getEntityData(final ItemLookupRequest lookupRequest, final ConversionOptions options)
	{
		final ItemModel item = lookupItem(lookupRequest);
		return toODataEntry(lookupRequest, options, item);
	}

	@Override
	public ItemLookupResult<ODataEntry> getEntities(final ItemLookupRequest lookupRequest, final ConversionOptions options)
	{
		final ItemSearchResult<ItemModel> searchResult = getItemSearchService().findItems(lookupRequest);
		final ItemLookupResult<ItemModel> result = ItemLookupResult.createFrom(searchResult.getItems(),
				searchResult.getTotalCount().orElse(-1));
		return result.map(item -> toODataEntry(lookupRequest, options, item));
	}

	@Override
	public void deleteItem(final ItemLookupRequest lookupRequest)
	{
		getDeleteItemValidators().forEach(v -> v.validate(lookupRequest));
		final ItemModel item = lookupItem(lookupRequest);
		getModelService().remove(item);
	}

	private ItemModel lookupItem(final ItemLookupRequest req)
	{
		return getItemSearchService()
				.findUniqueItem(req)
				.orElseThrow(() -> new ItemNotFoundException(req.getIntegrationItem()));
	}

	private ODataEntry toODataEntry(final StorageRequest request, final ItemModel model) throws EdmException
	{
		final ConversionOptions conversionOptions = conversionOptionsBuilder().build();
		return toODataEntry(request.toLookupRequest(), conversionOptions, model);
	}

	private ODataEntry toODataEntry(final ItemLookupRequest lookupRequest, final ConversionOptions options, final ItemModel item)
	{
		try
		{
			final ItemConversionRequest conversionRequest = lookupRequest.toConversionRequest(item, options);
			return getModelEntityService().getODataEntry(conversionRequest);
		}
		catch (final EdmException e)
		{
			throw new RetrievalErrorRuntimeException(item.getItemtype(), e);
		}
	}

	@Required
	protected ModelEntityService getModelEntityService()
	{
		return modelEntityService;
	}

	public void setModelEntityService(final ModelEntityService service)
	{
		modelEntityService = service;
	}

	private ItemSearchService getItemSearchService()
	{
		return itemSearchService;
	}

	@Required
	public void setItemSearchService(final ItemSearchService service)
	{
		itemSearchService = service;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService service)
	{
		modelService = service;
	}

	/**
	 * @deprecated not used since persistence hooks have moved to {@link de.hybris.platform.inboundservices.persistence.impl.DefaultItemPersistenceService}
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected PersistHookExecutor getPersistHookRegistry()
	{
		return persistHookRegistry;
	}

	public void setPersistenceService(final ItemPersistenceService service) {
		persistenceService = service;
	}

	private ItemPersistenceService getPersistenceService() {
		if (persistenceService == null)
		{
			persistenceService = ApplicationBeans.getBean("inboundServicesItemPersistenceService", ItemPersistenceService.class);
		}
		return persistenceService;
	}

	/**
	 * @deprecated not used since persistence hooks have moved to {@link de.hybris.platform.inboundservices.persistence.impl.DefaultItemPersistenceService}
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setPersistHookRegistry(final PersistHookExecutor registry)
	{
		persistHookRegistry = registry;
	}

	/**
	 * @deprecated not used anymore
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @deprecated not used anymore
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @deprecated not used anymore
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected TransactionTemplate getTransactionTemplate()
	{
		return transactionTemplate;
	}

	/**
	 * @deprecated not used anymore
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setTransactionTemplate(final TransactionTemplate transactionTemplate)
	{
		this.transactionTemplate = transactionTemplate;
	}

	/**
	 * @deprecated not used anymore
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ContextItemModelService getItemModelService()
	{
		return itemModelService;
	}

	/**
	 * @deprecated not used anymore
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setItemModelService(final ContextItemModelService service)
	{
		itemModelService = service;
	}

	/**
	 * @deprecated validators are not used by this service anymore. Custom validators can be plugged into
	 * {@link de.hybris.platform.inboundservices.persistence.impl.DefaultItemDeletionService#setDeleteItemValidators(List)}
	 * @see #deleteItem(ItemLookupRequest)
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected List<ItemSearchRequestValidator> getDeleteItemValidators()
	{
		return deleteItemValidators;
	}

	/**
	 * @deprecated used only in deprecated {@link #deleteItem(ItemLookupRequest)} method and will be removed together with that method.
	 * Custom validators can be plugged into {@link de.hybris.platform.inboundservices.persistence.impl.DefaultItemDeletionService#setDeleteItemValidators(List)}
	 * @see #deleteItem(ItemLookupRequest)
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setDeleteItemValidators(final List<ItemSearchRequestValidator> deleteItemValidators)
	{
		if (deleteItemValidators != null)
		{
			this.deleteItemValidators = deleteItemValidators;
		}
	}
}
