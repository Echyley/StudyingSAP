/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import org.apache.log4j.Logger;

public class SaprevenuecloudorderManager extends GeneratedSaprevenuecloudorderManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( SaprevenuecloudorderManager.class.getName() );
	
	public static final SaprevenuecloudorderManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SaprevenuecloudorderManager) em.getExtension(SaprevenuecloudorderConstants.EXTENSIONNAME);
	}
	
}
