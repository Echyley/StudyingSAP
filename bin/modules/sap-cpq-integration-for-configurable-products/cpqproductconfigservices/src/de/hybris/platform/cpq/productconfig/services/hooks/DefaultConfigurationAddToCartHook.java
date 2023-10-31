/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.hooks;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Default CPQ implementation of the {@link CommerceAddToCartMethodHook}
 */
public class DefaultConfigurationAddToCartHook implements CommerceAddToCartMethodHook
{
	private static final Logger LOG = Logger.getLogger(DefaultConfigurationAddToCartHook.class);
	private final ConfigurationService configurationService;
	private final ConfigurableChecker configurableChecker;
	private final ModelService modelService;

	/**
	 * Default constructor accepting dependencies
	 *
	 * @param configurationService
	 * @param configurableChecker
	 * @param modelService
	 */
	public DefaultConfigurationAddToCartHook(final ConfigurationService configurationService,
			final ConfigurableChecker configurableChecker, final ModelService modelService)
	{
		super();
		this.configurationService = configurationService;
		this.configurableChecker = configurableChecker;
		this.modelService = modelService;
	}

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		LOG.debug("CPQ-Configuration beforeAddToCart start");

		final ProductModel product = parameters.getProduct();

		if (configurableChecker.isCloudCPQConfigurableProduct(product))
		{
			LOG.debug("CPQ-Configuration - force create new cart entry");
			parameters.setCreateNewEntry(true);
		}
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		LOG.debug("CPQ-Configuration afterAddToCart start");
		final AbstractOrderEntryModel entryModel = result.getEntry();
		final ProductModel productModel = entryModel.getProduct();

		if (entryModel.getPk() == null)
		{
			LOG.warn("Entry could not be added due to issue: " + result.getStatusCode());
			return;
		}

		if (!configurableChecker.isCloudCPQConfigurableProduct(productModel))
		{
			return;
		}

		final List<AbstractOrderEntryProductInfoModel> productInfoModels = entryModel.getProductInfos();

		if (productInfoModels.size() != 1)
		{
			throw new IllegalStateException(
					"Exactly one product info expected at cart entry, but found [" + productInfoModels.size() + "]");
		}

		final CloudCPQOrderEntryProductInfoModel cpqProductInfo = (CloudCPQOrderEntryProductInfoModel) productInfoModels.get(0);
		if (StringUtils.isEmpty(cpqProductInfo.getConfigurationId()))
		{
			if (StringUtils.isEmpty(parameters.getCpqConfigId()))
			{
				LOG.debug("create default CPQ-Configuration and save to cartEntry");
				final String configId = configurationService.createConfiguration(productModel.getCode());
				configurationService.makeConfigurationPermanent(configId);
				cpqProductInfo.setConfigurationId(configId);
			}
			else
			{
				cpqProductInfo.setConfigurationId(parameters.getCpqConfigId());
			}
			cpqProductInfo.setProductInfoStatus(ProductInfoStatus.SUCCESS);
			modelService.save(cpqProductInfo);
		}
	}
}
