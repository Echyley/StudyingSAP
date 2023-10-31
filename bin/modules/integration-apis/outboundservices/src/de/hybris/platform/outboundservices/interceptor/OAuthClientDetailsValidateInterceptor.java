/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.interceptor;

import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import com.google.common.base.Preconditions;

/**
 * A {@link ValidateInterceptor} for {@link OAuthClientDetailsModel} to clear outboundServicesRestTemplateCacheRegion
 * whenever the model is modified.
 */
public class OAuthClientDetailsValidateInterceptor implements ValidateInterceptor<OAuthClientDetailsModel>
{
	private final EHCacheRegion outboundServicesRestTemplateCacheRegion;

	/**
	 * A constructor to instantiate the {@link OAuthClientDetailsValidateInterceptor}.
	 *
	 * @param outboundServicesRestTemplateCacheRegion the instance of OutboundServicesRestTemplateCacheRegion.
	 */
	public OAuthClientDetailsValidateInterceptor(final EHCacheRegion outboundServicesRestTemplateCacheRegion)
	{
		Preconditions.checkArgument(outboundServicesRestTemplateCacheRegion != null,
				"outboundServicesRestTemplateCacheRegion cannot be null.");
		this.outboundServicesRestTemplateCacheRegion = outboundServicesRestTemplateCacheRegion;
	}

	@Override
	public void onValidate(final OAuthClientDetailsModel oAuthClientDetailsModel, final InterceptorContext ctx)
	{
		if (ctx.isModified(oAuthClientDetailsModel))
		{
			outboundServicesRestTemplateCacheRegion.clearCache();
		}
	}
}