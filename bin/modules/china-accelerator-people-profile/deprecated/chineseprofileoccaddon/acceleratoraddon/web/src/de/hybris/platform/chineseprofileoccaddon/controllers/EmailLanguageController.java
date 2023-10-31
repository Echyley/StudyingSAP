/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileoccaddon.controllers;

import de.hybris.platform.chineseprofilefacades.customer.ChineseCustomerFacade;
import de.hybris.platform.chineseprofileoccaddon.utils.ChineseProfileUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


/**
 * Web Services Controller to expose the functionality of setting customer email language.
 *
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@Tag(name = "Email Language")
public class EmailLanguageController
{
	@Resource(name = "chineseCustomerFacade")
	private ChineseCustomerFacade customerFacade;

	@Resource(name = "emailLanguageValidator")
	private Validator emailLanguageValidator;

	private static final String EMAIL_LANGUAGE = "languageIsoCode";

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/emaillanguage", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Sets email language for the customer.", description = "Sets email language for the customer.")
	@ApiBaseSiteIdAndUserIdParam
	public void replaceCustomerEmailLanguage(
			@Parameter(description = "Email language iso code", required = true) @RequestParam final String languageIsoCode,
			final HttpServletRequest request)
	{
		ChineseProfileUtils.validate(languageIsoCode, EMAIL_LANGUAGE, emailLanguageValidator);
		customerFacade.saveCurrentUserWithEmailLanguage(languageIsoCode);
	}

}
