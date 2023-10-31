/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service;

import java.util.Optional;

import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSynchronizationModel;

/**
 * Service that provides functionality for managing merchandising synchronization.
 */
public interface MerchSyncService
{
	/**
	 * Creates merchandising synchronization model
	 *
	 * @param config      merchandising configuration for which synchronization is running
	 * @param operationId operation identifier
	 * @param type        type of synchronization (e.g FULL, UPDATE)
	 * @return created synchronization model
	 */
	MerchSynchronizationModel createMerchSychronization(final MerchProductDirectoryConfigModel config, String operationId, final String type);


	/**
	 * Creates merchandising synchronization model
	 *
	 * @param config      merchandising configuration for which synchronization is running
	 * @param operationId operation identifier
	 * @param type        type of synchronization (e.g FULL, UPDATE)
	 * @return created synchronization model
	 */
	default MerchSynchronizationModel createMerchSychronization(final MerchSnConfigModel config, final String operationId, final String type)
	{
		return null;
	}

	/**
	 * Return merchandising synchronization with given operatio id
	 *
	 * @param operationId operation identifier
	 * @return optional of merchandisig synchronization
	 */
	Optional<MerchSynchronizationModel> getMerchSynchronization(String operationId);

	/**
	 * Method check if merchandising synchronization already failed
	 *
	 * @param operationId operatio identifier
	 * @return true if synchronization failed , false if synchronization not failed
	 */
	boolean isMerchSyncFailed(String operationId);

	/**
	 * Method complete synchronization process (e.g set proper end date and status)
	 *
	 * @param operationId      operation identifier
	 * @param numberOfProducts Number of synchronized products
	 */
	void completeMerchSyncProcess(String operationId, Long numberOfProducts);

	/**
	 * Save error information in synchronization process with given operation id
	 *
	 * @param operationId  operation identifier
	 * @param errorMessage error message
	 * @param exception    exception
	 */
	void saveErrorInfo(String operationId, String errorMessage, Exception exception);

}
