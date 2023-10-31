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
package com.hybris.datahub.core.config;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.impex.ImportConfig;

import com.hybris.datahub.core.dto.ItemImportTaskData;

public interface ImportConfigStrategy
{
	/**
	 * Creates an ImpEx ImportConfig
	 * @param ctx the context for the import config
	 * @return the newly created <code>ImportConfig</code>
	 * @throws ImpExException
	 */
	ImportConfig createImportConfig(final ItemImportTaskData ctx) throws ImpExException;
}
