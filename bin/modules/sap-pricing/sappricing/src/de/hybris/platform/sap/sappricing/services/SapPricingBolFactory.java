/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricing.services;


import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricing;


public interface SapPricingBolFactory
{
	/**
	 * @return Search BO implementation
	 */
	SapPricing getSapPricing();
}
