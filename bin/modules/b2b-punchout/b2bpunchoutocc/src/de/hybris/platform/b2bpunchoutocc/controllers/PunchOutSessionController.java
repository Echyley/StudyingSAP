/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.controllers;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionExpired;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.constants.PunchOutSetupOperation;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;
import de.hybris.platform.b2b.punchout.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutocc.dto.PunchOutTokenWsDTO;
import de.hybris.platform.b2bpunchoutocc.exception.PunchOutCartMissingException;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.cxml.CXML;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import de.hybris.platform.b2bpunchoutocc.dto.PunchOutSessionInfoWsDTO;
import de.hybris.platform.b2bpunchoutocc.dto.RequisitionFormDataWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;


@Controller
@RequestMapping(value = "/{baseSiteId}/punchout/sessions")
@Tag(description = "PunchOut", name = "PunchOut")
public class PunchOutSessionController
{
	@Resource(name = "punchOutSessionService")
	private PunchOutSessionService punchoutSessionService;
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;
	@Resource(name = "punchOutUserAuthenticationStrategy")
	private PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy;
	@Resource
	private PunchOutService punchOutService;

	@GetMapping(value = "/{sid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(operationId = "getPunchOutSessionInfo", summary = "Get PunchOut session info.", description = "Return the user ID, cart ID, token, PunchOut level, PunchOut operation and selected item.")
	@ApiBaseSiteIdParam
	@SecurePortalUnauthenticatedAccess
	public PunchOutSessionInfoWsDTO getPunchOutSessionInfo(
			@Parameter(description = "PunchOut session identifier", required = true) @PathVariable final String sid,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final StoredPunchOutSessionModel storedSession = punchoutSessionService.loadStoredPunchOutSessionModel(sid);
		if (storedSession == null)
		{
			throw new NotFoundException("sid is not supplied or not existing");
		}

		final PunchOutSession punchoutSession = (PunchOutSession) storedSession.getPunchOutSession();
		try
		{
			punchoutSessionService.checkAndActivatePunchOutSession(punchoutSession);
		}
		catch (PunchOutSessionNotFoundException | PunchOutSessionExpired exception)
		{
			throw new NotFoundException("no valid sid found");
		}
		final B2BCustomerModel b2BCustomerModel = punchoutSessionService.loadB2BCustomerModel(punchoutSession);
		final String uid = b2BCustomerModel.getUid();
		final String customerId = b2BCustomerModel.getCustomerID();
		PunchOutSessionInfoWsDTO punchOutSessionInfoWsDTO = dataMapper.map(punchoutSession, PunchOutSessionInfoWsDTO.class);
		if (storedSession.getCart() != null)
		{
			punchOutSessionInfoWsDTO.setCartId(storedSession.getCart().getCode());
		}
		else
		{
			throw new PunchOutCartMissingException("There was an internal error due to empty cart. Please try again.");
		}
		punchOutUserAuthenticationStrategy.authenticate(uid, request, response);
		final OAuth2AccessToken punchOutToken = punchoutSessionService.getPunchOutTokenByUidAndSid(uid, sid);
		PunchOutTokenWsDTO punchOutTokenWsDTO = new PunchOutTokenWsDTO();
		punchOutTokenWsDTO.setAccessToken(punchOutToken.getValue());
		punchOutTokenWsDTO.setTokenType(punchOutToken.getTokenType());
		punchOutSessionInfoWsDTO.setToken(punchOutTokenWsDTO);
		punchOutSessionInfoWsDTO.setCustomerId(customerId);
		punchOutSessionInfoWsDTO
				.setPunchOutOperation(EnumUtils.getEnumIgnoreCase(PunchOutSetupOperation.class, punchoutSession.getOperation()));
		punchOutSessionInfoWsDTO.setPunchOutLevel(punchoutSession.getPunchoutLevel());
		return dataMapper.map(punchOutSessionInfoWsDTO, PunchOutSessionInfoWsDTO.class, fields);
	}

	@GetMapping(value = "/{sid}/requisition", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(operationId = "getRequisitionFormData", summary = "Get order message and url for procurement system.", description = "Return the order message in base64 encoded cXML format and the url that will be used to submit the order.")
	@ApiBaseSiteIdParam
	@SecurePortalUnauthenticatedAccess
	public RequisitionFormDataWsDTO getRequisitionFormData(
			@Parameter(description = "An indicator to show you want to generate requisition form data with or without cart entries. If true, will generate data with empty cart, otherwise will generate data with cart entries.") @RequestParam(required = false, defaultValue = "false") boolean discardCartEntries,
			@Parameter(description = "PunchOut session identifier", required = true) @PathVariable final String sid,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final CXML cXML;
		final PunchOutSession punchOutSession;
		try
		{
			punchOutSession = punchoutSessionService.loadPunchOutSession(sid);
			punchoutSessionService.setCurrentCartFromPunchOutSetup(sid);
		}
		catch (PunchOutSessionNotFoundException | PunchOutSessionExpired e)
		{
			if (StringUtils.isEmpty(e.getMessage()))
			{
				throw new NotFoundException("no valid sid found");
			}
			else
			{
				throw new NotFoundException(e.getMessage());
			}
		}
		if (discardCartEntries)
		{
			cXML = punchOutService.processCancelPunchOutOrderMessage();
		}
		else
		{
			cXML = punchOutService.processPunchOutOrderMessage();
		}
		final RequisitionFormDataWsDTO requisitionFormDataWsDTO = new RequisitionFormDataWsDTO();
		requisitionFormDataWsDTO.setOrderAsCXML(PunchOutUtils.transformCXMLToBase64(cXML));
		requisitionFormDataWsDTO.setBrowseFormPostUrl(punchOutSession.getBrowserFormPostUrl());
		return dataMapper.map(requisitionFormDataWsDTO, RequisitionFormDataWsDTO.class, fields);
	}

}
