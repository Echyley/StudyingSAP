/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.workflow.controller;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.data.CMSWorkflowEditableItemData;
import de.hybris.platform.cmsfacades.workflow.CMSWorkflowFacade;
import de.hybris.platform.cmssmarteditwebservices.dto.CMSWorkflowEditableItemListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.CMSWorkflowEditableItemWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping(value = API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/workfloweditableitems")
@Tag(name = "workflows")
public class WorkflowEditableItemsController
{
	@Resource
	private DataMapper dataMapper;

	@Resource
	private CMSWorkflowFacade cmsWorkflowFacade;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			operationId = "getObjectListOfEditableItems",
			summary = "Retrieves the list of objects that tells whether a particular item is editable in any workflow or not", //
			description = "Retrieves the list of objects that tells whether a particular item is editable in any workflow or not")
	@Parameter(name = "catalogId", description = "The uid of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The uid of the catalog version", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "404", description = "When no cms item is found matching the given uid (UnknownIdentifierException).")
	@ApiResponse(responseCode = "200", description = "The dto containing the list of objects that tells whether a particular item is editable in any workflow or not.")
	public CMSWorkflowEditableItemListWsDTO getEditableWorkflowItems( //
			@Parameter(description = "List of item uids", required = true)
			@RequestParam("itemUids")
			final List<String> itemUids)
	{
		final List<CMSWorkflowEditableItemData> editableItems = getCmsWorkflowFacade().getSessionUserEditableItems(itemUids);
		final CMSWorkflowEditableItemListWsDTO currentUserEditableItemListWsDTO = new CMSWorkflowEditableItemListWsDTO();
		currentUserEditableItemListWsDTO
				.setEditableItems(getDataMapper().mapAsList(editableItems, CMSWorkflowEditableItemWsDTO.class, null));
		return currentUserEditableItemListWsDTO;
	}

	protected CMSWorkflowFacade getCmsWorkflowFacade()
	{
		return cmsWorkflowFacade;
	}

	public void setCmsWorkflowFacade(final CMSWorkflowFacade cmsWorkflowFacade)
	{
		this.cmsWorkflowFacade = cmsWorkflowFacade;
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
