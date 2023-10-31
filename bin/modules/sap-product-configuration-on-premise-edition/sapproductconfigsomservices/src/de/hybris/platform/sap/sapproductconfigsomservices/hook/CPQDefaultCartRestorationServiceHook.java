/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.hook;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.hook.CartRestorationServiceHook;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Hook implementation for CartRestorationServiceHook for adding configuration after creating item.
 */
public class CPQDefaultCartRestorationServiceHook implements CartRestorationServiceHook
{
	private ProductConfigurationSomService productConfigurationService;
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Override
	public void afterCreateItemHook(final AbstractOrderEntryModel orderEntry, final Item item)
	{
		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(orderEntry.getProduct()))
		{
			final ConfigModel configModel = getProductConfigurationService().getConfigModel(orderEntry.getProduct().getCode(),
					orderEntry.getExternalConfiguration());
			if (configModel != null)
			{
				final CPQItem cpqItem = (CPQItem) item;
				cpqItem.setProductConfiguration(configModel);
				cpqItem.setConfigurable(true);
				getProductConfigurationService().setIntoSession(cpqItem.getHandle(), configModel.getId());
			}
		}
	}

	protected ProductConfigurationSomService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	@Required
	public void setProductConfigurationService(final ProductConfigurationSomService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return cpqConfigurableChecker;
	}

	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}
}
