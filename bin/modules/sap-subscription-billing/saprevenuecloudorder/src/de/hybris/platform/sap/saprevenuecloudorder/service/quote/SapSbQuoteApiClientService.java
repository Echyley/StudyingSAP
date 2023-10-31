/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.quote;



/**
 * Generated {@link SapSbQuoteApiClientService}
 */
public interface SapSbQuoteApiClientService {

	/**
	 * Fetch Quote proposal document
	 * @param quoteCode logo code
	 * @return Quote proposal document Url
	 */
	byte[] fetchProposalDocument(final String quoteCode);
	
}
