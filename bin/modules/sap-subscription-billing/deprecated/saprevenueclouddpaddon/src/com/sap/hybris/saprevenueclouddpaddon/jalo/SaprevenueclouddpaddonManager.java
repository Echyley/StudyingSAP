/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.jalo;

import com.sap.hybris.saprevenueclouddpaddon.constants.SaprevenueclouddpaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class SaprevenueclouddpaddonManager extends GeneratedSaprevenueclouddpaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SaprevenueclouddpaddonManager.class.getName() );
	
	public static final SaprevenueclouddpaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SaprevenueclouddpaddonManager) em.getExtension(SaprevenueclouddpaddonConstants.EXTENSIONNAME);
	}
	
}
