/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.controllers;

import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.smarteditwebservices.configuration.facade.SmarteditConfigurationFacade;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;
import de.hybris.platform.smarteditwebservices.dto.ConfigurationDataListWsDto;
import de.hybris.platform.smarteditwebservices.security.IsAuthorizedAdmin;
import de.hybris.platform.smarteditwebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * Controller to manage cms configuration data
 */
@Controller
@RequestMapping(API_VERSION + "/configurations")
@Tag(name = "configurations")
public class ConfigurationController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

	@Resource
	private SmarteditConfigurationFacade smarteditConfigurationFacade;

	@GetMapping
	@ResponseBody
	@IsAuthorizedCmsManager
	@Operation(operationId = "getConfigurations", summary = "Get All Configurations", description = "Endpoint to retrieve all cms configuration data")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin or CMS Manager to access this resource") })
	public ConfigurationDataListWsDto loadAll()
	{
		final ConfigurationDataListWsDto configurations = new ConfigurationDataListWsDto();
		configurations.setConfigurations(getSmarteditConfigurationFacade().findAll());
		return configurations;
	}

	@PostMapping
	@ResponseBody
	@IsAuthorizedAdmin
	@Operation(operationId = "saveConfiguration", summary = "Save a Configuration", description = "Endpoint to create cms configuration data")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin to access this resource") })
	public ConfigurationData save( //
			@Parameter(description = "Configuration data", required = true)
			@RequestBody
			final ConfigurationData data)
	{
		try
		{
			return getSmarteditConfigurationFacade().create(data);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@GetMapping(value = "/{key:.+}")
	@ResponseBody
	@IsAuthorizedCmsManager
	@Operation(operationId = "getConfigurationByKey", summary = "Find a Configuration by Key", description = "Endpoint to retrieve cms configuration data that matches the given key value")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin or CMS Manager to access this resource") })
	public ConfigurationData findByKey( //
			@Parameter(description = "Configuration data identifier", required = true)
			@PathVariable("key")
			final String key)
	{
		return getSmarteditConfigurationFacade().findByUid(key);
	}

	@PutMapping(value = "/{key:.+}")
	@ResponseBody
	@IsAuthorizedAdmin
	@Operation(operationId = "updateConfiguration", summary = "Update a Configuration", description = "Endpoint to update cms configuration data")
	@ApiResponses(value =
	{ //
			@ApiResponse(responseCode = "400", description = "Configuration data input is invalid"),
			@ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin to access this resource") //
	})
	public ConfigurationData update( //
			@Parameter(description = "Configuration data", required = true)
			@RequestBody
			final ConfigurationData data, //
			@Parameter(description = "Configuration data identifier", required = true)
			@PathVariable("key")
			final String key)
	{
		try
		{
			return getSmarteditConfigurationFacade().update(key, data);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@DeleteMapping(value = "/{key:.+}")
	@ResponseBody
	@IsAuthorizedAdmin
	@Operation(operationId = "removeConfiguration", summary = "Remove a Configuration", description = "Endpoint to remove cms configuration data that matches the given key")
	@ApiResponses(value =
	{ @ApiResponse(responseCode = "401", description = "Must be authenticated as an Admin to access this resource") })
	public void delete( //
			@Parameter(description = "Configuration data identifier", required = true)
			@PathVariable("key")
			final String key)
	{
		getSmarteditConfigurationFacade().delete(key);
	}

	public SmarteditConfigurationFacade getSmarteditConfigurationFacade()
	{
		return smarteditConfigurationFacade;
	}

	public void setSmarteditConfigurationFacade(final SmarteditConfigurationFacade smarteditConfigurationFacade)
	{
		this.smarteditConfigurationFacade = smarteditConfigurationFacade;
	}
}
