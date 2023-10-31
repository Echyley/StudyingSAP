/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.dao;

import de.hybris.platform.core.model.ItemModel;

import java.util.List;


/**
 * Interface for generic access to the requested item.
 */
public interface GenericConfigurationDao
{

	/**
	 * Retrieves all models for the given type code.
	 * 
	 * @param code
	 *           item type code (String)
	 * @return list of found models
	 */
	public List<ItemModel> getAllModelsForCode(final String code);

}
