/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.timedaccesspromotionenginebackoffice.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.timedaccesspromotionenginebackoffice.constants.TimedaccesspromotionenginebackofficeConstants;
import org.apache.log4j.Logger;

public class TimedaccesspromotionenginebackofficeManager extends GeneratedTimedaccesspromotionenginebackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( TimedaccesspromotionenginebackofficeManager.class.getName() );
	
	public static final TimedaccesspromotionenginebackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (TimedaccesspromotionenginebackofficeManager) em.getExtension(TimedaccesspromotionenginebackofficeConstants.EXTENSIONNAME);
	}
	
}
