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
package de.hybris.platform.dataimportcommons.facades.impl;

import de.hybris.platform.dataimportcommons.facades.DataItemImportResult;
import de.hybris.platform.servicelayer.impex.ImportResult;

/**
 * Converts <code>ImportResult</code> from the <code>ImportService</code> into an <code>DataItemImportResult</code> and
 * guarantees consistency of the item import result regardless of the import result version and implementation.
 */
public interface ImportResultConverter<T extends DataItemImportResult>
{
	/**
	 * Converts service import result to item import result
	 *
	 * @param importRes result received from the import service.
	 * @return import result data corresponding to the service import result.
	 */
	T convert(ImportResult importRes);
}
