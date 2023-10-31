/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.model.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.sapmodel.model.SAPDeliveryModeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

/**
 * Populator for additional fields specific for the delivery mode mapping
 */
public class DefaultDeliveryModeMappingPopulator implements Populator<SAPDeliveryModeModel, Map<String, Object>>
{

	public void populate(final SAPDeliveryModeModel source, final Map<String, Object> target) throws ConversionException
	{
		target.put("deliveryMode", source.getDeliveryMode().getCode());
	}

}
