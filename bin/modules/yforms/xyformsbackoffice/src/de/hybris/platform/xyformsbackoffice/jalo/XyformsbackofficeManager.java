/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.xyformsbackoffice.constants.XyformsbackofficeConstants;
import org.apache.log4j.Logger;

public class XyformsbackofficeManager extends GeneratedXyformsbackofficeManager
{
	private static final Logger LOG = Logger.getLogger( XyformsbackofficeManager.class.getName() );

	public static final XyformsbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (XyformsbackofficeManager) em.getExtension(XyformsbackofficeConstants.EXTENSIONNAME);
	}
	
}
