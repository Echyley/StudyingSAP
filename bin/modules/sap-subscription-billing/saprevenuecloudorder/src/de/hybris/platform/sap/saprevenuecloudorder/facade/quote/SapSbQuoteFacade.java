/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade.quote;

/**
 * Interface that provides an API for actions on Quote
 */
public interface SapSbQuoteFacade
{

	/**
	 * Makes a call to external system to fetch the document
	 * 
	 * @param quoteCode Quote Code
	 * @return document as a byte array
	 */
	public byte[] downloadQuoteProposalDocument(String quoteCode);

}
