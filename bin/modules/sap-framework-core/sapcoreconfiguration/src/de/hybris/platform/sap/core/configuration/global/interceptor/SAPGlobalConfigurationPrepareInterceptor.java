/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.global.interceptor;

import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.util.Config;


/**
 * Perform preparation of SAPGlobalConfiguration model before save.
 * 
 */
public class SAPGlobalConfigurationPrepareInterceptor implements PrepareInterceptor<SAPGlobalConfigurationModel>
{

	@Override
	public void onPrepare(final SAPGlobalConfigurationModel sapGlobalConfiguration, final InterceptorContext interceptorContext)
			throws InterceptorException
	{

		// Add core_name if not filled yet
		if (sapGlobalConfiguration.getCore_name() == null || sapGlobalConfiguration.getCore_name().isEmpty())
		{
			sapGlobalConfiguration.setCore_name(Config.getString(SapcoreconfigurationConstants.GLOBAL_CONFIGURATION_NAME_PROPERTY,
					SapcoreconfigurationConstants.GLOBAL_CONFIGURATION_NAME));
		}

	}

}
