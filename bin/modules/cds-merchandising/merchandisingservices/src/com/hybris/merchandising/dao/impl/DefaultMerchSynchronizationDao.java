/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Map;
import java.util.Optional;

import com.hybris.merchandising.dao.MerchSynchronizationDao;
import com.hybris.merchandising.model.MerchSynchronizationModel;

/**
 * Default implementation of {@link MerchSynchronizationDao}
 */
public class DefaultMerchSynchronizationDao extends DefaultGenericDao<MerchSynchronizationModel> implements MerchSynchronizationDao
{
	/**
	 * Creates DAO for {@link MerchSynchronizationModel}.
	 */
	public DefaultMerchSynchronizationDao()
	{
		super(MerchSynchronizationModel._TYPECODE);
	}

	@Override
	public Optional<MerchSynchronizationModel> findByOperationId(final String operationId)
	{
		final Map<String, Object> queryParams = Map.of(MerchSynchronizationModel.OPERATIONID, operationId);
		return find(queryParams).stream().findFirst();
	}
}
