/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.config;

import de.hybris.platform.dataimportcommons.log.Log;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class SSLNotEnabledWarning implements InitializingBean
{
	private static final Logger LOGGER = Log.getLogger(SSLNotEnabledWarning.class);

	private boolean sslEnabled;

	@Override
	public void afterPropertiesSet()
	{
		if(!sslEnabled)
		{
			LOGGER.warn("***************************************************************************************");
			LOGGER.warn("Data Hub Adapter is running in HTTP mode.");
			LOGGER.warn("");
			LOGGER.warn("This may be suitable for DEVELOPMENT but should be disabled in PRODUCTION.");
			LOGGER.warn("Require HTTPS protocol by setting property datahubadapter.security.https.enabled=true.");
			LOGGER.warn("***************************************************************************************");
		}
	}

	public void setSslEnabled(final boolean sslEnabled)
	{
		this.sslEnabled = sslEnabled;
	}
}
