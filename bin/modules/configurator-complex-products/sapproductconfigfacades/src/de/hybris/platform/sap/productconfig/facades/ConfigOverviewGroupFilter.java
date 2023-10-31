/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.Set;


/**
 * Handling group filtering.
 */
public interface ConfigOverviewGroupFilter
{
	/**
	 * Checks which groups are filtered out and which are displayed.
	 *
	 * @param instanceModel
	 *           current instance
	 * @param filteredOutgroups
	 *           already filtered out groups
	 * @return ids of the groups to be displayed
	 */
	Set<String> getGroupsToBeDisplayed(final InstanceModel instanceModel, final Set<String> filteredOutgroups);
}
