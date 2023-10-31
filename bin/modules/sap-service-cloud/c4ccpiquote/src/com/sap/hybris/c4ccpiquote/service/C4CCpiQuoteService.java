/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.service;


/**
 * Interface to handle basic calls to commerce
 */
public interface C4CCpiQuoteService
{
	/**
	 * method to get site and store information from commerce database
	 *
	 * @param salesOrganization parameter as a String
	 * @param distributionChannel parameter as a String
	 * @param division parameter as a String
	 * @return Base site id as a String
	 */
	public String getSiteAndStoreFromSalesArea(String salesOrganization, String distributionChannel, String division);
}
