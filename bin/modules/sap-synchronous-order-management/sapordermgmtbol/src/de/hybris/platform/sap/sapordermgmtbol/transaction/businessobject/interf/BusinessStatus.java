/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the BusinessStatus object. <br>
 *
 */
public interface BusinessStatus extends Cloneable
{

	/**
	 * Returns status.<br>
	 *
	 * @return status
	 */
	EStatus getStatus();

	/**
	 * Returns true if the status is "Not Relevant".<br>
	 *
	 * @return true if the status is "Not Relevant"
	 */
	boolean isNotRelevant();

	/**
	 * Returns true if the status is "Not Processed". which is the same as Open.<br>
	 *
	 * @return true if the status is "Not Processed"
	 */
	boolean isNotProcessed();

	/**
	 * Returns true if the status is "Partially Processed".<br>
	 *
	 * @return true if the status is "Partially Processed"
	 */
	boolean isPartiallyProcessed();

	/**
	 * Returns true if the status is "Processed".<br>
	 *
	 * @return true if the status is "Processed"
	 */
	boolean isProcessed();

	/**
	 * Initializes the BusinessStatus object.<br>
	 *
	 * @param dlvStatus
	 *           Delivery Status
	 * @param rjStatus
	 *           Rejection Status
	 */
	void init(EStatus dlvStatus, EStatus rjStatus);

	/**
	 * Initializes the BusinessStatus object.<br>
	 *
	 * @param key
	 *           - status
	 */
	void init(EStatus key);

	/**
	 * Initializes the BusinessStatus object.<br>
	 */
	void init();

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.
	 *
	 *
	 * @return returns a deep copy
	 */
	@SuppressWarnings(
	{ "squid:S1161", "squid:S2975" })
	Object clone();

}