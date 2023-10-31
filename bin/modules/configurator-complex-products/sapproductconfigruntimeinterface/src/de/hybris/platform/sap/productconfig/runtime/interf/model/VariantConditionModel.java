/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.math.BigDecimal;


/**
 * Represents the variant condition model including key and factor.
 */
public interface VariantConditionModel extends BaseModel
{
	/**
	 * @return the key
	 */
	String getKey();

	/**
	 * @param key
	 *           the key to set
	 */
	void setKey(String key);

	/**
	 * @return the factor
	 */
	BigDecimal getFactor();

	/**
	 * @param factor
	 *           the factor to set
	 */
	void setFactor(BigDecimal factor);

	/**
	 * Creates a copy of this VariantConditionModel object
	 *
	 * @return copy of variant condition
	 */
	VariantConditionModel copy();
}
