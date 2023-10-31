/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapqualtricsaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sapqualtricsaddon.constants.SapqualtricsaddonConstants;
import org.apache.log4j.Logger;

public class SapqualtricsaddonManager extends GeneratedSapqualtricsaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SapqualtricsaddonManager.class.getName() );
	
	public static final SapqualtricsaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapqualtricsaddonManager) em.getExtension(SapqualtricsaddonConstants.EXTENSIONNAME);
	}
	
}
