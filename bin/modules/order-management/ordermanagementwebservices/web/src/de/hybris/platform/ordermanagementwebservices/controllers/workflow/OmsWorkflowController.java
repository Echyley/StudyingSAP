/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementwebservices.controllers.workflow;

import de.hybris.platform.ordermanagementfacades.workflow.OmsWorkflowFacade;
import de.hybris.platform.ordermanagementfacades.workflow.data.WorkflowActionData;
import de.hybris.platform.ordermanagementfacades.workflow.data.WorkflowActionDataList;
import de.hybris.platform.ordermanagementfacades.workflow.data.WorkflowCodesDataList;
import de.hybris.platform.ordermanagementwebservices.controllers.OmsBaseController;
import de.hybris.platform.ordermanagementwebservices.dto.workflow.WorkflowActionListWsDto;
import de.hybris.platform.ordermanagementwebservices.dto.workflow.WorkflowCodesListWsDto;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * WebResource exposing {@link OmsWorkflowFacade}
 * https://host:port/ordermanagementwebservices/workflows
 */
@Controller
@RequestMapping(value = "/workflows")
@Tag(name = "Workflow Operations")
public class OmsWorkflowController extends OmsBaseController
{
	@Resource
	private OmsWorkflowFacade omsWorkflowFacade;

	/**
	 * Request to get all active workflow actions assigned to the current user
	 */
	@RequestMapping(value = "/actions", method = RequestMethod.GET)
	@ResponseBody
	@Secured(ADMIN_GROUP)
	@Operation(operationId = "getWorkflowActions", summary = "Finds all active workflow actions for the current user", description = "Finds all active workflow actions for the current user.")
	public WorkflowActionListWsDto getWorkflowActions()
	{
		final WorkflowActionDataList workflowActionDataList = new WorkflowActionDataList();
		final List<WorkflowActionData> workflowActions = omsWorkflowFacade.getWorkflowActions();
		workflowActionDataList.setWorkflowActions(workflowActions);
		return dataMapper.map(workflowActionDataList, WorkflowActionListWsDto.class);
	}

	/**
	 * Request to decide the workflow action belonging to the given workflow with the provided decision.
	 *
	 * @param workflowCode
	 * 		the workflow which contains the action to be decided
	 * @param workflowDecisionCode
	 * 		the decision which is to be taken when the action is decided
	 */
	@RequestMapping(value = "/{workflowCode}/{workflowDecisionCode}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Secured(ADMIN_GROUP)
	@Operation(operationId = "decideWorkflowAction", summary = "Performs the workflow actions with the provided decision.", description = "Performs the workflow actions with the provided decision.")
	public void decideWorkflowAction(
			@Parameter(description = "Workflow code", required = true) @PathVariable @NotNull final String workflowCode,
			@Parameter(description = "Workflow Decision code", required = true) @PathVariable @NotNull final String workflowDecisionCode)
	{
		getOmsWorkflowFacade().decideAction(workflowCode, workflowDecisionCode);
	}

	/**
	 * Request to decide the workflow actions belonging to the given list of workflows with the provided decision.
	 *
	 * @param workflowCodesListWsDto
	 * 		the list of codes for workflows containing actions to be decided
	 * @param workflowDecisionCode
	 * 		the decision which is to be taken when the action is decided
	 */
	@RequestMapping(value = "/decide-action/{workflowDecisionCode}", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Secured(ADMIN_GROUP)
	@Operation(operationId = "decideWorkflowActions", summary = "Performs the workflow actions belonging to a list of workflows with the provided decision.", description = "Performs the workflow actions belonging to a list of workflows with the provided decision.")
	public void decideWorkflowActions(
			@Parameter(description = "The DecideWorkflowActionsRequestWsDto containing information for deciding workflow actions", required = true) @RequestBody @NotNull final WorkflowCodesListWsDto workflowCodesListWsDto,
			@Parameter(description = "Workflow Decision code", required = true) @PathVariable @NotNull final String workflowDecisionCode,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final WorkflowCodesDataList workflowCodesDataList = dataMapper
				.map(workflowCodesListWsDto, WorkflowCodesDataList.class, fields);
		getOmsWorkflowFacade().decideActions(workflowCodesDataList, workflowDecisionCode);
	}

	protected OmsWorkflowFacade getOmsWorkflowFacade()
	{
		return omsWorkflowFacade;
	}

}
