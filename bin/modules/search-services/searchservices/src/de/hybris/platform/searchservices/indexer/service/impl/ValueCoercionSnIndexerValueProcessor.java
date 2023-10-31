/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProcessor;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;


/**
 * Implementation of {@link SnIndexerValueProvider} that adjusts the value based on the field configuration.
 */
public class ValueCoercionSnIndexerValueProcessor implements SnIndexerValueProcessor
{
	@Override
	public Object process(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper, final Object source)
			throws SnIndexerException
	{
		if (source == null)
		{
			return null;
		}

		if (fieldWrapper.isLocalized() || fieldWrapper.isQualified())
		{
			if (!(source instanceof Map))
			{
				throw new SnIndexerException(MessageFormat.format(
						"Field value is not compatible with localized/qualified fields, conversion is not possible [field={0}, value={1}, type={2}, supportedTypes={3}]",
						fieldWrapper.getFieldId(), source, source.getClass().getName(), Map.class.getName()));
			}

			final Map<Object, Object> target = new HashMap<>();

			for (final Entry<Object, Object> entry : ((Map<Object, Object>) source).entrySet())
			{
				final Object key = entry.getKey();
				final Object value = entry.getValue();
				final Object newValue = adjustToMultiValuedConfiguration(fieldWrapper, value);

				if (newValue != null)
				{
					target.put(key, newValue);
				}
			}

			return MapUtils.isNotEmpty(target) ? target : null;
		}
		else
		{
			if (source instanceof Map)
			{
				throw new SnIndexerException(MessageFormat.format(
						"Field value is not compatible with non localized/qualified fields, conversion is not possible [field={0}, value={1}, type={2}]",
						fieldWrapper.getFieldId(), source, source.getClass().getName()));
			}

			return adjustToMultiValuedConfiguration(fieldWrapper, source);
		}
	}

	protected Object adjustToMultiValuedConfiguration(final SnIndexerFieldWrapper fieldWrapper, final Object value)
	{
		if (value == null)
		{
			return null;
		}

		final boolean isCollection = value instanceof Collection;
		Object newValue = value;

		if (!isCollection && fieldWrapper.isMultiValued())
		{
			newValue = List.of(newValue);
		}
		else if (isCollection && !fieldWrapper.isMultiValued())
		{
			newValue = ((Collection) newValue).stream().filter(Objects::nonNull).findFirst().orElse(null);
		}
		else if (isCollection)
		{
			final List<Object> newCollectionValue = (List<Object>) ((Collection) newValue).stream().filter(Objects::nonNull)
					.collect(Collectors.toList());
			newValue = CollectionUtils.isNotEmpty(newCollectionValue) ? newCollectionValue : null;
		}

		return newValue;
	}
}
