/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.entitlementbackoffice.jalo;

import de.hybris.platform.entitlementbackoffice.constants.EntitlementbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class EntitlementbackofficeManager extends GeneratedEntitlementbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( EntitlementbackofficeManager.class.getName() );
	
	public static final EntitlementbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (EntitlementbackofficeManager) em.getExtension(EntitlementbackofficeConstants.EXTENSIONNAME);
	}
	
}
