/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;

public class EntityPropertyProcessor extends AbstractPropertyProcessor
{
	private ModelEntityService modelEntityService;

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attribute)
	{
		return !attribute.isCollection() && !attribute.isPrimitive() && !attribute.isMap();
	}

	@Override
	protected void processEntityInternal(final ODataEntry oDataEntry, final String propertyName, final Object value,
			final ItemConversionRequest conversionRequest) throws EdmException
	{
		if (value != null)
		{
			final ItemConversionRequest subRequest = conversionRequest.propertyConversionRequest(propertyName, value);
			final ODataEntry entry = getModelEntityService().getODataEntry(subRequest);
			oDataEntry.getProperties().putIfAbsent(propertyName, entry);
		}
	}

	protected ModelEntityService getModelEntityService()
	{
		return modelEntityService;
	}

	@Required
	public void setModelEntityService(final ModelEntityService modelEntityService)
	{
		this.modelEntityService = modelEntityService;
	}
}
