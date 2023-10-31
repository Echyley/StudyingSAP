/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.swaggerintegration.constants.SwaggerintegrationConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;


@UnitTest
public class CommerceMediaTypeCustomizerTest
{
	private CommerceMediaTypeCustomizer commerceMediaTypeCustomizer;

	private HandlerMethod irrelevant;

	private List<String> producesMediaTypes;

	private List<String> consumesMediaTypes;

	@Before
	public void setup() throws NoSuchMethodException
	{
		commerceMediaTypeCustomizer = new CommerceMediaTypeCustomizer(SwaggerintegrationConstants.DEFAULT_CONSUMES_MEDIA_TYPES,
				SwaggerintegrationConstants.DEFAULT_PRODUCES_MEDIA_TYPES);
		irrelevant = new HandlerMethod(new Object(), "equals", Object.class);
		producesMediaTypes = SwaggerintegrationConstants.DEFAULT_PRODUCES_MEDIA_TYPES;
		consumesMediaTypes = SwaggerintegrationConstants.DEFAULT_CONSUMES_MEDIA_TYPES;
	}

	@Test
	public void shouldReplaceWildCardWithXmlAndJsonInResponses()
	{
		//given
		Operation operation = new Operation();
		String responseName = "200";
		setupOperationResponse(operation, responseName, SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD);
		MediaType mediaType = operation.getResponses().get(responseName).getContent()
				.get(SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD);

		//when
		commerceMediaTypeCustomizer.customize(operation, irrelevant);

		//then
		Assert.assertNull(
				operation.getResponses().get(responseName).getContent().get(SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD));
		Assert.assertEquals(producesMediaTypes.size(), operation.getResponses().get(responseName).getContent().size());
		Assert.assertEquals(mediaType, operation.getResponses().get(responseName).getContent().get(producesMediaTypes.get(0)));
		Assert.assertEquals(mediaType, operation.getResponses().get(responseName).getContent().get(producesMediaTypes.get(1)));
	}


	@Test
	public void shouldReplaceWildCardWithXmlAndJsonInRequestBody()
	{
		Operation operation = new Operation();
		setupOperationRequestBody(operation, SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD);
		MediaType mediaType = operation.getRequestBody().getContent().get(SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD);

		commerceMediaTypeCustomizer.customize(operation, irrelevant);

		Assert.assertNull(operation.getRequestBody().getContent().get(SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD));
		Assert.assertEquals(consumesMediaTypes.size(), operation.getRequestBody().getContent().size());
		Assert.assertEquals(mediaType, operation.getRequestBody().getContent().get(consumesMediaTypes.get(0)));
		Assert.assertEquals(mediaType, operation.getRequestBody().getContent().get(consumesMediaTypes.get(1)));
	}

	@Test
	public void shouldKeepApplicationXmlAtFrontInResponses()
	{
		//given
		Operation operation = new Operation();
		String responseName = "200";
		setupOperationResponse(operation, responseName, producesMediaTypes.get(1));
		operation.getResponses().get(responseName).getContent().put(producesMediaTypes.get(0), new MediaType());

		//when
		commerceMediaTypeCustomizer.customize(operation, irrelevant);

		Map.Entry<String, MediaType>[] mediaTypes = new Map.Entry[2];
		operation.getResponses().get(responseName).getContent().entrySet().toArray(mediaTypes);

		//then
		Assert.assertEquals(producesMediaTypes.get(0), mediaTypes[0].getKey());
		Assert.assertEquals(producesMediaTypes.get(1), mediaTypes[1].getKey());
	}

	@Test
	public void shouldKeepApplicationXmlAtFrontInRequestBody()
	{
		Operation operation = new Operation();
		setupOperationRequestBody(operation, consumesMediaTypes.get(1), consumesMediaTypes.get(0));

		commerceMediaTypeCustomizer.customize(operation, irrelevant);
		Map.Entry<String, MediaType>[] mediaTypes = new Map.Entry[2];
		operation.getRequestBody().getContent().entrySet().toArray(mediaTypes);

		Assert.assertEquals(consumesMediaTypes.get(0), mediaTypes[0].getKey());
		Assert.assertEquals(consumesMediaTypes.get(1), mediaTypes[1].getKey());
	}

	@Test
	public void shouldDoNothingWhenSpecifiedInResponses()
	{
		//given
		Operation operation = new Operation();
		String responseName = "200";
		String mediaTypeName = javax.ws.rs.core.MediaType.APPLICATION_JSON;
		setupOperationResponse(operation, responseName, mediaTypeName);
		int originalSize = operation.getResponses().get(responseName).getContent().size();
		MediaType mediaType = operation.getResponses().get(responseName).getContent().get(mediaTypeName);

		//when
		commerceMediaTypeCustomizer.customize(operation, irrelevant);

		//then
		Assert.assertEquals(originalSize, operation.getResponses().get(responseName).getContent().size());
		Assert.assertEquals(mediaType, operation.getResponses().get(responseName).getContent().get(mediaTypeName));
	}

	@Test
	public void shouldDoNothingWhenSpecifiedInRequestBody()
	{
		Operation operation = new Operation();
		String wildCard = SwaggerintegrationConstants.MEDIA_TYPE_WILDCARD;
		String jsonType = consumesMediaTypes.get(1);
		setupOperationRequestBody(operation, wildCard, jsonType);
		int originalSize = operation.getRequestBody().getContent().size();
		MediaType wildCardMediaType = operation.getRequestBody().getContent().get(wildCard);
		MediaType jsonMediaType = operation.getRequestBody().getContent().get(jsonType);

		commerceMediaTypeCustomizer.customize(operation, irrelevant);

		Assert.assertEquals(originalSize, operation.getRequestBody().getContent().size());
		Assert.assertEquals(wildCardMediaType, operation.getRequestBody().getContent().get(wildCard));
		Assert.assertEquals(jsonMediaType, operation.getRequestBody().getContent().get(jsonType));
	}

	private void setupOperationResponse(Operation operation, String responseName, String mediaTypeName)
	{
		operation.setResponses(new ApiResponses().addApiResponse(responseName,
				new ApiResponse().content(new Content().addMediaType(mediaTypeName, new MediaType()))));
	}

	private void setupOperationRequestBody(final Operation operation, String... mediaTypes)
	{
		Content content = new Content();
		Arrays.stream(mediaTypes).forEach(mediaType -> content.addMediaType(mediaType, new MediaType()));
		operation.setRequestBody(new RequestBody().content(content));
	}
}
