/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricing.services;

import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;

/**
 * Sap Partner Service
 */
public interface SapPartnerService {
	
	/**
	 * Method to fetch partner function 
	 * 
	 * @return SapPricingPartnerFunction
	 */
	public SapPricingPartnerFunction getPartnerFunction();

}
