/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.swaggerintegration.config;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;


@UnitTest
public class CommerceRespCodeProviderTest
{
	private static final String DEFAULT_ENDPOINT_PATH = "/test/path";
	private CommerceRespCodeProvider target;

	@Before
	public void setUp()
	{
		target = new CommerceRespCodeProvider();
	}

	@Test
	public void testCustomiseErrorListSchemaNameShouldBeSame()
	{
		Class<ErrorListWsDTO> clazz = ErrorListWsDTO.class;
		io.swagger.v3.oas.annotations.media.Schema schemaAnnotation = clazz.getDeclaredAnnotation(
				io.swagger.v3.oas.annotations.media.Schema.class);
		Assert.assertNotNull(schemaAnnotation);

		Assert.assertEquals(CommerceRespCodeProvider.ERROR_LIST_NAME, schemaAnnotation.name());
	}

	@Test
	public void testErrorListNotExistShouldOnlyAddStatusCode()
	{
		// given
		OpenAPI openAPI = createTestOpenAPI(true, false);

		// when
		target.customise(openAPI);

		// then
		openAPI.getPaths().forEach((pathKey, pathItem) -> pathItem.readOperations().forEach(operation -> {
			ApiResponses responses = operation.getResponses();

			ApiResponse response401 = responses.get(Integer.toString(HttpStatus.UNAUTHORIZED.value()));
			Assert.assertNotNull(response401);
			Assert.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), response401.getDescription());
			Assert.assertNull(response401.getContent());

			ApiResponse response403 = responses.get(Integer.toString(HttpStatus.FORBIDDEN.value()));
			Assert.assertNotNull(response403);
			Assert.assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), response403.getDescription());
			Assert.assertNull(response403.getContent());

			ApiResponse response404 = responses.get(Integer.toString(HttpStatus.NOT_FOUND.value()));
			Assert.assertNotNull(response404);
			Assert.assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response404.getDescription());
			Assert.assertNull(response404.getContent());
		}));
	}

	@Test
	public void testCustomiseErrorListExistsShouldAddCodeAndSchema()
	{
		// given
		OpenAPI openAPI = createTestOpenAPI(true, true);

		// when
		target.customise(openAPI);

		// then
		openAPI.getPaths().forEach((pathKey, pathItem) -> pathItem.readOperations().forEach(operation -> {
			ApiResponses responses = operation.getResponses();

			ApiResponse response401 = responses.get(Integer.toString(HttpStatus.UNAUTHORIZED.value()));
			Assert.assertNotNull(response401);
			Assert.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), response401.getDescription());
			Assert.assertNull(CommerceRespCodeProvider.ERROR_LIST_SCHEMA_REF,
					response401.getContent().get(APPLICATION_XML).getSchema().get$schema());
			Assert.assertNull(CommerceRespCodeProvider.ERROR_LIST_SCHEMA_REF,
					response401.getContent().get(APPLICATION_JSON).getSchema().get$schema());

			ApiResponse response403 = responses.get(Integer.toString(HttpStatus.FORBIDDEN.value()));
			Assert.assertNotNull(response403);
			Assert.assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), response403.getDescription());
			Assert.assertNull(CommerceRespCodeProvider.ERROR_LIST_SCHEMA_REF,
					response403.getContent().get(APPLICATION_XML).getSchema().get$schema());
			Assert.assertNull(CommerceRespCodeProvider.ERROR_LIST_SCHEMA_REF,
					response403.getContent().get(APPLICATION_JSON).getSchema().get$schema());

			ApiResponse response404 = responses.get(Integer.toString(HttpStatus.NOT_FOUND.value()));
			Assert.assertNotNull(response404);
			Assert.assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response404.getDescription());
			Assert.assertNull(CommerceRespCodeProvider.ERROR_LIST_SCHEMA_REF,
					response404.getContent().get(APPLICATION_XML).getSchema().get$schema());
			Assert.assertNull(CommerceRespCodeProvider.ERROR_LIST_SCHEMA_REF,
					response404.getContent().get(APPLICATION_JSON).getSchema().get$schema());
		}));
	}

	@Test
	public void testCustomiseComponentsNotExistsShouldOnlyAddStatusCode()
	{
		// given
		OpenAPI openAPI = createTestOpenAPI(false, true);

		// when
		target.customise(openAPI);

		// then
		openAPI.getPaths().forEach((pathKey, pathItem) -> pathItem.readOperations().forEach(operation -> {
			ApiResponses responses = operation.getResponses();

			ApiResponse response401 = responses.get(Integer.toString(HttpStatus.UNAUTHORIZED.value()));
			Assert.assertNotNull(response401);
			Assert.assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), response401.getDescription());
			Assert.assertNull(response401.getContent());

			ApiResponse response403 = responses.get(Integer.toString(HttpStatus.FORBIDDEN.value()));
			Assert.assertNotNull(response403);
			Assert.assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), response403.getDescription());
			Assert.assertNull(response403.getContent());

			ApiResponse response404 = responses.get(Integer.toString(HttpStatus.NOT_FOUND.value()));
			Assert.assertNotNull(response404);
			Assert.assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), response404.getDescription());
			Assert.assertNull(response404.getContent());
		}));
	}

	@Test
	public void testCustomise401ExistsShouldNotModify()
	{
		// given
		final String testDescription = "Test Desc";
		OpenAPI openAPI = createTestOpenAPI(false, true);
		ApiResponses responses = openAPI.getPaths().get(DEFAULT_ENDPOINT_PATH).getGet().getResponses();
		ApiResponse response = new ApiResponse();
		response.setDescription(testDescription);
		responses.addApiResponse(Integer.toString(HttpStatus.FORBIDDEN.value()), response);

		// when
		target.customise(openAPI);

		// then
		ApiResponse response403 = responses.get(Integer.toString(HttpStatus.FORBIDDEN.value()));
		Assert.assertNotNull(response403);
		Assert.assertEquals(testDescription, response403.getDescription());
		Assert.assertNull(response403.getContent());
	}

	@Test
	public void testCustomizeResponseStatusCodeShouldBeSorted()
	{
		// given
		final String testDescription = "Test Desc";
		OpenAPI openAPI = createTestOpenAPI(false, false);
		ApiResponses responses = openAPI.getPaths().get(DEFAULT_ENDPOINT_PATH).getGet().getResponses();
		ApiResponse response200 = new ApiResponse();
		response200.setDescription(testDescription);
		responses.addApiResponse(Integer.toString(HttpStatus.OK.value()), response200);
		ApiResponse response500 = new ApiResponse();
		response500.setDescription(testDescription);
		responses.addApiResponse(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), response500);

		// when
		target.customise(openAPI);

		// then
		responses = openAPI.getPaths().get(DEFAULT_ENDPOINT_PATH).getGet().getResponses();
		List<String> sortedRespStatus = new ArrayList<>();
		responses.forEach((key, value) -> {
			sortedRespStatus.add(key);
		});

		Assert.assertEquals(5, sortedRespStatus.size());
		Assert.assertEquals(Integer.toString(HttpStatus.OK.value()), sortedRespStatus.get(0));
		Assert.assertEquals(Integer.toString(HttpStatus.UNAUTHORIZED.value()), sortedRespStatus.get(1));
		Assert.assertEquals(Integer.toString(HttpStatus.FORBIDDEN.value()), sortedRespStatus.get(2));
		Assert.assertEquals(Integer.toString(HttpStatus.NOT_FOUND.value()), sortedRespStatus.get(3));
		Assert.assertEquals(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()), sortedRespStatus.get(4));
	}

	private OpenAPI createTestOpenAPI(final boolean createComponents, final boolean createErrorList)
	{
		OpenAPI openAPI = new OpenAPI();

		// create default paths
		Operation getOperation = new Operation();
		getOperation.setResponses(new ApiResponses());

		PathItem pathItem = new PathItem();
		pathItem.get(getOperation);

		Paths paths = new Paths();
		paths.addPathItem(DEFAULT_ENDPOINT_PATH, pathItem);

		openAPI.setPaths(paths);

		if (createComponents)
		{
			Components components = new Components();
			openAPI.setComponents(components);

			if (createErrorList)
			{
				Schema<Object> schema = new Schema<>();
				schema.type("object");

				components.addSchemas(CommerceRespCodeProvider.ERROR_LIST_NAME, schema);
			}
		}

		return openAPI;
	}
}