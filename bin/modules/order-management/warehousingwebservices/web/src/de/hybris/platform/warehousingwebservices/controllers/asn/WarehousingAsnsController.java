/*
 * [y] hybris Platform
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousingwebservices.controllers.asn;

import de.hybris.platform.warehousingfacades.asn.WarehousingAsnFacade;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.asn.AsnWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

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
 * WebResource exposing {@link WarehousingAsnFacade} http://host:port/warehousingwebservices/asns
 */
@Controller
@RequestMapping(value = "/asns")
@Tag(name = "Advanced Shipping Notice's Operations")
public class WarehousingAsnsController extends WarehousingBaseController
{
	@Resource
	private WarehousingAsnFacade warehousingAsnFacade;
	@Resource
	private Validator asnValidator;
	@Resource
	private Validator asnEntryValidator;

	/**
	 * Request to create a {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param asnWsDTO
	 * 		object representing {@link AsnWsDTO}
	 * @return created {@link AsnData}
	 * @throws {@link
	 * 		WebserviceValidationException}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createAsn", summary = "Creates an advanced shipping notice", description = "Creates a new advanced shipping notice as given in the request.")
	public AsnWsDTO createAsn(
			@Parameter(description = "AsnWsDTO containing information about the asn to be created", required = true) @RequestBody final AsnWsDTO asnWsDTO,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		validate(asnWsDTO, "asnWsDTO", asnValidator);
		asnWsDTO.getAsnEntries().forEach(entry -> validate(entry, "asnEntryWsDTO", asnEntryValidator));
		final AsnData asnData = dataMapper.map(asnWsDTO, AsnData.class);
		final AsnData createdAsnData = warehousingAsnFacade.createAsn(asnData);

		return dataMapper.map(createdAsnData, AsnWsDTO.class, fields);
	}


	/**
	 * Request to confirm receipt of {@link AsnWsDTO}
	 *
	 * @param internalId
	 * 		{@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel#INTERNALID}
	 * 		for the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} to be confirmed
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{internalId}/confirm-receipt", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "confirmAsnReceipt", summary = "Confirms the receipt of an advanced shipping notice", description = "Confirms the receipt of an advanced shipping notice.")
	public AsnWsDTO confirmAsnReceipt(
			@Parameter(description = "Internal Id for the advanced shipping notice to be confirmed", required = true) @PathVariable @NotNull final String internalId,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final AsnData updatedAsnData = warehousingAsnFacade.confirmAsnReceipt(internalId);
		return dataMapper.map(updatedAsnData, AsnWsDTO.class, fields);
	}



	/**
	 * Request to cancel an {@link AsnWsDTO}
	 *
	 * @param internalId
	 * 		{@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel#INTERNALID}
	 * 		for the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} to be cancelled
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{internalId}/cancel", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	@Operation(operationId = "cancelAsn", summary = "Cancels an advanced shipping notice", description = "Cancels an advanced shipping notice.")
	public AsnWsDTO cancelAsn(
			@Parameter(description = "Internal Id for the advanced shipping notice to be cancelled", required = true) @PathVariable @NotNull final String internalId,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final AsnData cancelledAsn = warehousingAsnFacade.cancelAsn(internalId);
		return dataMapper.map(cancelledAsn, AsnWsDTO.class, fields);
	}
}
