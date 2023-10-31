/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.clients;

/**
 * CPQ Api Client service
 */
public interface SapCpqQuoteApiClientService {
	
	
    /**
     * 
     * @param quoteCode
     * @return
     */
	byte[] fetchProposalDocument(final String quoteCode);
	
}
