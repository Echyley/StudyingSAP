/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationaddon.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.personalizationaddon.constants.PersonalizationaddonConstants;

import org.apache.log4j.Logger;


public class PersonalizationaddonManager extends GeneratedPersonalizationaddonManager
{
	private static final Logger LOG = Logger.getLogger(PersonalizationaddonManager.class.getName());

	public static PersonalizationaddonManager getInstance()
	{
		return (PersonalizationaddonManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(PersonalizationaddonConstants.EXTENSIONNAME);
	}
}
