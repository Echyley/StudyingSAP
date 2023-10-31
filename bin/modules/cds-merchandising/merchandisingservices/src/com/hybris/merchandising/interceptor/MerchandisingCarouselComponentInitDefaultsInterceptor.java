/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.interceptor;

import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import com.hybris.merchandising.addon.model.MerchandisingCarouselComponentModel;

public class MerchandisingCarouselComponentInitDefaultsInterceptor
		implements InitDefaultsInterceptor<MerchandisingCarouselComponentModel>
{
	private static final int DEFAULT_NUMBER_OF_PRODUCTS = 5;
	private static final int DEFAULT_VIEWPORT_PERCENTAGE = 80;

	@Override
	public void onInitDefaults(final MerchandisingCarouselComponentModel model, final InterceptorContext ctx)
			throws InterceptorException
	{
		if (model.getNumberToDisplay() <= 0)
		{
			model.setNumberToDisplay(DEFAULT_NUMBER_OF_PRODUCTS);
		}
		if (model.getViewportPercentage() <= 0)
		{
			model.setViewportPercentage(DEFAULT_VIEWPORT_PERCENTAGE);
		}
	}
}
