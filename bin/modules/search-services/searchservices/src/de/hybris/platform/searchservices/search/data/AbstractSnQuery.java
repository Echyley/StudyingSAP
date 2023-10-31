/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

import de.hybris.platform.searchservices.util.JsonUtils;


public abstract class AbstractSnQuery
{
	public abstract String getType();

	@Override
	public String toString()
	{
		return JsonUtils.toJson(this);
	}
}
