/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.returnsexchangeb2b.jalo;

import com.sap.hybris.returnsexchangeb2b.constants.Sapreturnsexchangeb2bConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class Sapreturnsexchangeb2bManager extends GeneratedSapreturnsexchangeb2bManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( Sapreturnsexchangeb2bManager.class.getName() );
	
	public static final Sapreturnsexchangeb2bManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapreturnsexchangeb2bManager) em.getExtension(Sapreturnsexchangeb2bConstants.EXTENSIONNAME);
	}
	
}
