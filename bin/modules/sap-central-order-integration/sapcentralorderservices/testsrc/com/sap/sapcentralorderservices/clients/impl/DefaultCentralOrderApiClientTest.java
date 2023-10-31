/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.clients.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants;
import com.sap.sapcentralorderservices.exception.SapCentralOrderException;


/**
 * @param <T>
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderApiClientTest
{


	DefaultCentralOrderApiClient defaultCentralOrderApiClient = new DefaultCentralOrderApiClient();


	DestinationService destinationService = mock(DestinationService.class);


	IntegrationRestTemplateFactory integrationRestTemplateFactory = mock(IntegrationRestTemplateFactory.class);


	RestOperations restOperations = mock(RestOperations.class);


	ResponseEntity<CentralOrderDetailsResponse> responseEntity = mock(ResponseEntity.class);

	@Test
	public void getEntityTest() throws SapCentralOrderException
	{
		defaultCentralOrderApiClient.setDestinationService(destinationService);
		defaultCentralOrderApiClient.setIntegrationRestTemplateFactory(integrationRestTemplateFactory);
		final UriComponents uriComponents = UriComponentsBuilder.fromUriString("/orders").build();

		final Class response = CentralOrderDetailsResponse.class;

		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final HttpEntity<String> requestEntity = new HttpEntity(header);
		final ConsumedDestinationModel destinationModel = new ConsumedDestinationModel();
		destinationModel.setUrl("http://something.com");

		when(destinationService.getDestinationByIdAndByDestinationTargetId(SapcentralorderservicesConstants.CENTRALORDERSERVICEDEST,
				SapcentralorderservicesConstants.CENTRALORDERSERVICEDESTTARGET)).thenReturn(destinationModel);

		when(integrationRestTemplateFactory.create(any(ConsumedDestinationModel.class))).thenReturn(restOperations);

		when(restOperations.exchange(any(URI.class), eq(HttpMethod.GET), eq(requestEntity), eq(response)))
				.thenReturn(responseEntity);

		defaultCentralOrderApiClient.getEntity(uriComponents, response);

		Mockito.verify(restOperations).exchange(any(URI.class), eq(HttpMethod.GET), eq(requestEntity), eq(response));

	}

}
