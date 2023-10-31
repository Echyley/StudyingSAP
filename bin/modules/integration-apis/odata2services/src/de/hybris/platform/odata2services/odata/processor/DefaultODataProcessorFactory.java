/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.integrationservices.security.AccessRightsService;
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService;
import de.hybris.platform.odata2services.odata.processor.handler.ODataProcessorHandler;
import de.hybris.platform.odata2services.odata.processor.handler.delete.DeleteParam;
import de.hybris.platform.odata2services.odata.processor.handler.persistence.PersistenceParam;
import de.hybris.platform.odata2services.odata.processor.handler.persistence.batch.BatchParam;
import de.hybris.platform.odata2services.odata.processor.handler.persistence.batch.ChangeSetParam;
import de.hybris.platform.odata2services.odata.processor.handler.read.ReadParam;

import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.springframework.beans.factory.annotation.Required;

/**
 * A default implementation of the factory, which creates new instance of the processor for every invocation.
 */
public class DefaultODataProcessorFactory implements ODataProcessorFactory
{
	private AccessRightsService accessRightsService;
	private ServiceNameExtractor serviceNameExtractor;
	private ItemTypeDescriptorService itemTypeDescriptorService;

	private ODataProcessorHandler<PersistenceParam, ODataResponse> persistenceHandler;
	private ODataProcessorHandler<BatchParam, ODataResponse> batchPersistenceHandler;
	private ODataProcessorHandler<ChangeSetParam, BatchResponsePart> changeSetPersistenceHandler;
	private ODataProcessorHandler<PersistenceParam, ODataResponse> patchPersistenceHandler;
	private ODataProcessorHandler<DeleteParam, ODataResponse> deleteHandler;
	private ODataProcessorHandler<ReadParam, ODataResponse> readHandler;

	@Override
	public ODataSingleProcessor createProcessor(final ODataContext context)
	{
		final OData2Processor processor = new OData2Processor();
		processor.setPersistenceHandler(getPersistenceHandler());
		processor.setBatchPersistenceHandler(getBatchPersistenceHandler());
		processor.setChangeSetPersistenceHandler(getChangeSetPersistenceHandler());
		processor.setPatchPersistenceHandler(getPatchPersistenceHandler());
		processor.setDeleteHandler(getDeleteHandler());
		processor.setReadHandler(getReadHandler());
		processor.setContext(context);
		processor.setAccessRightsService(accessRightsService);
		processor.setServiceNameExtractor(getServiceNameExtractor());
		processor.setItemTypeDescriptorService(getItemTypeDescriptorService());
		return processor;
	}

	protected ODataProcessorHandler<PersistenceParam, ODataResponse> getPersistenceHandler()
	{
		return persistenceHandler;
	}

	@Required
	public void setPersistenceHandler(final ODataProcessorHandler<PersistenceParam, ODataResponse> persistenceHandler)
	{
		this.persistenceHandler = persistenceHandler;
	}

	protected ODataProcessorHandler<BatchParam, ODataResponse> getBatchPersistenceHandler()
	{
		return batchPersistenceHandler;
	}

	@Required
	public void setBatchPersistenceHandler(final ODataProcessorHandler<BatchParam, ODataResponse> batchPersistenceHandler)
	{
		this.batchPersistenceHandler = batchPersistenceHandler;
	}

	protected ODataProcessorHandler<ChangeSetParam, BatchResponsePart> getChangeSetPersistenceHandler()
	{
		return changeSetPersistenceHandler;
	}

	@Required
	public void setChangeSetPersistenceHandler(
			final ODataProcessorHandler<ChangeSetParam, BatchResponsePart> changeSetPersistenceHandler)
	{
		this.changeSetPersistenceHandler = changeSetPersistenceHandler;
	}

	protected ODataProcessorHandler<PersistenceParam, ODataResponse> getPatchPersistenceHandler()
	{
		return patchPersistenceHandler;
	}

	@Required
	public void setPatchPersistenceHandler(final ODataProcessorHandler<PersistenceParam, ODataResponse> patchPersistenceHandler)
	{
		this.patchPersistenceHandler = patchPersistenceHandler;
	}

	protected ODataProcessorHandler<DeleteParam, ODataResponse> getDeleteHandler()
	{
		return deleteHandler;
	}

	@Required
	public void setDeleteHandler(final ODataProcessorHandler<DeleteParam, ODataResponse> deleteHandler)
	{
		this.deleteHandler = deleteHandler;
	}

	protected ODataProcessorHandler<ReadParam, ODataResponse> getReadHandler()
	{
		return readHandler;
	}

	@Required
	public void setReadHandler(final ODataProcessorHandler<ReadParam, ODataResponse> readHandler)
	{
		this.readHandler = readHandler;
	}

	@Required
	public void setAccessRightsService(
			final AccessRightsService accessRightsService)
	{
		this.accessRightsService = accessRightsService;
	}

	protected ServiceNameExtractor getServiceNameExtractor()
	{
		return serviceNameExtractor;
	}

	@Required
	public void setServiceNameExtractor(final ServiceNameExtractor serviceNameExtractor)
	{
		this.serviceNameExtractor = serviceNameExtractor;
	}

	protected ItemTypeDescriptorService getItemTypeDescriptorService()
	{
		return itemTypeDescriptorService;
	}

	@Required
	public void setItemTypeDescriptorService(
			final ItemTypeDescriptorService itemTypeDescriptorService)
	{
		this.itemTypeDescriptorService = itemTypeDescriptorService;
	}
}
