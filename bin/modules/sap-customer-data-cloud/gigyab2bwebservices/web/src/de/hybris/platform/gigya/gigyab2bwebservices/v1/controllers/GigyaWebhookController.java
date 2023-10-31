/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bwebservices.v1.controllers;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.gigya.gigyab2bfacades.webhook.GigyaB2BWebhookFacade;
import de.hybris.platform.gigya.gigyab2bwebservices.constants.Gigyab2bwebservicesConstants;
import de.hybris.platform.gigya.gigyab2bwebservices.dto.GigyaWebHookResponseWSDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Gigya Webhook")
public class GigyaWebhookController
{
	private static final Logger LOG = LoggerFactory.getLogger(GigyaWebhookController.class);

	@Resource(name = "gigyaB2BWebhookFacade")
	private GigyaB2BWebhookFacade gigyaB2BWebhookFacade;

	/**
	 * Receives the webhook events and creates users.
	 * 
	 * @param request
	 *            the HTTP Request
	 * @param baseSiteId
	 *            base site identifier
	 * @param jwtToken
	 *            JWT Token for payload verification
	 * @param gigyawebhookRequestDTO
	 *            CDC Webhook payload
	 * @param response
	 *            the HTTP Response
	 * @return GigyaWebHookResponseWSDTO
	 */
	@ResponseBody
	@PostMapping(value = "/b2bwebhook", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "receiveWebhookEvents", summary = "Receive webhook events from SAP Customer Data Cloud and process them.", description = "Receives the webhook events and schedules a task to create B2B users with their roles.")
	@ApiBaseSiteIdParam
	@ApiResponse(responseCode = "200", description = "Ok")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	public GigyaWebHookResponseWSDTO receiveWebhookEvents(@Parameter(hidden = true) final HttpServletRequest request,
	                @Parameter(description = "The Base site identifier", required = true) @PathVariable final String baseSiteId,
	                @Parameter(description = "The JWT Token sent by SAP CDC in the header", required = true) @RequestHeader(Gigyab2bwebservicesConstants.GIGYA_JWT_HEADER) final String jwtToken,
	                @RequestBody final GigyaWebHookRequest gigyawebhookRequest,
	                @Parameter(hidden = true) final HttpServletResponse response)
	{

		LOG.info("Gigya Webhook Event(s) endpoint invoked.");
		String payloadHash = generatePayloadHash(gigyawebhookRequest);
		boolean jwtValid = gigyaB2BWebhookFacade.validateWebHookJWTToken(jwtToken, baseSiteId, payloadHash);
		if (!jwtValid)
		{
			LOG.error("Gigya JWT Token is invalid, rejecting webhook request with nonce. {} ", gigyawebhookRequest.getNonce());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return createResponse(HttpServletResponse.SC_BAD_REQUEST, Gigyab2bwebservicesConstants.ERROR,
			                Gigyab2bwebservicesConstants.ERROR_MESSAGE);
		}

		gigyawebhookRequest.setBaseSiteId(baseSiteId);

		// Schedule task
		gigyaB2BWebhookFacade.receiveGigyaWebHookEvents(gigyawebhookRequest);

		// Sucessfully scheduled
		return createResponse(HttpServletResponse.SC_OK, Gigyab2bwebservicesConstants.SUCCESS,
		                Gigyab2bwebservicesConstants.SUCCESS_MESSAGE);

	}

	/**
	 * Generate the hash from the payload
	 * 
	 * @param gigyawebhookRequestDTO
	 * @return String
	 */
	private String generatePayloadHash(GigyaWebHookRequest gigyawebhookRequest)
	{

		try
		{
			final var mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			var reqPayload = mapper.writeValueAsString(gigyawebhookRequest);

			final var digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(reqPayload.getBytes(StandardCharsets.UTF_8));

			return String.format("%040x", new BigInteger(1, hash)); // return
			                                                        // the HEX
			                                                        // value
		}
		catch (NoSuchAlgorithmException | JsonProcessingException e)
		{
			LOG.error("Gigya JWT Payload verification failed. ", e);
		}
		return "";
	}

	/**
	 * Creates the Webhook controller Response
	 * 
	 * @return GigyaWebHookResponseWSDTO
	 */
	GigyaWebHookResponseWSDTO createResponse(int code, String result, String message)
	{
		var gigyaResponse = new GigyaWebHookResponseWSDTO();
		gigyaResponse.setCode(code);
		gigyaResponse.setResult(result);
		gigyaResponse.setMessage(message);

		return gigyaResponse;
	}
}
