/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.users.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.users.UserFacade;
import de.hybris.platform.cmswebservices.dto.UserDataWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;


/**
 * Controller to retrieve Users.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/users")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 300)
@Tag(name = "users")
public class UserController
{
	@Resource
	private UserFacade cmsUserFacade;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{userId:.+}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Gets user by id.", description = "Retrieves a specific user instance that matches given id.", operationId = "getUserDataById")

	@ApiResponse(responseCode = "400", description = "When the user was not found (CMSItemNotFoundException) or when there was a problem during conversion (ConversionException).")
	@ApiResponse(responseCode = "200", description = "UserDataInfo")
	public UserDataWsDTO getUserDataById(
			@Parameter(description = "The unique identifier of the user", required = true) @PathVariable final String userId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getCmsUserFacade().getUserById(userId), UserDataWsDTO.class);
	}

	protected UserFacade getCmsUserFacade()
	{
		return cmsUserFacade;
	}

	public void setCmsUserFacade(final UserFacade cmsUserFacade)
	{
		this.cmsUserFacade = cmsUserFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
