/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.azure.dtu.jalo;

import de.hybris.platform.azure.dtu.constants.AzureDtuConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

public class AzureDtuManager extends GeneratedAzureDtuManager
{
	public static final AzureDtuManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (AzureDtuManager) em.getExtension(AzureDtuConstants.EXTENSIONNAME);
	}
}
