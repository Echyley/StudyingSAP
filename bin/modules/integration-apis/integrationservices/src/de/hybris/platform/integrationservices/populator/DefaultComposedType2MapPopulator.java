/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.populator;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;

import java.util.Map;


/**
 * Populate the composed type of item model's attribute to Map.
 * Note, the enumerate meta type dose not include.
 */
public class DefaultComposedType2MapPopulator extends AbstractItemToMapPopulator
{
	private IntegrationObjectConversionService conversionService;

	@Override
	protected void populateToMap(
			final TypeAttributeDescriptor attribute,
			final ItemToMapConversionContext context,
			final Map<String, Object> target)
	{
		final Object value = attribute.accessor().getValue(context.getPayloadObject());

		if (attribute.getAttributeType().isInstance(value))
		{
			final ItemToMapConversionContext subContext = context.createSubContext(value, attribute.getAttributeType());
			target.put(attribute.getAttributeName(), getConversionService().convert(subContext));
		}
	}

	@Override
	protected boolean isApplicable(final TypeAttributeDescriptor attributeDescriptor)
	{
		final TypeDescriptor itemType = attributeDescriptor.getAttributeType();
		return !(attributeDescriptor.isCollection() || attributeDescriptor.isMap()
				|| itemType.isPrimitive() || itemType.isEnumeration());
	}

	public IntegrationObjectConversionService getConversionService()
	{
		return conversionService;
	}

	public void setConversionService(final IntegrationObjectConversionService service)
	{
		conversionService = service;
	}
}
