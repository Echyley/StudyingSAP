/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedservicewebservices.dto.ASMPointOfServiceListWsDTO;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Agent Controller")
public class AgentController
{

	@Resource
	private AssistedServiceFacade assistedServiceFacade;

	@Resource
	private DataMapper dataMapper;

	@Operation(operationId = "getPointOfServices", summary = "Retrieves agent's assigned point of services", description = "Retrieves a list of point of services assigned to the agent")
	@RequestMapping(value = "agents/{agentId}/pointOfServices", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ASMPointOfServiceListWsDTO getPointOfServices(
			@Parameter(description = "Identifier of the agent or the literal : 'current' for currently authenticated agent", required = true) @PathVariable("agentId") final String userId)
	{
		final PointOfServiceDataList pointsOfServiceList = new PointOfServiceDataList();
		pointsOfServiceList.setPointOfServices(getAssistedServiceFacade().getStoresByAgentId(userId));

		return getDataMapper().map(pointsOfServiceList, ASMPointOfServiceListWsDTO.class);
	}

	public DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public AssistedServiceFacade getAssistedServiceFacade()
	{
		return assistedServiceFacade;
	}
	
}
