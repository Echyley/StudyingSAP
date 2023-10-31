/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smartedit.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.smartedit.constants.SmarteditConstants;


public class SmarteditManager extends GeneratedSmarteditManager
{
	
	public static final SmarteditManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SmarteditManager) em.getExtension(SmarteditConstants.EXTENSIONNAME);
	}
	
}
