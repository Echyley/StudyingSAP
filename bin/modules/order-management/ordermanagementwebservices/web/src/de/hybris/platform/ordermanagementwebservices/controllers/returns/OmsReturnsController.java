/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementwebservices.controllers.returns;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ordermanagementfacades.constants.OrdermanagementfacadesConstants;
import de.hybris.platform.ordermanagementfacades.order.data.CancelReasonDataList;
import de.hybris.platform.ordermanagementfacades.order.data.ReturnStatusDataList;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.RefundReasonDataList;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnActionDataList;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationWsDTO;
import de.hybris.platform.ordermanagementwebservices.controllers.OmsBaseController;
import de.hybris.platform.ordermanagementwebservices.dto.order.CancelReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.CancelReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.RefundReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnActionListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnSearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnStatusListWsDTO;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * WebResource exposing {@link de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade}
 * http://host:port/ordermanagementwebservices/returns
 */
@Controller
@RequestMapping(value = "/returns")
@Tag(name = "Returns Operations")
public class OmsReturnsController extends OmsBaseController
{
	@Resource
	private OmsReturnFacade omsReturnFacade;

	@Resource(name = "cancelReturnRequestValidator")
	private Validator cancelReturnRequestValidator;

	@Resource(name = "returnRequestValidator")
	private Validator returnRequestValidator;

	@Resource(name = "returnEntryValidator")
	private Validator returnEntryValidator;

	@Resource(name = "returnActionValidator")
	private Validator returnActionValidator;

	@Resource(name = "refundReasonValidator")
	private Validator refundReasonValidator;

	@Resource(name = "cancelReasonValidator")
	private Validator cancelReasonValidator;

	@Resource(name = "priceValidator")
	private Validator priceValidator;

	/**
	 * Request to get paged returns in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of returns
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturns",summary = "Finds a paginated list of all the returns in the system", description = "Returns a paginated list of all the returns in the system")
	public ReturnSearchPageWsDTO getReturns(
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Number of items to be displayed per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{

		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ReturnRequestData> returns = omsReturnFacade.getReturns(pageableData);
		return dataMapper.map(returns, ReturnSearchPageWsDTO.class, fields);
	}

	/**
	 * Request to get paged returns with certain return status(s)
	 *
	 * @param returnStatuses
	 * 		a list of valid return statuses separated by ","
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of returns that complies with conditions above
	 * @throws de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException
	 * 		in case of passing a wrong return status validation exception will be thrown
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "status/{returnStatuses}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturnsByStatus", summary = "Finds a paginated list of returns with one of a set of desired statuses", description = "Returns a paginated list of returns with one of a set of desired statuses")
	public ReturnSearchPageWsDTO getReturnsByStatus(
			@Parameter(description = "Set of desired return statuses", required = true) @PathVariable final String returnStatuses,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Number of items to be displayed per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
			throws WebserviceValidationException
	{
		final Set<ReturnStatus> statusSet = extractReturnStatuses(returnStatuses);
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ReturnRequestData> returns = omsReturnFacade.getReturnsByStatuses(pageableData, statusSet);
		return dataMapper.map(returns, ReturnSearchPageWsDTO.class, fields);
	}

	/**
	 * Request to get ReturnRequest by its code
	 *
	 * @param code
	 * 		the code of the requested return
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the requested returnRequest that complies with conditions above
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturnForReturnCode", summary = "Finds a specific return request by its code", description = "Returns a specific return request by its code")
	public ReturnRequestWsDTO getReturnForReturnCode(
			@Parameter(description = "Return request code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ReturnRequestData returns = omsReturnFacade.getReturnForReturnCode(code);
		return dataMapper.map(returns, ReturnRequestWsDTO.class, fields);
	}

	/**
	 * Request to update ReturnRequest by its code
	 *
	 * @param code
	 * 		the code of the requested return
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the requested returnRequest that complies with conditions above
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "updateReturnByReturnCode", summary = "Updates a return request corresponding to the given code with the modifications provided", description = "Updates a return request corresponding to the given code with the modifications provided.")
	public ReturnRequestWsDTO updateReturnByReturnCode(
			@Parameter(description = "The ReturnRequestModificationWsDTO containing the desired modifications to be applied", required = true) @NotNull @RequestBody final ReturnRequestModificationWsDTO returnRequestModificationWsDTO,
			@Parameter(description = "Return request code", required = true) @NotNull @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ReturnRequestModificationData returnRequestModificationData = dataMapper
				.map(returnRequestModificationWsDTO, ReturnRequestModificationData.class, fields);
		final ReturnRequestData returnRequest = omsReturnFacade.updateReturnRequest(code, returnRequestModificationData);
		return dataMapper.map(returnRequest, ReturnRequestWsDTO.class, fields);
	}


	/**
	 * Request to get all {@link ReturnStatus} in the system
	 *
	 * @return list of return statuses
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/statuses", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturnStatuses", summary = "Finds a list of all return possible return statuses", description = "Returns a list of all return possible return statuses")
	public ReturnStatusListWsDTO getReturnStatuses()
	{
		final List<ReturnStatus> returnStatuses = omsReturnFacade.getReturnStatuses();
		final ReturnStatusDataList returnStatusList = new ReturnStatusDataList();
		returnStatusList.setStatuses(returnStatuses);
		return dataMapper.map(returnStatusList, ReturnStatusListWsDTO.class);
	}

	/**
	 * Request to get returnEntries for the given {@link de.hybris.platform.returns.model.ReturnRequestModel#CODE}
	 *
	 * @param code
	 * 		return's code for the requested return entries
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of returnEntries fulfilling the above conditions
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/entries", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturnEntriesForOrderCode", summary = "Finds a paginated list of return entries for a return request corresponding to the given code", description = "Returns a paginated list of return entries for a return request corresponding to the given code")
	public ReturnEntrySearchPageWsDTO getReturnEntriesForOrderCode(
			@Parameter(description = "Return request code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Number of items to display per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ReturnEntryData> returnEntrySearchPageData = omsReturnFacade
				.getReturnEntriesForReturnCode(code, pageableData);
		return dataMapper.map(returnEntrySearchPageData, ReturnEntrySearchPageWsDTO.class, fields);
	}

	/**
	 * Request to create return in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param returnRequestWsDTO
	 * 		object representing {@link ReturnRequestWsDTO}
	 * @return created return
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createReturnRequest", summary = "Creates a return request", description = "Creates a return request")
	public ReturnRequestWsDTO createReturnRequest(
			@Parameter(description = "The ReturnRequestWsDTO holding all required information to create a return request", required = true) @RequestBody final ReturnRequestWsDTO returnRequestWsDTO,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		validate(returnRequestWsDTO, "returnRequestWsDTO", returnRequestValidator);
		final Set<Integer> entryNumbers = new HashSet<>();
		returnRequestWsDTO.getReturnEntries().forEach(returnEntryWsDTO -> {
			validate(returnEntryWsDTO, "returnEntryWsDTO", returnEntryValidator);
			validate(returnEntryWsDTO.getRefundAmount(), "PriceWsDTO", priceValidator);
			validate(new String[] { returnEntryWsDTO.getAction() }, "action", returnActionValidator);
			validate(new String[] { returnEntryWsDTO.getRefundReason() }, "refundReason", refundReasonValidator);
			if (entryNumbers.contains(returnEntryWsDTO.getOrderEntry().getEntryNumber()))
			{
				throw new IllegalArgumentException(
						String.format(Localization.getLocalizedString("ordermanagementwebservices.returns.error.duplicateorderentry"),
								returnEntryWsDTO.getOrderEntry().getEntryNumber()));
			}
			entryNumbers.add(returnEntryWsDTO.getOrderEntry().getEntryNumber());
		});
		final ReturnRequestData returnRequestData = dataMapper.map(returnRequestWsDTO, ReturnRequestData.class);
		final ReturnRequestData createdReturnRequestData = omsReturnFacade.createReturnRequest(returnRequestData);

		return dataMapper.map(createdReturnRequestData, ReturnRequestWsDTO.class);
	}

	/**
	 * Request to get return cancellation reasons
	 *
	 * @return list of cancel reasons
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/cancel-reasons", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturnsCancellationReasons", summary = "Finds a list of all possible return cancellation reasons", description = "Finds a list of all possible return cancellation reasons")
	public CancelReasonListWsDTO getReturnsCancellationReasons()
	{
		final List<CancelReason> cancelReasons = omsReturnFacade.getCancelReasons();
		final CancelReasonDataList cancelReasonList = new CancelReasonDataList();
		cancelReasonList.setReasons(cancelReasons);
		return dataMapper.map(cancelReasonList, CancelReasonListWsDTO.class);
	}

	/**
	 * Request to get refund reasons
	 *
	 * @return list of refund reasons
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/refund-reasons", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getRefundReasons", summary = "Finds a list of all possible refund reasons", description = "Finds a list of all possible return refund reasons")
	public RefundReasonListWsDTO getRefundReasons()
	{
		final List<RefundReason> refundReasons = omsReturnFacade.getRefundReasons();
		final RefundReasonDataList refundReasonList = new RefundReasonDataList();
		refundReasonList.setRefundReasons(refundReasons);
		return dataMapper.map(refundReasonList, RefundReasonListWsDTO.class);
	}

	/**
	 * Request to get return actions
	 *
	 * @return list of return actions
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/actions", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getReturnActions", summary = "Finds a list of all possible return actions", description = "Finds a list of all possible return actions")
	public ReturnActionListWsDTO getReturnActions()
	{
		final List<ReturnAction> returnActions = omsReturnFacade.getReturnActions();
		final ReturnActionDataList returnActionList = new ReturnActionDataList();
		returnActionList.setReturnActions(returnActions);
		return dataMapper.map(returnActionList, ReturnActionListWsDTO.class);
	}

	/**
	 * Request to approve Return Request
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/approve", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "approveReturnRequest", summary = "Approves a return request corresponding to the given code", description = "Approves a return request corresponding to the given code")
	public void approveReturnRequest(@Parameter(description = "Return request code", required = true) @PathVariable final String code)
	{
		omsReturnFacade.approveReturnRequest(code);
	}

	/**
	 * Request to cancel a {@link de.hybris.platform.returns.model.ReturnRequestModel}.
	 *
	 * @param cancelReturnRequestWsDTO
	 * 		contains information about the cancellation of the return request.
	 * @throws WebserviceValidationException
	 * 		in case the request body has erroneous fields.
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "cancelReturnRequest", summary = "Cancels a return request", description = "Cancels a return request")
	public void cancelReturnRequest(
			@Parameter(description = "The CancelReturnRequestWsDTO containing information about the return request cancellation", required = true) @RequestBody final CancelReturnRequestWsDTO cancelReturnRequestWsDTO)
			throws WebserviceValidationException
	{
		validate(cancelReturnRequestWsDTO, "cancelReturnRequestWsDto", cancelReturnRequestValidator);
		validate(new String[] { cancelReturnRequestWsDTO.getCancelReason() }, "cancelReason", cancelReasonValidator);

		final CancelReturnRequestData cancelReturnRequestData = dataMapper
				.map(cancelReturnRequestWsDTO, CancelReturnRequestData.class);
		omsReturnFacade.cancelReturnRequest(cancelReturnRequestData);
	}

	/**
	 * Request to reverse payment manually.
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/manual/reverse-payment", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReversePayment", summary = "Requests manual reversal of the payment for a return", description = "Requests manual reversal of the payment for a return")
	public void manuallyReversePayment(
			@Parameter(description = "Return request code", required = true) @NotNull @PathVariable final String code)
	{
		omsReturnFacade.requestManualPaymentReversalForReturnRequest(code);
	}

	/**
	 * Request to reverse tax manually.
	 *
	 * @param code
	 * 		code for the requested returnRequest
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/manual/reverse-tax", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReverseTax", summary = "Requests manual reversal of the taxes for a return", description = "Requests manual reversal of the taxes for a return")
	public void manuallyReverseTax(
			@Parameter(description = "Return request code", required = true) @NotNull @PathVariable final String code)
	{
		omsReturnFacade.requestManualTaxReversalForReturnRequest(code);
	}

	/**
	 * Extracts the {@link ReturnStatus} from the provided String representation
	 *
	 * @param statuses
	 * 		a comma-separated string that represent {@link ReturnStatus}
	 * @return the newly extracted {@link Set<ReturnStatus>}
	 */
	protected Set<ReturnStatus> extractReturnStatuses(final String statuses)
	{
		final String statusesStrings[] = statuses.split(OrdermanagementfacadesConstants.OPTIONS_SEPARATOR);

		final Set<ReturnStatus> statusesEnum = new HashSet<>();
		try
		{
			for (final String status : statusesStrings)
			{
				statusesEnum.add(ReturnStatus.valueOf(status));
			}
		}
		catch (final IllegalArgumentException e)
		{
			throw new WebserviceValidationException(e.getMessage());
		}
		return statusesEnum;
	}
}
