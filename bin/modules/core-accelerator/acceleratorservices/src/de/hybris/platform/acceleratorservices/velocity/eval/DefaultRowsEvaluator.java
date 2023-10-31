/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.platform.acceleratorservices.velocity.eval.key.CutOffEdgesKeyEvaluator;
import de.hybris.platform.acceleratorservices.velocity.eval.key.InvalidPropertyKeyFormatException;
import de.hybris.platform.acceleratorservices.velocity.eval.key.KeyEvaluator;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRowsEvaluator implements RowsEvaluator<StringEvaluatedRow, String>
{
	private static final int TYPE_INDEX = 0;
	private static final int KEY_INDEX = 1;
	private static final int VALUE_INDEX = 2;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultRowsEvaluator.class);

	private final Map<String, StringEvaluatedRow> container = new LinkedHashMap<>();

	private KeyEvaluator<String> keyEvaluator = new CutOffEdgesKeyEvaluator();

	private Comparator<StringEvaluatedRow> rowsComparator;

	protected Map<String, StringEvaluatedRow> getContainer()
	{
		return container;
	}

	@Override
	public Set<StringEvaluatedRow> evaluateRows(final Map<String, ?> properties)
	{
		final SortedSet<StringEvaluatedRow> result = new TreeSet<>(rowsComparator);
		for (final String keyProperty : properties.keySet())
		{
			//override a new evaluated row by removing old existing
			final EvaluatedRow<?, ? extends Map> singleRow = evaluateSingleRow(keyProperty, properties);
			if (result.contains(singleRow))
			{
				result.remove(singleRow);
			}
			result.add((StringEvaluatedRow) singleRow);
		}
		return result;
	}

	@Override
	public Set<StringEvaluatedRow> evaluateRows(final String key, final Map<String, ?> properties)
	{
		final SortedSet<StringEvaluatedRow> result = new TreeSet<>(rowsComparator);

		for (final String keyProperty : properties.keySet())
		{
			//override a new evaluated row by removing old existing
			final EvaluatedRow<?, ? extends Map> singleRow = evaluateSingleRow(key, keyProperty, properties);
			if (singleRow != null)
			{
				if (result.contains(singleRow))
				{
					result.remove(singleRow);
				}
				result.add((StringEvaluatedRow) singleRow);
			}
		}
		return result;
	}

	protected EvaluatedRow<?, ? extends Map> evaluateSingleRow(final String keyDiscriminator, final String keyProperty,
			final Map<String, ?> properties)
	{
		final String[] keyTokens = evaluateKey(keyProperty);
		if (keyTokens[TYPE_INDEX].equalsIgnoreCase(keyDiscriminator))
		{
			return mergeEvaluatedRowsIfNeeded(keyProperty, properties, keyTokens);
		}
		else
		{
			return null;
		}
	}

	protected EvaluatedRow<?, ? extends Map> evaluateSingleRow(final String keyProperty, final Map<String, ?> properties)
	{
		final String[] keyTokens = evaluateKey(keyProperty);

		return mergeEvaluatedRowsIfNeeded(keyProperty, properties, keyTokens);
	}

	private EvaluatedRow<?, ? extends Map> mergeEvaluatedRowsIfNeeded(final String keyProperty, final Map<String, ?> properties,
			final String[] keyTokens)
	{
		final StringEvaluatedRow resultEntry;
		final String key = keyTokens[KEY_INDEX];

		final EvaluatedRow<String, Map<String, String>> existingEntry = getContainer().get(key);
		if (existingEntry == null)
		{
			final Map values = evaluateValues(keyProperty, properties, keyTokens);
			getContainer().put(key, resultEntry = new StringEvaluatedRow(key, values));
			return resultEntry;
		}
		//merge policy here
		final Map mergedMap = new LinkedHashMap(existingEntry.getValues());
		mergedMap.putAll(evaluateValues(keyProperty, properties, keyTokens));

		resultEntry = new StringEvaluatedRow(key, Collections.unmodifiableMap(mergedMap));
		getContainer().put(key, resultEntry);
		return resultEntry;
	}

	private String[] evaluateKey(final String keyProperty)
	{
		final String[] keyTokens = getKeyEvaluator().eval(keyProperty);
		validateEntry(keyTokens);
		if (LOG.isTraceEnabled())
		{
			LOG.trace("Found property {}", keyProperty);
		}
		return keyTokens;
	}


	private Map<String, String> evaluateValues(final String keyProperty, final Map<String, ?> properties, final String[] keyTokens)
	{
		return Collections.singletonMap(keyTokens[VALUE_INDEX], (String) properties.get(keyProperty));
	}

	protected String[] validateEntry(final String[] keyTokens)
	{
		if (keyTokens.length != VALUE_INDEX + 1)
		{
			throw new InvalidPropertyKeyFormatException(
					"Inappropriate key tokens found. Expected pattern '(\\w+\\.)(\\w+\\.){1,}(\\w+)' (example product.code.name) but got a ["
							+ print(keyTokens) + "]");
		}
		return keyTokens;
	}

	private String print(final String[] tokens)
	{
		final StringBuilder builder = new StringBuilder();
		for (final String single : tokens)
		{
			builder.append(single);
		}
		return builder.toString();
	}

	@Resource
	public void setRowsComparator(final Comparator<StringEvaluatedRow> rowsComparator)
	{
		this.rowsComparator = rowsComparator;
	}


	public void setKeyEvaluator(final KeyEvaluator<String> keyEvaluator)
	{
		this.keyEvaluator = keyEvaluator;
	}

	protected KeyEvaluator<String> getKeyEvaluator()
	{
		return keyEvaluator;
	}
}
