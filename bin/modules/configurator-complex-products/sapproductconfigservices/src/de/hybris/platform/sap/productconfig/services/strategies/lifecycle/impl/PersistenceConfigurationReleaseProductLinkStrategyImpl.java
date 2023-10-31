/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationReleaseProductLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link ConfigurationReleaseProductLinkStrategy}
 */
public class PersistenceConfigurationReleaseProductLinkStrategyImpl extends SessionServiceAware
		implements ConfigurationReleaseProductLinkStrategy
{

	private static final Logger LOG = Logger.getLogger(PersistenceConfigurationReleaseProductLinkStrategyImpl.class);

	private ModelService modelService;

	@Override
	public void releaseCartEntryProductRelation(final AbstractOrderEntryModel cartEntry)
	{
		final ProductConfigurationModel productConfiguration = cartEntry.getProductConfiguration();
		Preconditions.checkNotNull(productConfiguration, "We expect a configuration attached to the abstract order entry");
		final Collection<ProductModel> product = productConfiguration.getProduct();
		if (!CollectionUtils.isEmpty(product))
		{
			final String productCode = product.iterator().next().getCode();
			getSessionAccessService().removeUiStatusForProduct(productCode);
			productConfiguration.setProduct(Collections.emptyList());
			getModelService().save(productConfiguration);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Unlinking product '%s' from config '%s'", productCode,
						productConfiguration.getConfigurationId()));
			}
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}










}
