/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProcessor;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;
import de.hybris.platform.searchservices.util.ParameterUtils;

import java.util.Collection;
import java.util.Map;


/**
 * Implementation of {@link SnIndexerValueProvider} that throws and exception for non optional fields without values.
 */
public class OptionalParamSnIndexerValueProcessor implements SnIndexerValueProcessor
{
	public static final String OPTIONAL_PARAM = "optional";
	public static final boolean OPTIONAL_PARAM_DEFAULT_VALUE = true;

	public static final String OPTIONAL_BLANK_STRING_PARAM = "optionalBlankString";
	public static final boolean OPTIONAL_BLANK_STRING_PARAM_DEFAULT_VALUE = true;

	@Override
	public Object process(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper, final Object source)
			throws SnIndexerException
	{
		final SnField field = fieldWrapper.getField();

		if (isOptional(fieldWrapper))
		{
			return source;
		}

		if (source == null)
		{
			throw new SnIndexerException("No value for field " + field.getId());
		}

		final boolean isOptionalBlankString = isOptionalBlankString(fieldWrapper);

		if (fieldWrapper.isLocalized() || fieldWrapper.isQualified())
		{
			final Map<Object, Object> mapSource = ((Map<Object, Object>) source);
			if (mapSource.isEmpty()
					|| mapSource.values().stream().allMatch(value -> isEmptyValue(fieldWrapper, value, isOptionalBlankString)))
			{
				throw new SnIndexerException("Empty value for field " + field.getId());
			}
		}
		else
		{
			if (isEmptyValue(fieldWrapper, source, isOptionalBlankString))
			{
				throw new SnIndexerException("Empty value for field " + field.getId());
			}
		}

		return source;
	}

	protected boolean isEmptyValue(final SnIndexerFieldWrapper fieldWrapper, final Object value,
			final boolean isOptionalBlankString)
	{
		if (value == null)
		{
			return true;
		}

		if (fieldWrapper.isMultiValued())
		{
			final Collection<Object> collectionValue = (Collection) value;
			return collectionValue.isEmpty()
					|| collectionValue.stream().allMatch(elementValue -> isEmptyValue(elementValue, isOptionalBlankString));
		}
		else
		{
			return isEmptyValue(value, isOptionalBlankString);
		}
	}

	protected boolean isEmptyValue(final Object value, final boolean isOptionalBlankString)
	{
		return value == null || (!isOptionalBlankString && (value instanceof String) && ((String) value).isBlank());
	}

	protected boolean isOptional(final SnIndexerFieldWrapper fieldWrapper)
	{
		return ParameterUtils.getBoolean(fieldWrapper.getValueProviderParameters(), OPTIONAL_PARAM, OPTIONAL_PARAM_DEFAULT_VALUE);
	}

	protected boolean isOptionalBlankString(final SnIndexerFieldWrapper fieldWrapper)
	{
		return ParameterUtils.getBoolean(fieldWrapper.getValueProviderParameters(), OPTIONAL_BLANK_STRING_PARAM,
				OPTIONAL_BLANK_STRING_PARAM_DEFAULT_VALUE);
	}
}
