/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.workflows.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.CMSCommentData;
import de.hybris.platform.cmsfacades.data.CMSWorkflowData;
import de.hybris.platform.cmsfacades.workflow.CMSWorkflowActionFacade;
import de.hybris.platform.cmswebservices.dto.CMSCommentListWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSCommentWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSWorkflowWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller to manage workflow actions. catalogId and versionId are needed as part of the endpoint to set the
 * activeCatalogVersion in the session which is later used by the workflow validators.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/workflows/{workflowCode}/actions")
@Tag(name = "workflow actions")
public class CMSWorkflowActionController
{
	@Resource
	private DataMapper dataMapper;
	@Resource
	private CMSWorkflowActionFacade cmsWorkflowActionFacade;
	@Resource
	private WebPaginationUtils webPaginationUtils;

	@GetMapping(value = "/{actionCode}/comments")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Gets workflow action comments.", description = "Retrieves a list of available workflow action comments for a specific workflow item.",
			operationId = "getActionComments")
	@Parameter(name = "catalogId", description = "The id of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The version of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

			@ApiResponse(responseCode = "404", description = "When no workflow or workflow action is found matching the given code (UnknownIdentifierException).")
			@ApiResponse(responseCode = "200", description = "The dto containing the workflow action comments.")
	public CMSCommentListWsDTO getActionComments( //
			@Parameter(description = "The code of the workflow", required = true)
			@PathVariable
			final String workflowCode, //
			@Parameter(description = "The code of the workflow action", required = true)
			@PathVariable
			final String actionCode, //
			@Parameter(description = "Pageable DTO", hidden = true)
			@ModelAttribute
			final PageableWsDTO pageableInfo)
	{
		final PageableData pageableData = getDataMapper().map(pageableInfo, PageableData.class);
		final SearchResult<CMSCommentData> actionCommentsSearchResult = getCmsWorkflowActionFacade().getActionComments(workflowCode,
				actionCode, pageableData);

		final CMSCommentListWsDTO cmsCommentListWsDTO = new CMSCommentListWsDTO();
		cmsCommentListWsDTO.setComments( //
				actionCommentsSearchResult.getResult().stream()//
						.map(comment -> getDataMapper().map(comment, CMSCommentWsDTO.class)) //
						.collect(Collectors.toList()) //
		);

		cmsCommentListWsDTO.setPagination(getWebPaginationUtils().buildPagination(actionCommentsSearchResult));
		return cmsCommentListWsDTO;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Gets a list of actions.", description = "Retrieves a list of available actions for a given workflow item, catalog and version ids.",
			operationId = "getActionsByWorkflowCode")
	@Parameter(name = "catalogId", description = "The id of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The version of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "404", description = "When no workflow is found matching the given code (UnknownIdentifierException).")
	@ApiResponse(responseCode = "200", description = "The dto containing the workflow items actions and decisions.")
	public CMSWorkflowWsDTO getActionsByWorkflowCode( //
			@Parameter(description = "The code of the workflow", required = true)
			@PathVariable
			final String workflowCode)
	{
		final CMSWorkflowData workflowActionsData = getCmsWorkflowActionFacade().getActionsForWorkflowCode(workflowCode);
		return getDataMapper().map(workflowActionsData, CMSWorkflowWsDTO.class);
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected CMSWorkflowActionFacade getCmsWorkflowActionFacade()
	{
		return cmsWorkflowActionFacade;
	}

	public void setCmsWorkflowActionFacade(final CMSWorkflowActionFacade cmsWorkflowActionFacade)
	{
		this.cmsWorkflowActionFacade = cmsWorkflowActionFacade;
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
