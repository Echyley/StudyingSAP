/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimwebservices.v2.controllers;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserList;
import de.hybris.platform.scimfacades.user.ScimUserFacade;
import de.hybris.platform.scimfacades.utils.ScimGroupUtils;
import de.hybris.platform.scimfacades.utils.ScimUtils;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping(value = "/Users" )
@Tag(description = "/Users", name = "Scim Users")
public class ScimUsersController
{

	private static final Logger LOG = Logger.getLogger(ScimUsersController.class);

	@Resource(name = "scimUserValidator")
	private Validator scimUserValidator;

	@Resource(name = "scimUserNameValidator")
	private Validator scimUserNameValidator;

	@Resource(name = "scimUserFacade")
	private ScimUserFacade scimUserFacade;

	@Resource(name = "scimUserEmailValidator")
	private Validator scimUserEmailValidator;

	@Operation(operationId = "createUser",summary = "Create user in the system")
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST)
	public ScimUser createUser(
			@Parameter(description = "The ScimUser that contains information about the user") @RequestBody final ScimUser scimUser)
	{
		LOG.debug("ScimUsersController.createUser entry. UserID=" + sanitize(scimUser.getId()));
		validate(scimUser, "user", scimUserValidator);
		validate(scimUser.getName(), "userName", scimUserNameValidator);
		validate(ScimUtils.getPrimaryEmail(scimUser.getEmails()), "emails", scimUserEmailValidator);

		return scimUserFacade.createUser(scimUser);
	}

	@Operation(operationId = "updateUser",summary = "Update user in the system")
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	public ScimUser updateUser(@Parameter(description = "User ID of the User") @PathVariable final String userId,
			@Parameter(description = "The ScimUser that contains information about the usder") @RequestBody final ScimUser scimUser)
	{
		LOG.debug("ScimUsersController.updateUser entry. UserID=" + scimUser.getId());

		validate(scimUser, "user", scimUserValidator);
		validate(scimUser.getName(), "userName", scimUserNameValidator);
		validate(ScimUtils.getPrimaryEmail(scimUser.getEmails()), "emails", scimUserEmailValidator);

		if (!StringUtils.equals(userId, scimUser.getId()))
		{
			throw new ScimException("Mismatch in user ids supplied for update " + userId);
		}

		return scimUserFacade.updateUser(userId, scimUser);
	}

	@Operation(operationId = "getUsers", summary = "Get users of type Employee", description = "Endpoint to retrive all users of type Employee from Commerce", security = @SecurityRequirement(name = "oauth", scopes = "oauth2_client_credentials"))
	@ApiResponses({@ApiResponse(responseCode = "200", description = "OK")})
	@GetMapping(produces = {"application/json"})
	public ScimUserList getUsers(@Parameter(description = "The starting point for fetch of users from Commerce on a page.") @RequestParam(value = "startIndex", defaultValue = "1") final int startIndex,
			@Parameter(description = "Total number of users to be fetched from Commerce on a page.") @RequestParam(value = "count", defaultValue = "0") final int count,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		LOG.debug("ScimUsersController.getUsers entry");
		final List<ScimUser> users = scimUserFacade.getUsers();
		ScimUserList scimPaginationData = new ScimUserList();
		scimPaginationData.setItemsPerPage(count);
		scimPaginationData.setStartIndex(startIndex);
		scimPaginationData.setResources(users);
		if(users != null) {
			scimPaginationData.setTotalResults(users.size());
		} else {
			scimPaginationData.setTotalResults(0);
		}
		final int totalResults = scimPaginationData.getTotalResults();
		if(count > totalResults) {
			scimPaginationData.setItemsPerPage(totalResults);
		}
		return ScimGroupUtils.searchScimUserPageData(scimPaginationData);
	}

	@Operation(operationId = "getUser",summary = "Get user from the system")
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public ScimUser getUser(@Parameter(description = "User ID of the User") @PathVariable final String userId)
	{
		LOG.debug("ScimUsersController.getUser entry. UserID=" + sanitize(userId));

		return scimUserFacade.getUser(sanitize(userId));
	}

	@Operation(operationId = "deleteUser",summary = "Delete user from the system")
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	public boolean deleteUser(@Parameter(description = "User ID of the User") @PathVariable final String userId)
	{
		LOG.debug("ScimUsersController.deleteUser entry. " + sanitize(userId));

		return scimUserFacade.deleteUser(sanitize(userId));
	}

	@Operation(operationId = "patchUser",summary = "Patch update user in the system")
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
	public ScimUser patchUser(@Parameter(description = "User ID of the User") @PathVariable final String userId,
			@Parameter(description = "The ScimUser that contains information about the usder") @RequestBody final ScimUser scimUser)
	{
		LOG.debug("ScimUsersController.patchUser entry.");

		return scimUserFacade.updateUser(userId, scimUser);
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
	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
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
