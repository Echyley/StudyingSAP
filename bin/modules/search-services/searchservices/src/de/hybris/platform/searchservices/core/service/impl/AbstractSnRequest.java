/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;


import de.hybris.platform.searchservices.core.service.SnRequest;


/**
 * Base class for implementations of {@link SnRequest}.
 */
public abstract class AbstractSnRequest implements SnRequest
{
	private final String indexTypeId;

	protected AbstractSnRequest(final String indexTypeId)
	{
		this.indexTypeId = indexTypeId;
	}

	@Override
	public String getIndexTypeId()
	{
		return indexTypeId;
	}
}
