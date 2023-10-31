/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.List;
import java.util.Map;


/**
 * Provides conflicting groups for given configuration model.
 */
public interface CPSConflictGroupHandler
{

	/**
	 * @param configModel
	 *                       configuration model for which conflicting groups has to be determined
	 * @return a map representing conflicting groups, where the map key is an instance id and the map value is a list of
	 *         requested group names
	 */
	Map<String, List<String>> retrieveNotCachedUniqueGroupIds(ConfigModel configModel);

	/**
	 * @param instanceId
	 *                       instance id the group belongs to
	 * @param groupName
	 *                       group name
	 * @param configModel
	 *                       configuration model the instance belongs to
	 * @return unique key for a UIGroup representing an Group
	 */
	String generateUniqueGroupName(String instanceId, String groupName, ConfigModel configModel);

}
