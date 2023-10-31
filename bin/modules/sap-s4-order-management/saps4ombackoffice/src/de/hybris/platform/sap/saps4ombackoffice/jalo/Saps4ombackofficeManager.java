/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4ombackoffice.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.saps4ombackoffice.constants.Saps4ombackofficeConstants;
import org.apache.log4j.Logger;

public class Saps4ombackofficeManager extends GeneratedSaps4ombackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( Saps4ombackofficeManager.class.getName() );
	
	public static final Saps4ombackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Saps4ombackofficeManager) em.getExtension(Saps4ombackofficeConstants.EXTENSIONNAME);
	}
	
}
