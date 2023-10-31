/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.orbeonweb.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.orbeonweb.constants.OrbeonwebConstants;
import org.apache.log4j.Logger;

public class OrbeonwebManager extends GeneratedOrbeonwebManager
{
	private static final Logger LOG = Logger.getLogger( OrbeonwebManager.class.getName() );
	
	public static final OrbeonwebManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (OrbeonwebManager) em.getExtension(OrbeonwebConstants.EXTENSIONNAME);
	}
	
}
