/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator;

import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessor;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * Default implementation for {@link EntityModelPopulator}
 */
public class DefaultEntityModelPopulator implements EntityModelPopulator
{
	private List<PropertyProcessor> propertyProcessors;

	@Override
	public void populateEntity(final ODataEntry oDataEntry, final ItemConversionRequest conversionRequest) throws EdmException
	{
		Preconditions.checkArgument(oDataEntry != null, "ItemModel cannot be null");
		Preconditions.checkArgument(conversionRequest != null, "ItemConversionRequest cannot be null");

		for (final PropertyProcessor propertyProcessor : propertyProcessors)
		{
			propertyProcessor.processEntity(oDataEntry, conversionRequest);
		}
	}

	protected List<PropertyProcessor> getPropertyProcessors()
	{
		return propertyProcessors;
	}

	@Required
	public void setPropertyProcessors(final List<PropertyProcessor> propertyProcessors)
	{
		this.propertyProcessors = propertyProcessors != null ? List.copyOf(propertyProcessors) : Collections.emptyList();
	}
}
