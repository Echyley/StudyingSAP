/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.languages.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmswebservices.data.LanguageListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to deal with languages.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/languages")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
@Tag(name = "languages")
public class LanguageController
{
	@Resource
	private LanguageFacade languageFacade;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Gets languages.", description = "Retrieves a list of available languages supported by a storefront.",
					operationId = "getLanguages")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "200", description = "List of languages.")
	public LanguageListData getLanguages()
	{
		final LanguageListData languageList = new LanguageListData();
		languageList.setLanguages(getLanguageFacade().getLanguages());
		return languageList;
	}

	protected LanguageFacade getLanguageFacade()
	{
		return languageFacade;
	}

	public void setLanguageFacade(final LanguageFacade languageFacade)
	{
		this.languageFacade = languageFacade;
	}

}
