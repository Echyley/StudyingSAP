/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;

import java.util.List;



/**
 * External representation of a configuration
 */
public interface Configuration
{
	/**
	 * @return Root instance of the configuration
	 */
	Instance getRootInstance();

	/**
	 * Sets root instance of the configuration
	 * 
	 * @param rootInstance
	 */
	void setRootInstance(Instance rootInstance);

	/**
	 * @return List of instances belonging to this configuration
	 */
	List<Instance> getInstances();

	/**
	 * @return List of partOf relations between instances
	 */
	List<PartOfRelation> getPartOfRelations();

	/**
	 * @return List of assigned characteristic values
	 */
	List<CharacteristicValue> getCharacteristicValues();

	/**
	 * @return List of context attributes
	 */
	List<ContextAttribute> getContextAttributes();

	/**
	 * Adds an instance to the instance list
	 * 
	 * @param instance
	 *           Instance to be added
	 */
	void addInstance(Instance instance);

	/**
	 * Adds a partOf relation
	 * 
	 * @param partOfRelation
	 */
	void addPartOfRelation(PartOfRelation partOfRelation);

	/**
	 * Adds a new characteristic value to the configuration
	 * 
	 * @param characteristicValue
	 */
	void addCharacteristicValue(CharacteristicValue characteristicValue);

	/**
	 * Adds a new context attribute to the configuration
	 * 
	 * @param contextAttribute
	 */
	void addContextAttribute(ContextAttribute contextAttribute);

	/**
	 * @return knowledge base key data
	 */
	KBKey getKbKey();

	/**
	 * Sets the knowledge base key data
	 * 
	 * @param kbKey
	 */
	void setKbKey(KBKey kbKey);

}
