/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.controllers;

import static de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants.DEFAULT_CURRENT_PAGE;
import static de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants.DEFAULT_PAGE_SIZE;
import static de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants.QUERY;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedservicefacades.user.data.AutoSuggestionCustomerData;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceCartBindException;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.assistedservicewebservices.dto.CustomerRegistrationFormWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.CustomerSearchPageWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.CustomerSuggestionWsDTO;
import de.hybris.platform.assistedservicewebservices.helper.CustomerHelper;
import de.hybris.platform.assistedservicewebservices.utils.PaginationUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCustomerNameStrategy;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


@Controller
public class CustomersController extends AbstractAssistedServiceWebServiceController
{
	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	@Resource(name = "customerHelper")
	private CustomerHelper customerHelper;

	@Resource
	private Validator customerRegistrationDataValidator;

	@Resource
	private DefaultCustomerNameStrategy defaultCustomerNameStrategy;

	@Operation(operationId = "getPageableCustomers", summary = "Retrieves customers", description = "Retrieves a paginated list of customers based on the provided query parameters. If a query term is present it will return customers based on that provided value. If orderId parameter is present, it will ignore the previous parameters and it will retrieve the customer associated to the given order. This can only be done by a user who is logged in ASM.")
	@RequestMapping(value = "/customers/search", method = RequestMethod.GET)
	@ResponseBody
	public CustomerSearchPageWsDTO getPageableCustomers(
			@Parameter(description = "Customer unique ID search term", required = false) @RequestParam(required = false) final String query,
			@Parameter(description = "Current page", required = false) @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Page size", required = false) @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "ID of the customer list", required = false) @RequestParam(required = false) final String customerListId,
			@Parameter(description = "ID of the order", required = false) @RequestParam(required = false) final String orderId,
			@Parameter(description = "ID of the base site", required = true) @RequestParam(required = true) final String baseSite,
			@Parameter(description = "Sort parameter. Possible values include: byUidAsc, byUidDesc,  byNameAsc, byNameDesc", required = false) @RequestParam(required = false) final String sort)
	{
		SearchPageData<CustomerData> customerSearchPageData;

		final PageableData pageableData = PaginationUtils.createPageableData(currentPage, pageSize, sort);

		if (StringUtils.isNotBlank(orderId))
		{
			customerSearchPageData = searchCustomerByOrder(orderId, pageableData);
		}
		else
		{
			if (StringUtils.isNotBlank(customerListId))
			{
				customerSearchPageData = getCustomersFromCustomerList(customerListId, query, pageableData);
			}
			else
			{
				customerSearchPageData = searchCustomersByQuery(query, pageableData);
			}
		}

		return getCustomerHelper().getCustomerSearchPageDto(customerSearchPageData);
	}

	@Operation(operationId = "autoComplete", summary = "Retrieves autocomplete information on customers based on the customer query")
	@RequestMapping(value = "/customers/autocomplete", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<CustomerSuggestionWsDTO> autoComplete(
			@Parameter(description = "Customer query: first characters of customer email or part of name. A BadRequestException is thrown when the query is too short. Minimum query length can be updated via `assistedserviceswebservices.customer.suggestions.minimum.query.length` property.", required = true) @RequestParam(required = true) final String customerQuery,
			@Parameter(description = "ID of the base site", required = true) @RequestParam(required = true) final String baseSite)
	{
		final int minQueryLength = Config.getInt("assistedserviceswebservices.customer.suggestions.minimum.query.length", 3);

		if (minQueryLength > customerQuery.length())
		{
			throw new BadRequestException("Customer query length must not be less than " + minQueryLength);
		}

		final List<AutoSuggestionCustomerData> customerSuggestions = getAssistedServiceFacade().getCustomerSuggestions(customerQuery);

		return getCustomerHelper().getCustomerSuggestions(customerSuggestions);
	}

	@Operation(operationId = "bindCart", summary = "Creates a bind between the provided customer ID and an anonymous cart.")
	@RequestMapping(value = "/bind-cart", method = RequestMethod.POST)
	public ResponseEntity<String> bindCart(@Parameter(description = "ID of the Customer", required = true) @RequestParam(value = "customerId") final String customerId,
										   @Parameter(description = "ID of the anonymous Cart", required = true) @RequestParam(value = "cartId") final String cartId,
										   @Parameter(description = "ID of the base site", required = true) @RequestParam final String baseSite)
	{
		try
		{
			getAssistedServiceFacade().bindCustomerToCartWithoutEmulating(customerId, cartId);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		catch (final AssistedServiceCartBindException e)
		{
			throw new BadRequestException(e.getMessage());
		}
		catch (final AssistedServiceException e)
		{
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	@Operation(operationId = "createCustomer", summary = "Creates a customer account", description = "Creates an account for a customer by assisted service agent. A notification email will be sent to the targeting email address for changing the password.")
	@RequestMapping(value = "/customers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public UserWsDTO createCustomer(@Parameter(description = "Customer registration form", required = true) @RequestBody final CustomerRegistrationFormWsDTO customerRegistrationForm,
									@Parameter(description = "ID of the base site", required = true) @RequestParam final String baseSite) throws AssistedServiceException {
		validate(customerRegistrationForm, "customerRegistrationForm", getCustomerRegistrationDataValidator());
		final String customerId = customerRegistrationForm.getEmailAddress();
		final String firstName = customerRegistrationForm.getFirstName();
		final String lastName = customerRegistrationForm.getLastName();
		final String customerName = getDefaultCustomerNameStrategy().getName(firstName, lastName);
		final CustomerData customerData = getAssistedServiceFacade().createCustomer(customerId, customerName);
		return getCustomerHelper().getUserWsDTO(customerData);
	}

	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}
	protected SearchPageData<CustomerData> getCustomersFromCustomerList(final String customerListId, final String query,
			final PageableData pageableData)
	{
		final Map<String, Object> parametersMap = new HashMap<>();

		if (StringUtils.isNotBlank(query))
		{
			parametersMap.put(QUERY, query);
		}
		return getCustomerListFacade().getPagedCustomersForCustomerListUID(customerListId,
				getCustomerFacade().getCurrentCustomerUid(), pageableData, parametersMap);
	}

	protected SearchPageData<CustomerData> searchCustomersByQuery(final String query, final PageableData pageableData)
	{
		final List<CustomerData> customers = assistedServiceFacade.getCustomers(query, pageableData);
		return createSearchPageData(customers, PaginationUtils.buildPaginationData(pageableData, customers));
	}

	protected SearchPageData<CustomerData> searchCustomerByOrder(final String orderId, final PageableData pageableData)
	{
		final List<CustomerData> customers = Arrays.asList(assistedServiceFacade.getCustomerByOrder(orderId));
		return createSearchPageData(customers, PaginationUtils.buildPaginationData(pageableData, customers));
	}

	public AssistedServiceFacade getAssistedServiceFacade()
	{
		return assistedServiceFacade;
	}

	public CustomerHelper getCustomerHelper()
	{
		return customerHelper;
	}

	public DefaultCustomerNameStrategy getDefaultCustomerNameStrategy() {
		return defaultCustomerNameStrategy;
	}

	public Validator getCustomerRegistrationDataValidator() {
		return customerRegistrationDataValidator;
	}
}
