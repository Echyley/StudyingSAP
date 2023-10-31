/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;


/**
 * Provides extension possibility for related before view handler
 */
public interface ProductConfigBeforeViewExtender
{
	/**
	 * Callback that is called before any view rendered (also not CPQ-related views)<br>
	 * checking on the viewName to ensure that certain logic is only executed in context of CPQ might be required.
	 *
	 * @param request
	 *           Http-Request
	 * @param response
	 *           Http-Response
	 * @param model
	 *           view model
	 * @param viewName
	 *           view name
	 */
	void execute(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final String viewName);
}
