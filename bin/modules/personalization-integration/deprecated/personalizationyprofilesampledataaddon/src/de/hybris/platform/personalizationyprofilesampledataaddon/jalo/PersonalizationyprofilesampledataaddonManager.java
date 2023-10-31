/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofilesampledataaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.personalizationyprofilesampledataaddon.constants.PersonalizationyprofilesampledataaddonConstants;

import org.apache.log4j.Logger;

public class PersonalizationyprofilesampledataaddonManager extends GeneratedPersonalizationyprofilesampledataaddonManager
{
	private static final Logger LOG = Logger.getLogger(PersonalizationyprofilesampledataaddonManager.class.getName());

	public static final PersonalizationyprofilesampledataaddonManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (PersonalizationyprofilesampledataaddonManager) em.getExtension(PersonalizationyprofilesampledataaddonConstants.EXTENSIONNAME);
	}
	
}
