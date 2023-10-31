/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.listeners;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.service.MerchSnConfigService;

/**
 * Interceptor for synchronizing {@link MerchSnConfigModel} items with Catalog Service
 */
public class MerchSnConfigValidateInterceptor implements ValidateInterceptor<MerchSnConfigModel>
{
	private MerchSnConfigService merchSnConfigService;


	@Required
	public void setMerchSnConfigService(final MerchSnConfigService merchSnConfigService)
	{
		this.merchSnConfigService = merchSnConfigService;
	}

	@Override
	public void onValidate(final MerchSnConfigModel merchSnConfigModel, final InterceptorContext interceptorContext) throws InterceptorException
	{
		if (valid(merchSnConfigModel))
		{
			merchSnConfigService.createOrUpdateProductDirectory(merchSnConfigModel, false);
		}
	}

	private boolean valid(final MerchSnConfigModel config)
	{
		return config.getSnIndexType() != null && config.getBaseSite() != null && config.getDisplayName() != null
				&& config.getDefaultLanguage() != null && config.getCurrency() != null && config.getRollUpStrategy() != null
				&& config.getRollUpStrategyField() != null;
	}

}
