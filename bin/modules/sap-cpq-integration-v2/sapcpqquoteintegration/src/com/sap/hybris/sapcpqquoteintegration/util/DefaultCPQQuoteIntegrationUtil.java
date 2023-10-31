/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.util;

import static com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants.CPQ_OUTBOUND_QUOTE_DESTINATION;

import org.apache.log4j.Logger;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * DefaultCPQQuoteIntegrationUtils Utility class
 * @includes methods 
 * cpqOutboundConsumedDestinationAvailability - checks for cpq outbound consumed destination 
 */
public class DefaultCPQQuoteIntegrationUtil {
	
	private static final Logger LOG = Logger.getLogger(DefaultCPQQuoteIntegrationUtil.class);
	

	/**
	 * Constructor to suppress creation of objects of utility class
	 */
	protected DefaultCPQQuoteIntegrationUtil() 
	{
		
	}

	/**
	 * Method checks for cpq outbound consumed destination 
	 * @param FlexibleSearchService
	 * @return boolean
	 */
	public static boolean cpqOutboundConsumedDestinationAvailability(FlexibleSearchService flexibleSearchService) {
 		try {
 			final ConsumedDestinationModel example = new ConsumedDestinationModel();
 			example.setId(CPQ_OUTBOUND_QUOTE_DESTINATION);
 			flexibleSearchService.getModelByExample(example);
 			return true;
 		} catch (final SystemException exception) {
 			LOG.error("Failed to find ConsumedDestination with id " + CPQ_OUTBOUND_QUOTE_DESTINATION);
 			return false;
 		}
 	}
}

