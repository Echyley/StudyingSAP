/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.workflows.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.CMSWorkflowData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.workflow.CMSWorkflowFacade;
import de.hybris.platform.cmswebservices.dto.CMSWorkflowListWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSWorkflowWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to manage workflows for CMSItems. catalogId and versionId are needed as part of the endpoint to set the
 * activeCatalogVersion in the session which is later used by the workflow validators.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/workflows")
@Tag(name = "workflows")
public class CMSWorkflowController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CMSWorkflowController.class);
	private static final String VALIDATION_EXCEPTION = "Validation exception";

	@Resource
	private DataMapper dataMapper;
	@Resource
	private CMSWorkflowFacade cmsWorkflowFacade;
	@Resource
	private WebPaginationUtils webPaginationUtils;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation( //
			summary = "Creates and start a new workflow.", description = "Generates a workflow instance containing CmsItems. The workflow is automatically started upon generation.",
			operationId = "doCreateWorkflow")
	@Parameter(name = "catalogId", description = "The id of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The version of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "201", description = "The dto containing the workflow info.")
	public CMSWorkflowWsDTO createWorkflow(
			@Parameter(description = "The DTO object containing all the information about the workflow to create", required = true) //
			@RequestBody //
			final CMSWorkflowWsDTO workflowInfo)
	{
		try
		{
			final CMSWorkflowData requestData = getDataMapper().map(workflowInfo, CMSWorkflowData.class);

			final CMSWorkflowData newWorkflowData = getCmsWorkflowFacade().createAndStartWorkflow(requestData);

			return getDataMapper().map(newWorkflowData, CMSWorkflowWsDTO.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@PutMapping(value = "/{workflowCode}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Updates an existing workflow.", description = "Provides a new workflow information for an existing workflow item.", operationId = "replaceWorkflow")
	@Parameter(name = "catalogId", description = "The id of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The version of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "404", description = "When no workflow is found matching the given code (UnknownIdentifierException).")
	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "The dto containing the workflow info.")
	public CMSWorkflowWsDTO editWorkflow(@Parameter(description = "The code of the workflow", required = true)
	@PathVariable
	final String workflowCode,
			@Parameter(description = "The DTO object containing all the information about the workflow to edit", required = true) //
			@RequestBody //
			final CMSWorkflowWsDTO dto) throws UnknownIdentifierException
	{
		try
		{
			final CMSWorkflowData requestData = getDataMapper().map(dto, CMSWorkflowData.class);
			final CMSWorkflowData newWorkflowData = getCmsWorkflowFacade().editWorkflow(workflowCode, requestData);

			return getDataMapper().map(newWorkflowData, CMSWorkflowWsDTO.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@GetMapping(params =
	{ "pageSize", "currentPage" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Finds all workflows containing the given attachments.", description = "Retrieves a list of available workflow "
					+ "instances that has the given CMSItem uuids as an attachment to the workflow.",
			operationId = "getAllWorkflowsForAttachments")
	@Parameter(name = "catalogId", description = "The id of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The version of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageSize", description = "Page size for paging", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "Catalog on which to search", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The requested ordering for the search results.", schema = @Schema(type = "string"), in = ParameterIn.QUERY)

	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "The list of dtos containing the workflow info. Never null.")
	public CMSWorkflowListWsDTO findAllWorkflowsForAttachments( //
			@Parameter(name = "attachment", description = "The uuid of the CMSItem attached to the workflow", required = false) //
			@RequestParam(required = false) //
			final String attachment, //
			@Parameter(name = "statuses", description = "The list of workflow statues. "
					+ "When none provided, the default statuses are set to RUNNING and PAUSED. This will return all active workflows.", required = false)
			@RequestParam(required = false, defaultValue = "RUNNING,PAUSED") //
			final List<String> statuses, //
			@Parameter(description = "Pageable DTO", required = true)
			@ModelAttribute
			final PageableWsDTO pageableInfo)
	{
		try
		{
			final CMSWorkflowData workflowData = new CMSWorkflowData();
			workflowData.setAttachments(Objects.nonNull(attachment) ? Arrays.asList(attachment) : Collections.emptyList());
			workflowData.setStatuses(statuses);

			final PageableData pageableData = getDataMapper().map(pageableInfo, PageableData.class);
			final SearchResult<CMSWorkflowData> workflowSearchResult = getCmsWorkflowFacade() //
					.findAllWorkflows(workflowData, pageableData);

			final CMSWorkflowListWsDTO workflowList = new CMSWorkflowListWsDTO();
			workflowList.setWorkflows(workflowSearchResult.getResult().stream() //
					.map(data -> getDataMapper().map(data, CMSWorkflowWsDTO.class))//
					.collect(Collectors.toList()));
			workflowList.setPagination(getWebPaginationUtils().buildPagination(workflowSearchResult));
			return workflowList;
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@GetMapping(value = "/{workflowCode}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Gets a workflow item.", description = "Retrieves a specific instance of the workflow for a given workflow code.",
			operationId = "getWorkflowByCode")
	@Parameter(name = "catalogId", description = "The id of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The version of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "404", description = "When no workflow is found matching the given code (UnknownIdentifierException).")
	@ApiResponse(responseCode = "200", description = "The dto containing the workflow item.")
	public CMSWorkflowWsDTO getWorkflowByCode( //
			@Parameter(description = "The code of the workflow", required = true)
			@PathVariable
			final String workflowCode)
	{
		final CMSWorkflowData workflowData = getCmsWorkflowFacade().getWorkflowForCode(workflowCode);
		return getDataMapper().map(workflowData, CMSWorkflowWsDTO.class);
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected CMSWorkflowFacade getCmsWorkflowFacade()
	{
		return cmsWorkflowFacade;
	}

	public void setCmsWorkflowFacade(final CMSWorkflowFacade cmsWorkflowFacade)
	{
		this.cmsWorkflowFacade = cmsWorkflowFacade;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}
}
