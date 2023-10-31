/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimwebservices.v2.controllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hybris.platform.scimfacades.ScimGroup;
import de.hybris.platform.scimfacades.ScimGroupList;
import de.hybris.platform.scimfacades.group.ScimGroupFacade;
import de.hybris.platform.scimfacades.utils.ScimGroupUtils;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "/Groups", name = "Scim Groups")
@RequestMapping(value = "/Groups")
public class ScimGroupsController {
	private static final Logger LOG = Logger.getLogger(ScimGroupsController.class);

	@Resource(name = "scimGroupFacade")
	private ScimGroupFacade scimGroupFacade;


	@Operation(operationId = "createScimGroup",summary = "Create group in Commerce", description = "Endpoint to create a scim group in Commerce", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
	@PostMapping(produces = {"application/json"}, consumes = {"application/json"})
	public ScimGroup createGroup(
			@Parameter(description = "The ScimGroup that contains information about the group") @RequestBody final ScimGroup scimGroup)
	{
		LOG.debug("ScimGroupController.createGroup entry. GroupID=" + sanitize(scimGroup.getId()));
		validate(sanitize(scimGroup.getId()));
		scimGroup.setId(sanitize(scimGroup.getId()));
		return scimGroupFacade.createGroup(scimGroup);
	}

	@Operation(operationId = "replaceScimGroup", summary = "Update group in Commerce", description = "Endpoint to update details of a scim group in Commerce for which Id is provided", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
	@PutMapping(value = {"/{groupId}"}, produces = {"application/json"}, consumes = {"application/json"})
	public ScimGroup updateGroup(@Parameter(description = "Group ID of the Group", required = true) @PathVariable final String groupId,
			@Parameter(description = "The ScimGroup that contains the information to be updated.") @RequestBody final ScimGroup scimGroup)
	{
		LOG.debug("ScimGroupsController.updateGroup entry. GroupID=" + scimGroup.getId());

		if (!StringUtils.equals(groupId, scimGroup.getId()))
		{
			throw new ScimException("Mismatch in group ids supplied for update " + groupId);
		}

		return scimGroupFacade.updateGroup(sanitize(groupId), scimGroup);
	}

	@Operation(operationId = "getScimGroup", summary = "Get group from Commerce", description = "Endpoint to get the details of scim group along with members from Commerce according to the group-id provided", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
	@GetMapping(value = {"/{groupId}"}, produces = {"application/json"})
	public ScimGroup getGroup(@Parameter(description = "Group ID of the Scim Group to fetch.", required = true) @PathVariable final String groupId)
	{

		LOG.debug("ScimGroupsController.getGroup entry. GroupID=" + sanitize(groupId));

		return scimGroupFacade.getGroup(sanitize(groupId));
	}

	@Operation(operationId = "getScimGroups", summary = "Get all scim groups from commerce", description = "Endpoint to retrieve all existing scim groups along with their members from Commerce.", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
	@GetMapping(produces = {"application/json"})
	public ScimGroupList getGroups(@Parameter(description = "The starting point for fetch of scim groups from Commerce on a page.") @RequestParam(value = "startIndex", defaultValue = "1") final int startIndex,
			@Parameter(description = "Total number of scim groups to be fetched from Commerce on a page.") @RequestParam(value = "count", defaultValue = "0") final int count,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		LOG.debug("ScimGroupsController.getGroups entry");
		final List<ScimGroup> groups = scimGroupFacade.getGroups();
		
		ScimGroupList scimPaginationData = new ScimGroupList();
		scimPaginationData.setItemsPerPage(count);
		scimPaginationData.setStartIndex(startIndex);
		scimPaginationData.setResources(groups);
		if(groups != null) {
			scimPaginationData.setTotalResults(groups.size());
		} else {
			scimPaginationData.setTotalResults(0);
		}
		final int totalResults = scimPaginationData.getTotalResults();
		if(count > totalResults) {
			scimPaginationData.setItemsPerPage(totalResults);
		}

		return ScimGroupUtils.searchScimGroupPageData(scimPaginationData);
	}
	
	@Operation(operationId = "removeScimGroup", summary = "Delete scim group from Commerce", description = "Endpoint to delete scim group from Commerce for which id is provided", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{groupId}", method = RequestMethod.DELETE)
	public void deleteGroup(@Parameter(description = "Group ID of the Scim Group to delete.", required = true) @PathVariable final String groupId)
	{
		LOG.debug("ScimGroupsController.deleteUser entry. " + sanitize(groupId));

		scimGroupFacade.deleteGroup(sanitize(groupId));
	}

	@Operation(operationId = "updateScimGroup", summary = "Patch update scim group in commerce", description = "Endpoint to update scim group in Commerce with the details provided", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{groupId}", method = RequestMethod.PATCH, produces = {"application/json"}, consumes = {"application/json"})
	public ScimGroup patchGroup(@Parameter(description = "Group ID of the Group", required = true) @PathVariable final String groupId,
			@Parameter(description = "The ScimGroup that contains information about the group") @RequestBody final ScimGroup scimGroup)
	{
		LOG.debug("ScimGroupsController.patchGroup entry.");

		return scimGroupFacade.updateGroup(sanitize(groupId), scimGroup);
	}

	/**
	 * Validates the object by using the passed validator
	 *
	 * @param object
	 *           the object to be validated
	 * @param objectName
	 *           the object name
	 * @param validator
	 *           validator which will validate the object
	 */
	protected void validate(final String id)
	{
		Pattern p = Pattern.compile("[^A-Za-z0-9]");
	    Matcher m = p.matcher(id);
		if(id == null || id.trim().isEmpty() || m.find()) {
			throw new WebserviceValidationException(new InterceptorException("Group with Id=" + id + "is invalid"));
		}
	}

	/**
	 * Method to sanitize the input string
	 *
	 * @param input
	 *           the input string
	 * @return String sanitized string
	 */
	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}
}