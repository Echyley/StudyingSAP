/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integration.monitoring.backoffice.jalo;

import de.hybris.platform.integration.monitoring.backoffice.constants.IntegrationmonitoringbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class IntegrationmonitoringbackofficeManager extends GeneratedIntegrationmonitoringbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( IntegrationmonitoringbackofficeManager.class.getName() );
	
	public static final IntegrationmonitoringbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (IntegrationmonitoringbackofficeManager) em.getExtension(IntegrationmonitoringbackofficeConstants.EXTENSIONNAME);
	}
	
}
