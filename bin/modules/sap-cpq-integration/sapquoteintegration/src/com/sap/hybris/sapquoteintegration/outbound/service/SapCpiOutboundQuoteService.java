/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.outbound.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;

import rx.Observable;


/**
 *
 */
public interface SapCpiOutboundQuoteService
{

	String OK = "OK";
	String RESPONSE_STATUS = "responseStatus";
	String RESPONSE_MESSAGE = "responseMessage";

	Observable<ResponseEntity<Map>> sendQuote(SAPCpiOutboundQuoteModel sapCpiOutboundQuoteModel);
	
	Observable<ResponseEntity<Map>> sendQuoteStatus(SAPCpiOutboundQuoteStatusModel sapCpiOutboundQuoteStatusModel);

	static boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		boolean isSentSuccessfully = false;
		if (OK.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS))
				&& responseEntityMap.getStatusCode().is2xxSuccessful())
		{
			isSentSuccessfully = true;
		}
		return isSentSuccessfully;
	}

	static String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{

		final Object next = Optional.ofNullable(responseEntityMap.getBody()).map(v -> v.keySet().iterator().next()).orElse(null);
		checkArgument(next != null, String.format("SCPI response entity key set cannot be null for property [%s]!", property));

		final String responseKey = next.toString();

		final Object propertyValue = Optional.ofNullable(responseEntityMap.getBody()).map(v -> v.get(responseKey)).map(v -> ((HashMap<?,?>) v).get(property)).orElse(null);
		checkArgument(propertyValue != null, String.format("SCPI response property [%s] value cannot be null!", property));

		return propertyValue.toString();

	}

}
