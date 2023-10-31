/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;


/**
 * Converter utilities.
 */
public final class ConverterUtils
{
	private ConverterUtils()
	{
		// utility class
	}

	/**
	 * Convert a single source value using the converter function.
	 *
	 * @param <S>
	 *           - source type
	 * @param <T>
	 *           - target type
	 * @param source
	 *           - source value
	 * @param converter
	 *           - converter function
	 *
	 * @return converted value
	 */
	public static final <S, T> T convert(final S source, final Function<S, T> converter)
	{
		if (source == null)
		{
			return null;
		}

		return converter.apply(source);
	}

	/**
	 * Convert a single source value using the converter function and an additional context.
	 *
	 * @param <C>
	 *           - context type
	 * @param <S>
	 *           - source type
	 * @param <T>
	 *           - target type
	 * @param context
	 *           - context
	 * @param source
	 *           - source value
	 * @param converter
	 *           - converter function
	 *
	 * @return converted value
	 */
	public static final <C, S, T> T convert(final C context, final S source, final BiFunction<C, S, T> converter)
	{
		if (source == null)
		{
			return null;
		}

		return converter.apply(context, source);
	}

	/**
	 * Convert a collection of source values using the converter function.
	 *
	 * @param <S>
	 *           - source type
	 * @param <T>
	 *           - target type
	 * @param source
	 *           - source value
	 * @param converter
	 *           - converter function
	 *
	 * @return list of converted values
	 */
	public static final <S, T> List<T> convertAll(final Collection<? extends S> source, final Function<S, T> converter)
	{
		if (source == null || source.isEmpty())
		{
			return Collections.emptyList();
		}

		return source.stream().map(converter::apply).collect(Collectors.toList());
	}

	/**
	 * Convert a collection of source value using the converter function and an additional context.
	 *
	 * @param <C>
	 *           - context type
	 * @param <S>
	 *           - source type
	 * @param <T>
	 *           - target type
	 * @param context
	 *           - context
	 * @param source
	 *           - source value
	 * @param converter
	 *           - converter function
	 *
	 * @return list of converted values
	 */
	public static final <P, S, T> List<T> convertAll(final P first, final Collection<S> source,
			final BiFunction<P, S, T> converter)
	{
		return CollectionUtils.isEmpty(source) ? Collections.emptyList()
				: source.stream().map(s -> converter.apply(first, s)).collect(Collectors.toList());
	}

	/**
	 * Convert a collection of source values using the converter function.
	 *
	 * @param <S>
	 *           - source type
	 * @param <T>
	 *           - target type
	 * @param source
	 *           - source value
	 * @param converter
	 *           - converter function
	 * @param filter
	 *           - filter
	 *
	 * @return list of converted values
	 */
	public static final <S, T> List<T> convertAll(final Collection<? extends S> source, final Function<S, T> converter,
			final Predicate<? super S> filter)
	{
		if (source == null || source.isEmpty())
		{
			return Collections.emptyList();
		}

		return source.stream().filter(filter).map(converter::apply).collect(Collectors.toList());
	}
}
