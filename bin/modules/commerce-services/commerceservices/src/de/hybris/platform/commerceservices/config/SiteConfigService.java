/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.config;

/**
 * Site config service is used to lookup a site specific configuration property.
 */
public interface SiteConfigService
{
	/**
	 * Get property for current base site
	 *
	 * @param siteUid  the site UID
	 * @param property the property to get
	 * @return the property value
	 */
	String getProperty(String siteUid, String property);
}
