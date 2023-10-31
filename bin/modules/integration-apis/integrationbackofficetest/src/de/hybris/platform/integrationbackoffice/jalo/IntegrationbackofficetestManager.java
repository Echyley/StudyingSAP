/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.jalo;

import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficetestConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class IntegrationbackofficetestManager extends GeneratedIntegrationbackofficetestManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( IntegrationbackofficetestManager.class.getName() );
	
	public static final IntegrationbackofficetestManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (IntegrationbackofficetestManager) em.getExtension(IntegrationbackofficetestConstants.EXTENSIONNAME);
	}
	
}
