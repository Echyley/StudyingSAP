/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.datahubbackoffice.service.datahub;

import de.hybris.platform.datahubbackoffice.exception.NoDataHubInstanceAvailableException;

/**
 * A special case representing a {@code DataHubServer} that is inaccessible. 
 */
public class InaccessibleDataHubServer extends DataHubServer
{
	private static final long serialVersionUID = 7712538865093121346L;
	private static final DataHubServerInfo INFO = new DataHubServerInfo("No Access", "localhost", "", "");

	public InaccessibleDataHubServer()
	{
		super(INFO);
	}

	@Override
	public boolean isAccessibleWithTimeout()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @throws NoDataHubInstanceAvailableException for every method invocation.
	 */
	@Override
	public String getLocation()
	{
		throw new NoDataHubInstanceAvailableException();
	}
}
