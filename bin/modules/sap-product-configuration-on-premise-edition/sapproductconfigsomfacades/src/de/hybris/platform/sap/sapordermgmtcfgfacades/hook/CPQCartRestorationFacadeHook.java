/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.hook;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.sapordermgmtb2bfacades.hook.CartRestorationFacadeHook;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Setting external configuration and base price after addCartEntriesToStandardCart
 */
public class CPQCartRestorationFacadeHook implements CartRestorationFacadeHook
{
	private ProductConfigurationSomService productConfigurationService;
	private ModelService modelService;

	@Override
	public void afterAddCartEntriesToStandardCart(final OrderEntryData entry, final AbstractOrderEntryModel entryModel)
	{
		if (getProductConfigurationService().isInSession(entry.getItemPK()))
		{
			entryModel.setExternalConfiguration(getProductConfigurationService().getExternalConfiguration(entry.getItemPK()));
			entryModel.setBasePrice(getProductConfigurationService().getTotalPrice(entry.getItemPK()));
			getModelService().save(entryModel);
		}
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationSomService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationSomService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
