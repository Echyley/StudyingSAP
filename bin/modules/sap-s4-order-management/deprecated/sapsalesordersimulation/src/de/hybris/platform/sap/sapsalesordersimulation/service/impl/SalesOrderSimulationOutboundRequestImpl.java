/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationOutboundRequest;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulationData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulationRequestData;
import de.hybris.platform.servicelayer.session.SessionService;

public class SalesOrderSimulationOutboundRequestImpl implements SalesOrderSimulationOutboundRequest {
	private static final String BASIC = "Basic ";
	private static final String X_CSRF_TOKEN = "X-CSRF-Token";
	private static final String X_CSRF_TOKEN_FETCH = "Fetch";
	private static final String OUTBOUND_SIMULATE_SALES_ORDER_DESTINATION = "sapOrderSimulateDestination";
	private static final String OUTBOUND_SIMULATE_SALES_ORDER_DESTINATION_TARGET_ID = "sapOrderSimulateOutboundDestination";
	private static final String METADATA = "$metadata";
	private static final String SALESORDERSIMULATE = "A_SalesOrderSimulation";
	private static final String AUTHORIZATION = "Authorization";
	private static final String COOKIE_SEPARATOR = "; ";
	private static final String X_CSRF_SET_COOKIES = "Set-Cookie";
	private static final String X_CSRF_COOKIE = "Cookie";
	private static final String CSRF_URL = "csrfURL";
	private static final String QUERY_SAP_CLIENT = "?sap-client=";
	private static final String SAP_CLIENT = "sap-client";

	private static final Logger LOG = LoggerFactory.getLogger(SalesOrderSimulationOutboundRequestImpl.class);
	private DestinationService<ConsumedDestinationModel> destinationService;
	private SessionService sessionService;
	private RestTemplate salesOrderSimulationRestTemplate;

	@Override
	public SalesOrderSimulationData getResponseFromSalesOrderSimulation(SalesOrderSimulationRequestData requestData) {
		final ConsumedDestinationModel destinationModel = getDestinationService().getDestinationByIdAndByDestinationTargetId(OUTBOUND_SIMULATE_SALES_ORDER_DESTINATION, OUTBOUND_SIMULATE_SALES_ORDER_DESTINATION_TARGET_ID);
		AuthenticationResult result = getAuthenticationDetails(destinationModel);
		HttpHeaders header = createHTTPHeader(result);
		return  postSimulationRequest(destinationModel.getUrl()+SALESORDERSIMULATE, requestData, header).getBody();

	}
	
	
	
	protected ResponseEntity<SalesOrderSimulationData> postSimulationRequest(String url,
			SalesOrderSimulationRequestData requestData, HttpHeaders header) {
		HttpEntity entity = new HttpEntity<>(requestData, header);
		ResponseEntity<SalesOrderSimulationData> salesOrderSimulationData = null;
		try
		{
			salesOrderSimulationData = getSalesOrderSimulationRestTemplate().exchange(url, HttpMethod.POST, entity, SalesOrderSimulationData.class);
		}
		catch (final RestClientException e)
		{
			LOG.error(String.format("Unable to rechout or fetch details form  url [{%s}] ",url));
			throw e;
		}
		return salesOrderSimulationData;
	}

	protected HttpHeaders createHTTPHeader(AuthenticationResult authenticationResult) {

		final HttpHeaders header = new HttpHeaders();
		
		header.add(X_CSRF_TOKEN, getCsrfToken(authenticationResult.getResponseHeader()));
		header.add(X_CSRF_COOKIE, getCookies(authenticationResult.getResponseHeader()));

		final List<MediaType> acceptList = new ArrayList<>();
		acceptList.add(MediaType.APPLICATION_JSON);
		header.setAccept(acceptList);
		header.setContentType(MediaType.APPLICATION_JSON);
		return header;
		
		
	}
	
	protected String getCsrfToken(final HttpHeaders headers) {
		final List<String> tokens = headers.get(X_CSRF_TOKEN);
		return CollectionUtils.isNotEmpty(tokens) ? tokens.get(0) : "";
	}
	protected  String getCookies(final HttpHeaders headers) {
		final List<String> cookies = headers.get(X_CSRF_SET_COOKIES);
		return StringUtils.join(cookies, COOKIE_SEPARATOR);

	}

	protected AuthenticationResult getAuthenticationDetails(ConsumedDestinationModel destinationModel) {
		AuthenticationResult authenticationResult = null;
		
		BasicCredentialModel basicCredentialModel = (BasicCredentialModel) destinationModel.getCredential();
		final String basicCredentials = basicCredentialModel.getUsername() + ":" + basicCredentialModel.getPassword();
		
		final HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.set(AUTHORIZATION, BASIC + Base64.getEncoder().encodeToString(basicCredentials.getBytes()));
		httpHeader.add(X_CSRF_TOKEN, X_CSRF_TOKEN_FETCH);
		
		String csrfTokenUrl = getCsrfURL(destinationModel);//destinationModel.getUrl()+ METADATA;
		try
		{
			final ResponseEntity<String> csrfTokenExchange = getSalesOrderSimulationRestTemplate().exchange(csrfTokenUrl, HttpMethod.GET, new HttpEntity<String>(httpHeader), String.class);
			authenticationResult = new AuthenticationResult();
			authenticationResult.setResponseHeader(csrfTokenExchange.getHeaders());
		}
		catch (final RestClientException e)
		{
			LOG.error(String.format("Remote System with the url [{%s}] is not reachable",destinationModel.getUrl()+ METADATA));
			throw e;
		}
		return authenticationResult;
	}
	
	protected String getCsrfURL(ConsumedDestinationModel destinationModel) {

		String csrfUrl;
		if(destinationModel.getAdditionalProperties().get(CSRF_URL) != null) {
			csrfUrl = destinationModel.getAdditionalProperties().get(CSRF_URL);
		} else {
			csrfUrl = destinationModel.getUrl() + METADATA;
		}
		
		if(destinationModel.getAdditionalProperties().get(SAP_CLIENT) != null) {
			csrfUrl = csrfUrl + QUERY_SAP_CLIENT +destinationModel.getAdditionalProperties().get(SAP_CLIENT);
		}
		
		return csrfUrl;
	}
	
	public RestTemplate getSalesOrderSimulationRestTemplate() {
		return salesOrderSimulationRestTemplate;
	}

	public void setSalesOrderSimulationRestTemplate(RestTemplate salesOrderSimulationRestTemplate) {
		this.salesOrderSimulationRestTemplate = salesOrderSimulationRestTemplate;
	}

	protected DestinationService<ConsumedDestinationModel> getDestinationService() {
		return destinationService;
	}

	public void setDestinationService(DestinationService<ConsumedDestinationModel> destinationService) {
		this.destinationService = destinationService;
	}

	protected SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}
	
	 
	
}