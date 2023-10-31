/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapomsreturnprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

import com.sap.hybris.sapomsreturnprocess.constants.YsapomsreturnprocessConstants;

@SuppressWarnings("PMD")
public class YsapomsreturnprocessManager extends GeneratedYsapomsreturnprocessManager
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger( YsapomsreturnprocessManager.class.getName() );
	
	public static final YsapomsreturnprocessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsapomsreturnprocessManager) em.getExtension(YsapomsreturnprocessConstants.EXTENSIONNAME);
	}
	
}
