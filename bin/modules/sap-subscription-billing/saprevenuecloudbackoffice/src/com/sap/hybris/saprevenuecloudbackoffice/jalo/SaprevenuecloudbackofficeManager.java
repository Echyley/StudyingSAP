/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudbackoffice.jalo;

import com.sap.hybris.saprevenuecloudbackoffice.constants.SaprevenuecloudbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class SaprevenuecloudbackofficeManager extends GeneratedSaprevenuecloudbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SaprevenuecloudbackofficeManager.class.getName() );
	
	public static final SaprevenuecloudbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SaprevenuecloudbackofficeManager) em.getExtension(SaprevenuecloudbackofficeConstants.EXTENSIONNAME);
	}
	
}
