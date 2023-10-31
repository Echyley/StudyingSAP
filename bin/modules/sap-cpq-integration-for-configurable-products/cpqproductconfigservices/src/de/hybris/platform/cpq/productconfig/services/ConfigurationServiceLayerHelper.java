/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;


/**
 * helper class providing common operations for the servie layer of sap.hybris.cpq module
 */
public interface ConfigurationServiceLayerHelper
{

	/**
	 * extracts the CPQ related product infos from the given entry
	 *
	 * @param entry
	 *           entry
	 * @return CPQ related product infos, or null if not existing
	 */
	CloudCPQOrderEntryProductInfoModel getCPQInfo(AbstractOrderEntryModel entry);


	/**
	 * Executes an action ensuring that that base site of the provided document is set as current base site.<br>
	 * After action execution, the previous current base site is restored.
	 *
	 * @param order
	 *           document providing the base site
	 * @param action
	 *           action to execute
	 */
	public void ensureBaseSiteSetAndExecuteConfigurationAction(final AbstractOrderModel order,
			final Consumer<BaseSiteModel> action);


	/**
	 * @param searchFunction
	 *           function to deliver pageable results
	 * @param searchResultConsumer
	 *           consumer for processing the results page wise
	 */
	<T> void processPageWise(final IntFunction<SearchPageData<T>> searchFunction, final Consumer<List<T>> searchResultConsumer);


	/**
	 * Retrieves order user if this case is relevant for impersonation
	 *
	 * @param entry
	 *                 abstract order entry
	 * @return order user
	 */
	default UserModel retrieveUserForAbstractOrderEntryIfRelevant(final AbstractOrderEntryModel entry)
	{
		return null;
	}

}
