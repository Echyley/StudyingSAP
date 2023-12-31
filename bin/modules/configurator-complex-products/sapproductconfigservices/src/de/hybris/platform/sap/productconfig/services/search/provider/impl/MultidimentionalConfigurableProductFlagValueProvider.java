/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.search.provider.impl;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.MultidimentionalProductFlagValueProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;

import org.springframework.beans.factory.annotation.Required;


/**
 * CPQ implementaion of the {@link MultidimentionalProductFlagValueProvider}.<br>
 * Enfores a multiemsional Product to be considered as configurable if it is CPQ-Configurable product.
 */
public class MultidimentionalConfigurableProductFlagValueProvider extends MultidimentionalProductFlagValueProvider
{
	private static final long serialVersionUID = 1L;
	private transient ConfiguratorSettingsService configuratorSettingsService;

	@Override
	public Object getFieldValue(final ProductModel product)
	{
		Boolean isMultidimentionalAndNotConfigurable = (Boolean) super.getFieldValue(product);

		if (isMultidimentionalAndNotConfigurable.booleanValue())
		{
			isMultidimentionalAndNotConfigurable = Boolean.valueOf(!getConfiguratorSettingsService()
					.getConfiguratorSettingsForProduct(product).stream()
					.anyMatch(configurator -> configurator.getConfiguratorType().equals(ConfiguratorType.CPQCONFIGURATOR)));
		}

		return isMultidimentionalAndNotConfigurable;
	}

	protected ConfiguratorSettingsService getConfiguratorSettingsService()
	{
		return configuratorSettingsService;
	}

	/**
	 * @param configuratorSettingsService
	 */
	@Required
	public void setConfiguratorSettingsService(final ConfiguratorSettingsService configuratorSettingsService)
	{
		this.configuratorSettingsService = configuratorSettingsService;
	}
}
