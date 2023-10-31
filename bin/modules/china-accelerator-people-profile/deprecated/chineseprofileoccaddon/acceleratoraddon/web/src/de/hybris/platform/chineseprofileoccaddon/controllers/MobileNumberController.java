/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileoccaddon.controllers;

import de.hybris.platform.chineseprofilefacades.customer.ChineseCustomerFacade;
import de.hybris.platform.chineseprofilefacades.data.MobileNumberVerificationData;
import de.hybris.platform.chineseprofileoccaddon.utils.ChineseProfileUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * Web Services Controller to expose the functionality of binding or unbinding customer mobile number.
 *
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/mobilenumber")
@Tag(name = "Mobile Number")
public class MobileNumberController
{
	@Resource(name = "chineseCustomerFacade")
	private ChineseCustomerFacade customerFacade;

	@Resource(name = "mobileNumberBindValidator")
	private Validator mobileNumberBindValidator;

	@Resource(name = "mobileNumberUnbindValidator")
	private Validator mobileNumberUnbindValidator;

	@Resource(name = "verificationCodeValidator")
	private Validator verificationCodeValidator;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	private static final String MOBILE_NUMBER_OBJ = "mobileNumberVerificationCode";

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/verificationcode", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Sends verification code.", description = "Sends verification code for binding or unbinding mobile number.")
	@ApiBaseSiteIdAndUserIdParam
	public void sendVerificationCode(
			@Parameter(description = "Customer's mobile number", required = true) @RequestParam final String mobileNumber,
			final HttpServletRequest request)
	{
		final MobileNumberVerificationData data = new MobileNumberVerificationData();
		data.setMobileNumber(mobileNumber);
		ChineseProfileUtils.validate(data, MOBILE_NUMBER_OBJ, verificationCodeValidator);

		final String verificationCode = customerFacade.generateVerificationCode();

		data.setVerificationCode(verificationCode);
		data.setMobileNumber(mobileNumber);

		customerFacade.saveVerificationCode(data);
		customerFacade.sendVerificationCode(mobileNumber, verificationCode);

	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	@Operation(summary = "Binds mobile number for the customer.", description = "Binds mobile number for the customer.")
	@ApiBaseSiteIdAndUserIdParam
	public UserWsDTO bindMobileNumber(
			@Parameter(description = "Customer's mobile number", required = true) @RequestParam final String mobileNumber,
			@Parameter(description = "Verification code", required = true) @RequestParam final String verificationCode,
			@ApiFieldsParam  @RequestParam(defaultValue = "DEFAULT") final String fields,
			final HttpServletRequest request)
	{
		final MobileNumberVerificationData data = new MobileNumberVerificationData();
		data.setMobileNumber(mobileNumber);
		data.setVerificationCode(verificationCode);
		ChineseProfileUtils.validate(data, MOBILE_NUMBER_OBJ, mobileNumberBindValidator);

		customerFacade.saveMobileNumber(mobileNumber);

		customerFacade.removeVerificationCode(mobileNumber);
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		return getDataMapper().map(customerData, UserWsDTO.class, fields);
	}


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Unbinds mobile number for the customer.", description = "Unbinds mobile number for the customer.")
	@ApiBaseSiteIdAndUserIdParam
	public void unbindMobileNumber(
			@Parameter(description = "Verification code", required = true) @RequestParam final String verificationCode,
			final HttpServletRequest request)
	{

		final MobileNumberVerificationData data = new MobileNumberVerificationData();
		data.setVerificationCode(verificationCode);
		ChineseProfileUtils.validate(data, MOBILE_NUMBER_OBJ, mobileNumberUnbindValidator);

		final String mobileNumber = customerFacade.getCurrentCustomer().getMobileNumber();
		customerFacade.unbindMobileNumber();
		customerFacade.removeVerificationCode(mobileNumber);
	}



	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

}
