/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dao;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;

import java.util.Collection;
import java.util.Optional;

import com.hybris.merchandising.model.MerchSnConfigModel;

/**
 * The DAO for {@link MerchSnConfigModel}
 */
public interface MerchSnConfigDao
{
	/**
	 * Finds all merchandising configurations.
	 *
	 * @return list of merchandising configurations or empty list if no configuration is found
	 */
	Collection<MerchSnConfigModel> findAll();

	/**
	 * Finds the merchandising configuration for a specific index type.
	 *
	 * @param indexType the index type
	 * @return the merchandising configuration
	 */
	Optional<MerchSnConfigModel> findByIndexedType(final SnIndexTypeModel indexType);

	/**
	 * Finds merchandising configuration for specific base site
	 *
	 * @param baseSite base site
	 * @return the merchandising configuration
	 */
	Optional<MerchSnConfigModel> findByBaseSite(final BaseSiteModel baseSite);
}
