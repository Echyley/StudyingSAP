/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.jalo;

import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

public class CmswebservicesManager extends GeneratedCmswebservicesManager
{
	public static final CmswebservicesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CmswebservicesManager) em.getExtension(CmswebservicesConstants.EXTENSIONNAME);
	}
	
}
