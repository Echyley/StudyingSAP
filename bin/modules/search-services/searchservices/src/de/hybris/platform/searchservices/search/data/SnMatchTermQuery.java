/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;


public class SnMatchTermQuery extends AbstractSnExpressionAndValueQuery
{
	public static final String TYPE = "matchTerm";

	@Override
	public String getType()
	{
		return TYPE;
	}
}
