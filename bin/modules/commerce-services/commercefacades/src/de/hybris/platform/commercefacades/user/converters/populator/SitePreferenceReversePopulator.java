/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class SitePreferenceReversePopulator implements Populator<SitePreferenceData, SitePreferenceModel>
{
	private final PointOfServiceService pointOfServiceService;

	public SitePreferenceReversePopulator(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	@Override
	public void populate(final SitePreferenceData source, final SitePreferenceModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (StringUtils.isNotBlank(source.getPickUpLocationName()))
		{
			final PointOfServiceModel pickUpLocation = pointOfServiceService.getPointOfServiceForName(source.getPickUpLocationName());
			target.setPickUpLocation(pickUpLocation);
		}
		else
		{
			target.setPickUpLocation(null);
		}
	}
}
