/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 * Interface for Configuration Mocks. There should be one implementation per KB.
 */
public interface ConfigMock
{
	/**
	 * @return the default configuration
	 */
	ConfigModel createDefaultConfiguration();

	/**
	 * simulates an update call for the given model, applying the mock dynamics
	 * @param model ConfigModel
	 */
	void checkModel(ConfigModel model);

	/**
	 * Allows to add attributes from product to the configurator mock model
	 *
	 * @param model
	 * @param productModel
	 */
	default void addProductAttributes(final ConfigModel model, final ProductModel productModel)
	{
		//nothing happens in default implementation
	}

	/**
	 * @deprecated method will be removed from interface, as it is internal.
	 */
	@Deprecated(since="2211")
	void checkInstance(ConfigModel model, InstanceModel instance);

	/**
	 * @deprecated will be removed from interface, as it is internal.
	 */
	@Deprecated(since="2211")
	void checkCstic(ConfigModel model, InstanceModel instance, CsticModel cstic);

	/**
	 * @deprecated ConfigsMocks have to be immutable/stateless since 2211. extract config id from the runtime configuration {@link ConfigModel} instead
	 * @param nextConfigId
	 */
	@Deprecated(since="2211")
	default void setConfigId(final String nextConfigId) {
		throw new UnsupportedOperationException("ConfigsMocks have to be immutable/stateless since 2211. extract config id from the runtime configuration instead");
	}

	/**
	 * Tells if a mock represents a changeable variant
	 *
	 * @return true if and only if mock represents a variant which is changeable, i.e. does not fall back to the base
	 *         product in case changes are done to its attributes
	 * @deprecated since 22.05: use {@link #isChangeableVariant()} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	default boolean isChangeabeleVariant()
	{
		return isChangeableVariant();
	}

	/**
	 * Tells if a mock represents a changeable variant
	 *
	 * @return true if and only if mock represents a variant which is changeable, i.e. does not fall back to the base
	 *         product in case changes are done to its attributes
	 *
	 */
	default boolean isChangeableVariant()
	{
		return false;
	}

	/**
	 * @deprecated will be removed from interface, as it is internal.
	 */
	@Deprecated(since="2211")
	void setI18NService(CommonI18NService i18nService);

	/**
	 * @deprecated ConfigsMocks have to be immutable/stateless since 2211.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	default void showDeltaPrices(final boolean showDeltaPrices) {
		throw new UnsupportedOperationException("ConfigsMocks have to be immutable/stateless since 2211. extract config id from the runtime configuration instead");
	}

	/**
	 * Enriches configuration model, applying the mock dynamics
	 * 
	 * @param model
	 *                 ConfigModel
	 */
	default void enrichConfigurationModel(final ConfigModel model)
	{
		//nothing happens in default implementation
	}

}
