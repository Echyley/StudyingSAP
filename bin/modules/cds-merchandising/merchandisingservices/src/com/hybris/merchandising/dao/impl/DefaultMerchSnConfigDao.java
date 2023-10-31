/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dao.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hybris.merchandising.dao.MerchSnConfigDao;
import com.hybris.merchandising.model.MerchSnConfigModel;


/**
 * Default implementation of {@link MerchSnConfigDao}.
 */
public class DefaultMerchSnConfigDao extends DefaultGenericDao<MerchSnConfigModel> implements MerchSnConfigDao
{
	/**
	 * Creates DAO for {@link MerchSnConfigModel}.
	 */
	public DefaultMerchSnConfigDao()
	{
		super(MerchSnConfigModel._TYPECODE);
	}

	@Override
	public Collection<MerchSnConfigModel> findAll()
	{
		return find();
	}

	@Override
	public Optional<MerchSnConfigModel> findByIndexedType(final SnIndexTypeModel indexedType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("indexedType", indexedType);

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(MerchSnConfigModel.SNINDEXTYPE, indexedType);

		final List<MerchSnConfigModel> merchSnConfigs = find(queryParams);

		return Optional.ofNullable(merchSnConfigs)
		               .filter(config -> !config.isEmpty())
		               .map(pdConfig -> pdConfig.get(0));
	}

	@Override
	public Optional<MerchSnConfigModel> findByBaseSite(final BaseSiteModel baseSite)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("baseSite", baseSite);

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(MerchSnConfigModel.BASESITE, baseSite);

		final List<MerchSnConfigModel> merchSnConfigs = find(queryParams);

		return Optional.ofNullable(merchSnConfigs)
		               .filter(config -> !config.isEmpty())
		               .map(pdConfig -> pdConfig.get(0));
	}
}
