/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.pricing.bol.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;

import org.apache.log4j.Logger;


public class ProductConfigurationSynchronousPricingAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationSynchronousPricingAddToCartStrategy.class);
	private final CPQConfigurableChecker cpqConfigurableChecker;
	private final ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy;

	public ProductConfigurationSynchronousPricingAddToCartStrategy(final CPQConfigurableChecker cpqConfigurableChecker,
			final ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy)
	{
		super();
		this.cpqConfigurableChecker = cpqConfigurableChecker;
		this.abstractOrderEntryLinkStrategy = abstractOrderEntryLinkStrategy;
	}

	@Override
	protected CommerceCartModification doAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final CommerceCartModification commerceCartModification = super.doAddToCart(parameter);

		final PK primaryKey = commerceCartModification.getEntry().getPk();

		if (primaryKey == null)
		{
			LOG.warn("Entry could not be added due to issue: " + commerceCartModification.getStatusCode());
			return commerceCartModification;
		}

		final ProductModel product = commerceCartModification.getEntry().getProduct();

		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(product))
		{
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(commerceCartModification.getEntry().getPk().toString(),
					parameter.getConfigId());
		}
		return commerceCartModification;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}



	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return abstractOrderEntryLinkStrategy;
	}


}
