/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

public class SnExistsQuery extends AbstractSnExpressionQuery
{
	public static final String TYPE = "exists";

	@Override
	public String getType()
	{
		return TYPE;
	}
}
