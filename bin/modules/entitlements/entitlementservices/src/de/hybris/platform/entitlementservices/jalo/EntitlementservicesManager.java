/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.entitlementservices.jalo;

import de.hybris.platform.entitlementservices.constants.EntitlementservicesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class EntitlementservicesManager extends GeneratedEntitlementservicesManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( EntitlementservicesManager.class.getName() );
	
	public static final EntitlementservicesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (EntitlementservicesManager) em.getExtension(EntitlementservicesConstants.EXTENSIONNAME);
	}
	
}
