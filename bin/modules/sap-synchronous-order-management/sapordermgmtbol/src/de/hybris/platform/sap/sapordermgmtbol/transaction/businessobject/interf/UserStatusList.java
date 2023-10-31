/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import java.util.List;


/**
 * Represents the UserStatusList object. <br>
 *
 */
public interface UserStatusList extends Cloneable
{

	/**
	 * Adds an additional User Status to the User Status List.<br>
	 *
	 * @param userStatus
	 *           User Status to be added to the User Stattus List
	 * @return true, if status was added to the list
	 */
	boolean addUserStatus(UserStatus userStatus);

	/**
	 * Returns the List of User Statuses.<br>
	 *
	 * @return List of User Statuses
	 */
	List<UserStatus> getUserStatusList();

	/**
	 * Returns the List of Active User Statuses.<br>
	 *
	 * @return List of Active User Statuses
	 */
	UserStatusList getActiveUserStatusList();

	/**
	 * Returns the string representation of the User Status List.<br>
	 *
	 * @return String representation of the User Status List
	 */
	@Override
	String toString();

	/**
	 * Clones the Object. Because this class only contains immutable objects, there is no difference between a shallow and
	 * deep copy.
	 *
	 *
	 * @return deep-copy of this object
	 */
	@SuppressWarnings(
	{ "squid:S1161", "squid:S2975" })
	Object clone();

}