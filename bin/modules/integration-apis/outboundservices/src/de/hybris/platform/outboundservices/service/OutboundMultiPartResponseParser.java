/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

/**
 * A service to parse the batch responses for the batch requests in outboundSync.
 */
public interface OutboundMultiPartResponseParser
{
	/**
	 * Parse the batch response.
	 *
	 * @param responseEntity The response for the batch request sent out in outboundSync, could contain multiple batch parts
	 *                       which consist of multiple responses.
	 * @return A list of responses which compose the batch response.
	 */
	List<ResponseEntity<Map>> parseMultiPartResponse(ResponseEntity<String> responseEntity);
}
