/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.entitlementfacades.jalo;

import de.hybris.platform.entitlementfacades.constants.EntitlementfacadesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class EntitlementfacadesManager extends GeneratedEntitlementfacadesManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( EntitlementfacadesManager.class.getName() );
	
	public static final EntitlementfacadesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (EntitlementfacadesManager) em.getExtension(EntitlementfacadesConstants.EXTENSIONNAME);
	}
	
}
