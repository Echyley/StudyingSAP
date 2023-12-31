/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;


/**
 * Populator for CPQ related product info data.
 * 
 * @param <T>
 *           Order entry product info model
 */
public class CPQConfigurationsPopulator<T extends AbstractOrderEntryProductInfoModel> implements
		Populator<T, List<ConfigurationInfoData>>
{
	@Override
	public void populate(final T source, final List<ConfigurationInfoData> target)
	{
		if (source.getConfiguratorType().equals(ConfiguratorType.CPQCONFIGURATOR))
		{
			if (!(source instanceof CPQOrderEntryProductInfoModel))
			{
				throw new ConversionException("Instance with type " + source.getConfiguratorType() + " is of class "
						+ source.getClass().getName() + " which is not convertible to " + CPQOrderEntryProductInfoModel.class.getName());
			}
			final ConfigurationInfoData item = new ConfigurationInfoData();
			final CPQOrderEntryProductInfoModel model = (CPQOrderEntryProductInfoModel) source;
			item.setConfiguratorType(model.getConfiguratorType());
			item.setStatus(model.getProductInfoStatus());
			item.setConfigurationLabel(model.getCpqCharacteristicName());
			item.setConfigurationValue(model.getCpqCharacteristicAssignedValues());
			target.add(item);
		}
	}

}
