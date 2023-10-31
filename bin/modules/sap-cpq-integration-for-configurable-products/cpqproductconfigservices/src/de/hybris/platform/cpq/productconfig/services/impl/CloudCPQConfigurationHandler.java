/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQConfiguratorSettingsModel;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Assigns proper configurator type (in the sense of generic configuration) for settings belonging to the CPQ
 * configurator.
 */
public class CloudCPQConfigurationHandler implements ProductConfigurationHandler
{
	@Override
	public List<AbstractOrderEntryProductInfoModel> createProductInfo(final AbstractConfiguratorSettingModel productSettings)
	{
		if (productSettings instanceof CloudCPQConfiguratorSettingsModel)
		{
			final CloudCPQOrderEntryProductInfoModel infomodel = new CloudCPQOrderEntryProductInfoModel();
			infomodel.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
			return Collections.singletonList(infomodel);
		}
		else
		{
			throw new IllegalArgumentException("Argument must be a type of CloudCPQConfiguratorSettingsModel, but actually is "
					+ (null == productSettings ? null : productSettings.getClass()));
		}
	}

	@Override
	public List<AbstractOrderEntryProductInfoModel> convert(final Collection<ProductConfigurationItem> items,
			final AbstractOrderEntryModel entry)
	{
		return Collections.emptyList();
	}
}
