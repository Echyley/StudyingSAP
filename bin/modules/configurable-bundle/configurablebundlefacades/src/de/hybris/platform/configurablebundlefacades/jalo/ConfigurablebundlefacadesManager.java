/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundlefacades.jalo;

import de.hybris.platform.configurablebundlefacades.constants.ConfigurablebundlefacadesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class ConfigurablebundlefacadesManager extends GeneratedConfigurablebundlefacadesManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ConfigurablebundlefacadesManager.class.getName() );
	
	public static final ConfigurablebundlefacadesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ConfigurablebundlefacadesManager) em.getExtension(ConfigurablebundlefacadesConstants.EXTENSIONNAME);
	}
	
}
