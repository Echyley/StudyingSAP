/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;
import org.apache.log4j.Logger;

public class SapmodelManager extends GeneratedSapmodelManager
{
	private static Logger log = Logger.getLogger( SapmodelManager.class.getName() );
	
	public static final SapmodelManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapmodelManager) em.getExtension(SapmodelConstants.EXTENSIONNAME);
	}
	
}
