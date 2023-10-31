/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsyncbackoffice.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.outboundsyncbackoffice.constants.OutboundsyncbackofficeConstants;
import org.apache.log4j.Logger;

public class OutboundsyncbackofficeManager extends GeneratedOutboundsyncbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( OutboundsyncbackofficeManager.class.getName() );
	
	public static final OutboundsyncbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (OutboundsyncbackofficeManager) em.getExtension(OutboundsyncbackofficeConstants.EXTENSIONNAME);
	}
	
}
