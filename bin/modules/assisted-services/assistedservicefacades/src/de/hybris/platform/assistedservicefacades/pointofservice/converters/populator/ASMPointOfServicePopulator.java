/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.pointofservice.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.springframework.util.Assert;


public class ASMPointOfServicePopulator implements Populator<PointOfServiceModel, PointOfServiceData>
{
	@Override
	public void populate(final PointOfServiceModel source, final PointOfServiceData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setName(source.getName());
		target.setDisplayName(source.getDisplayName());
	}
}
