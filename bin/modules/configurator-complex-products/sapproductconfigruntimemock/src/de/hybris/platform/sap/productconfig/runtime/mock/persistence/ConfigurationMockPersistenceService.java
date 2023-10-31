/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.persistence;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Simple persistence for Mock Configuration Engine, suited for test and demo usage.
 */
public interface ConfigurationMockPersistenceService
{

	/**
	 * persists a config model
	 *
	 * @param model
	 *           model to persist
	 */
	void writeConfigModel(ConfigModel model);

	/**
	 * reads a config model from persistence
	 *
	 * @param configId
	 *           model id
	 * @return reads a config model by id
	 */
	ConfigModel readConfigModel(String configId);

	/**
	 * delete the config model identified by given id if existing
	 *
	 * @param configId
	 *           model id to delete
	 */
	void deleteConfigModel(String configId);

	/**
	 * reads the external config state from persistence
	 *
	 * @param configId
	 *           modelId
	 * @return ConfigModel in external State
	 */
	ConfigModel readExtConfigModel(final String configId);

	/**
	 * persist config model in external state
	 *
	 * @param extConfigModel
	 *           Model to persist
	 */
	void writeExtConfigModel(final ConfigModel extConfigModel);

}
