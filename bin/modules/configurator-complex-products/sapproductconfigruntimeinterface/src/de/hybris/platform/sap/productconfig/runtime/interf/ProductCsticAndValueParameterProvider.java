/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;

import java.util.Map;


/**
 * Provides characteristic language independent and language dependent names as well as characteristic values from
 * knowledgebase
 */
public interface ProductCsticAndValueParameterProvider
{

	/**
	 * Retrieves characteristic language independent and language dependent names as well as characteristic values from
	 * knowledgebase.
	 *
	 * @param productCode
	 *           product code for which the data is retrieved
	 * @return Map with characteristic language independent name as a key and CsticParameterWithValues as a value
	 */
	Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode);
}
