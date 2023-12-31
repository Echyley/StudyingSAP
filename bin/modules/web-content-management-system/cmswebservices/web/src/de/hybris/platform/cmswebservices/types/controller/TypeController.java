/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.types.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to deal with component types.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/types")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 360)
@Tag(name = "types")
public class TypeController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TypeController.class);

	@Resource
	private ComponentTypeFacade componentTypeFacade;
	@Resource
	private DataMapper dataMapper;

	@GetMapping
	@ResponseBody
	@Operation(summary = "Gets all component types.", description = "Retrieves a list of all component types.", operationId = "getAllComponentTypes")

	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a list of ComponentTypeData, never") //
	public ComponentTypeListData getAllComponentTypes( //
			@Parameter(description = "Read only mode for attributes", schema = @Schema(defaultValue = "false"))
			@RequestParam(value = "readOnly", required = false, defaultValue = "false")
			final Boolean readOnly)
	{
		final List<ComponentTypeData> componentTypes = getDataMapper() //
				.mapAsList(getComponentTypeFacade().getAllCmsItemTypes(Arrays.asList(StructureTypeCategory.values()), readOnly),
						ComponentTypeData.class, null);

		final ComponentTypeListData listDto = new ComponentTypeListData();
		listDto.setComponentTypes(componentTypes);
		return listDto;
	}

	@GetMapping(params =
	{ "category" })
	@ResponseBody
	@Operation(summary = "Gets all component types by category.", description = "Finds all CMS component types filtered by a given category.",
					operationId = "getAllComponentTypesByCategory")

	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a list of ComponentTypeData, never null") //

	public ComponentTypeListData getAllComponentTypesByCategory(
			@Parameter(description = "The component type category of the types to be returned.", required = true)
			@RequestParam(value = "category")
			final String category, //
			@Parameter(description = "Read only mode for attributes", schema = @Schema(defaultValue = "false"))
			@RequestParam(value = "readOnly", required = false, defaultValue = "false")
			final Boolean readOnly)
	{
		final List<ComponentTypeData> componentTypes = getDataMapper().mapAsList(getComponentTypeFacade().getAllCmsItemTypes(
				Collections.singletonList(StructureTypeCategory.valueOf(category)), readOnly), ComponentTypeData.class, null);

		final ComponentTypeListData listDto = new ComponentTypeListData();
		listDto.setComponentTypes(componentTypes);
		return listDto;
	}

	@GetMapping(value = "/{code}")
	@ResponseBody
	@Operation(summary = "Gets component type by code.", description = "Retrieves a specific instance of the CMS component type for a given code.",
					operationId = "getComponentTypeByCode")

	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a ComponentTypeData DTO")
	@ApiResponse(responseCode = "400", description = "When the code provided does not match any existing type(ComponentTypeNotFoundException).") //

	public ComponentTypeData getComponentTypeByCode( //
			@Parameter(description = "Component type code", required = true)
			@PathVariable
			final String code, //
			@Parameter(description = "Read only mode for attributes", schema = @Schema(defaultValue = "false"))
			@RequestParam(value = "readOnly", required = false, defaultValue = "false")
			final Boolean readOnly) throws ComponentTypeNotFoundException
	{
		return getDataMapper().map(getComponentTypeFacade().getCmsItemTypeByCodeAndMode(code, null, readOnly),
				ComponentTypeData.class);
	}

	@GetMapping(params =
	{ "code", "mode" })
	@ResponseBody
	@Operation(summary = "Gets component type by code and mode.", description = "Retrieves a specific instance of the CMS component type by code and mode.",
					operationId = "getComponentTypeByCodeAndMode")

	@ApiResponse(responseCode = "400", description = "When the code provided does not match any existing type (ComponentTypeNotFoundException).")
	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a ComponentTypeData DTO; or and empty list if the type and mode are not found.") //
	public ComponentTypeListData getComponentTypeByCodeAndMode( //
			@Parameter(description = "Component type code", required = true)
			@RequestParam(value = "code")
			final String code, //
			@Parameter(description = "The mode of the structure type", required = true)
			@RequestParam(value = "mode")
			final String mode, //
			@Parameter(description = "Read only mode for attributes", schema = @Schema(defaultValue = "false"))
			@RequestParam(value = "readOnly", required = false, defaultValue = "false")
			final Boolean readOnly) throws ComponentTypeNotFoundException
	{
		final List<ComponentTypeData> componentTypes = new ArrayList<>();
		final ComponentTypeListData componentTypeListData = new ComponentTypeListData();
		try
		{
			componentTypes.add(getDataMapper().map(getComponentTypeFacade().getCmsItemTypeByCodeAndMode(code, mode, readOnly),
					ComponentTypeData.class));
		}
		catch (final ComponentTypeNotFoundException e)
		{
			LOGGER.debug("Component Type not found for the provided type code and mode", e);
		}
		componentTypeListData.setComponentTypes(componentTypes);
		return componentTypeListData;
	}

	public ComponentTypeFacade getComponentTypeFacade()
	{
		return componentTypeFacade;
	}

	public void setComponentTypeFacade(final ComponentTypeFacade componentTypeFacade)
	{
		this.componentTypeFacade = componentTypeFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

}
