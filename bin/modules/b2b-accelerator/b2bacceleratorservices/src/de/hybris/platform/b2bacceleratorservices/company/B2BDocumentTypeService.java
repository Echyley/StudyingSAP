/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.company;

import de.hybris.platform.servicelayer.search.SearchResult;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;


/**
 * Provides services for the B2BDocument type
 */
public interface B2BDocumentTypeService
{
	/**
	 * Gets all document types.
	 * 
	 * @return a SearchResult<B2BDocumentTypeModel> containing document types.
	 */
	SearchResult<B2BDocumentTypeModel> getAllDocumentTypes();
}
