/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;

import java.util.Map;


/**
 * Service for retrieving characteristics and their possible values for rules in backoffice
 */
public interface ProductCsticAndValueParameterProviderService
{
	/**
	 * Retrieves characteristics and their possible values for a given product to be displayed in backoffice ui
	 * 
	 * @param productCode
	 *           product code
	 * @return map of cstics with their possible values
	 */
	Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode);
}
