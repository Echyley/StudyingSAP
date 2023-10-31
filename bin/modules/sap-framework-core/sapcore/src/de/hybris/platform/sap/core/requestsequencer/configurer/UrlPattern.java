/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.requestsequencer.configurer;

import java.util.List;


/**
 * Interface for maintaining URL Patterns which are used by the RequestSequencerFilter.
 */
public interface UrlPattern
{

	/**
	 * @param urlRegEx
	 *           hold the url pattern as a regular expression
	 */
	public void setIncludeUrlRegExList(final List<String> urlRegEx);

	/**
	 * @return the URL Pattern
	 */
	public List<String> getIncludeUrlRegExList();


	/**
	 * @param urlRegEx
	 *           hold the url pattern as a regular expression
	 */
	public void setExcludeUrlRegExList(final List<String> urlRegEx);

	/**
	 * @return the URL Pattern
	 */
	public List<String> getExcludeUrlRegExList();

}
