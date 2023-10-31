/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.navigationentrytypes.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.navigationentrytypes.NavigationEntryTypesFacade;
import de.hybris.platform.cmswebservices.data.NavigationEntryTypeData;
import de.hybris.platform.cmswebservices.data.NavigationEntryTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 * Controller to get the supported Navigation Node Entry Types
 * @deprecated since 1811 - no longer needed
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/navigationentrytypes")
@Deprecated(since = "1811", forRemoval = true)
@Hidden
public class NavigationEntryTypesController
{
	@Resource
	private NavigationEntryTypesFacade navigationEntryTypesFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Finds all navigation entry types.", description = "Finds all navigation entry types available.", operationId = "getNavigationEntryTypes")
	@ApiResponse(responseCode = "200", description = "The navigation entry types supported")
	public NavigationEntryTypeListData findAllNavigationEntryTypes()
	{
		final List<NavigationEntryTypeData> navigationEntries = getDataMapper()
				.mapAsList(getNavigationEntryTypesFacade().getNavigationEntryTypes(), NavigationEntryTypeData.class, null);

		final NavigationEntryTypeListData navigationEntryTypeListData = new NavigationEntryTypeListData();
		navigationEntryTypeListData.setNavigationEntryTypes(navigationEntries);
		return navigationEntryTypeListData;
	}


	protected NavigationEntryTypesFacade getNavigationEntryTypesFacade()
	{
		return navigationEntryTypesFacade;
	}

	public void setNavigationEntryTypesFacade(final NavigationEntryTypesFacade navigationEntryTypesFacade)
	{
		this.navigationEntryTypesFacade = navigationEntryTypesFacade;
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
