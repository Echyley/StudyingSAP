/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsweb.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.xyformsweb.constants.XyformswebConstants;
import org.apache.log4j.Logger;

public class XyformswebManager extends GeneratedXyformswebManager
{
	private static final Logger LOG = Logger.getLogger( XyformswebManager.class.getName() );
	
	public static final XyformswebManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (XyformswebManager) em.getExtension(XyformswebConstants.EXTENSIONNAME);
	}
	
}
