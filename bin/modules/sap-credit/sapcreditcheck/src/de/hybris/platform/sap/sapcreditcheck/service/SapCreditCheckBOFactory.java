/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcreditcheck.service;

import de.hybris.platform.sap.sapcreditcheck.businessobject.SapCreditCheckBO; 

/**
 * 
 */
public interface SapCreditCheckBOFactory
{

	/**
	 * @return SapCreditCheckBO
	 */
	public SapCreditCheckBO getSapCreditCheckBO();
}
