/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapreturnprocess.jalo;

import com.sap.hybris.sapreturnprocess.constants.YsaperpreturnprocessConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class YsaperpreturnprocessManager extends GeneratedYsaperpreturnprocessManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( YsaperpreturnprocessManager.class.getName() );
	
	public static final YsaperpreturnprocessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsaperpreturnprocessManager) em.getExtension(YsaperpreturnprocessConstants.EXTENSIONNAME);
	}
	
}
