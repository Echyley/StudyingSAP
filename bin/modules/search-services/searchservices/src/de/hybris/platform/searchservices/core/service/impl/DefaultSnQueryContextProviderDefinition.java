/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.core.service.impl;

import de.hybris.platform.searchservices.core.service.SnQueryContextProvider;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;


/**
 * Class that allows to register query context providers.
 */
public class DefaultSnQueryContextProviderDefinition implements Comparable<DefaultSnQueryContextProviderDefinition>
{
	private int priority;
	private SnQueryContextProvider queryContextProvider;

	/**
	 * Returns the priority.
	 *
	 * @return the priority
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * Sets the priority.
	 *
	 * @param priority
	 *           - the priority
	 */
	public void setPriority(final int priority)
	{
		this.priority = priority;
	}

	/**
	 * Returns the query context provider.
	 *
	 * @return the query context provider
	 */
	public SnQueryContextProvider getQueryContextProvider()
	{
		return queryContextProvider;
	}

	/**
	 * Sets the query context provider.
	 *
	 * @param queryContextProvider
	 *           - the query context provider
	 */
	public void setQueryContextProvider(final SnQueryContextProvider queryContextProvider)
	{
		this.queryContextProvider = queryContextProvider;
	}

	@Override
	public int compareTo(final DefaultSnQueryContextProviderDefinition that)
	{
		return Integer.compare(that.getPriority(), this.getPriority());
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null || this.getClass() != obj.getClass())
		{
			return false;
		}

		final DefaultSnQueryContextProviderDefinition that = (DefaultSnQueryContextProviderDefinition) obj;
		return new EqualsBuilder().append(this.priority, that.priority).append(this.queryContextProvider, that.queryContextProvider)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.queryContextProvider, this.priority);
	}
}
