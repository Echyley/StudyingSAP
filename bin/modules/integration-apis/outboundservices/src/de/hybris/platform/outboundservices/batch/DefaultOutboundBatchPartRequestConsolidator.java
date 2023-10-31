/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.batch;

import de.hybris.platform.odata2services.odata.impl.ODataBatchParsingException;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;
import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO;
import de.hybris.platform.outboundservices.service.OutboundMultiPartRequestConsolidator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSet;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSetPart;
import org.apache.olingo.odata2.api.client.batch.BatchPart;
import org.apache.olingo.odata2.core.batch.BatchRequestWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

/**
 * A default implementation of {@link OutboundMultiPartRequestConsolidator}. The default olingo BatchRequestWriter is used here.
 * The consolidator returns a String representing the payload of the batch request. To use the olingo BatchRequestWriter, {@link OutboundBatchRequestPartDTO}
 * is needed which provides the required information needed to consolidate the requests.
 */
public class DefaultOutboundBatchPartRequestConsolidator implements OutboundMultiPartRequestConsolidator
{
	private final EntitySetNameGenerator entitySetNameGenerator;
	private final Converter<HttpHeaders, Map<String, String>> httpHeaderToMapConverter;
	private final Gson mapToStringConverter = new Gson();

	/**
	 * The constructor of the class.
	 *
	 * @param entitySetNameGenerator   used to get the entitySetName which will be written to the consolidated payload.
	 * @param httpHeaderToMapConverter used to convert a HttpHeader which is a multi-value map to a normal map.
	 */
	public DefaultOutboundBatchPartRequestConsolidator(@NotNull final EntitySetNameGenerator entitySetNameGenerator,
	                                                   @NotNull final Converter<HttpHeaders, Map<String, String>> httpHeaderToMapConverter)
	{
		Preconditions.checkArgument(entitySetNameGenerator != null, "EntitySetNameGenerator can't be null.");
		Preconditions.checkArgument(httpHeaderToMapConverter != null, "EntitySetNameGenerator can't be null.");
		this.entitySetNameGenerator = entitySetNameGenerator;
		this.httpHeaderToMapConverter = httpHeaderToMapConverter;
	}

	@Override
	public String consolidate(final List<OutboundBatchRequestPartDTO> requestDTOs, final String boundary)
	{
		final List<BatchPart> batchBody = requestDTOs
				.stream()
				.map(this::createBatchChangeSetPart)
				.map(this::createBatchChangeSet).toList();

		final BatchRequestWriter writer = new BatchRequestWriter();
		try (final InputStream batchRequest = writer.writeBatchRequest(batchBody, boundary))
		{
			return IOUtils.toString(batchRequest, StandardCharsets.UTF_8);
		}
		catch (final IOException | RuntimeException e)
		{
			throw new ODataBatchParsingException(e);
		}
	}

	private BatchPart createBatchChangeSet(final BatchChangeSetPart part)
	{
		final BatchChangeSet changeSet = BatchChangeSet.newBuilder().build();
		changeSet.add(part);
		return changeSet;
	}

	private BatchChangeSetPart createBatchChangeSetPart(final OutboundBatchRequestPartDTO requestDTO)
	{
		return BatchChangeSetPart.method(requestDTO.getRequestType().name())
		                         .uri((entitySetNameGenerator.generate(requestDTO.getItemType())))
		                         .body(convert(requestDTO.getHttpEntity().getBody()))
		                         .headers(httpHeaderToMapConverter.convert(requestDTO.getHttpEntity().getHeaders()))
		                         .contentId(requestDTO.getChangeID())
		                         .build();
	}

	private String convert(final Map<String, Object> requestBody)
	{
		return mapToStringConverter.toJson(requestBody);
	}
}
