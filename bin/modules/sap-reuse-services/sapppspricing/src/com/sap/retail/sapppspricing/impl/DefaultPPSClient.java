/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateResponse;
import com.sap.retail.sapppspricing.PPSClient;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * PPS client supporting remote call via REST. For the remote call OAuth
 * authentication is supported. via configuration (see {@link PPSConfigService})
 */
public class DefaultPPSClient extends DefaultPPSClientBeanAccessor implements PPSClient {

	// ID to relate this request to entry in server log
	protected static final String X_REQUEST_ID = "x-request-id";
	protected static final String AUTHORIZATION = "Authorization";
	protected static final String CLIENT_ID = "client_id";
	protected static final String CLIENT_SECRET = "client_secret";
	protected static final String GRANT_TYPE = "grant_type";
	protected static final String EQUALS = "=";
	protected static final String AND = "&";
	protected static final String DESTINATIONID = "scpOPPServiceDestination";
	protected static final String DESTINATIONTARGET = "scpOPPSDestinationTarget";
	protected static final String GRANT_TYPE_VALUE = "client_credentials";
	protected static final int SIZE = 8;
	protected static final double TIMECONVERSION_TO_MS = 1000000;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPPSClient.class);
	private BaseStoreService baseStoreService;
	private FlexibleSearchService flexibleSearchService;
	private DestinationService<ConsumedDestinationModel> destinationService;
	
	/**
	 * @return the destinationService
	 */
	public DestinationService<ConsumedDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	/**
	 * @param destinationService
	 *           the destinationService to set
	 */
	public void setDestinationService(final DestinationService<ConsumedDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}
	
	public PriceCalculateResponse callPPS(final PriceCalculate priceCalculate, final SAPConfigurationModel sapConfig) {
		LOG.debug("entering callPPSRemote()");
		final long t1 = System.nanoTime();
		PriceCalculateResponse responseBody = null;
		String str;
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				HttpClients.createDefault());
		RestTemplate rest = new RestTemplate(requestFactory);
		HttpHeaders httpHeaders;
		final ConsumedDestinationModel destinationModel = getDestinationService().getDestinationByIdAndByDestinationTargetId(DESTINATIONID,DESTINATIONTARGET);
		if (destinationModel == null) {
			throw new ModelNotFoundException("Provided destination was not found.");
		}
		final ConsumedOAuthCredentialModel credential = (ConsumedOAuthCredentialModel) destinationModel.getCredential();
		final String username = credential.getClientId();
		final String password = credential.getClientSecret();
		final String tokenUrl = credential.getOAuthUrl();
		try {
			httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			ArrayList<MediaType> al = new ArrayList<>();
			al.add(MediaType.APPLICATION_JSON);
			httpHeaders.setAccept(al);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
			mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
			str = mapper.writeValueAsString(priceCalculate);
			LOG.debug("Request Body ={}", str);
		} catch (JsonProcessingException e) {
			LOG.error("Json processing exception {}", e.getMessage());
			return new PriceCalculateResponse();
		}
		String token = getToken(username,password,tokenUrl);
		httpHeaders.set(AUTHORIZATION, "Bearer " + token);
		try {
			ResponseEntity<PriceCalculateResponse> result = rest.exchange(destinationModel.getUrl(), HttpMethod.POST,
					new HttpEntity<String>(str, httpHeaders), PriceCalculateResponse.class);
			responseBody = result.getBody();

			LOG.debug("Response Body ={}", responseBody);

			if (!result.getStatusCode().is2xxSuccessful()) {
				throw new SapPPSPricingRuntimeException("Unexpected return code " + result.getStatusCode().value());
			} else {
				final long t2 = System.nanoTime();
				if (LOG.isDebugEnabled()) {
					LOG.debug("Request duration in ms {}", (t2 - t1) / TIMECONVERSION_TO_MS);
				}
				return responseBody;
			}
		} catch (Exception e) {
			LOG.error("Exception occured within Rest {}", e.getMessage());
		}
		return responseBody;
	}

	private String getToken(String clientId, String clientSecret, String tokenUrl) {
		String result = "";
		HttpURLConnection urlConnection = null;

		try {
			URL url = new URL(tokenUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(HttpMethod.POST.name());
			urlConnection.setDoOutput(true);

			String data = URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) + EQUALS + URLEncoder.encode(clientId, StandardCharsets.UTF_8);
			data += AND + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8) + EQUALS + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8);
			data += AND + URLEncoder.encode(GRANT_TYPE, StandardCharsets.UTF_8) + EQUALS + URLEncoder.encode(GRANT_TYPE_VALUE, StandardCharsets.UTF_8);

			urlConnection.connect();

			writeToOutputStream(urlConnection, data);

			result = readFromInputStream(urlConnection);

			String[] json = result.split(":");
			String[] token = json[1].split("\"");
			result = token[1];
		} catch (IOException e) {
			LOG.error("Exception occured within getToken method {}", e.getMessage());
			return result;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return result;
	}

	/**
	 * Write to output stream
	 * @param urlConnection
	 * @param data
	 * @throws IOException
	 */
	private void writeToOutputStream(HttpURLConnection urlConnection, String data) throws IOException {
		OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream(), StandardCharsets.UTF_8);
		wr.write(data);
		wr.flush();
	}

	/**
	 * Read from input stream
	 * @param urlConnection
	 * @return String
	 * @throws IOException
	 */
	private String readFromInputStream(HttpURLConnection urlConnection) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8), SIZE);
		return reader.readLine();
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

}
