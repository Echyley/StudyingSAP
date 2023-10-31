/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.batch;

import de.hybris.platform.odata2services.odata.impl.ODataBatchParsingException;
import de.hybris.platform.outboundservices.service.OutboundMultiPartResponseParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.client.batch.BatchSingleResponse;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.batch.v2.BatchParser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A default implementation of {@link OutboundMultiPartResponseParser}. The default olingo parser is used here, and it returns a list
 * of {@link BatchSingleResponse}. To use the olingo parser, the input {@link ResponseEntity} will be converted to {@link ODataResponse}.
 * To be used in other modules that don't have dependency on OData, the {@link ODataResponse} will be converted to {@link ResponseEntity} and returned.
 */
public class DefaultOutboundBatchResponseParser implements OutboundMultiPartResponseParser
{
	private static final String CONTENT_ID = "Content-ID";
	private final Converter<ResponseEntity<String>, ODataResponse> converter;

	public DefaultOutboundBatchResponseParser(@NotNull final Converter<ResponseEntity<String>, ODataResponse> converter)
	{
		Preconditions.checkArgument(converter != null, "ResponseEntity To ODataResponse converter can't be null.");
		this.converter = converter;
	}

	/**
	 * Parse a {@link ResponseEntity} as a batch response.
	 *
	 * @param responseEntity The response for the batch request sent out in outboundSync, could contain multiple batch parts
	 *                       which consist of multiple responses.
	 * @return a list of ResponseEntity that compose the batch response.
	 */
	@Override
	public List<ResponseEntity<Map>> parseMultiPartResponse(final ResponseEntity<String> responseEntity)
	{
		final ODataResponse response = converter.convert(responseEntity);
		if (response == null)
		{
			throw new ODataBatchParsingException();
		}
		final List<BatchSingleResponse> batchSingleResponses = parseBatchResponse(response);
		return batchSingleResponses.stream()
		                           .map(this::convert)
		                           .collect(Collectors.toUnmodifiableList());
	}

	private List<BatchSingleResponse> parseBatchResponse(final ODataResponse response)
	{
		final BatchParser batchParser = new BatchParser(response.getContentHeader(), true);
		try (final InputStream entityAsStream = response.getEntityAsStream())
		{
			return batchParser.parseBatchResponse(entityAsStream);
		}
		catch (final ODataException | IOException e)
		{
			throw new ODataBatchParsingException(e);
		}
	}

	private ResponseEntity<Map> convert(final BatchSingleResponse entity)
	{
		final Map<String, Object> entityBody;
		final Gson jsonToMapParser = new Gson();
		try
		{
			entityBody = jsonToMapParser.fromJson(entity.getBody(), Map.class);
		}
		catch (final JsonSyntaxException | ClassCastException e)
		{
			throw new ODataBatchParsingException(e);
		}
		final HttpHeaders headers = new HttpHeaders();
		headers.setAll(entity.getHeaders());
		headers.set(CONTENT_ID, entity.getContentId());
		return new ResponseEntity<>(entityBody, headers,
				Objects.requireNonNull(HttpStatus.resolve(Integer.parseInt(entity.getStatusCode()))));
	}
}
