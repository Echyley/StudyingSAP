/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.handler.delete;

import de.hybris.platform.core.Registry;
import de.hybris.platform.inboundservices.persistence.ItemDeletionService;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.processor.handler.ODataProcessorHandler;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.springframework.beans.factory.annotation.Required;

/**
 * ODataProcessor handler that deletes an item
 */
public class DeleteHandler implements ODataProcessorHandler<DeleteParam, ODataResponse>
{
	private ItemLookupRequestFactory itemLookupRequestFactory;
	private PersistenceService persistenceService;
	private ItemDeletionService itemDeletionService;

	@Override
	public ODataResponse handle(final DeleteParam param)
	{
		final ItemLookupRequest itemLookupRequest = getItemLookupRequest(param);

		getItemDeletionService().deleteItem(itemLookupRequest);

		return ODataResponse.newBuilder().entity("").status(HttpStatusCodes.OK).build();
	}

	private ItemLookupRequest getItemLookupRequest(final DeleteParam param)
	{
		return getItemLookupRequestFactory().create(param.getUriInfo(), param.getContext());
	}

	/**
	 * @deprecated this service is no longer used
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected PersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	/**
	 * @deprecated this service is no longer used
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected ItemLookupRequestFactory getItemLookupRequestFactory()
	{
		return itemLookupRequestFactory;
	}

	@Required
	public void setItemLookupRequestFactory(final ItemLookupRequestFactory itemLookupRequestFactory)
	{
		this.itemLookupRequestFactory = itemLookupRequestFactory;
	}

	public void setItemDeletionService(final ItemDeletionService service) {
		this.itemDeletionService = service;
	}

	private ItemDeletionService getItemDeletionService()
	{
		if (itemDeletionService == null)
		{
			itemDeletionService = Registry.getApplicationContext().getBean("itemDeletionService", ItemDeletionService.class);
		}
		return itemDeletionService;
	}
}
