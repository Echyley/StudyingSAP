/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.facades;

import com.hybris.datahub.core.dto.ItemImportTaskData;

/**
 * Facade for scheduling the ImpexImport via the TaskService
 */
public interface ItemImportTaskRunningFacade
{
	/**
	 * Schedule an import task.
	 * @param itemImportTask the dto containing details that will be used by the ImpexImport
	 */
	void scheduleImportTask(ItemImportTaskData itemImportTask);
}
