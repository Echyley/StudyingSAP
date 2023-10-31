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
package de.hybris.platform.warehousingwebservices.controllers.order;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.ConsignmentWsDTO;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.ordermanagementwebservices.dto.payment.PaymentTransactionEntryWsDTO;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousingfacades.constants.WarehousingfacadesConstants;
import de.hybris.platform.warehousingfacades.order.WarehousingConsignmentFacade;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentCodeDataList;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentReallocationData;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentStatusDataList;
import de.hybris.platform.warehousingfacades.order.data.DeclineReasonDataList;
import de.hybris.platform.warehousingfacades.order.data.PackagingInfoData;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentCodesWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentEntrySearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentReallocationWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.order.ConsignmentStatusListWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.DeclineReasonListWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.PackagingInfoWsDTO;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseSearchPageWsDto;
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
 * WebResource exposing {@link de.hybris.platform.warehousingfacades.order.WarehousingConsignmentFacade}
 * http://host:port/warehousingwebservices/consignments
 */
@Controller
@RequestMapping(value = "/consignments")
@Tag(name = "Consignment's Operations")
public class WarehousingConsignmentsController extends WarehousingBaseController
{
	@Resource
	private WarehousingConsignmentFacade warehousingConsignmentFacade;

	@Resource(name = "packagingInfoDTOValidator")
	private Validator packagingInfoDTOValidator;

	@Resource(name = "consignmentReallocationValidator")
	private Validator consignmentReallocationValidator;

	@Resource(name = "declineEntryValidator")
	private Validator declineEntryValidator;

	@Resource(name = "declineReasonValidator")
	private Validator declineReasonValidator;

	/**
	 * Request to get all {@link ConsignmentModel} in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of {@link ConsignmentModel}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getConsignments", summary = "Finds a paginated list of consignments", description = "Finds a paginated list of consignments.")
	public ConsignmentSearchPageWsDto getConsignments(
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ConsignmentData> consignmentSearchPageData = warehousingConsignmentFacade
				.getConsignments(pageableData);
		return dataMapper.map(consignmentSearchPageData, ConsignmentSearchPageWsDto.class, fields);
	}

	/**
	 * Request to get {@link ConsignmentModel} for the given code
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param code
	 * 		code to get the required consignment
	 * @return {@link ConsignmentModel} details for the given code
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getConsignmentForCode", summary = "Finds a consignment by a given consignment code", description = "Finds a consignment by a given consignment code.")
	public ConsignmentWsDTO getConsignmentForCode(
			@Parameter(description = "The consignment code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ConsignmentData consignment = warehousingConsignmentFacade.getConsignmentForCode(code);
		return dataMapper.map(consignment, ConsignmentWsDTO.class, fields);
	}

	/**
	 * Request to get all sourcing locations for the given {@value de.hybris.platform.ordersplitting.model.ConsignmentModel#CODE}
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the requested sourcing location
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of locations compliant to the above conditions
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/sourcing-locations", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getSourcingLocationsForConsignmentCode", summary = "Finds a paginated list of sourcing locations for a given consignment code", description = "Finds a paginated list of sourcing locations for a given consignment code.")
	public WarehouseSearchPageWsDto getSourcingLocationsForConsignmentCode(
			@Parameter(description = "The consignment code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<WarehouseData> warehouseSearchPageData = warehousingConsignmentFacade
				.getSourcingLocationsForConsignmentCode(code, pageableData);

		return dataMapper.map(warehouseSearchPageData, WarehouseSearchPageWsDto.class, fields);
	}

	/**
	 * Request to get all {@link ConsignmentModel} with certain consignment status(es)
	 *
	 * @param consignmentStatuses
	 * 		a list of valid {@link ConsignmentStatus} separated by ","
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of {@link ConsignmentModel} that complies with conditions above
	 * @throws WebserviceValidationException
	 * 		in case of passing a wrong {@link ConsignmentStatus}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "status/{consignmentStatuses}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getConsignmentsByStatus", summary = "Finds a paginated list of consignments for a given consignment status", description = "Finds a paginated list of consignments for a given consignment status")
	public ConsignmentSearchPageWsDto getConsignmentsByStatus(
			@Parameter(description = "Consignment status", required = true) @PathVariable final String consignmentStatuses,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
			throws WebserviceValidationException
	{
		final Set<ConsignmentStatus> statusSet = extractConsignmentStatuses(consignmentStatuses);
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ConsignmentData> consignmentSearchPageData = warehousingConsignmentFacade
				.getConsignmentsByStatuses(pageableData, statusSet);
		return dataMapper.map(consignmentSearchPageData, ConsignmentSearchPageWsDto.class, fields);
	}

	/**
	 * Request to get all {@link ConsignmentStatus} in the system
	 *
	 * @return list of {@link ConsignmentStatus}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/statuses", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getConsignmentStatuses", summary = "Finds a list of all consignment's statuses", description = "Finds a list of all consignment's statuses.")
	public ConsignmentStatusListWsDTO getConsignmentStatuses()
	{
		final List<ConsignmentStatus> consignmentStatuses = warehousingConsignmentFacade.getConsignmentStatuses();
		final ConsignmentStatusDataList consignmentStatusList = new ConsignmentStatusDataList();
		consignmentStatusList.setStatuses(consignmentStatuses);
		return dataMapper.map(consignmentStatusList, ConsignmentStatusListWsDTO.class);
	}

	/**
	 * Request to get all {@link DeclineReason} available in the system.
	 *
	 * @return list of {@link DeclineReason}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/decline-reasons", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getDeclineReasons", summary = "Finds a list of all decline reasons", description = "Finds a list of all decline reasons.")
	public DeclineReasonListWsDTO getDeclineReasons()
	{
		final List<DeclineReason> declineReasons = warehousingConsignmentFacade.getDeclineReasons();
		final DeclineReasonDataList declineReasonList = new DeclineReasonDataList();
		declineReasonList.setReasons(declineReasons);
		return dataMapper.map(declineReasonList, DeclineReasonListWsDTO.class);
	}

	/**
	 * Request to get all {@link de.hybris.platform.ordersplitting.model.ConsignmentEntryModel} for the given {@value de.hybris.platform.ordersplitting.model.ConsignmentModel#CODE}
	 *
	 * @param code
	 * 		{@value ConsignmentModel#CODE} for the requested consignment entries
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return list of {@link de.hybris.platform.ordersplitting.model.ConsignmentEntryModel} fulfilling the above conditions
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/entries", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getConsignmentEntriesForConsignmentCode", summary = "Finds a paginated list of consignment entries for a given consignment code", description = "Finds a paginated list of consignment entries for a given consignment code.")
	public ConsignmentEntrySearchPageWsDto getConsignmentEntriesForConsignmentCode(
			@Parameter(description = "The consignment code", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<ConsignmentEntryData> consignmentEntrySearchPageData = warehousingConsignmentFacade
				.getConsignmentEntriesForConsignmentCode(code, pageableData);
		return dataMapper.map(consignmentEntrySearchPageData, ConsignmentEntrySearchPageWsDto.class, fields);
	}

	/**
	 * Request to confirm {@link ConsignmentModel}'s shipping
	 *
	 * @param code
	 * 		consignment's code for the requested consignment
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/confirm-shipping", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "confirmShipConsignment", summary = "Confirms a consignment's shipping for a given consignment code", description = "Confirms a consignment's shipping for a given consignment code.")
	public void confirmShipConsignment(@Parameter(description = "The consignment code", required = true) @PathVariable final String code)
	{
		warehousingConsignmentFacade.confirmShipConsignment(code);
	}

	/**
	 * Request to confirm {@link ConsignmentModel}'s pickup
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the requested consignment
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/confirm-pickup", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "confirmPickupConsignment", summary = "Confirms a consignment's pickup for a given consignment code", description = "Confirms a consignment's pickup for a given consignment code.")
	public void confirmPickupConsignment(
			@Parameter(description = "The consignment code", required = true) @PathVariable final String code)
	{
		warehousingConsignmentFacade.confirmPickupConsignment(code);
	}

	/**
	 * Request to check if {@link ConsignmentModel} can be confirmed
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the requested consignment
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/is-confirmable", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "isConsignmentConfirmable", summary = "Checks if a consignment can be confirmed", description = "Checks if a consignment can be confirmed.")
	public Boolean isConsignmentConfirmable(
			@Parameter(description = "The consignment code", required = true) @PathVariable final String code)
	{
		return warehousingConsignmentFacade.isConsignmentConfirmable(code);
	}

	/**
	 * Request to get {@link PackagingInfoWsDTO} for the given {@link ConsignmentModel#CODE}
	 *
	 * @param code
	 * 		code of the consignment for which to get the packaging information
	 * @return the packaging information of the {@link ConsignmentModel}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/packaging-info", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getPackagingInfo", summary = "Finds the packaging information for the given consignment code", description = "Finds the packaging information for the given consignment code.")
	public PackagingInfoWsDTO getPackagingInfo(
			@Parameter(description = "The consignment code", required = true) @PathVariable @NotNull final String code)
	{
		final PackagingInfoData packagingInfo = warehousingConsignmentFacade.getConsignmentPackagingInformation(code);
		return dataMapper.map(packagingInfo, PackagingInfoWsDTO.class);
	}

	/**
	 * Request to update a {@link de.hybris.platform.ordersplitting.model.ConsignmentModel} with a new Packaging information.
	 *
	 * @param packagingInfo
	 * 		the {@link PackagingInfoWsDTO} to update the consignment with
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for which to update the packaging information
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return updated {@link ConsignmentWsDTO}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/packaging-info", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "updatePackagingInfo", summary = "Updates a consignment's packaging information", description = "Updates a consignment's packaging information.")
	public ConsignmentWsDTO updatePackagingInfo(
			@Parameter(description = "The PackagingInfoWsDTO to update the consignment with", required = true) @RequestBody final PackagingInfoWsDTO packagingInfo,
			@Parameter(description = "The consignment code", required = true) @PathVariable @NotNull final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		validate(packagingInfo, "packagingInfo", getPackagingInfoDTOValidator());
		final PackagingInfoData packagingInfoData = dataMapper.map(packagingInfo, PackagingInfoData.class, fields);
		final ConsignmentData consignmentData = warehousingConsignmentFacade
				.updateConsignmentPackagingInformation(code, packagingInfoData);
		return dataMapper.map(consignmentData, ConsignmentWsDTO.class, fields);
	}

	/**
	 * Request to reallocate a {@link de.hybris.platform.ordersplitting.model.ConsignmentModel} to a new warehouse.
	 *
	 * @param consignmentReallocationWsDTO
	 * 		the dto containing entries to be reallocated
	 * @param code
	 * 		{@link ConsignmentModel#CODE} to be reallocated
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/reallocate", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "reallocateConsignment", summary = "Reallocates a given consignment", description = "Reallocates a given consignment.")
	public void reallocateConsignment(
			@Parameter(description = "The ConsignmentReallocationWsDTO containing entries to be reallocated", required = true) @RequestBody final ConsignmentReallocationWsDTO consignmentReallocationWsDTO,
			@Parameter(description = "The consignment code", required = true) @PathVariable @NotNull final String code)
	{
		validate(consignmentReallocationWsDTO, "consignmentReallocationWsDTO", consignmentReallocationValidator);
		consignmentReallocationWsDTO.getDeclineEntries().forEach(declineEntryWsDTO -> {
			validate(declineEntryWsDTO, "declineEntryWsDTO", declineEntryValidator);
			if (declineEntryWsDTO.getReason() != null)
			{
				declineEntryWsDTO.setReason(declineEntryWsDTO.getReason().toUpperCase());
				validate(new String[] { declineEntryWsDTO.getReason() }, "reason", declineReasonValidator);
			}
		});
		final ConsignmentReallocationData consignmentReallocationData = dataMapper
				.map(consignmentReallocationWsDTO, ConsignmentReallocationData.class);
		warehousingConsignmentFacade.reallocateConsignment(code, consignmentReallocationData);
	}

	/**
	 * Request to pick a {@link ConsignmentModel} and generate its Pick Slip
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the requested consignment
	 * @param printSlip
	 * 		Flag for backwards compatibility. Used to check if the pick slip will be generated
	 * @return the optionally generated html Pick Slip template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/pick", method = RequestMethod.POST, produces = { MediaType.ALL_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "pickConsignment", summary = "Request to pick a given Consignment and optionally generate its Pick Slip", description = "Request to pick a given Consignment and optionally generate its Pick Slip.")
	public String pickConsignment(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code,
			@Parameter(description = "Flag for backwards compatibility. Used to check if the pick slip will be generated ") @RequestParam(required = false, defaultValue = "true") final boolean printSlip)
	{
		return warehousingConsignmentFacade.pickConsignment(code, printSlip);
	}

	/**
	 * Request to pick, if applicable, each {@link ConsignmentModel} for the provided list of consignment codes
	 * and generate a consolidated pick slip.
	 *
	 * @param consignmentCodes
	 * 		a list of consignment codes
	 * @return the generated html Consolidated Pick Slip template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/consolidated-pick", method = RequestMethod.POST, produces = { MediaType.ALL_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "consolidatedPickConsignments", summary = "Request to pick multiple Consignments and generate a consolidated Pick Slip", description = "Request to pick multiple Consignments and generate a consolidated Pick Slip.")
	public String consolidatedPickConsignments(
			@Parameter(description = "The ConsignmentsCodesWsDTO which contains the Consignment codes", required = true) @RequestBody final ConsignmentCodesWsDTO consignmentCodes)
	{
		final ConsignmentCodeDataList consignmentCodeDataList = dataMapper.map(consignmentCodes, ConsignmentCodeDataList.class);

		return warehousingConsignmentFacade.consolidatedPickSlip(consignmentCodeDataList);
	}

	/**
	 * Request to pack a {@link ConsignmentModel} and optionally generate its Pack Label
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the request consignment
	 * @param printSlip
	 * 		Flag for backwards compatibility. Used to check if the pack label will be generated
	 * @return the optionally generated html Pack Label template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/pack", method = RequestMethod.POST, produces = { MediaType.ALL_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "packConsignment", summary = "Request to pack a given Consignment and optionally generate its Pack Label", description = "Request to pack a given Consignment and optionally generate its Pack Label.")
	public String packConsignment(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code,
			@Parameter(description = "Flag for backwards compatibility. Used to check if the pack label will be generated") @RequestParam(required = false, defaultValue = "true") final boolean printSlip)
	{
		return warehousingConsignmentFacade.packConsignment(code, printSlip);
	}

	/**
	 * Request to get the export form of a {@link ConsignmentModel}
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the request consignment
	 * @return the generated html Export Form template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/export-form", method = RequestMethod.GET, produces = { MediaType.ALL_VALUE })
	@ResponseBody
	@Operation(operationId = "getExportForm", summary = "Request to ge the Export Form for a given Consignment", description = "Request to ge the Export Form for a given Consignment.")
	public String getExportForm(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code)
	{
		return warehousingConsignmentFacade.getExportForm(code);
	}

	/**
	 * Request to get the shipping label of a {@link ConsignmentModel}
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the request consignment
	 * @return the generated html Shipping Label template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/shipping-label", method = RequestMethod.GET, produces = { MediaType.ALL_VALUE })
	@ResponseBody
	@Operation(operationId = "getShippingLabel", summary = "Request to get the Shipping Label for a given Consignment", description = "Request to get the Shipping Label for a given Consignment.")
	public String getShippingLabel(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code)
	{
		return warehousingConsignmentFacade.getShippingLabel(code);
	}

	/**
	 * Request to get the return shipping label of a Consignment
	 *
	 * @param code
	 * 		consignment's code for the request consignment
	 * @return the generated html Return Shipping Label template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/return-shipping-label", method = RequestMethod.GET, produces = { MediaType.ALL_VALUE })
	@ResponseBody
	@Operation(operationId = "getReturnShippingLabel", summary = "Request to get the Return Shipping Label for a given Consignment", description = "Request to get the Return Shipping Label for a given Consignment.")
	public String getReturnShippingLabel(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code)
	{
		return warehousingConsignmentFacade.getReturnShippingLabel(code);
	}

	/**
	 * Request to get the return form of a {@link ConsignmentModel}
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the request consignment
	 * @return the generated html Return Form template
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/return-form", method = RequestMethod.GET, produces = { MediaType.ALL_VALUE })
	@ResponseBody
	@Operation(operationId = "getReturnForm", summary = "Request to get the Return form for a given Consignment", description = "Request to get the Return form for a given Consignment.")
	public String getReturnForm(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code)
	{
		return warehousingConsignmentFacade.getReturnForm(code);
	}

	/**
	 * Request to take a payment for a {@link ConsignmentModel}
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the request consignment
	 * @return the {@link PaymentTransactionEntryWsDTO}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/take-payment", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "takePayment", summary = "Request to take a payment for a given Consignment", description = "Request to take a payment for a given Consignment.")
	public PaymentTransactionEntryWsDTO takePayment(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PaymentTransactionEntryData transaction = warehousingConsignmentFacade.takePayment(code);
		return dataMapper.map(transaction, PaymentTransactionEntryWsDTO.class, fields);
	}

	/**
	 * Request to manually release {@link ConsignmentModel} from the waiting step after the payment capture has failed
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the request consignment
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/capture-payment", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleasePaymentCapture", summary = "Moves a consignment out of the waiting step after the payment capture has failed.", description = "Moves a consignment out of the waiting step after the payment capture has failed.")
	public void manuallyReleasePaymentCapture(
			@Parameter(description = "Code corresponding to the desired consignment", required = true) @PathVariable @NotNull final String code)
	{
		warehousingConsignmentFacade.manuallyReleasePaymentCapture(code);
	}

	/**
	 * Request to manully release {@link ConsignmentModel} from the waiting step after a tax commit failure
	 *
	 * @param code
	 * 		{@link ConsignmentModel#CODE} for the requested consignment
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/manual/commit-tax", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "manuallyReleaseTaxCommit", summary = "Moves a consignment out of the waiting step after a tax commit has failed", description = "Moves a consignment out of the waiting step after a tax commit has failed.")
	public void manuallyReleaseTaxCommit(
			@Parameter(description = "Code corresponding to the desired consignment") @PathVariable @NotNull final String code)
	{
		warehousingConsignmentFacade.manuallyReleaseTaxCommit(code);
	}

	/**
	 * Extract the set of {@link ConsignmentStatus} from the request
	 *
	 * @param statuses
	 * 		"," separated {@link ConsignmentStatus}
	 * @return set of {@link ConsignmentStatus}
	 * @throws WebserviceValidationException
	 * 		in case of passing a wrong {@link ConsignmentStatus}
	 */
	protected Set<ConsignmentStatus> extractConsignmentStatuses(final String statuses)
	{
		final String statusesStrings[] = statuses.split(WarehousingfacadesConstants.OPTIONS_SEPARATOR);

		final Set<ConsignmentStatus> statusesEnum = new HashSet<>();
		try
		{
			for (final String status : statusesStrings)
			{
				statusesEnum.add(ConsignmentStatus.valueOf(status));
			}
		}
		catch (final IllegalArgumentException e)
		{
			throw new WebserviceValidationException(e.getMessage());
		}
		return statusesEnum;
	}

	protected Validator getPackagingInfoDTOValidator()
	{
		return packagingInfoDTOValidator;
	}

	public void setPackagingInfoDTOValidator(final Validator packagingInfoDTOValidator)
	{
		this.packagingInfoDTOValidator = packagingInfoDTOValidator;
	}

	protected Validator getConsignmentReallocationValidator()
	{
		return consignmentReallocationValidator;
	}

	public void setConsignmentReallocationDTOValidator(final Validator consignmentReallocationValidator)
	{
		this.consignmentReallocationValidator = consignmentReallocationValidator;
	}
}
