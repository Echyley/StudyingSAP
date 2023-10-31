/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.clients.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;

import java.net.URI;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.sapcentralorderservices.clients.CentralOrderApiClient;
import com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants;
import com.sap.sapcentralorderservices.exception.SapCentralOrderException;

/**
 *
 */
public class DefaultCentralOrderApiClient implements CentralOrderApiClient
{
	private static final Logger LOG = Logger.getLogger(DefaultCentralOrderApiClient.class);

	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private DestinationService destinationService;

	/**
	 * @return the integrationRestTemplateFactory
	 */
	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	/**
	 * @param integrationRestTemplateFactory
	 *           the integrationRestTemplateFactory to set
	 */
	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	/**
	 * @return the destinationService
	 */
	public DestinationService getDestinationService()
	{
		return destinationService;
	}

	/**
	 * @param destinationService
	 *           the destinationService to set
	 */
	public void setDestinationService(final DestinationService destinationService)
	{
		this.destinationService = destinationService;
	}


	public void postEntity(final UriComponents uriComponents, final Object entity) throws SapCentralOrderException
	{
		postEntity(uriComponents, entity, Void.class);

	}

	/**
	 * @throws SapCentralOrderException
	 *
	 */
	public <T> T postEntity(final UriComponents uriComponents, final Object entity, final Class<T> response)
			throws SapCentralOrderException
	{

		//Fetch Auth Token
		final ConsumedDestinationModel destinationModel = (ConsumedDestinationModel) getDestinationService()
				.getDestinationByIdAndByDestinationTargetId(SapcentralorderservicesConstants.CENTRALORDERSERVICEDEST,
						SapcentralorderservicesConstants.CENTRALORDERSERVICEDESTTARGET);
		if (destinationModel == null)
		{
			throw new SapCentralOrderException(
					String.format("Missing Destination model [%s]", SapcentralorderservicesConstants.CENTRALORDERSERVICEDEST));
		}
		final RestOperations restOperations = buildRestOperations(destinationModel);


		//Build Url
		final URI uri = UriComponentsBuilder.fromHttpUrl(destinationModel.getUrl()).uriComponents(uriComponents).build().toUri();

		//Call API
		try
		{
			LOG.info(String.format("Calling post uri: %s", uri.toString()));
			final ResponseEntity<T> responseEntity = restOperations.postForEntity(uri, entity, response);
			return responseEntity.getBody();
		}
		catch (final HttpClientErrorException ex)
		{
			LOG.error(String.format("Client Exception occurred while calling uri %s", uri.toString()));
			throw ex;
		}
		catch (final HttpServerErrorException ex)
		{
			LOG.error(ex);
			throw new SapCentralOrderException("Unable to perform operation. Check server logs for more details.");
		}
		catch (final RestClientException ex)
		{
			LOG.error(ex);
			throw new SapCentralOrderException("Unable to perform operation. Check configuration or logs for more details.");
		}
		catch (final Exception ex)
		{
			LOG.error(ex);
			throw new SapCentralOrderException("Unknown Exception");
       }
   }

	/**
	 *
	 * @param uriComponents
	 * @param clazz
	 * @param <T>
	 * @return {@link ResponseEntity<T>}
	 * @throws SapCentralOrderException
	 */
	public <T> ResponseEntity<T> getEntity(final UriComponents uriComponents, final Class<T> clazz) throws SapCentralOrderException
	{
		return getRawEntity(uriComponents, clazz);
	}

	/**
	 * @throws SapCentralOrderException
	 *
	 */
	public <T> ResponseEntity<T> getRawEntity(final UriComponents uriComponents, final Class<T> clazz)
			throws SapCentralOrderException
	{

		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final HttpEntity<String> requestEntity = new HttpEntity(header);

		//Fetch Auth Token
		final ConsumedDestinationModel destinationModel = (ConsumedDestinationModel) getDestinationService()
				.getDestinationByIdAndByDestinationTargetId(SapcentralorderservicesConstants.CENTRALORDERSERVICEDEST,
						SapcentralorderservicesConstants.CENTRALORDERSERVICEDESTTARGET);
		if (destinationModel == null)
		{
			throw new SapCentralOrderException(
					String.format("Missing Destination model [%s]", SapcentralorderservicesConstants.CENTRALORDERSERVICEDEST));
		}
		final RestOperations restOperations = buildRestOperations(destinationModel);

		//Build Url
		final URI uri = UriComponentsBuilder.fromUriString(destinationModel.getUrl()).uriComponents(uriComponents).build().toUri();

		//Call API
		try
		{
			LOG.info(String.format("Calling uri: %s", uri.toString()));
			return restOperations.exchange(uri, HttpMethod.GET, requestEntity, clazz);
		}
		catch (final HttpClientErrorException ex)
		{
			throw ex;
		}
		catch (final HttpServerErrorException ex)
		{
			throw new SapCentralOrderException("Unable to perform operation. Check server logs for more details.");
		}
		catch (final RestClientException ex)
		{
			throw new SapCentralOrderException("Unable to perform operation. Check configuration or logs for more details.");
		}
		catch (final Exception ex)
		{
			throw new SapCentralOrderException("Unknown Exception");
		}
	}

	/**
	 * Building RestOperations include Fetching OAuth Credentials only as of 1905.11
	 *
	 * @param destinationModel
	 *           Consumed Destination Model which contains Authentication details
	 * @return {@link RestOperations} Object which contains OAuth token
	 */

	private RestOperations buildRestOperations(final ConsumedDestinationModel destinationModel)
	{
		return getIntegrationRestTemplateFactory().create(destinationModel);
  }


}
