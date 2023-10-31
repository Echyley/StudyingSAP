/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.batch;

import de.hybris.platform.odata2services.odata.impl.ODataBatchParsingException;
import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO;
import de.hybris.platform.outboundservices.service.MultiPartRequestGenerator;
import de.hybris.platform.outboundservices.service.OutboundMultiPartRequestConsolidator;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Preconditions;

/**
 * Generates an OData2 batch request from a list of individual requests. Batch Request has the certain Content-type that requires a boundary
 * parameter.
 */
public class DefaultBatchRequestGenerator implements MultiPartRequestGenerator
{
	private static final String CONTENT_TYPE = HttpHeaders.CONTENT_TYPE;
	private static final String CONTENT_TYPE_TEMPLATE = "multipart/mixed; boundary=%s";
	private static final String BODY_BOUNDARY_PREFIX = "batch_";

	private final OutboundMultiPartRequestConsolidator consolidator;

	/**
	 * Constructor of {@link DefaultBatchRequestGenerator}
	 * @param requestConsolidator used to consolidate multiple individual requests into one payload.
	 */
	public DefaultBatchRequestGenerator(@NotNull final OutboundMultiPartRequestConsolidator requestConsolidator)
	{
		Preconditions.checkArgument(requestConsolidator != null, "Multi-Part request consolidator must be provided");
		this.consolidator = requestConsolidator;
	}

	/**
	 * Generates an OData2 batch http request from a list of {@link OutboundBatchRequestPartDTO} that represent individual http requests.
	 *
	 * @param requestDTOs List of DTOs that contain each individual request and their model type
	 * @return {@link HttpEntity<String>} containing the String body of the batch request payload and the necessary headers
	 */
	@Override
	public HttpEntity<String> generate(final List<OutboundBatchRequestPartDTO> requestDTOs)
	{
		final String batchBoundary = generateBoundary();
		final HttpHeaders headers = generateHttpHeaderForBatchRequest(requestDTOs, batchBoundary);
		final String body = consolidator.consolidate(requestDTOs, batchBoundary);
		return new HttpEntity<>(body, headers);
	}

	private String generateBoundary()
	{
		return BODY_BOUNDARY_PREFIX + UUID.randomUUID();
	}

	private HttpHeaders generateHttpHeaderForBatchRequest(final List<OutboundBatchRequestPartDTO> requestDTOs,
	                                                      final String boundary)
	{
		final HttpHeaders newHeaders = new HttpHeaders();
		newHeaders.addAll(getFirstDTO(requestDTOs).getHttpEntity().getHeaders());
		newHeaders.set(CONTENT_TYPE, CONTENT_TYPE_TEMPLATE.formatted(boundary));
		return newHeaders;
	}

	private OutboundBatchRequestPartDTO getFirstDTO(final List<OutboundBatchRequestPartDTO> requestDTOs)
	{
		return requestDTOs.stream().findFirst().orElseThrow(ODataBatchParsingException::new);
	}
}
