/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.search.data;

import java.util.Locale;


public abstract class AbstractSnExpressionQuery extends AbstractSnQuery
{
	private String expression;
	private Locale language;
	private String qualifierId;

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(final String expression)
	{
		this.expression = expression;
	}

	public Locale getLanguage()
	{
		return language;
	}

	public void setLanguage(final Locale language)
	{
		this.language = language;
	}

	public String getQualifierId()
	{
		return qualifierId;
	}

	public void setQualifierId(final String qualifierId)
	{
		this.qualifierId = qualifierId;
	}
}
