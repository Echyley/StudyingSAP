/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapdpordermanagement.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.ysapdpordermanagement.constants.YsapdpordermanagementConstants;
import org.apache.log4j.Logger;

public class YsapdpordermanagementManager extends GeneratedYsapdpordermanagementManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( YsapdpordermanagementManager.class.getName() );
	
	public static final YsapdpordermanagementManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsapdpordermanagementManager) em.getExtension(YsapdpordermanagementConstants.EXTENSIONNAME);
	}
	
}
