/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.converter;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Preconditions;

/**
 * This class will convert a {@link ResponseEntity} to an {@link ODataResponse}. Normally the conversion happens when a OData service
 * or class will be used and an OData object is needed.
 */
public class DefaultResponseEntityToODataResponseConverter implements Converter<ResponseEntity<String>, ODataResponse>
{
	private final Converter<HttpHeaders, Map<String, String>> httpHeaderToMapConverter;

	public DefaultResponseEntityToODataResponseConverter(
			@NotNull final Converter<HttpHeaders, Map<String, String>> httpHeaderToMapConverter)
	{
		Preconditions.checkArgument(httpHeaderToMapConverter != null, "EntitySetNameGenerator can't be null.");
		this.httpHeaderToMapConverter = httpHeaderToMapConverter;
	}

	@Override
	public ODataResponse convert(final ResponseEntity<String> responseEntity)
	{
		final var builder = ODataResponse.newBuilder();

		builder.entity(responseEntity.getBody())
		       .status(HttpStatusCodes.fromStatusCode(responseEntity.getStatusCode().value()));
		final Map<String, String> singleValueMap = httpHeaderToMapConverter.convert(responseEntity.getHeaders());
		if (singleValueMap != null)
		{
			singleValueMap.keySet().forEach(key -> builder.header(key, singleValueMap.get(key)));
		}

		return builder.build();
	}
}
