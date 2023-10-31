/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 */
package de.hybris.platform.integrationservices.populator;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;


/**
 * Populate the atomic type of item model's attribute to Map.
 */
public class DefaultAtomicType2MapPopulator extends AbstractItemToMapPopulator
{
	private Converter<Object, Object> converter = new AtomicTypeValueConverter();

	@Override
	protected void populateToMap(
			final TypeAttributeDescriptor attribute,
			final ItemToMapConversionContext context,
			final Map<String, Object> target)
	{
		final Object value = attribute.accessor().getValue(context.getPayloadObject());

		if (value == null)
		{
			return;
		}

		final String attributeName = attribute.getAttributeName();
		final Object convertedValue = converter.convert(value);
		target.put(attributeName, convertedValue);
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attributeDescriptor)
	{
		return attributeDescriptor.isPrimitive() && !attributeDescriptor.isCollection();
	}

	public void setConverter(final Converter<Object, Object> converter)
	{
		this.converter = converter;
	}
}
