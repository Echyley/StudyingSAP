/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.pci;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;

import com.hybris.charon.exp.HttpException;


/**
 * Exception Handler for PCI.
 */
public interface PCIRequestErrorHandler
{

	/**
	 * Handles any Sever-Side HTTP-Exception when creating an anlytics document.
	 *
	 * @param ex
	 *           HttpException
	 * @param analyticsDocumentInput
	 * @return analytics document
	 */
	AnalyticsDocument processCreateAnalyticsDocumentHttpError(HttpException ex, AnalyticsDocument analyticsDocumentInput);

	/**
	 * Processes runtime exceptions like timeout or non-existing server. Default behavior: Timeout exceptions are handled
	 * gracefully (just logged) while other runtime exceptions are re-thrown.
	 * 
	 * @param ex
	 *           Runtime exception that wraps the actual cause
	 * @param analyticsDocumentInput
	 * @return analytics
	 */
	AnalyticsDocument processCreateAnalyticsDocumentRuntimeException(RuntimeException ex,
			AnalyticsDocument analyticsDocumentInput);
}
