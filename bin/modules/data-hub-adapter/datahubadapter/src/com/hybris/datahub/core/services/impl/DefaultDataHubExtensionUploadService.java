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

package com.hybris.datahub.core.services.impl;

import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

import com.hybris.datahub.client.ClientConfiguration;
import com.hybris.datahub.client.extension.ExtensionClient;
import com.hybris.datahub.client.extension.ExtensionSource;
import com.hybris.datahub.core.services.DataHubExtensionUploadService;


/**
 * This implementation of the extension upload service uses the DataHub's REST API to upload extensions to the DataHub
 * server.
 */
public class DefaultDataHubExtensionUploadService implements DataHubExtensionUploadService, InitializingBean
{
	private ExtensionClient client;
	private ClientConfiguration cfg = new ClientConfiguration();
	private String url;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		client = createClient(url);
	}

	@Override
	public void uploadExtension(final ExtensionSource extSrc) throws IOException
	{
		client().deployExtension(extSrc);
	}

	/**
	 * Specifies location of the DataHub to which the extensions will be deployed.
	 *
	 * @param url
	 *           a URL for the root of the DataHub REST API.
	 */
	@Required
	public void setDataHubUrl(final String url)
	{
		this.url = url;
	}

	/**
	 * Injects an optional Configuration for the underlaying rest client
	 * @param configuration
	 */
	public void setClientConfiguration(final ClientConfiguration configuration)
	{
		cfg = configuration;
	}

	/**
	 * Creates a client for communicating to the DataHub REST API
	 *
	 * @param url
	 *           a URL for the root of the DataHub REST API
	 * @return the client to use for communicating to the DataHub server.
	 */
	protected ExtensionClient createClient(final String url)
	{
		return new ExtensionClient(cfg, url);
	}

	protected ExtensionClient client()
	{
		Preconditions.checkState(client != null, "Call setDataHubUrl(String) method before using the client");
		return client;
	}


}
