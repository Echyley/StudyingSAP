/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.util;

import de.hybris.platform.searchservices.core.SnRuntimeException;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.text.StringSubstitutor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * JSON utilities.
 */
public final class JsonUtils
{
	private static final String NON_NULL_VALUE_TYPE_MESSAGE = "valueType cannot be null";
	private static final String NON_NULL_RESOURCE_MESSAGE = "resource cannot be null";

	private static final char VAR_ESCAPE = '\\';

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static
	{
		objectMapper.registerModule(new JsonModule());
	}

	private JsonUtils()
	{
		// utility class
	}

	/**
	 * Converts an object to a JSON string.
	 *
	 * @param source
	 *           - the source object
	 *
	 * @return a JSON string
	 */
	public static final String toJson(final Object source)
	{
		if (source == null)
		{
			return null;
		}

		try
		{
			return objectMapper.writeValueAsString(source);
		}
		catch (final IOException e)
		{
			throw new SnRuntimeException(e);
		}
	}

	/**
	 * Converts a JSON string to an object.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param source
	 *           - the source JSON string
	 * @param valueType
	 *           - the value type of the target object
	 *
	 * @return an object
	 */
	public static final <T> T fromJson(final String source, final Class<T> valueType)
	{
		return fromJson(source, valueType, null);
	}

	/**
	 * Converts a JSON string to an object, using variable substitution.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param source
	 *           - the source JSON string
	 * @param valueType
	 *           - the value type of the target object
	 * @param parameters
	 *           - the parameters used for variable substitution
	 *
	 * @return an object
	 */
	public static final <T> T fromJson(final String source, final Class<T> valueType, final Map<String, String> parameters)
	{
		Objects.requireNonNull(valueType, NON_NULL_VALUE_TYPE_MESSAGE);

		return fromJson(source, objectMapper.getTypeFactory().constructType(valueType), parameters);
	}

	/**
	 * Converts a JSON string to an object.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param source
	 *           - the source JSON string
	 * @param valueType
	 *           - the value type of the target object
	 *
	 * @return an object
	 */
	public static final <T> T fromJson(final String source, final TypeReference<T> valueType)
	{

		return fromJson(source, valueType, null);
	}

	/**
	 * Converts a JSON string to an object, using variable substitution.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param source
	 *           - the source JSON string
	 * @param valueType
	 *           - the value type of the target object
	 * @param parameters
	 *           - the parameters used for variable substitution
	 *
	 * @return an object
	 */
	public static final <T> T fromJson(final String source, final TypeReference<T> valueType, final Map<String, String> parameters)
	{
		Objects.requireNonNull(valueType, NON_NULL_VALUE_TYPE_MESSAGE);

		return fromJson(source, objectMapper.getTypeFactory().constructType(valueType), parameters);
	}

	/**
	 * Converts a JSON string to an object, using variable substitution.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param source
	 *           - the source JSON string
	 * @param valueType
	 *           - the value type of the target object
	 * @param parameters
	 *           - the parameters used for variable substitution
	 *
	 * @return an object
	 */
	private static final <T> T fromJson(final String source, final JavaType valueType, final Map<String, String> parameters)
	{
		Objects.requireNonNull(valueType, NON_NULL_VALUE_TYPE_MESSAGE);

		if (source == null)
		{
			return null;
		}

		final String input = MapUtils.isEmpty(parameters) ? source
				: (new StringSubstitutor(parameters, StringSubstitutor.DEFAULT_VAR_START, StringSubstitutor.DEFAULT_VAR_END,
						VAR_ESCAPE)).replace(source);

		try
		{
			return objectMapper.readValue(input, valueType);
		}
		catch (final IOException e)
		{
			throw new SnRuntimeException(e);
		}
	}

	/**
	 * Loads a JSON string from a file and converts it to an object.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param resource
	 *           - the resource that contains the JSON string
	 * @param valueType
	 *           - the value type of the target object
	 *
	 * @return an object
	 */
	public static final <T> T loadJson(final String resource, final Class<T> valueType)
	{
		return loadJson(resource, valueType, null);
	}

	/**
	 * Loads a JSON string from a file and converts it to an object, using variable substitution.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param resource
	 *           - the resource that contains the JSON string
	 * @param valueType
	 *           - the value type of the target object
	 * @param parameters
	 *           - the parameters used for variable substitution
	 *
	 * @return an object
	 */
	public static final <T> T loadJson(final String resource, final Class<T> valueType, final Map<String, String> parameters)
	{
		Objects.requireNonNull(resource, NON_NULL_RESOURCE_MESSAGE);
		Objects.requireNonNull(valueType, NON_NULL_VALUE_TYPE_MESSAGE);

		final URL url = JsonUtils.class.getResource(resource);
		final String source = IOUtils.toString(url);
		return fromJson(source, valueType, parameters);
	}

	/**
	 * Loads a JSON string from a file and converts it to an object.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param resource
	 *           - the resource that contains the JSON string
	 * @param valueType
	 *           - the value type of the target object
	 *
	 * @return an object
	 */
	public static final <T> T loadJson(final String resource, final TypeReference<T> valueType)
	{
		return loadJson(resource, valueType, null);
	}

	/**
	 * Loads a JSON string from a file and converts it to an object, using variable substitution.
	 *
	 * @param <T>
	 *           - the type of the target object
	 * @param resource
	 *           - the resource that contains the JSON string
	 * @param valueType
	 *           - the value type of the target object
	 * @param parameters
	 *           - the parameters used for variable substitution
	 *
	 * @return an object
	 */
	public static final <T> T loadJson(final String resource, final TypeReference<T> valueType,
			final Map<String, String> parameters)
	{
		Objects.requireNonNull(resource, NON_NULL_RESOURCE_MESSAGE);
		Objects.requireNonNull(valueType, NON_NULL_VALUE_TYPE_MESSAGE);

		final URL url = JsonUtils.class.getResource(resource);
		final String source = IOUtils.toString(url);
		return fromJson(source, valueType, parameters);
	}
}
