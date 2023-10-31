/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentbackoffice.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sapdigitalpaymentbackoffice.constants.SapdigitalpaymentbackofficeConstants;
import org.apache.log4j.Logger;

public class SapdigitalpaymentbackofficeManager extends GeneratedSapdigitalpaymentbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SapdigitalpaymentbackofficeManager.class.getName() );
	
	public static final SapdigitalpaymentbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapdigitalpaymentbackofficeManager) em.getExtension(SapdigitalpaymentbackofficeConstants.EXTENSIONNAME);
	}
	
}
