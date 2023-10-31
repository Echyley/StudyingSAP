/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package ywebservicespackage.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

import ywebservicespackage.constants.YWebServicesConstants;


public class YWebServicesManager extends GeneratedYWebServicesManager
{
	public static final YWebServicesManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YWebServicesManager) em.getExtension(YWebServicesConstants.EXTENSIONNAME);
	}

}
