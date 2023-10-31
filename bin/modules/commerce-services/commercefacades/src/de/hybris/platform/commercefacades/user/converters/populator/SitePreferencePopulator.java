/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;

import org.springframework.util.Assert;


public class SitePreferencePopulator implements Populator<SitePreferenceModel, SitePreferenceData>
{

	@Override
	public void populate(final SitePreferenceModel source, final SitePreferenceData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (Objects.nonNull(source.getPickUpLocation()))
		{
			target.setPickUpLocationName(source.getPickUpLocation().getName());
		}
	}

}
