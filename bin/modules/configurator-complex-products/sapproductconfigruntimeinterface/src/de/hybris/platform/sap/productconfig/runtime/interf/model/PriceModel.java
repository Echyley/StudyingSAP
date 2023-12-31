/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ZeroPriceModelImpl;

import java.math.BigDecimal;


/**
 * Represents the price model including currency.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface PriceModel extends BaseModel
{

	/**
	 * Value-Object to model the case, when no price information is available
	 */
	PriceModel NO_PRICE = new ZeroPriceModelImpl();

	/**
	 * Value-Object to model the case, when no price information was not fetched, yet.
	 */
	PriceModel PRICE_NA = new ZeroPriceModelImpl();

	/**
	 * @param currency price currency
	 */
	void setCurrency(String currency);

	/**
	 * @return price currency
	 */
	String getCurrency();

	/**
	 * @return price value
	 */
	BigDecimal getPriceValue();

	/**
	 * @param priceValue price value
	 */
	void setPriceValue(BigDecimal priceValue);

	/**
	 * Checks whether this is a valid price
	 *
	 * @return <code>true</code> only if a NON-Zero price value and a currency are assigned
	 */
	boolean hasValidPrice();

	/**
	 * @return old price without discount
	 */
	BigDecimal getObsoletePriceValue();

	/**
	 * @param obsoletePriceValue old price without discount
	 */
	void setObsoletePriceValue(BigDecimal obsoletePriceValue);


}
