/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.impl;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.CannotCreateReferencedItemException;
import de.hybris.platform.inboundservices.persistence.ContextItemModelService;
import de.hybris.platform.inboundservices.persistence.ItemModelPopulator;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.populator.ContextReferencedItemModelService;
import de.hybris.platform.inboundservices.persistence.validation.ItemPersistRequestValidator;
import de.hybris.platform.inboundservices.persistence.validation.PersistenceContextValidator;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyValueGenerator;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.ItemSearchService;
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService;
import de.hybris.platform.odata2services.converter.IntegrationObjectItemNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.populator.EntityModelPopulator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Maps;

/**
 * Default implementation for {@link ModelEntityService}
 */
public class DefaultModelEntityService implements ModelEntityService, ContextItemModelService, ContextReferencedItemModelService
{
	private EntityModelPopulator entityModelPopulator;
	private ItemModelPopulator itemModelPopulator;
	private ItemSearchService searchService;
	private ModelService modelService;
	private IntegrationKeyValueGenerator<TypeDescriptor, ODataEntry> keyValueGenerator;
	private ItemTypeDescriptorService itemTypeDescriptorService;
	private ContextReferencedItemModelService contextReferencedItemModelService;
	private ContextItemModelService contextItemModelService;

	/**
	 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.impl.DefaultContextReferencedItemModelService#deriveItemsReferencedInAttributeValue(PersistenceContext, TypeAttributeDescriptor)}
	 */
	@Deprecated(since = "21.05.0-RC1", forRemoval = true)
	@Override
	public Collection<ItemModel> deriveItemsReferencedInAttributeValue(final PersistenceContext context,
	                                                                   final TypeAttributeDescriptor attribute)
	{
		return contextReferencedItemModelService != null
				? contextReferencedItemModelService.deriveItemsReferencedInAttributeValue(context, attribute)
				: deriveItemsReferencedInAttributeValueFallback(context, attribute);
	}

	private List<ItemModel> deriveItemsReferencedInAttributeValueFallback(final PersistenceContext context,
	                                                                      final TypeAttributeDescriptor attribute)
	{
		return context.getReferencedContexts(attribute).stream()
		              .map(refItemCtx -> deriveReferencedItemModel(attribute, refItemCtx))
		              .collect(Collectors.toList());
	}

	/**
	 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.impl.DefaultContextReferencedItemModelService#deriveReferencedItemModel(TypeAttributeDescriptor, PersistenceContext)}
	 */
	@Deprecated(since = "21.05.0-RC1", forRemoval = true)
	@Override
	public ItemModel deriveReferencedItemModel(final TypeAttributeDescriptor attribute,
	                                           final PersistenceContext referencedItemContext)
	{
		return contextReferencedItemModelService != null
				? contextReferencedItemModelService.deriveReferencedItemModel(attribute, referencedItemContext)
				: deriveReferencedItemModelFallback(attribute, referencedItemContext);
	}

	private ItemModel deriveReferencedItemModelFallback(final TypeAttributeDescriptor attribute,
	                                                    final PersistenceContext referencedItemContext)
	{
		final ItemModel item = findOrCreateItem(referencedItemContext);
		return Optional.ofNullable(item)
		               .orElseThrow(() -> new CannotCreateReferencedItemException(attribute, referencedItemContext));
	}

	/**
	 * @deprecated use {@link de.hybris.platform.inboundservices.persistence.impl.DefaultContextItemModelService#findOrCreateItem(PersistenceContext)}
	 */
	@Deprecated(since = "21.05.0-RC1", forRemoval = true)
	@Override
	public ItemModel findOrCreateItem(final PersistenceContext context)
	{
		return getContextItemModelService().findOrCreateItem(context);
	}

	@Override
	public ODataEntry getODataEntry(final ItemConversionRequest conversionRequest) throws EdmException
	{
		final ODataEntry entry = new ODataEntryImpl(Maps.newHashMap(), new MediaMetadataImpl(),
				new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl());
		entityModelPopulator.populateEntity(entry, conversionRequest);
		entry.getProperties().put(INTEGRATION_KEY_PROPERTY_NAME, getIntegrationKey(conversionRequest, entry));
		return entry;
	}

	private String getIntegrationKey(final ItemConversionRequest conversionRequest, final ODataEntry entry) throws EdmException
	{
		final String typeCode = conversionRequest.getEntityType().getName();
		final TypeDescriptor typeDescriptor = getItemTypeDescriptorService().getTypeDescriptor(
				conversionRequest.getIntegrationObjectCode(), typeCode)
		                                                                    .orElseThrow(
				                                                                    () -> new IntegrationObjectItemNotFoundException(
						                                                                    conversionRequest.getIntegrationObjectCode(),
						                                                                    typeCode));

		return getKeyValueGenerator().generate(typeDescriptor, entry);
	}

	@Override
	public int count(final ItemLookupRequest lookupRequest)
	{
		return searchService.countItems(lookupRequest);
	}

	/**
	 * @param request a request from which the item needs to be populated.
	 * @param item an item to populate
	 * @deprecated not used anymore; will be removed
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected void populateItem(final StorageRequest request, final ItemModel item)
	{
		getItemModelPopulator().populate(item, request);
	}

	@Required
	public void setSearchService(final ItemSearchService service)
	{
		searchService = service;
	}

	protected EntityModelPopulator getEntityModelPopulator()
	{
		return entityModelPopulator;
	}

	/**
	 * @param entityModelPopulator a populator implementation to use.
	 * @deprecated not used anymore in this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setEntityModelPopulator(final EntityModelPopulator entityModelPopulator)
	{
		this.entityModelPopulator = entityModelPopulator;
	}

	/**
	 * @deprecated not used anymore in this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ItemModelPopulator getItemModelPopulator()
	{
		return itemModelPopulator;
	}

	@Required
	public void setItemModelPopulator(final ItemModelPopulator populator)
	{
		itemModelPopulator = populator;
	}

	/**
	 * @deprecated not used anymore in this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService a model service implementation to use
	 * @deprecated not used anymore in this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected IntegrationKeyValueGenerator<TypeDescriptor, ODataEntry> getKeyValueGenerator()
	{
		return keyValueGenerator;
	}

	@Required
	public void setKeyValueGenerator(
			final IntegrationKeyValueGenerator<TypeDescriptor, ODataEntry> keyValueGenerator)
	{
		this.keyValueGenerator = keyValueGenerator;
	}

	/**
	 * @deprecated not used anymore by this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected ItemTypeDescriptorService getItemTypeDescriptorService()
	{
		return itemTypeDescriptorService;
	}

	/**
	 * @param itemTypeDescriptorService service implementation to use
	 * @deprecated not used anymore by this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setItemTypeDescriptorService(final ItemTypeDescriptorService itemTypeDescriptorService)
	{
		this.itemTypeDescriptorService = itemTypeDescriptorService;
	}

	/**
	 * @param persistenceContextValidators validators to use
	 * @deprecated not used anymore. Use {@link de.hybris.platform.inboundservices.persistence.impl.DefaultContextItemModelService},
	 * in which the validators are used.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setPersistenceContextValidators(final List<PersistenceContextValidator> persistenceContextValidators)
	{
		// not used, but can't be deleted while deprecated
	}

	/**
	 * @param itemPersistRequestValidators validators to use
	 * @deprecated not used anymore. Use {@link de.hybris.platform.inboundservices.persistence.impl.DefaultContextItemModelService},
	 * in which the validators are used.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setItemPersistRequestValidators(final List<ItemPersistRequestValidator> itemPersistRequestValidators)
	{
		// not used, but can't be deleted while deprecated
	}

	/**
	 * @param contextReferencedItemModelService service implementation to use
	 * @deprecated not used anymore by this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setContextReferencedItemModelService(
			final ContextReferencedItemModelService contextReferencedItemModelService)
	{
		this.contextReferencedItemModelService = contextReferencedItemModelService;
	}

	/**
	 * @param contextItemModelService service implementation to use
	 * @deprecated not used anymore by this service implementation.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setContextItemModelService(final ContextItemModelService contextItemModelService)
	{
		this.contextItemModelService = contextItemModelService;
	}

	private ContextItemModelService getContextItemModelService()
	{
		if (contextItemModelService == null)
		{
			contextItemModelService = Registry.getApplicationContext()
			                                  .getBean("contextItemModelService", ContextItemModelService.class);
		}
		return contextItemModelService;
	}
}
