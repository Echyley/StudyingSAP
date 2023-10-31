/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

public abstract class AbstractSnExpressionAndValueQuery extends AbstractSnExpressionQuery
{
	private Object value;

	public Object getValue()
	{
		return value;
	}

	public void setValue(final Object value)
	{
		this.value = value;
	}
}
