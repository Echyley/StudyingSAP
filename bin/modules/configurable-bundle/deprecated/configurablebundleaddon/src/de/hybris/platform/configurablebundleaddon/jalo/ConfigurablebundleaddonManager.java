/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleaddon.jalo;

import de.hybris.platform.configurablebundleaddon.constants.ConfigurablebundleaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class ConfigurablebundleaddonManager extends GeneratedConfigurablebundleaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ConfigurablebundleaddonManager.class.getName() );
	
	public static final ConfigurablebundleaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ConfigurablebundleaddonManager) em.getExtension(ConfigurablebundleaddonConstants.EXTENSIONNAME);
	}

}
