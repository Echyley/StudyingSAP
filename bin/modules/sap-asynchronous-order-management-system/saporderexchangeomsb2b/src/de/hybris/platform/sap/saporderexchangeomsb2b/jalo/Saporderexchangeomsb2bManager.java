/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeomsb2b.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.saporderexchangeomsb2b.constants.Saporderexchangeomsb2bConstants;
import org.apache.log4j.Logger;

public class Saporderexchangeomsb2bManager extends GeneratedSaporderexchangeomsb2bManager
{
	private static Logger log = Logger.getLogger( Saporderexchangeomsb2bManager.class.getName() );
	
	public static final Saporderexchangeomsb2bManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Saporderexchangeomsb2bManager) em.getExtension(Saporderexchangeomsb2bConstants.EXTENSIONNAME);
	}
	
}
