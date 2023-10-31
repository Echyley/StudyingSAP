/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.populator;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Populate a map from a non-localized map type attribute, such as String2StringMapType, of an item model.
 */
public class DefaultNonLocalizedMapType2MapPopulator extends AbstractItemToMapPopulator
{

	private static final String KEY = "key";
	private static final String VALUE = "value";

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attrDesc)
	{
		return attrDesc.isMap() && !attrDesc.isLocalized();
	}

	@Override
	protected void populateToMap(final TypeAttributeDescriptor attr,
	                             final ItemToMapConversionContext context,
	                             final Map<String, Object> target)
	{
		final Object value = attr.accessor().getValue(context.getPayloadObject());
		if (value instanceof Map && attr.getAttributeName() != null)
		{
			target.put(attr.getAttributeName(), buildListOfMaps((Map<?, ?>) value));
		}
	}

	private List<Map<String, Object>> buildListOfMaps(final Map<?, ?> map)
	{
		return map.entrySet()
		          .stream()
		          .filter(entry -> entry.getKey() != null)
		          .map(this::mapEntry)
		          .collect(Collectors.toList());
	}

	private Map<String, Object> mapEntry(final Map.Entry<?, ?> mapEntry)
	{
		final Map<String, Object> map = new HashMap<>();
		map.put(KEY, mapEntry.getKey());
		map.put(VALUE, mapEntry.getValue());
		return map;
	}

}
