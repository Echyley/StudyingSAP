/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import java.util.Map;

/**
 * Default {@link String} representation of {@link EvaluatedRow}. Assumes that key is a single value string.
 */
public class StringEvaluatedRow implements EvaluatedRow<String, Map<String, String>>, Comparable<StringEvaluatedRow>
{
	private final String key;
	private final Map<String, String> value;

	public StringEvaluatedRow(final String key, final Map<String, String> value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public Map<String, String> getValues()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return String.format("<<%s>> value [%s]", key, value);
	}

	@Override
	public int hashCode()
	{
		return getKey().hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() == obj.getClass()) {
			return getKey().equals(((StringEvaluatedRow) obj).getKey());
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(final StringEvaluatedRow given)
	{
		return getKey().compareTo(given.getKey());
	}
}
