/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.populators;


import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.converters.Populator;


/**
 * Populates {@link BaseSiteData} from {@link BaseSiteModel}
 */
public class BaseSiteSecurePopulator implements Populator<BaseSiteModel, BaseSiteData>
{
	@Override
	public void populate(final BaseSiteModel source, final BaseSiteData target)
	{
		target.setEnableRegistration(source.isEnableRegistration());
	}
}
