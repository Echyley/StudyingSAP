/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateservices.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguratorSettingModel;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguredProductInfoModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class TextFieldConfigurationHandler implements ProductConfigurationHandler
{
	@Override
	public List<AbstractOrderEntryProductInfoModel> createProductInfo(final AbstractConfiguratorSettingModel productSettings)
	{
		if (productSettings instanceof TextFieldConfiguratorSettingModel)
		{
			final TextFieldConfiguratorSettingModel textSetting = (TextFieldConfiguratorSettingModel) productSettings;

			final TextFieldConfiguredProductInfoModel result = new TextFieldConfiguredProductInfoModel();
			result.setConfiguratorType(ConfiguratorType.TEXTFIELD);
			result.setConfigurationLabel(textSetting.getTextFieldLabel());
			result.setConfigurationValue(textSetting.getTextFieldDefaultValue());
			setProductInfoStatus(result);
			return Collections.singletonList(result);
		}
		else
		{
			throw new IllegalArgumentException("Argument must be a type of TextFieldConfiguratorSettingsModel");
		}
	}

	@Override
	public List<AbstractOrderEntryProductInfoModel> convert(final Collection<ProductConfigurationItem> items,
			final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("items", items);
		return items.stream().map(this::checkForNull).map(item -> {
			final TextFieldConfiguredProductInfoModel result = new TextFieldConfiguredProductInfoModel();
			result.setConfigurationLabel(item.getKey());
			if (item.getValue() != null)
			{
				result.setConfigurationValue(item.getValue().toString());
			}
			result.setConfiguratorType(ConfiguratorType.TEXTFIELD);
			return result;
		}).map(this::setProductInfoStatus).collect(Collectors.toList());
	}

	protected TextFieldConfiguredProductInfoModel setProductInfoStatus(final TextFieldConfiguredProductInfoModel item)
	{
		final String value = item.getConfigurationValue();
		if (value == null || value.isEmpty())
		{
			item.setProductInfoStatus(ProductInfoStatus.ERROR);
		}
		else
		{
			item.setProductInfoStatus(ProductInfoStatus.SUCCESS);
		}
		return item;
	}

	protected ProductConfigurationItem checkForNull(final ProductConfigurationItem item)
	{
		validateParameterNotNull(item, "Items of the input collection must not be null");
		return item;
	}
}
