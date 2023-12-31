/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.strategy;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Class implements special treatment for the configurable products
 *
 * Adds an item to the cart for the product with product configuration. Always put the basic product in the cart even if
 * the variant exists. The product configuration does not supports variants handling
 *
 * @deprecated since 22.05 release due to the newly implemented AddToCartValidator to especially support configurable
 *             products
 */
@Deprecated(since = "2205", forRemoval = true)
public class ProductConfigAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{

	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy;
	private static final Logger LOG = Logger.getLogger(ProductConfigAddToCartStrategy.class);

	@Override
	protected void validateAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		final CartModel cartModel = parameters.getCart();
		final ProductModel productModel = parameters.getProduct();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(productModel, "Product model cannot be null");

		// First Condition, copied from super implementaion - Hybris does not allow to buy a base product if variants exist (see super implementation)
		// Second Condition, exemption for CPQ - However CPQ allows to buy a ERP base product, even if ERP Varaiants exists.
		if (productModel.getVariantType() != null && !getCpqConfigurableChecker().isCPQConfigurableProduct(productModel))
		{
			throw new CommerceCartModificationException("Choose a variant instead of the base product");
		}

		if (parameters.getQuantity() < 1)
		{
			throw new CommerceCartModificationException("Quantity must not be less than one");
		}

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

	/**
	 * Set helper, to check if the related product is CPQ configurable
	 *
	 * @param cpqConfigurableChecker
	 *           configurator checker
	 */
	@Deprecated(since = "2205", forRemoval = true)
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return abstractOrderEntryLinkStrategy;
	}

	/**
	 * @param abstractOrderEntryLinkStrategy
	 */
	@Deprecated(since = "2205", forRemoval = true)
	@Required
	public void setAbstractOrderEntryLinkStrategy(final ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy)
	{
		this.abstractOrderEntryLinkStrategy = abstractOrderEntryLinkStrategy;
	}
}
