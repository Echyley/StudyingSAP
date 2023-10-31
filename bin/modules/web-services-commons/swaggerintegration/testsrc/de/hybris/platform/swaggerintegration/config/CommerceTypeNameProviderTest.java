/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CommerceTypeNameProviderTest
{
	private CommerceTypeNameProvider commerceTypeNameProvider;
	private static final String TYPE_NAME_SUFFIX = "WsDTO";

	@Before
	public void setUp()
	{
		commerceTypeNameProvider = new CommerceTypeNameProvider();
	}

	@Test
	public void shouldNotThrowExceptionsWhenEmptyApiDoc()
	{
		final OpenAPI api = new OpenAPI();
		final Components originalComponents = api.getComponents();
		commerceTypeNameProvider.customise(api);
		Assert.assertEquals(originalComponents, api.getComponents());
	}

	@Test
	public void shouldRemoveSuffixInRequestBody()
	{
		final OpenAPI api = new OpenAPI();
		api.setPaths(preparePaths().addPathItem("path1", preparePostWithRequestBody()));

		Assert.assertTrue(getRefWithSuffix(getSchemasOfRequestBodies(api)).findAny().isPresent());
		commerceTypeNameProvider.customise(api);
		Assert.assertFalse(getRefWithSuffix(getSchemasOfRequestBodies(api)).findAny().isPresent());
	}

	@Test
	public void shouldRemoveSuffixInResponses()
	{
		final OpenAPI api = new OpenAPI();

		api.setPaths(
				preparePaths().addPathItem("path1", prepareGetWithResponses()).addPathItem("path2", prepareArraySchemaInResponse()));
		Assert.assertTrue(getRefWithSuffix(getSchemasOfResponses(api)).findAny().isPresent());
		Assert.assertTrue(
				getRefWithSuffixInItems(getSchemasOfResponses(api).filter(schema -> schema instanceof ArraySchema)).findAny()
						.isPresent());
		commerceTypeNameProvider.customise(api);
		Assert.assertFalse(getRefWithSuffix(getSchemasOfResponses(api)).findAny().isPresent());
		Assert.assertFalse(
				getRefWithSuffixInItems(getSchemasOfResponses(api).filter(schema -> schema instanceof ArraySchema)).findAny()
						.isPresent());
	}

	@Test
	public void shouldRemoveSuffixInParameters()
	{
		final OpenAPI api = new OpenAPI();
		api.setPaths(preparePaths().addPathItem("path1", prepareGetWithParameters()));

		Assert.assertTrue(getRefWithSuffix(getSchemasOfParameters(api)).findAny().isPresent());
		commerceTypeNameProvider.customise(api);
		Assert.assertFalse(getRefWithSuffix(getSchemasOfParameters(api)).findAny().isPresent());
	}

	@Test
	public void shouldRemoveSuffixInSchemaNameAndProperties()
	{
		final OpenAPI api = new OpenAPI();
		api.setPaths(preparePaths().addPathItem("path1", prepareGetWithParameters()));
		api.setComponents(new Components().schemas(prepareSchemasWithRef()));

		Assert.assertTrue(getStringWithSuffix(getSchemaNamesOfComponents(api)).findAny().isPresent());
		Assert.assertTrue(getRefWithSuffix(getSchemasOfComponents(api)).findAny().isPresent());
		commerceTypeNameProvider.customise(api);
		Assert.assertFalse(getStringWithSuffix(getSchemaNamesOfComponents(api)).findAny().isPresent());
		Assert.assertFalse(getRefWithSuffix(getSchemasOfComponents(api)).findAny().isPresent());
	}

	@Test
	public void shouldSortSchemasByName()
	{
		final OpenAPI api = new OpenAPI();
		api.setPaths(preparePaths().addPathItem("path1", prepareGetWithParameters()));
		final Map<String, Schema> schemaMap = new LinkedHashMap<>();
		final Schema foo = new Schema();
		foo.type("object");
		foo.addProperty("p1", targetSchema());
		schemaMap.put("Foo", foo);

		final Schema bar = new Schema();
		bar.type("object");
		bar.addProperty("p2", targetSchema());
		schemaMap.put("Bar", bar);
		api.setComponents(new Components().schemas(schemaMap));

		final List<String> names = getSchemaNameList(api);
		Assert.assertEquals("Foo", names.get(0));
		Assert.assertEquals("Bar", names.get(1));
		commerceTypeNameProvider.customise(api);
		final List<String> sortedNames = getSchemaNameList(api);
		Assert.assertEquals("Bar", sortedNames.get(0));
		Assert.assertEquals("Foo", sortedNames.get(1));
	}

	private Map<String, Schema> prepareSchemasWithRef()
	{
		final Map<String, Schema> schemaMap = new HashMap<>();
		final Schema schema = new Schema();
		schema.type("object");
		final String propertyName = "productCategories";
		final String schemaName = "CategorySearchResultWsDTO";
		schema.addProperty(propertyName, targetSchema());
		schema.addProperty(propertyName + "prop2", targetSchema());
		schema.addProperty(propertyName + "prop3", new Schema().type("array").items(targetSchema()));
		schemaMap.put(schemaName, schema);
		return schemaMap;
	}

	private List<String> getSchemaNameList(final OpenAPI api)
	{
		final List<String> names = new ArrayList<>();
		api.getComponents().getSchemas().forEach( (k, v) -> {
			names.add(k);
		});
		return names;
	}

	private Stream<String> getSchemaNamesOfComponents(final OpenAPI api)
	{
		return api.getComponents().getSchemas().keySet().stream();
	}

	private Stream<Schema> getSchemasOfComponents(final OpenAPI api)
	{
		final List<Schema> schemas = new ArrayList<>();
		final Stream<Schema> listInProperties =
				api.getComponents().getSchemas().values().stream().flatMap(item -> item.getProperties().values().stream());
		schemas.addAll(listInProperties.collect(Collectors.toList()));

		final Stream<Schema> listInItems = schemas.stream()
		                                          .filter(item -> item.getType() != null && item.getType().equals("array"))
		                                          .map(Schema::getItems);
		schemas.addAll(listInItems.collect(Collectors.toList()));
		return schemas.stream();
	}

	private Stream<Schema> getSchemasOfParameters(final OpenAPI api)
	{
		return api.getPaths().values().stream().flatMap(item -> item.getGet().getParameters().stream())
		          .map(Parameter::getSchema);
	}

	private Stream<Schema> getSchemasOfRequestBodies(final OpenAPI api)
	{
		return api.getPaths().values().stream().flatMap(item -> item.readOperations().stream())
		          .map(Operation::getRequestBody)
		          .filter(Objects::nonNull)
		          .map(RequestBody::getContent)
		          .flatMap(c -> c.values().stream())
		          .map(MediaType::getSchema);
	}

	private Stream<Schema> getSchemasOfResponses(final OpenAPI api)
	{
		return api.getPaths().values().stream().flatMap(item -> item.readOperations().stream())
		          .map(Operation::getResponses)
		          .flatMap(apiResponses -> apiResponses.values().stream())
		          .filter(Objects::nonNull)
		          .map(ApiResponse::getContent)
		          .flatMap(c -> c.values().stream())
		          .map(MediaType::getSchema);
	}

	private Stream<String> getStringWithSuffix(final Stream<String> schemaNamesOfComponents)
	{
		return schemaNamesOfComponents.filter(ref -> ref.contains(TYPE_NAME_SUFFIX));
	}

	private Stream<String> getRefWithSuffix(final Stream<Schema> schemas)
	{
		return schemas.map(Schema::get$ref).filter(
				ref -> ref != null && ref.contains(TYPE_NAME_SUFFIX)
		);
	}

	private Stream<String> getRefWithSuffixInItems(final Stream<Schema> schemas)
	{
		return schemas.map(Schema::getItems).map(Schema::get$ref).filter(ref -> ref != null && ref.contains(TYPE_NAME_SUFFIX));
	}

	private Paths preparePaths()
	{
		final Paths paths = new Paths();
		return paths;
	}

	private PathItem prepareGetWithParameters()
	{
		final PathItem item = new PathItem();
		final Operation get = new Operation();
		final Parameter param = new Parameter();
		param.setName("testParam");
		param.setSchema(targetSchema());
		get.setParameters(List.of(param));
		item.setGet(get);
		return item;
	}

	private PathItem preparePostWithRequestBody()
	{
		final PathItem item = new PathItem();
		final Operation post = new Operation();
		post.setRequestBody(new RequestBody().content(new Content().addMediaType("application/json", new MediaType().schema(
				targetSchema()
		))));
		item.setPost(post);
		return item;
	}

	private PathItem prepareGetWithResponses()
	{
		final PathItem item = new PathItem();
		final Operation get = new Operation();
		final ApiResponses responses = new ApiResponses();
		final Content content = new Content();
		content.addMediaType("application/json", new MediaType().schema(targetSchema()));
		responses.addApiResponse("200", new ApiResponse().content(content));
		get.setResponses(responses);
		item.setGet(get);
		return item;
	}

	private PathItem prepareArraySchemaInResponse()
	{
		final PathItem item = new PathItem();
		final Operation get = new Operation();
		final ApiResponses responses = new ApiResponses();
		final Content content = new Content();
		content.addMediaType("application/xml", new MediaType().schema(new ArraySchema().items(targetSchema())));
		responses.addApiResponse("200", new ApiResponse().content(content));
		get.setResponses(responses);
		item.setGet(get);
		return item;
	}

	private Schema targetSchema()
	{
		return new Schema().$ref("#/components/schemas/SynchronizationWsDTO");
	}
}
