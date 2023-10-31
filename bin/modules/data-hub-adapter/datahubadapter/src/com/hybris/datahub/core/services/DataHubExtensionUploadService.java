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

package com.hybris.datahub.core.services;

import java.io.IOException;

import com.hybris.datahub.client.DataHubBlockedException;
import com.hybris.datahub.client.extension.ExtensionSource;
import com.hybris.datahub.client.extension.InvalidDataHubExtensionException;


/**
 * A service for deploying DataHub extensions to the server.
 */
public interface DataHubExtensionUploadService
{
	/**
	 * Uploads a DataHub extension to the DataHub server.
	 *
	 * @param extSrc
	 *           a source of the extension definition.
	 * @throws IOException
	 *            when failed to read extension definition from the source
	 * @throws DataHubBlockedException
	 *            if extension cannot be uploaded now due to DataHub performing some uninterruptable operation. If this
	 *            exception received, the extension deployment should be attempted later.
	 * @throws InvalidDataHubExtensionException
	 *            if the extension defined in the source is invalid or it's not in XML format or the XML is malformed.
	 */
	void uploadExtension(final ExtensionSource extSrc) throws IOException;
}
