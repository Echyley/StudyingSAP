/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smartedit.facade;

import java.util.Map;


/**
 * Facade used to retrieve specific values from the project.properties file
 */
public interface SettingsFacade
{
	/**
	 * @return Values from the project.properties file for the following keys:
	 * - smartedit.validFileMimeTypeCodes
	 * - smartedit.sso.enabled
	 * - smartedit.globalBasePath
	 */
	Map<String, Object> getSettings();
}
