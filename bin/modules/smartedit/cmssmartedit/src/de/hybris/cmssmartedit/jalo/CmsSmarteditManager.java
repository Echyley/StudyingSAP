/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.cmssmartedit.jalo;

import de.hybris.cmssmartedit.constants.CmsSmarteditConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

public class CmsSmarteditManager extends GeneratedCmsSmarteditManager
{
	public static final CmsSmarteditManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CmsSmarteditManager) em.getExtension(CmsSmarteditConstants.EXTENSIONNAME);
	}
	
}
