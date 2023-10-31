/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;

import rx.Observable;


/**
 * 
 * SapCpqCpiOutboundQuoteService is an interface having the functionality of sending the quote,
 * quote status and checking its status of sent quote
 */
public interface SapCpqCpiOutboundQuoteService
{

	String OK = "OK";
	String RESPONSE_STATUS = "responseStatus";
	String RESPONSE_MESSAGE = "responseMessage";
	
	/**
	 * Method to send a quote
	 * @param sapCPQOutboundQuoteModel		the Outbound Quote Model
	 * @return 								a matching quote
	 */
	Observable<ResponseEntity<Map>> sendQuote(SAPCPQOutboundQuoteModel sapCPQOutboundQuoteModel);
	/**
	 * Method to send quote status
	 * @param sapCPQOutboundQuoteStatusModel 	the Outbound Quote Status Model
	 * @return 									status of the quote
	 */
	Observable<ResponseEntity<Map>> sendQuoteStatus(SAPCPQOutboundQuoteStatusModel sapCPQOutboundQuoteStatusModel);
	
	/**
	 * Method to check if the quote is sent successfully
	 * @param responseEntityMap		the Response Entity Map
	 * @return 						the boolean value for the quote status
	 */
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

	/**
	 * Method to get the property value(i.e external quote id)
	 * @param responseEntityMap 	the Response Entity Map
	 * @param property  			the property type
	 * @return						the property value as string
	 */
	static String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{
		
		final Object next = Optional.ofNullable(responseEntityMap.getBody()).map(v -> v.keySet().iterator().next()).orElse(null);
		checkArgument(next != null, String.format("SCPI response entity key set cannot be null for property [%s]!", property));

		final String responseKey = next.toString();
		checkArgument(!responseKey.isEmpty(),
				String.format("SCPI response property cannot be empty for property [%s]!", property));
		
		final Object propertyValue = Optional.ofNullable(responseEntityMap.getBody()).map(v -> v.get(responseKey)).map(v -> ((HashMap<?,?>) v).get(property)).orElse(null);
		checkArgument(propertyValue != null, String.format("SCPI response property [%s] value cannot be null!", property));

		return propertyValue.toString();

	}

}
