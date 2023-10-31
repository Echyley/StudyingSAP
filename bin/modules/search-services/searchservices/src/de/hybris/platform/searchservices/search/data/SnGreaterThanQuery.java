/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;


public class SnGreaterThanQuery extends AbstractSnExpressionAndValueQuery
{
	public static final String TYPE = "gt";

	@Override
	public String getType()
	{
		return TYPE;
	}
}
