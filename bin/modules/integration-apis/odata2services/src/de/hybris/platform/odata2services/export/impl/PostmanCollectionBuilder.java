/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.odata2services.dto.ExportEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;

/**
 * A Postman collection builder to build a Postman collection given a {@link Set < ExportEntity >}.
 */
final class PostmanCollectionBuilder
{
	private static final String HTTP_METHOD = "POST";
	private static final String BASIC_AUTH = "basic";

	private static final String JSON = "json";
	private static final String MODE = "raw";
	private static final String TYPE = "string";
	private static final String HOST_URL_KEY = "hostUrl";
	private static final String PASSWORD_KEY = "password";
	private static final String PASSWORD_VALUE = "{{password}}";
	private static final String USERNAME_KEY = "username";
	private static final String USERNAME_VALUE = "{{username}}";

	private static final String INTEGRATION_KEY = "integrationKey";
	private static final String NAME_DELIMITER = "_";
	private static final String PATH_DELIMITER = "/";

	private static final String COLLECTION_NAME = "ImportConfiguration";
	private static final String COLLECTION_SCHEMA = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";

	private final ObjectMapper mapper;
	private Set<ExportEntity> exportEntities;

	private PostmanCollectionBuilder()
	{
		mapper = new ObjectMapper();
	}

	/**
	 * A factory method to instantiate a Postman collection builder
	 *
	 * @return a PostmanCollectionBuilder instance
	 */
	static PostmanCollectionBuilder postmanCollectionBuilder()
	{
		return new PostmanCollectionBuilder();
	}

	PostmanCollectionBuilder withExportEntities(final Set<ExportEntity> exportEntities)
	{
		this.exportEntities = Collections.unmodifiableSet(exportEntities);
		return this;
	}

	PostmanCollection build()
	{
		final List<PostmanCollection.Item> items = exportEntities.stream()
		                                                         .flatMap(exportEntity -> buildItems(exportEntity).stream())
		                                                         .collect(Collectors.toList());

		final PostmanCollection postmanCollection = new PostmanCollection();
		postmanCollection.setItems(items);
		postmanCollection.setVariables(getVariables());
		postmanCollection.setAuth(getAuth());
		postmanCollection.setInfo(getInfo());

		return postmanCollection;
	}

	private List<PostmanCollection.Item> buildItems(final ExportEntity exportEntity)
	{
		return exportEntity.getRequestBodies()
		                   .stream()
		                   .map(this::buildBody)
		                   .map(body -> buildRequest(exportEntity.getRequestUrl(), body))
		                   .map(this::buildItem)
		                   .collect(Collectors.toList());
	}

	private PostmanCollection.Body buildBody(final String jsonBody)
	{
		final PostmanCollection.Raw raw = new PostmanCollection.Raw();
		raw.setLanguage(JSON);

		final PostmanCollection.Options options = new PostmanCollection.Options();
		options.setRaw(raw);

		final PostmanCollection.Body body = new PostmanCollection.Body();
		body.setOptions(options);
		body.setMode(MODE);
		body.setRaw(jsonBody);

		return body;
	}

	private PostmanCollection.Request buildRequest(final String requestUrl, final PostmanCollection.Body body)
	{
		final PostmanCollection.Request request = new PostmanCollection.Request();
		request.setUrl(getUrl(requestUrl));
		request.setBody(body);
		request.setMethod(HTTP_METHOD);
		request.setAuth(getAuth());

		return request;
	}

	private PostmanCollection.Item buildItem(final PostmanCollection.Request request)
	{
		final PostmanCollection.Item item = new PostmanCollection.Item();
		item.setName(getItemName(request));
		item.setRequest(request);

		return item;
	}

	private PostmanCollection.Auth getAuth()
	{
		final PostmanCollection.Credential password = new PostmanCollection.Credential();
		password.setType(TYPE);
		password.setKey(PASSWORD_KEY);
		password.setValue(PASSWORD_VALUE);

		final PostmanCollection.Credential username = new PostmanCollection.Credential();
		username.setType(TYPE);
		username.setKey(USERNAME_KEY);
		username.setValue(USERNAME_VALUE);

		final PostmanCollection.Auth auth = new PostmanCollection.Auth();
		auth.setType(BASIC_AUTH);
		auth.setCredentials(List.of(password, username));

		return auth;
	}

	private List<PostmanCollection.Variable> getVariables()
	{
		final PostmanCollection.Variable password = new PostmanCollection.Variable();
		password.setKey(PASSWORD_KEY);
		password.setId(PASSWORD_KEY);
		password.setValue(null);

		final PostmanCollection.Variable username = new PostmanCollection.Variable();
		username.setKey(USERNAME_KEY);
		username.setId(USERNAME_KEY);
		username.setValue(null);

		final PostmanCollection.Variable hostUrl = new PostmanCollection.Variable();
		hostUrl.setKey(HOST_URL_KEY);
		hostUrl.setId(HOST_URL_KEY);
		hostUrl.setValue(null);

		return List.of(password, username, hostUrl);
	}

	private PostmanCollection.Info getInfo()
	{
		final PostmanCollection.Info info = new PostmanCollection.Info();
		info.setName(COLLECTION_NAME);
		info.setSchema(COLLECTION_SCHEMA);

		return info;
	}

	private PostmanCollection.Url getUrl(final String requestUrl)
	{
		final List<String> urlParts = new ArrayList<>(Arrays.asList(requestUrl.split(PATH_DELIMITER)));
		final PostmanCollection.Url url = new PostmanCollection.Url();
		url.setRaw(requestUrl);
		url.setHosts(List.of(urlParts.get(0)));
		urlParts.remove(0);
		url.setPaths(urlParts);

		return url;
	}

	private String getItemName(final PostmanCollection.Request request)
	{
		return Iterables.getLast(request.getUrl().getPaths()) + extractIntegrationKey(request).orElse(Strings.EMPTY);
	}

	private Optional<String> extractIntegrationKey(final PostmanCollection.Request request)
	{
		try
		{
			final ObjectNode body = mapper.readValue(request.getBody().getRaw(), ObjectNode.class);
			return body.has(INTEGRATION_KEY) ? Optional.of(getIntegrationKey(body)) : Optional.empty();
		}
		catch (final JsonProcessingException e)
		{
			return Optional.empty();
		}
	}

	private String getIntegrationKey(final ObjectNode body)
	{
		return NAME_DELIMITER + body.get(INTEGRATION_KEY).toString().replace("\"", "");
	}

}
