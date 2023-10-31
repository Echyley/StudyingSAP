/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.enums.SnFieldType;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProcessor;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;
import de.hybris.platform.searchservices.util.ParameterUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Implementation of {@link SnIndexerValueProvider} that allows to split string values.
 */
public class SplitParamSnIndexerValueProcessor implements SnIndexerValueProcessor
{
	public static final String SPLIT_PARAM = "split";
	public static final boolean SPLIT_PARAM_DEFAULT_VALUE = false;

	public static final String SPLIT_REGEX_PARAM = "splitRegex";
	public static final String SPLIT_REGEX_PARAM_DEFAULT_VALUE = "\\s+";

	@Override
	public Object process(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper, final Object source)
			throws SnIndexerException
	{
		if (source == null || !isSplit(fieldWrapper))
		{
			return source;
		}

		final SnField field = fieldWrapper.getField();

		if (field.getFieldType() != SnFieldType.STRING && field.getFieldType() != SnFieldType.TEXT)
		{
			throw new SnIndexerException("Split can only be applied on fields of type STRING or TEXT");
		}

		final String splitRegex = resolveSplitRegex(fieldWrapper);
		final Pattern splitPattern = Pattern.compile(splitRegex);

		if (fieldWrapper.isLocalized() || fieldWrapper.isQualified())
		{
			final Map<Object, Object> target = new HashMap<>((Map<Object, Object>) source);
			for (final Entry<Object, Object> entry : target.entrySet())
			{
				final Object value = entry.getValue();
				final Object newValue = split(fieldWrapper, value, splitPattern);

				if (newValue != value)
				{
					entry.setValue(newValue);
				}
			}

			return target;
		}
		else
		{
			return split(fieldWrapper, source, splitPattern);
		}
	}

	protected Object split(final SnIndexerFieldWrapper fieldWrapper, final Object value, final Pattern splitPattern)
	{
		if (value == null)
		{
			return null;
		}

		if (fieldWrapper.isMultiValued())
		{
			final Collection<Object> collectionValue = (Collection) value;
			return collectionValue.stream().flatMap(v -> splitToStream(v, splitPattern)).collect(Collectors.toList());
		}
		else
		{
			return splitToSingleValue(value, splitPattern);
		}
	}

	protected Stream<Object> splitToStream(final Object value, final Pattern splitPattern)
	{
		if (value instanceof String)
		{
			return Arrays.stream(splitPattern.split((String) value));
		}
		else
		{
			return Stream.of(value);
		}
	}

	protected Object splitToSingleValue(final Object value, final Pattern splitPattern)
	{
		if (value instanceof String)
		{
			final String[] result = splitPattern.split((String) value);
			if (result.length == 0)
			{
				return null;
			}
			else
			{
				return result[0];
			}
		}
		else
		{
			return value;
		}
	}

	protected boolean isSplit(final SnIndexerFieldWrapper fieldWrapper)
	{
		return ParameterUtils.getBoolean(fieldWrapper.getValueProviderParameters(), SPLIT_PARAM, SPLIT_PARAM_DEFAULT_VALUE);
	}

	protected String resolveSplitRegex(final SnIndexerFieldWrapper fieldWrapper)
	{
		return ParameterUtils.getString(fieldWrapper.getValueProviderParameters(), SPLIT_REGEX_PARAM,
				SPLIT_REGEX_PARAM_DEFAULT_VALUE);
	}
}
