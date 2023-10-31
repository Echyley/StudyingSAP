/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the OverallStatusOrder object. <br>
 *
 */
public interface UserStatus extends Cloneable
{

	/**
	 * Returns the User Status Key.<br>
	 *
	 * @return User Status Key
	 */
	String getKey();

	/**
	 * Sets the User Status Key.<br>
	 *
	 * @param key
	 *           User Status Key
	 */
	void setKey(String key);

	/**
	 * Returns the User Status Description.<br>
	 *
	 * @return User Status Description
	 */
	String getDescription();

	/**
	 * Sets the User Status Description.<br>
	 *
	 * @param description
	 *           User Status Description
	 */
	void setDescription(String description);

	/**
	 * Returns true or false if the user status is respectively active or not .<br>
	 *
	 * @return Returns boolean value which is true, if the user status is active, and false otherwise.
	 */
	boolean isActive();

	/**
	 * Set user status active / inactive.<br>
	 *
	 * @param active
	 *           If true, the user status will be set to active
	 */
	void setActive(boolean active);

	/**
	 * Clones the Object. Because this class only contains immutable objects, there is no difference between a shallow and
	 * deep copy.
	 *
	 * @return deep-copy of this object
	 */
	@SuppressWarnings(
	{ "squid:S1161", "squid:S2975" })
	Object clone();

}