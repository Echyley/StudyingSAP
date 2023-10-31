/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Represents the Schedline (schedule line) object. <br>
 *
 */
public interface Schedline extends BusinessObject, Cloneable
{

	/**
	 * Retrieves the committed date of the schedule line.<br>
	 *
	 * @return Committed Date
	 */
	Date getCommittedDate();

	/**
	 * Sets the committed date or the schedule line.<br>
	 *
	 * @param committedDate
	 *           date to set
	 */
	void setCommittedDate(Date committedDate);

	/**
	 * Retrieves the committed quantity of the schedule line.<br>
	 *
	 * @return Committed Quantity
	 */
	BigDecimal getCommittedQuantity();

	/**
	 * Sets the committed quantity of the schedule line.<br>
	 *
	 * @param committedQuantity
	 *           quantity to set
	 */
	void setCommittedQuantity(BigDecimal committedQuantity);

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.<br>
	 *
	 *
	 * @return returns a deep copy
	 */
	@SuppressWarnings(
	{ "squid:S1161", "squid:S2975" })
	Object clone();

	/**
	 * Sets schedule lines' unit
	 *
	 * @param unit
	 */
	void setUnit(String unit);

	/**
	 * Retrieving unit ID in language independent format
	 *
	 * @return Schedule lines' unit
	 */
	String getUnit();

}