/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.controllers;

import de.hybris.platform.assistedservicewebservices.dto.CustomerListWsDTO;
import de.hybris.platform.assistedservicewebservices.helper.CustomerListsHelper;
import de.hybris.platform.commercefacades.user.data.CustomerListData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Controller
public class CustomerListsController extends AbstractAssistedServiceWebServiceController
{
	@Resource(name = "customerListsHelper")
	private CustomerListsHelper customerListsHelper;

	@Operation(operationId = "getCustomerLists", summary = "Retrieves customer lists", description = "Retrieves a list of all customer lists. This can only be done when logged in ASM.")
	@RequestMapping(value = "/customerlists", method = RequestMethod.GET)
	@ResponseBody
	public UserGroupListWsDTO getCustomerLists(
			@Parameter(description = "ID of the base site", required = true) @RequestParam(required = true) final String baseSite)
	{
		final String currentCustomerUid = getCustomerFacade().getCurrentCustomerUid();
		final List<UserGroupData> customerLists = getCustomerListFacade().getCustomerListsForEmployee(currentCustomerUid);
		return getCustomerListsHelper().getCustomerListDto(customerLists);
	}

	@Operation(operationId = "getCustomerListDetails", summary = "Retrieves the details for a customer list", description = "Retrieves the details of a customer list with a valid ID")
	@RequestMapping(value = "/customerlists/{customerlist}", method = RequestMethod.GET)
	@ResponseBody
	public CustomerListWsDTO getCustomerListDetails(
			@Parameter(description = "ID of the customer list", required = true) @PathVariable("customerlist") final String customerlist,
			@Parameter(description = "ID of the base site", required = true) @RequestParam(required = true) final String baseSite)
	{
		final String currentCustomerUid = getCustomerFacade().getCurrentCustomerUid();
		final CustomerListData customerListData = getCustomerListFacade().getCustomerListForUid(customerlist, currentCustomerUid);
		return getCustomerListsHelper().getSingleCustomerListDto(customerListData);
	}

	public CustomerListsHelper getCustomerListsHelper()
	{
		return customerListsHelper;
	}
}
