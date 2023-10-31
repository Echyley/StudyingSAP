/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.user;

/**
 * Provide the method to modify the multisite related attributes in the current session
 */
public interface MultiSiteUserSessionService
{
	/**
	 * Fetch the visible sites bases on {@link de.hybris.platform.commerceservices.model.user.SiteEmployeeGroupModel}
	 * for the current user, and set the site into current session
	 */
	void setBaseSitesAttributeInSession();

	/**
	 * Remote the multi site related attributes from current session
	 */
	void removeBaseSitesAttributeInSession();
}
