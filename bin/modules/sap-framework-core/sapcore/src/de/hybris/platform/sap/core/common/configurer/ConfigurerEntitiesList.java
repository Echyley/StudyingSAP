/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.configurer;

import java.util.List;



/**
 * general Interface for holding the Entities of a Configurer.
 * 
 * @param <T>
 *           Type of the Configurer Class
 */
public interface ConfigurerEntitiesList<T>
{
	/**
	 * Add an Entity to the List.
	 * 
	 * @param entity
	 *           entity which will be added to the List
	 */
	public void addEntity(T entity);

	/**
	 * @return the list of entities
	 */
	public List<T> getEntities();

}
