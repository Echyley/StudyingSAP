/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 */
package de.hybris.platform.integrationservices.populator;

import static java.util.stream.Collectors.toMap;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import java.util.Map;


/**
 * Populate the enumerate meta type of item model's attribute to Map.
 */
public class DefaultEnumerationMetaType2MapPopulator extends AbstractItemToMapPopulator
{
	private static final String HYBRIS_ENUM_CODE = "code";

	@Override
	protected void populateToMap(
			final TypeAttributeDescriptor attribute,
			final ItemToMapConversionContext context,
			final Map<String, Object> target)
	{
		final HybrisEnumValue value = (HybrisEnumValue) attribute.accessor().getValue(context.getPayloadObject());

		if(value != null)
		{
			final Map<String, Object> map =
					attribute.getAttributeType().getAttributes().stream()
							.filter(attr -> HYBRIS_ENUM_CODE.equals(attr.getQualifier()))
							.collect(toMap(TypeAttributeDescriptor::getAttributeName, v -> value.getCode()));
			target.put(attribute.getAttributeName(), map);
		}
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attributeDescriptor)
	{
		return attributeDescriptor.getAttributeType().isEnumeration() && !attributeDescriptor.isCollection();
	}
}
