/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.notificationocc.controllers;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationfacades.facades.SiteMessageFacade;
import de.hybris.platform.notificationocc.dto.SiteMessageSearchResultWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/notifications/sitemessages")
@Tag(name = "Site Messages")
public class SiteMessageController
{

	private static final String MAX_PAGE_SIZE_KEY = "webservicescommons.pagination.maxPageSize";

	@Resource(name = "siteMessageFacade")
	private SiteMessageFacade siteMessageFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@Operation(summary = "Gets all site messages of current customer.", description = "Returns the site messages of current customer.")
	@Parameter(name = "needsTotal", description = "The flag for indicating if total number of results is needed or not.", schema = @Schema(type = "string", allowableValues = {"true", "false"}, defaultValue = "true"), required = false, in = ParameterIn.QUERY)
	@Parameter(name = "pageSize", description = "The number of results returned per page.", required = false, schema = @Schema(type = "string", defaultValue = "10"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The current result page requested.", required = false, schema = @Schema(type = "string", defaultValue = "0"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The sorting method applied to the return results.", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "userId", description = "User identifier or one of the literals : \'current\' for currently authenticated user, \'anonymous\' for anonymous user.", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "baseSiteId", description = "Base site identifier.", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public SiteMessageSearchResultWsDTO siteMessages(final HttpServletRequest request)
	{
		final Map<String, String> parameters = getParameterMapFromRequest(request);
		final SearchPageData searchPageData = getWebPaginationUtils().buildSearchPageData(parameters);
		recalculatePageSize(searchPageData);

		return getDataMapper().map(getSiteMessageFacade().getPaginatedSiteMessages(searchPageData),
				SiteMessageSearchResultWsDTO.class);
	}

	protected void recalculatePageSize(final SearchPageData searchPageData)
	{
		int pageSize = searchPageData.getPagination().getPageSize();
		if (pageSize <= 0)
		{
			final int maxPageSize = Config.getInt(MAX_PAGE_SIZE_KEY, 1000);
			pageSize = getWebPaginationUtils().getDefaultPageSize();
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
			searchPageData.getPagination().setPageSize(pageSize);
		}
	}

	protected Map getParameterMapFromRequest(final HttpServletRequest request)
	{
		final Map<String, String[]> parameterMap = request.getParameterMap();
		final Map<String, String> result = new LinkedHashMap<String, String>();
		if (MapUtils.isEmpty(parameterMap))
		{
			return result;
		}
		for (final Map.Entry<String, String[]> entry : parameterMap.entrySet())
		{
			if (entry.getValue().length > 0)
			{
				result.put(entry.getKey(), entry.getValue()[0]);
			}
		}
		return result;
	}

	protected SiteMessageFacade getSiteMessageFacade()
	{
		return siteMessageFacade;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

}
