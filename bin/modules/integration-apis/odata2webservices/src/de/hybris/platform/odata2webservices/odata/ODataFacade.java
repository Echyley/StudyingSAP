/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;

/**
 * Receives an ODataContext and delegates its handling. Returns an ODataResponse.
 */
public interface ODataFacade
{
	/**
	 * Obtains ODataResponse with a stream that contains odata EDMX schema specified by the {@code oDataContext}.
	 *
	 * @param oDataContext contains information about what schema should be retrieved.
	 * @return requested ODataResponse with a stream that contains EDMX schema
	 */
	ODataResponse handleGetSchema(ODataContext oDataContext);

	/**
	 * Handles create, read, update or delete requests on an integration object item.
	 *
	 * @param oDataContext contains the information about the item
	 * @return response with information about the item
	 */
	ODataResponse handleRequest(ODataContext oDataContext);
}
