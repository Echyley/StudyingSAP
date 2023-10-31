/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems;

import java.util.List;


/**
 * Service to provide list of fields that should be returned by CMS Item API.
 */
public interface CMSItemAttributeFilterEnablerService
{
	/**
	 * Returns the list of attributes by mode.
	 * @param typeCode the type for which the list of attributes should be returned
	 * @param mode the mode that represents the list of fields.
	 * @return the list of fields.
	 */
	List<String> getAttributes(String typeCode, String mode);

	/**
	 * Returns the list of attributes. The mode is retrieved from the session if it was set by {@link CMSItemAttributeFilterEnablerService#setMode(String)}.
	 * @param typeCode the type for which the list of attributes should be returned
	 * @return the list of fields.
	 */
	List<String> getAttributes(String typeCode);

	/**
	 * Clear the cache that stores the configuration.
	 * @exception InterruptedException if thread is interrupted
	 */
	void clearCache() throws InterruptedException;

	/**
	 * Method saves the mode that should be used to retrieve the list of attributes to the session.
	 * @param mode the mode that should be used to retrieve the list of attributes.
	 */
	void setMode(String mode);

	/**
	 * Method retrieves the attribute mode from the session.
	 * @return the mode, null if mode has not been set.
	 */
	String getMode();

	/**
	 * Verifies whether the attribute is allowed or not.
	 * @param typeCode the type code where the attribute exists
	 * @param attribute the attribute qualifier.
	 * @return true if the attribute is allowed, false otherwise
	 */
	boolean isAttributeAllowed(String typeCode, String attribute);
}
