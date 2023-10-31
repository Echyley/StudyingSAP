/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.listeners;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.service.MerchSnConfigService;

/**
 * Interceptor for capturing deletion of {@link MerchSnConfigModel} items for synchronisation with Catalog Service
 */
public class MerchSnConfigRemoveInterceptor implements RemoveInterceptor<MerchSnConfigModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(MerchSnConfigRemoveInterceptor.class);

	private MerchSnConfigService merchSnConfigService;

	@Override
	public void onRemove(final MerchSnConfigModel model, final InterceptorContext ctx) throws InterceptorException
	{
		try
		{
			merchSnConfigService.deleteProductDirectory(model);
		}
		catch (final Exception e)
		{
			LOG.error("Error during removing product directory configuration from Catalog Service", e);
		}
	}

	@Required
	public void setMerchSnConfigService(final MerchSnConfigService merchSnConfigService)
	{
		this.merchSnConfigService = merchSnConfigService;
	}

}
