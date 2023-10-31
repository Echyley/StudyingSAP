/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.swaggerintegration.config;


import java.util.TreeMap;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import static de.hybris.platform.swaggerintegration.constants.SwaggerintegrationConstants.DEFAULT_PRODUCES_MEDIA_TYPES;


/**
 * One customisation implementation of {@link OpenApiCustomiser} that additional
 * HTTP Response Status Code (400/401/403) at the end of each endpoint
 *
 * @since 2211
 */
public class CommerceRespCodeProvider implements OpenApiCustomiser
{
	static final String ERROR_LIST_NAME = "errorList";
	static final String ERROR_LIST_SCHEMA_REF = "#/components/schemas/errorList";

	@Override
	public void customise(final OpenAPI openApi)
	{
		final Paths paths = openApi.getPaths();
		if (paths == null)
		{
			return;
		}

		boolean errorTypeExist = checkErrorListExist(openApi);

		paths.forEach(
				(pathKey, pathItem) -> pathItem.readOperations().forEach(operation -> handleOperation(operation, errorTypeExist)));
	}

	private void handleOperation(final Operation operation, final boolean errorTypeExist)
	{
		ApiResponses apiResponses = operation.getResponses();
		if (apiResponses == null)
		{
			apiResponses = new ApiResponses();
			operation.setResponses(apiResponses);
		}
		addErrorResponse(HttpStatus.UNAUTHORIZED, errorTypeExist, apiResponses);
		addErrorResponse(HttpStatus.FORBIDDEN, errorTypeExist, apiResponses);
		addErrorResponse(HttpStatus.NOT_FOUND, errorTypeExist, apiResponses);

		sortResponses(apiResponses);
	}

	private void sortResponses(final ApiResponses apiResponses)
	{
		final TreeMap<String, ApiResponse> sortedMap = new TreeMap<>(apiResponses);

		apiResponses.clear();
		apiResponses.putAll(sortedMap);
	}

	private void addErrorResponse(final HttpStatus httpStatus, final boolean errorTypeExist, final ApiResponses apiResponses)
	{
		final String statusCode = Integer.toString(httpStatus.value());
		ApiResponse apiResponse = apiResponses.get(statusCode);
		if (apiResponse == null)
		{
			ApiResponse missedResponse = new ApiResponse();
			missedResponse.setDescription(httpStatus.getReasonPhrase());
			if (errorTypeExist)
			{
				addSchemaContent(missedResponse);
			}
			apiResponses.addApiResponse(statusCode, missedResponse);
		}
	}

	private void addSchemaContent(ApiResponse missedResponse)
	{
		Content content = new Content();
		DEFAULT_PRODUCES_MEDIA_TYPES.forEach(mediaType -> content.addMediaType(mediaType, createMediaTypeToErrorListSchema()));
		missedResponse.setContent(content);
	}

	private MediaType createMediaTypeToErrorListSchema()
	{
		Schema<Object> schema = new Schema<>();
		schema.set$ref(ERROR_LIST_SCHEMA_REF);

		MediaType mediaType = new MediaType();
		mediaType.setSchema(schema);

		return mediaType;
	}

	private boolean checkErrorListExist(final OpenAPI openApi)
	{
		Components components = openApi.getComponents();
		if (components != null)
		{
			return components.getSchemas() != null && components.getSchemas().get(ERROR_LIST_NAME) != null;
		}

		return false;
	}
}
