/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.platform.swaggerintegration.constants.SwaggerintegrationConstants;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

/**
 * Commerce implementation of {@link OperationCustomizer} that replaces the media types of both consumes and produces for open API
 * as provided media types, in either case:
 * 1. It's set as {@link org.springframework.http.MediaType#ALL_VALUE  }
 * 2. It's a list of {@link org.springframework.http.MediaType#APPLICATION_XML_VALUE}, {@link org.springframework.http.MediaType#APPLICATION_JSON_VALUE}
 */
public class CommerceMediaTypeCustomizer implements OperationCustomizer
{
	public static final int ONLY_WILDCARD_SIZE = 1;

	private List<String> consumesMediaTypes;
	private List<String> producesMediaTypes;

	public CommerceMediaTypeCustomizer(List<String> consumesMediaTypes, List<String> producesMediaTypes)
	{
		this.consumesMediaTypes = List.copyOf(consumesMediaTypes);
		this.producesMediaTypes = List.copyOf(producesMediaTypes);
	}

	@Override
	public Operation customize(final Operation operation, final HandlerMethod handlerMethod)
	{
		if (operation == null)
		{
			return null;
		}
		customizeRequestBody(operation);
		customizeResponses(operation);
		return operation;
	}

	private void customizeRequestBody(final Operation operation)
	{
		Optional.ofNullable(operation.getRequestBody())
				.ifPresent(requestBody -> customizeConsumesContent(requestBody.getContent()));
	}


	private void customizeConsumesContent(Content content)
	{
		if (content != null && !content.isEmpty())
		{
			sortMediaTypes(content, consumesMediaTypes);
			replaceOnlyWildcard(content, consumesMediaTypes);
		}
	}

	private void customizeResponses(final Operation operation)
	{

		Optional.ofNullable(operation.getResponses())
				.map(apiResponses -> apiResponses.values().stream().map(ApiResponse::getContent).toList())
				.ifPresent(contents -> contents.forEach(this::customizeProducesContent));
	}

	private void customizeProducesContent(Content content)
	{
		if (content != null && !content.isEmpty())
		{
			sortMediaTypes(content, producesMediaTypes);
			replaceOnlyWildcard(content, producesMediaTypes);
		}
	}

	private void replaceOnlyWildcard(Content content, List<String> mediaTypes)
	{
		if (content.size() == ONLY_WILDCARD_SIZE && content.containsKey(SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD))
		{
			MediaType mediaTypeObj = content.get(SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD);
			content.clear();
			mediaTypes.stream().forEach(mediaType -> content.addMediaType(mediaType, mediaTypeObj));
		}
	}

	private void sortMediaTypes(Content content, List<String> mediaTypes)
	{
		if (content.size() == mediaTypes.size() && mediaTypes.stream().allMatch(content::containsKey))
		{
			for (final String mediaType : mediaTypes)
			{
				MediaType mediaTypeObj = content.get(mediaType);
				content.remove(mediaType);
				content.put(mediaType, mediaTypeObj);
			}
		}
	}
}
