/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smartedit.controllers;

import de.hybris.platform.smartedit.dto.ConfigurationData;
import de.hybris.platform.smartedit.dto.ConfigurationDataListWsDto;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * Unauthenticated controller using various gateways to send the requested CRUD operations to the secured webservice
 * responsible of executing the operation.
 * <p>
 * By default, {@code smarteditwebservices} is the targeted web extension. This is defined by the property
 * {@code configurationServiceLocation}.
 */
@RestController("configurationController")
@RequestMapping("/configuration")
@Tag(name = "configurations")
public class ConfigurationController
{
	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String SECURED = "secured";
	private static final String DUMMY_TOKEN = "dummy";

	private static final Logger LOG = Logger.getLogger(ConfigurationController.class);

	private final HttpGETGateway httpGETGateway;
	private final HttpPOSTGateway httpPOSTGateway;
	private final HttpPUTGateway httpPUTGateway;
	private final HttpDELETEGateway httpDELETEGateway;

	private final ObjectMapper mapper = new ObjectMapper();


	@Autowired
	public ConfigurationController(final HttpGETGateway httpGETGateway, final HttpPOSTGateway httpPOSTGateway,
			final HttpPUTGateway httpPUTGateway, final HttpDELETEGateway httpDELETEGateway)
	{
		this.httpGETGateway = httpGETGateway;
		this.httpPOSTGateway = httpPOSTGateway;
		this.httpPUTGateway = httpPUTGateway;
		this.httpDELETEGateway = httpDELETEGateway;
	}

	@GetMapping
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Get configurations", description = "Endpoint to retrieve configurations.",
					operationId = "getConfiguration")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin or CMS Manager to access this resource") })
	@Parameter(name = "Authorization", description = "Authorization bearer token", required = true, schema = @Schema(type = "string"), in = ParameterIn.HEADER)
	public ResponseEntity<Collection<ConfigurationData>> getConfiguration(final HttpServletRequest request) throws IOException
	{
		String data = null;
		try
		{
			data = httpGETGateway.loadAll("", getAuthorization(request));
		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(e);
			return new ResponseEntity<>(e.getStatusCode());
		}
		final ConfigurationDataListWsDto configurations = mapper.readValue(data, ConfigurationDataListWsDto.class);
		return new ResponseEntity<>(configurations.getConfigurations(), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	@Operation(summary = "Create configurations", description = "Endpoint to create configurations.",
					operationId = "createConfiguration")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin to access this resource") })
	@Parameter(name = "Authorization", description = "Authorization bearer token", required = true, schema = @Schema(type = "string"), in = ParameterIn.HEADER)
	public ResponseEntity<ConfigurationData> saveConfiguration( //
			@Parameter(description = "Map representing the configurations to create", required = true) //
			@RequestBody
			final Map<String, String> payload, //
			final HttpServletRequest request) throws IOException
	{
		payload.remove(SECURED);
		String stringPayload = null;
		try
		{
			stringPayload = httpPOSTGateway.save(payload, getAuthorization(request));
		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(e);
			return new ResponseEntity<>(e.getStatusCode());
		}
		return new ResponseEntity<>(mapper.readValue(stringPayload, ConfigurationData.class), HttpStatus.CREATED);
	}

	@PutMapping(value = "/{key:.+}")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Update configurations", description = "Endpoint to update configurations.",
					operationId = "replaceConfiguration")
	@ApiResponses(value =
	{ //
			@ApiResponse(responseCode = "400", description = "Configuration data input is invalid"),
			@ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin to access this resource"),
			@ApiResponse(responseCode = "405", description = "The configuration key is missing from the payload") //
	})
	@Parameter(name = "Authorization", description = "Authorization bearer token", required = true, schema = @Schema(type = "string"), in = ParameterIn.HEADER)
	public ResponseEntity<ConfigurationData> updateConfiguration( //
			@Parameter(description = "Map representing the configurations to update", required = true) //
			@RequestBody
			final Map<String, String> payload, //
			@Parameter(description = "The key of the configuration to update", required = true) //
			@PathVariable("key")
			final String configId, //
			final HttpServletRequest request) throws IOException
	{
		Collection<ConfigurationData> configurationDataCollection = getConfiguration(request).getBody();
		if(configurationDataCollection == null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		final Optional<ConfigurationData> optional = configurationDataCollection.stream() //
				.filter(configurationData -> configurationData.getKey().equals(configId)) //
				.findFirst();

		if (optional.isPresent())
		{
			String stringPayload = null;
			try
			{
				stringPayload = httpPUTGateway.update(payload, configId, getAuthorization(request));
			}
			catch (final HttpClientErrorException e)
			{
				LOG.error(e);
				return new ResponseEntity<>(e.getStatusCode());
			}
			return new ResponseEntity<>(mapper.readValue(stringPayload, ConfigurationData.class), HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>(HttpStatus.OK);
		}
	}

	@DeleteMapping(value = "/{key:.+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete configurations", description = "Endpoint to delete configurations.",
					operationId = "removeConfiguration")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin to access this resource") })
	@Parameter(name = "Authorization", description = "Authorization bearer token", required = true, schema = @Schema(type = "string"), in = ParameterIn.HEADER)
	public ResponseEntity<Void> deleteConfiguration( //
			@Parameter(description = "The key of the configuration to delete", required = true) //
			@PathVariable("key")
			final String configId, //
			final HttpServletRequest request) throws IOException
	{
		final Map<String, String> configuration = new HashMap<>();
		Collection<ConfigurationData> configurationDataCollection = getConfiguration(request).getBody();
		if(configurationDataCollection == null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		configurationDataCollection.stream() //
				.filter(configurationData -> configurationData.getKey().equals(configId)) //
				.forEach(configurationData -> {
					configuration.put(KEY, configurationData.getKey());
					configuration.put(VALUE, configurationData.getValue());
				});

		try
		{
			httpDELETEGateway.delete(configuration, configId, getAuthorization(request));
		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(e);
			return new ResponseEntity<>(e.getStatusCode());
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	protected String getAuthorization(final HttpServletRequest request)
	{
		String auth = request.getHeader(HEADER_AUTHORIZATION);
		if (auth == null)
		{
			auth = DUMMY_TOKEN;
		}
		return auth;
	}
}
