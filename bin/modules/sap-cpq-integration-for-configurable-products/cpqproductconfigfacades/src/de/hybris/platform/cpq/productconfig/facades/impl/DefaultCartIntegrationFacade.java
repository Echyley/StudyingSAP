/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.facades.CartIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link CartIntegrationFacade}
 */
public class DefaultCartIntegrationFacade implements CartIntegrationFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCartIntegrationFacade.class);
	private final CartService cartService;
	private final ModelService modelService;
	private final CommerceCartService commerceCartService;
	private final ProductService productService;
	private final ConfigurationService configurationService;
	private final AbstractOrderIntegrationService abstractOrderIntegrationService;
	private final Converter<CommerceCartModification, CartModificationData> cartModificationConverter;


	


	/**
	 * Default Constructor. Expects all required dependencies as constructor args
	 *
	 * @param cartService
	 *           cart service
	 * @param modelService
	 *           model service
	 * @param commerceCartService
	 *           commerce cart service
	 * @param productService
	 *           product service
	 * @param configurationService
	 *           configuration service
	 * @param abstractOrderIntegrationService
	 *           Used for retrieving the configuration ID per configurable entry
	 * @param cartModificationConverter
	 *           converts commerce- into cartModificationdata
	 */
	public DefaultCartIntegrationFacade(final CartService cartService, final ModelService modelService,
			final CommerceCartService commerceCartService, final ProductService productService,
			final ConfigurationService configurationService, final AbstractOrderIntegrationService abstractOrderIntegrationService,
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartService = cartService;
		this.modelService = modelService;
		this.commerceCartService = commerceCartService;
		this.productService = productService;
		this.configurationService = configurationService;
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
		this.cartModificationConverter = cartModificationConverter;
	}


	@Override
	public CartModificationData addConfigurationToCart(final String configId, final long quantity)
			throws CommerceCartModificationException
	{
		final long startTime = logFacadeCallStart("ADD configuration TO CART [CONFIG_ID='%s']", configId);
		getConfigurationService().makeConfigurationPermanent(configId);
		final String rootProductCode = getRootProduct(configId);
		final ProductModel product = getProductService().getProductForCode(rootProductCode);
		final CartModel cart = getCartService().getSessionCart();
		final CommerceCartModification modificationModel = createCartItem(product, cart, configId, quantity);

		if (LOG.isDebugEnabled())
		{
			final AbstractOrderEntryModel entry = modificationModel.getEntry();
			final String pk = entry != null && entry.getPk() != null ? entry.getPk().toString() : null;
			final Long quantityAdded = entry != null ? entry.getQuantity() : 0;
			LOG.debug(String.format("Added product '%s' to cart with quantity '%d', referenced by cart entry PK '%s'",
					product.getCode(), quantityAdded, pk));
		}
		final CartModificationData cartModificationData = getCartModificationConverter().convert(modificationModel);
		logFacadeCallDone("ADD configuration TO CART", startTime);
		return cartModificationData;
	}

	protected String getRootProduct(final String configId)
	{
		final ConfigurationSummaryData configurationSummary = getConfigurationService().getConfigurationSummary(configId);
		Preconditions.checkState(configurationSummary != null, "We expect to have a configuration summary");
		final String productSystemId = configurationSummary.getConfiguration().getProductSystemId();
		Preconditions.checkState(productSystemId != null, "We expect the summary to carry a root product code");
		return productSystemId;
	}


	protected CommerceCartModification createCartItem(final ProductModel product, final CartModel cart, final String configId,
			final long quantity) throws CommerceCartModificationException
	{
		final CommerceCartParameter commerceCartParameter = prepareCommerceCartParameterForAddToCart(cart, product, quantity, true,
				configId);
		return getCommerceCartService().addToCart(commerceCartParameter);
	}

	protected CommerceCartParameter prepareCommerceCartParameterForAddToCart(final CartModel cart, final ProductModel product,
			final long quantity, final boolean forceNewEntry, final String configId)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cart);
		parameter.setProduct(product);
		parameter.setQuantity(quantity);
		parameter.setUnit(product.getUnit());
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setCpqConfigId(configId);
		return parameter;
	}

	protected long logFacadeCallStart(final String format, final Object... args)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			LOG.debug(String.format(format, args));
		}
		return startTime;
	}

	protected void logFacadeCallDone(final String operation, final long startTime)
	{
		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug(String.format("%s in FACADE took  %s ms", operation, duration));
		}
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Override
	public ProductConfigData getConfigIdForSessionCartEntry(final Integer entryNumber)
	{
		final AbstractOrderEntryModel entry = cartService.getEntryForNumber(cartService.getSessionCart(), entryNumber);
		final String configId = getAbstractOrderIntegrationService().getConfigIdForAbstractOrderEntry(entry);
		final String clonedConfigId = getConfigurationService().cloneConfiguration(configId, false);
		final ProductConfigData data = new ProductConfigData();
		data.setConfigId(clonedConfigId);
		return data;
	}

	@Override
	public String getProductCodeForSessionCartEntry(final Integer entryNumber)
	{
		final AbstractOrderEntryModel entry = getCartService().getEntryForNumber(getCartService().getSessionCart(), entryNumber);
		return entry.getProduct().getCode();
	}

	@Override
	public CartModificationData updateCartEntryFromConfiguration(final String configId, final int cartEntryNumber)
	{
		getConfigurationService().makeConfigurationPermanent(configId);
		final CartModel sessionCart = getCartService().getSessionCart();
		final AbstractOrderEntryModel entry = getCartService().getEntryForNumber(sessionCart, cartEntryNumber);
		final CloudCPQOrderEntryProductInfoModel productInfo = (CloudCPQOrderEntryProductInfoModel) entry.getProductInfos().get(0);
		final String oldConfigId = productInfo.getConfigurationId();
		productInfo.setConfigurationId(configId);
		getModelService().save(productInfo);
		invalidateConfigurationSummaryCache(oldConfigId);
		if (!configId.equals(oldConfigId))
		{
			removeObsoleteConfiguration(oldConfigId);
		}
		recalculateCart(cartEntryNumber);
		final CommerceCartModification updateModification = fillCommerceCartModification(entry);
		return getCartModificationConverter().convert(updateModification);
	}

	protected CommerceCartModification fillCommerceCartModification(final AbstractOrderEntryModel entryToUpdate)
	{
		final CommerceCartModification modification = new CommerceCartModification();

		modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		modification.setQuantity(entryToUpdate.getQuantity().longValue());
		modification.setEntry(entryToUpdate);
		return modification;

	}


	protected void removeObsoleteConfiguration(final String obsoleteConfigId)
	{
		if (null != obsoleteConfigId)
		{
			getConfigurationService().deleteConfiguration(obsoleteConfigId);
		}
	}

	protected void invalidateConfigurationSummaryCache(final String configId)
	{
		getConfigurationService().removeCachedConfigurationSummary(configId);
	}


	protected void recalculateCart(final int cartEntryNumber)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		final CartModel sessionCart = getCartService().getSessionCart();
		sessionCart.setCalculated(false);
		parameter.setCart(sessionCart);
		parameter.setRecalculate(true);
		final AbstractOrderEntryModel entry = getCartService().getEntryForNumber(sessionCart, cartEntryNumber);
		entry.setCalculated(Boolean.FALSE);
		getCommerceCartService().calculateCart(parameter);
		getModelService().save(sessionCart);
	}


	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return this.abstractOrderIntegrationService;
	}

	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}


}
