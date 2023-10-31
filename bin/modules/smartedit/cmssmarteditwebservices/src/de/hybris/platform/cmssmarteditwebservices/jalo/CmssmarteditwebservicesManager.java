/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;

public class CmssmarteditwebservicesManager extends GeneratedCmssmarteditwebservicesManager
{

	public static final CmssmarteditwebservicesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CmssmarteditwebservicesManager) em.getExtension(CmssmarteditwebservicesConstants.EXTENSIONNAME);
	}
	
}
