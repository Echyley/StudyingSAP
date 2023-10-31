/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.xyformsfacades.constants.XyformsfacadesConstants;
import org.apache.log4j.Logger;

public class XyformsfacadesManager extends GeneratedXyformsfacadesManager
{
	private static final Logger LOG = Logger.getLogger( XyformsfacadesManager.class.getName() );
	
	public static final XyformsfacadesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (XyformsfacadesManager) em.getExtension(XyformsfacadesConstants.EXTENSIONNAME);
	}
	
}
