/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/**
 * {@link HttpHeaderToMapConverter} is used to convert a {@link HttpHeaders} which is a {@link MultiValueMap} to a single-value map
 * which is normally used in building OData object's headers.
 */
public class HttpHeaderToMapConverter implements Converter<HttpHeaders, Map<String, String>>
{
	private static final String HEADER_VALUE_SEPARATOR = ",";

	/**
	 * Converts HttpHeaders, which contain a map with a list of Strings as values, into a map with single Strings as values
	 *
	 * @param headers the headers to be converted.
	 * @return a single-value map.
	 */
	@Override
	public Map<String, String> convert(final HttpHeaders headers)
	{
		return headers.entrySet()
		              .stream()
		              .collect(Collectors.toMap(
				              Map.Entry::getKey,
				              e -> convertToSingleValue(e.getValue())));
	}

	private String convertToSingleValue(final List<String> multiValues)
	{
		return multiValues
				.stream()
				.filter(StringUtils::isNotEmpty)
				.distinct()
				.collect(Collectors.joining(HEADER_VALUE_SEPARATOR));
	}
}
