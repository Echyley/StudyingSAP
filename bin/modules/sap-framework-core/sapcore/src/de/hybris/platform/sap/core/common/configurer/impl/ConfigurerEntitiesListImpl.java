/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.configurer.impl;

import de.hybris.platform.sap.core.common.configurer.ConfigurerEntitiesList;
import de.hybris.platform.sap.core.constants.SapcoreConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation for {@link de.hybris.platform.sap.core.common.configurer.ConfigurerEntitiesList}.
 * 
 * @param <T>
 */

public class ConfigurerEntitiesListImpl<T> implements ConfigurerEntitiesList<T>
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(ConfigurerEntitiesListImpl.class.getName());

	private final List<T> entities = new ArrayList<T>();

	@Override
	public void addEntity(final T entity)
	{
		entities.add(entity);
	}

	@Override
	public List<T> getEntities()
	{
		return entities;
	}

	@Override
	public String toString()
	{
		String toString = "";

		toString = super.toString() + SapcoreConstants.CRLF + "Parameters: " + SapcoreConstants.CRLF;

		for (final T entity : entities)
		{
			toString = toString + entity.toString();
		}

		return toString;
	}

}
