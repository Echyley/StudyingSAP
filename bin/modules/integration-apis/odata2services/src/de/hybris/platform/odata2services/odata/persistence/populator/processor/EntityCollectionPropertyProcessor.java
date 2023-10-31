/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;

public class EntityCollectionPropertyProcessor extends AbstractCollectionPropertyProcessor
{
	private ModelEntityService modelEntityService;

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return typeAttributeDescriptor.isCollection() && !typeAttributeDescriptor.isPrimitive();
	}

	@Override
	protected List<ODataEntry> deriveDataFeedEntries(final ItemConversionRequest request, final String propertyName, final Object value) throws EdmException
	{
		final ItemConversionRequest relatedRequest = request.propertyConversionRequest(propertyName, value);
		return getListOfODataEntries((Collection<?>) value, relatedRequest);
	}

	protected List<ODataEntry> getListOfODataEntries(final Collection<?> values, final ItemConversionRequest request)
			throws EdmException
	{
		final List<ODataEntry> list = new ArrayList<>(values.size());
		for (final Object value : values)
		{
			final ItemConversionRequest newRequest = itemConversionRequestBuilder().from(request)
					.withValue(value)
					.build();
			final ODataEntry newEntry = modelEntityService.getODataEntry(newRequest);
			list.add(newEntry);
		}
		return list;
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
