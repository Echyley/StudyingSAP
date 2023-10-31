/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateResponse;

/**
 * Client for the Promotion Pricing Service
 *
 */
public interface PPSClient
{
	/**
	 * Call promotion pricing service
	 *
	 * @param priceCalculate
	 *           {@link com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate}
	 * @param sapConfig
	 *           SAP base store configuration
	 * @return {@link com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateResponse}
	 * 
	 */
	PriceCalculateResponse callPPS(PriceCalculate priceCalculate, SAPConfigurationModel sapConfig);

}
