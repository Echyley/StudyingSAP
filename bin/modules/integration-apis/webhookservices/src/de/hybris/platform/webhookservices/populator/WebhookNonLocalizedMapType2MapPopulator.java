/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.populator;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.populator.DefaultNonLocalizedMapType2MapPopulator;
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext;
import de.hybris.platform.webhookservices.model.WebhookPayloadModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A webhook populator for special types with non-localized map attributes.
 */
public class WebhookNonLocalizedMapType2MapPopulator extends DefaultNonLocalizedMapType2MapPopulator
{
	private static final List<String> SPECIAL_TYPES = List.of(WebhookPayloadModel._TYPECODE);

	@Override
	protected void populateToMap(final TypeAttributeDescriptor attr, final ItemToMapConversionContext context,
			final Map<String, Object> target)
	{
		if (isSpecialMappingApplicable(attr))
		{
			populateToSpecialMap(attr, context, target);
		}
		else
		{
			super.populateToMap(attr, context, target);
		}
	}

	private boolean isSpecialMappingApplicable(final TypeAttributeDescriptor attr)
	{
		return SPECIAL_TYPES.contains(attr.getTypeDescriptor().getTypeCode()) && attr.isMap() && attr.getAttributeName() != null;
	}

	private void populateToSpecialMap(final TypeAttributeDescriptor attr, final ItemToMapConversionContext context,
			final Map<String, Object> target)
	{
		final var eventDetailsMap = (Map<?, ?>) attr.accessor().getValue(context.getPayloadObject());
		target.put(attr.getAttributeName(), buildMap(eventDetailsMap));

	}

	private Map<?, ?> buildMap(final Map<?, ?> eventDetailsMap)
	{
		return eventDetailsMap.entrySet().stream()
				.filter(entry -> entry.getKey() != null)
				.collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
	}

}
