/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;


import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.core.service.SnResponse;


/**
 * Base class for implementations of {@link SnResponse}.
 */
public abstract class AbstractSnResponse implements SnResponse
{
	private final SnIndexConfiguration indexConfiguration;
	private final SnIndexType indexType;

	protected AbstractSnResponse(final SnIndexConfiguration indexConfiguration, final SnIndexType indexType)
	{
		this.indexConfiguration = indexConfiguration;
		this.indexType = indexType;
	}

	@Override
	public SnIndexConfiguration getIndexConfiguration()
	{
		return indexConfiguration;
	}

	@Override
	public SnIndexType getIndexType()
	{
		return indexType;
	}
}
