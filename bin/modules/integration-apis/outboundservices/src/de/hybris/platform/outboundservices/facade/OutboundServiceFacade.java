/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;

/**
 * Facade which orchestrates the {@link IntegrationObjectConversionService} and {@link IntegrationRestTemplateFactory} to integrate
 * with the RESTful endpoint.
 */
public interface OutboundServiceFacade
{
	/**
	 * Method orchestrates the services to build the payload and to integrate with restful endpoint.
	 *
	 * @param itemModel             the model to be converted
	 * @param integrationObjectCode the name of the integration object to convert this model as payload
	 * @param destination           endpoint destination information
	 * @return rx.Observable which allows the caller to subscribe the callback method
	 * @deprecated use {@link #send(SyncParameters)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	Observable<ResponseEntity<Map>> send(ItemModel itemModel, String integrationObjectCode, String destination);

	/**
	 * Method orchestrates the services to build the payload and to integrate with restful endpoint.
	 *
	 * @param params a parameter object holding data about the item synchronization request
	 * @return rx.Observable which allows the caller to subscribe the callback method that provides result of the item
	 * sending.
	 */
	default Observable<ResponseEntity<Map>> send(final SyncParameters params)
	{
		return Observable.empty();
	}

	/**
	 * Method orchestrates the services to build a batch payload and to integrate with restful endpoint.
	 *
	 * @param params a parameter object holding a list of parameters with data about the parts for the batch synchronization
	 *               request
	 * @return ResponseEntity that provides result of the request.
	 */
	default ResponseEntity<String> sendBatch(final List<SyncParameters> params)
	{
		return null;
	}
}
