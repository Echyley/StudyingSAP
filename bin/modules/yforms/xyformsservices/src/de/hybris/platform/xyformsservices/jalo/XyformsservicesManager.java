/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsservices.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.xyformsservices.constants.XyformsservicesConstants;
import org.apache.log4j.Logger;

public class XyformsservicesManager extends GeneratedXyformsservicesManager
{
	private static final Logger LOG = Logger.getLogger( XyformsservicesManager.class.getName() );
	
	public static final XyformsservicesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (XyformsservicesManager) em.getExtension(XyformsservicesConstants.EXTENSIONNAME);
	}
	
}
