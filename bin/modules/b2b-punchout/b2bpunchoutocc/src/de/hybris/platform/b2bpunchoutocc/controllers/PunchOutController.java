/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.controllers;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.aop.annotation.PunchOutAuthentication;
import de.hybris.platform.b2b.punchout.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.CXMLDesensitizer;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


/**
 * Controller to handle a PunchOut Transactions.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/punchout/cxml")
@Tag(description = "PunchOut", name = "PunchOut")
public class PunchOutController
{
	private static final Logger LOG = Logger.getLogger(PunchOutController.class);

	@Resource(name = "punchOutService")
	private PunchOutService punchOutService;

	@Resource(name = "punchOutUserAuthenticationStrategy")
	private PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy;

	@Resource(name = "occSupportedTransactionURLPaths")
	private Map<String, String> occSupportedTransactionURLPaths;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "cXMLDesensitizer")
	private CXMLDesensitizer cXMLDesensitizer;

	/**
	 * Handles a profile request from the punch out provider.
	 *
	 * @param request
	 *           The cXML with the request information.
	 * @return A cXML with the profile response.
	 */
	@PostMapping(value = "/profile", consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE}, produces = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
	@ResponseBody
	@Operation(operationId = "createPunchOutProfileRequest", summary = "Handles a PunchOut Profile Request", description = "Handles a Profile request from the Punch Out provider. It returns a list of URLs that provide basic information about the Punch Out Transactions supported by this CXML server.")
	@ApiBaseSiteIdParam
	@PunchOutAuthentication
	@SecurePortalUnauthenticatedAccess
	public CXML createPunchOutProfileRequest(
			@Parameter(description = "The CXML Profile Request. The header identifies the buyer organization, whereas the payload is empty.", required = true) @RequestBody final CXML request,
			@RequestHeader("host") String host,
			@Parameter(description = "Base site identifier", required = true) @PathVariable final String baseSiteId)
	{
		printCXmlDebugLog(request);
		setSupportedURLS(host, baseSiteId);
		final CXML cxml = getPunchOutService().processProfileRequest(request);
		printCXmlDebugLog(cxml);
		return cxml;
	}

	// Generate profile list for occ
	private void setSupportedURLS(final String host, final String baseSiteId)
	{
		final Map<String, String> profilePathsMap = new HashMap<>();
		final String OccURL = getOccAPIEndpoint(host, baseSiteId);
		for (final Map.Entry<String, String> entry : occSupportedTransactionURLPaths.entrySet())
		{
			profilePathsMap.put(entry.getKey(), OccURL.concat(entry.getValue()));
		}
		sessionService.setAttribute(PunchOutUtils.SUPPORTED_TRANSACTION_URL_PATHS, profilePathsMap);
	}

	// get api host either from ccv2 env or request
	private String getOccAPIEndpoint(String host, final String baseSiteId)
	{
		// this property returns in ccv2 like https://api.xxx.model-t.yyy.cloud
		final String OCCURLFromConfig = configurationService.getConfiguration().getString("ccv2.services.api.url.0");
		final String occRoot = configurationService.getConfiguration().getString("ext.b2bpunchoutocc.extension.webmodule.webroot","/occ/v2");

		return StringUtils.isNotEmpty(OCCURLFromConfig) ?
				OCCURLFromConfig.concat(occRoot).concat("/").concat(baseSiteId):
				configurationService.getConfiguration().getString("webservicescommons.required.channel", "https").concat(host)
						.concat(occRoot).concat("/").concat(baseSiteId);
	}

	/**
	 * Handles a Order Request from the Punch Out Provider.
	 *
	 * @param requestBody
	 *           The cXML containing the order to be processed.
	 * @param request
	 *           The servlet request.
	 * @param response
	 *           The servlet response.
	 * @return A cXML with the Order Response, containing the status of the processing of the order.
	 */
	@PostMapping(value = "/order", consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE}, produces = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
	@ResponseBody
	@Operation(operationId = "createPunchOutPurchaseOrderRequest", summary = "Handles a Order Request from the Punch Out Provider.", description = "A purchase order is a formal request from a buying organization to this CXML supplier to fulfill a contract. ")
	@ApiBaseSiteIdParam
	@PunchOutAuthentication
	@SecurePortalUnauthenticatedAccess
	public CXML createPunchOutPurchaseOrderRequest(
		@Parameter(description = "The cXML containing the order to be processed. It consists of a header that contains fields such as order id, order date,ship to and tax. The payload contains information pertaining to individual items ordered.", required = true)
		@RequestBody
		final CXML requestBody, final HttpServletRequest request,
		final HttpServletResponse response)
	{
		printCXmlDebugLog(requestBody);
		final String identity = getPunchOutService().retrieveIdentity(requestBody);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Order Identity:" + identity);
		}

		final CXML cxml;
		// if user exists
		if (identity != null)
		{
			punchOutUserAuthenticationStrategy.authenticate(identity, request, response);

			cxml = punchOutService.processPurchaseOrderRequest(requestBody);
		}
		else
		{
			final String message = "Unable to find user " + identity;
			LOG.error(message);
			cxml = CXMLBuilder.newInstance().withResponseCode(HttpStatus.UNAUTHORIZED)
							  .withResponseMessage(message).create();
		}
		printCXmlDebugLog(cxml);

		return cxml;
	}

	/**
	 * Receives a request from the punchout provider and sends it the information to access the hybris site.
	 *
	 * @param requestBody
	 *           The cXML file with the punchout user requisition.
	 * @return A cXML file with the access information.
	 */
	@PostMapping(value = "/setup", consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE}, produces = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
	@ResponseBody
	@Operation(operationId = "createPunchOutSetUpRequest", summary = "This transaction is used to initiate a Punch Out Session.", description = "Used to create a new Punch Out session by authenticating a Punch Out user.This happens when the user of the Procurement system selects a Punch Out Item. ")
	@ApiBaseSiteIdParam
	@PunchOutAuthentication
	@SecurePortalUnauthenticatedAccess
	public CXML createPunchOutSetUpRequest(
		@Parameter(description = "The header contains credentials that uniquely identify the buyer in the Procurement system. The request indicates whether it is a create,edit or inspect transaction. ", required = true)
		@RequestBody
		final CXML requestBody)
	{
		printCXmlDebugLog(requestBody);
		final CXML cxml = getPunchOutService().processPunchOutSetUpRequest(requestBody);
		printCXmlDebugLog(cxml);
		return cxml;
	}

	public PunchOutService getPunchOutService()
	{
		return punchOutService;
	}

	public void setPunchOutService(final PunchOutService punchoutService)
	{
		this.punchOutService = punchoutService;
	}

	private void printCXmlDebugLog(CXML cxml) {
		if (LOG.isDebugEnabled())
		{
			final String cxmlString = PunchOutUtils.marshallFromBeanTree(cxml);
			try
			{
				LOG.debug("cXML: " + cXMLDesensitizer.desensitizeCXMLData(cxmlString));
			}
			catch (PunchOutException e)
			{
				LOG.warn("failed to desensitize CXML");
			}
		}
	}
}
