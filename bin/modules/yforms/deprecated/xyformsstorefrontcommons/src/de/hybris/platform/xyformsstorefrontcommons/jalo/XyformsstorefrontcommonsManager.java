/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsstorefrontcommons.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.xyformsstorefrontcommons.constants.XyformsstorefrontcommonsConstants;
import org.apache.log4j.Logger;

public class XyformsstorefrontcommonsManager extends GeneratedXyformsstorefrontcommonsManager
{
	private static final Logger LOG = Logger.getLogger( XyformsstorefrontcommonsManager.class.getName() );
	
	public static final XyformsstorefrontcommonsManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (XyformsstorefrontcommonsManager) em.getExtension(XyformsstorefrontcommonsConstants.EXTENSIONNAME);
	}
	
}
