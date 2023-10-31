/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

public class PrimitivePropertyProcessor extends AbstractPropertyProcessor
{
	@Override
	protected  boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return typeAttributeDescriptor.isPrimitive() && !typeAttributeDescriptor.isCollection();
	}

	@Override
	protected void processEntityInternal(final ODataEntry oDataEntry, final String propertyName, final Object value,
			final ItemConversionRequest request)
	{
		oDataEntry.getProperties().putIfAbsent(propertyName, getPropertyValue(value));
	}

	private Object getPropertyValue(final Object value)
	{
		return value instanceof Date ? DateUtils.toCalendar((Date) value) : value;
	}

	@Override
	protected boolean shouldPropertyBeConverted(final ItemConversionRequest conversionRequest, final String propertyName)
	{
		return true;
	}
}
