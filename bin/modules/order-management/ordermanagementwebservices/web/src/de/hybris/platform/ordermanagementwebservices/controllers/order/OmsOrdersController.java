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
package de.hybris.platform.ordermanagementwebservices.controllers.order;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.constants.OrdermanagementfacadesConstants;
import de.hybris.platform.ordermanagementfacades.fraud.data.FraudReportDataList;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.ordermanagementfacades.order.cancel.OrderCancelRecordEntryData;
import de.hybris.platform.ordermanagementfacades.order.data.CancelReasonDataList;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;
import de.hybris.platform.ordermanagementfacades.order.data.OrderStatusDataList;
import de.hybris.platform.ordermanagementwebservices.controllers.OmsBaseController;
import de.hybris.platform.ordermanagementwebservices.dto.fraud.FraudReportListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.CancelReasonListWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderCancelRecordEntryWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderCancelRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderEntrySearchPageWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderSearchPageWsDto;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderStatusListWsDTO;
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
 * WebResource exposing {@link OmsOrderFacade}
 * http://host:port/ordermanagementwebservices/orders
 */
@Controller
@RequestMapping(value = "/orders")
@Tag(name = "Orders Operations")
public class OmsOrdersController extends OmsBaseController
{
	@Resource
	private OmsOrderFacade omsOrderFacade;

	@Resource(name = "orderRequestValidator")
	private Validator orderRequestValidator;

	@Resource(name = "orderEntryRequestValidator")
	private Validator orderEntryRequestValidator;

	@Resource(name = "orderCancelRequestValidator")
	private Validator orderCancelRequestValidator;

	@Resource(name = "cancelEntryValidator")
	private Validator cancelEntryValidator;

	@Resource(name = "cancelReasonValidator")
	private Validator cancelReasonValidator;


	/**
	 * Request to get all orders in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of orders
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrders", summary = "Finds all orders in the system", description = "Returns all orders in the system.")
	public OrderSearchPageWsDto getOrders(
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page number") @RequestParam(required = false, defaultValue = "0") final int currentPage,
			@Parameter(description = "Number of items to be displayed per page") @RequestParam(required = false, defaultValue = "10") final int pageSize,
			@Parameter(description = "Method in which to sort results") @RequestParam(required = false, defaultValue = "asc") final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<OrderData> orderSearchPageData = getOmsOrderFacade().getOrders(pageableData);
		return dataMapper.map(orderSearchPageData, OrderSearchPageWsDto.class, fields);
	}

	/**
	 * Request to get an order by code
	 *
	 * @param code
	 * 		the code of the requested order
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the order
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrderForCode", summary = "Finds a single order by a given order code", description = "Returns a single order in the system.")
	public OrderWsDTO getOrderForCode(@Parameter(description = "The order code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData order = getOmsOrderFacade().getOrderForCode(code);
		return dataMapper.map(order, OrderWsDTO.class, fields);
	}

	/**
	 * Request to get all orders with certain order status(es)
	 *
	 * @param orderStatuses
	 * 		a list of valid order statuses separated by ","
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of orders fulfilling the above conditions
	 * @throws WebserviceValidationException
	 * 		in case of passing a wrong order status validation exception will be thrown
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "status/{orderStatuses}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrdersByStatus",summary = "Finds a paginated list of orders for a given set of order statuses", description = "Returns a paginated list of orders for a given set of order statuses.")
	public OrderSearchPageWsDto getOrdersByStatus(
			@Parameter(description = "List of desired order statuses", required = true) @PathVariable final String orderStatuses,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Number of items to be displayed per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
			throws WebserviceValidationException
	{
		final Set<OrderStatus> statusSet = extractOrderStatuses(orderStatuses);
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<OrderData> orderSearchPageData = getOmsOrderFacade().getOrdersByStatuses(pageableData, statusSet);
		return dataMapper.map(orderSearchPageData, OrderSearchPageWsDto.class, fields);
	}

	/**
	 * Request to get all {@link OrderStatus} in the system
	 *
	 * @return list of order statuses
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/statuses", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrdersByStatuses",summary = "Finds a list of all possible order statuses in the system", description = "Returns a list of all possible order statuses in the system.")
	public OrderStatusListWsDTO getOrderStatuses()
	{
		final List<OrderStatus> orderStatuses = getOmsOrderFacade().getOrderStatuses();
		final OrderStatusDataList orderStatusList = new OrderStatusDataList();
		orderStatusList.setStatuses(orderStatuses);
		return dataMapper.map(orderStatusList, OrderStatusListWsDTO.class);
	}

	/**
	 * Request to get orderEntries for the given {@link de.hybris.platform.core.model.order.OrderModel#CODE}
	 *
	 * @param code
	 * 		order's code for the requested order entries
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of orderEntries fulfilling the above conditions
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/entries", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrderEntriesForOrderCode",summary = "Finds a paginated list of order entries for the order with the given code", description = "Returns a paginated list of order entries for the order with the given code.")
	public OrderEntrySearchPageWsDTO getOrderEntriesForOrderCode(
			@Parameter(description = "Order code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page number") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Number of items to display per page") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Method in which to sort results") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<OrderEntryData> orderEntrySearchPageData = getOmsOrderFacade()
				.getOrderEntriesForOrderCode(code, pageableData);
		return dataMapper.map(orderEntrySearchPageData, OrderEntrySearchPageWsDTO.class, fields);
	}

	/**
	 * Request to get orderEntry for the given {@link de.hybris.platform.core.model.order.OrderModel#CODE} and
	 * {@link de.hybris.platform.core.model.order.OrderEntryModel#ENTRYNUMBER}
	 *
	 * @param code
	 * 		order's code for the requested order entries
	 * @param entryNumber
	 * 		the entry number
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the list of orderEntries fulfilling the above conditions
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/entries/{entryNumber}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrderEntryForOrderCodeAndEntryNumber",summary = "Finds the order entry corresponding to the given entry number for the order with the given code", description = "Returns the order entry corresponding to the given entry number for the order with the given code.")
	public OrderEntryWsDTO getOrderEntryForOrderCodeAndEntryNumber(
			@Parameter(description = "Order code", required = true) @PathVariable final String code,
			@Parameter(description = "Order entry number", required = true) @PathVariable final String entryNumber,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderEntryData orderEntryData = getOmsOrderFacade()
				.getOrderEntryForOrderCodeAndEntryNumber(code, Integer.valueOf(entryNumber));
		return dataMapper.map(orderEntryData, OrderEntryWsDTO.class, fields);
	}

	/**
	 * Request to get all {@link CancelReason} in the system
	 *
	 * @return list of cancel reasons
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/cancel-reasons", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getCancelReason",summary = "Finds a list of all cancellation reasons", description = "Returns a list of all cancellation reasons.")
	public CancelReasonListWsDTO getCancelReason()
	{
		final List<CancelReason> cancelReasons = getOmsOrderFacade().getCancelReasons();
		final CancelReasonDataList cancelReasonList = new CancelReasonDataList();
		cancelReasonList.setReasons(cancelReasons);
		return dataMapper.map(cancelReasonList, CancelReasonListWsDTO.class);
	}

	/**
	 * Request to get fraud reports for a certain order
	 *
	 * @param code
	 * 		order's code for which to get the fraud reports
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return list of the order's fraud reports
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/fraud-reports", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getOrderFraudReports",summary = "Finds a list of all fraud reports for an order with the given code", description = "Returns a list of all fraud reports for an order with the given code.")
	public FraudReportListWsDTO getOrderFraudReports(
			@Parameter(description = "Order code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final FraudReportDataList fraudReports = new FraudReportDataList();
		fraudReports.setReports(getOmsOrderFacade().getOrderFraudReports(code));
		return dataMapper.map(fraudReports, FraudReportListWsDTO.class, fields);
	}

	/**
	 * Request to submit order in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param orderRequestWsDTO
	 * 		object representing {@link OrderRequestWsDTO}
	 * @return submitted order
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "submitOrder", summary = "Submits an order to the system", description = "Posts an order to the system.")
	public OrderWsDTO submitOrder(
			@Parameter(description = "The OrderRequestWsDTO that contains all information for a new order", required = true) @RequestBody final OrderRequestWsDTO orderRequestWsDTO,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		validateOrderRequest(orderRequestWsDTO);
		final OrderRequestData orderRequestData = dataMapper.map(orderRequestWsDTO, OrderRequestData.class);
		final OrderData submittedOrderData = getOmsOrderFacade().submitOrder(orderRequestData);

		return dataMapper.map(submittedOrderData, OrderWsDTO.class, fields);
	}

	/**
	 * Request to approve a potentially fraudulent order
	 *
	 * @param code
	 * 		order's code for which to approve the fraud check
	 * @throws IllegalStateException
	 * 		when the order is not in the {@link OrderStatus.WAIT_FRAUD_MANUAL_CHECK} status
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/fraud-reports/approve", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "approvePotentiallyFraudulentOrder",summary = "Approves an order that could potentially be fraudulent", description = "Approves an order that could potentially be fraudulent.")
	public void approvePotentiallyFraudulentOrder(
			@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code) throws IllegalStateException
	{
		getOmsOrderFacade().approvePotentiallyFraudulentOrder(code);
	}

	/**
	 * Request to reject a potentially fraudulent order
	 *
	 * @param code
	 * 		order's code for which to reject the fraud check
	 * @throws IllegalStateException
	 * 		when the order is not in the {@link OrderStatus.WAIT_FRAUD_MANUAL_CHECK} status
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/fraud-reports/reject", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "rejectPotentiallyFraudulentOrder",summary = "Rejects an order that could be potentially fraudulent", description = "Rejects an order that could potentially be fraudulent.")
	public void rejectPotentiallyFraudulentOrder(
			@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code) throws IllegalStateException
	{
		getOmsOrderFacade().rejectPotentiallyFraudulentOrder(code);
	}

	/**
	 * Request to create order cancellation in the system
	 *
	 * @param orderCancelRequestWsDTO
	 * 		object representing {@link OrderCancelRequestWsDTO}
	 * @param code
	 * 		code of the order to be cancelled
	 * @return {@link OrderCancelRecordEntryWsDTO} showing the created cancel record
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/cancel", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createCancelRequest",summary = "Creates an order cancellation in the system", description = "Creates an order cancellation request.")
	public OrderCancelRecordEntryWsDTO createCancelRequest(
			@Parameter(description = "The OrderCancelRequestWsDTO holding the information of the order we want to cancel", required = true) @RequestBody final OrderCancelRequestWsDTO orderCancelRequestWsDTO,
			@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		validate(orderCancelRequestWsDTO, "orderCancelRequestWsDTO", orderCancelRequestValidator);
		orderCancelRequestWsDTO.getEntries().forEach(cancelEntryWsDTO -> {
			validate(cancelEntryWsDTO, "cancelEntryWsDTO", cancelEntryValidator);
			validate(new String[] { cancelEntryWsDTO.getCancelReason() }, "cancelReason", cancelReasonValidator);

		});
		final OrderCancelRequestData orderCancelRequestData = dataMapper.map(orderCancelRequestWsDTO, OrderCancelRequestData.class);
		orderCancelRequestData.setOrderCode(code);

		final OrderCancelRecordEntryData orderCancelRecordEntryData = getOmsOrderFacade()
				.createRequestOrderCancel(orderCancelRequestData);

		return dataMapper.map(orderCancelRecordEntryData, OrderCancelRecordEntryWsDTO.class);
	}

	/**
	 * Request to manually release the {@link OrderModel} from the waiting step after payment void fails.
	 *
	 * @param code
	 * 		the {@link OrderModel#CODE} for the order to be released
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/void-payment", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleasePaymentVoid",summary = "Moves an order out of the waiting step after payment authorization cancellation has failed, indicating payment was voided.", description = "Moves an order out of the waiting step after payment authorization cancellation has failed, indicating payment was voided.")
	public void manuallyReleasePaymentVoid(
			@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		getOmsOrderFacade().manuallyReleasePaymentVoid(code);
	}

	/**
	 * Request to manually release the {@link OrderModel} from the waiting step after tax void fails.
	 *
	 * @param code
	 * 		the {@link OrderModel#CODE} for the order to be released
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/void-tax", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleaseTaxVoid",summary = "Moves an order out of the waiting step after tax post void has failed.", description = "Moves an order out of the waiting step after tax post void has failed.")
	public void manuallyReleaseTaxVoid(@Parameter(description = "code", required = true) @PathVariable @NotNull final String code)
	{
		getOmsOrderFacade().manuallyReleaseTaxVoid(code);
	}

	/**
	 * Request to manually release the {@link OrderModel} form the waiting step after tax commit has failed.
	 *
	 * @param code
	 * 		the {@link OrderModel#CODE} for the order to be released
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/commit-tax", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleaseTaxCommit",summary = "Moves an order out of the waiting step after tax commit has failed.", description = "Moves an order out of the waiting step after tax commit has failed.")
	public void manuallyReleaseTaxCommit(@Parameter(description = "code", required = true) @PathVariable @NotNull final String code)
	{
		getOmsOrderFacade().manuallyReleaseTaxCommit(code);
	}

	/**
	 * Request to manually release the {@link OrderModel} form the waiting step after tax requote fails.
	 *
	 * @param code
	 * 		the {@link OrderModel#CODE} for the order to be released
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/requote-tax", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleaseTaxRequote",summary = "Moves an order out of the waiting step after tax requote has failed.", description = "Moves an order out of the waiting step after tax requote has failed.")
	public void manuallyReleaseTaxRequote(@Parameter(description = "code", required = true) @PathVariable @NotNull final String code)
	{
		getOmsOrderFacade().manuallyReleaseTaxRequote(code);
	}

	/**
	 * Request to manually release the {@link OrderModel} from the waiting step after payment reauth fails.
	 *
	 * @param code
	 * 		the {@link OrderModel#CODE} for the order to be released
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/reauth-payment", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleasePaymentReauth",summary = "Moves an order out of the waiting step after payment reauthorization has failed, indicating payment was reauthorized.", description = "Moves an order out of the waiting step after payment reauthorization has failed, indicating payment was reauthorized.")
	public void manuallyReleasePaymentReauth(
			@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		getOmsOrderFacade().manuallyReleasePaymentReauth(code);
	}

	/**
	 * Request to manually release the {@link OrderModel} from the waiting step after delivery cost commit fails.
	 *
	 * @param code
	 * 		the {@link OrderModel#CODE} for the order to be released
	 */
	@Secured({ CUSTOMER_SUPPORT_AGENT_GROUP, CUSTOMER_SUPPORT_MANAGER_GROUP, CUSTOMER_SUPPORT_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/delivery-cost-commit", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleaseDeliveryCostCommit",summary = "Moves an order out of the waiting step after delivery cost tax commit has failed, indicating tax was committed.", description = "Moves an order out of the waiting step after delivery cost tax commit has failed, indicating tax was committed.")
	public void manuallyReleaseDeliveryCostCommit(
			@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		getOmsOrderFacade().manuallyReleaseDeliveryCostCommit(code);
	}


	/**
	 * Validates {@link OrderRequestWsDTO} for null checks
	 *
	 * @param orderRequestWsDTO
	 * 		the {@link OrderRequestWsDTO}
	 */
	protected void validateOrderRequest(final OrderRequestWsDTO orderRequestWsDTO)
	{
		validate(orderRequestWsDTO, "orderRequestWsDTO", orderRequestValidator);
		orderRequestWsDTO.getEntries().forEach(
				orderEntryRequestWsDTO -> validate(orderEntryRequestWsDTO, "orderEntryRequestWsDTO", orderEntryRequestValidator));
	}

	/**
	 * Populates a set of {@link OrderStatus} out of the provided string
	 *
	 * @param statuses
	 * 		a comma-separated string that represents {@link OrderStatus}
	 * @return the newly populated {@link Set<OrderStatus>}
	 */
	protected Set<OrderStatus> extractOrderStatuses(final String statuses)
	{
		final String[] statusesStrings = statuses.split(OrdermanagementfacadesConstants.OPTIONS_SEPARATOR);

		final Set<OrderStatus> statusesEnum = new HashSet<>();
		try
		{
			for (final String status : statusesStrings)
			{
				statusesEnum.add(OrderStatus.valueOf(status));
			}
		}
		catch (final IllegalArgumentException e)
		{
			throw new WebserviceValidationException(e.getMessage());
		}
		return statusesEnum;
	}

	public OmsOrderFacade getOmsOrderFacade()
	{
		return omsOrderFacade;
	}
}
