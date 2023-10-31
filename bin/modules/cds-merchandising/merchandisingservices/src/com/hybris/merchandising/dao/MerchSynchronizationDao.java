/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dao;

import java.util.Optional;

import com.hybris.merchandising.model.MerchSynchronizationModel;

/**
 * The {@link MerchSynchronizationModel} DAO.
 */
public interface MerchSynchronizationDao
{

	/**
	 * Finds merch synchronization model by operation identifier
	 *
	 * @param operationId operation identifier
	 * @return optional of merch synchronization model
	 */
	Optional<MerchSynchronizationModel> findByOperationId(String operationId);

}
