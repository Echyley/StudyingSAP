/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


public class PersistenceConfigurationAssignmentResolverStrategyImpl implements ConfigurationAssignmentResolverStrategy
{
	private CartService cartService;
	private ProductConfigurationPersistenceService persistenceService;
	private ConfigurationModelCacheStrategy configurationModelCacheStrategy;
	private ConfigurationVariantUtil configurationVariantUtil;
	private CommerceCartService commerceCartService;
	private UserService userService;

	@Override
	public ProductConfigurationRelatedObjectType retrieveRelatedObjectType(final String configId)
	{
		ProductConfigurationRelatedObjectType assignedToType = ProductConfigurationRelatedObjectType.UNKNOWN;

		//Try to get related abstract order entry
		final AbstractOrderEntryModel entry = retrieveOrderEntry(configId);

		if (entry != null)
		{
			if (entry instanceof OrderEntryModel)
			{
				assignedToType = ProductConfigurationRelatedObjectType.ORDER_ENTRY;
			}
			else if (entry instanceof QuoteEntryModel)
			{
				assignedToType = ProductConfigurationRelatedObjectType.QUOTE_ENTRY;
			}
			else if (entry instanceof CartEntryModel)
			{
				if (isSessionCartEntry((CartEntryModel) entry))
				{
					assignedToType = ProductConfigurationRelatedObjectType.CART_ENTRY;
				}
				else
				{
					assignedToType = ProductConfigurationRelatedObjectType.SAVEDCART_ENTRY;
				}
			}
		}
		else if (!getPersistenceService().getByConfigId(configId).getProduct().isEmpty())
		{
			assignedToType = ProductConfigurationRelatedObjectType.PRODUCT;
		}

		return assignedToType;
	}

	@Override
	public ProductConfigurationRelatedObjectType retrieveRelatedObjectType(final AbstractOrderModel order)
	{
		ProductConfigurationRelatedObjectType assignedToType = ProductConfigurationRelatedObjectType.UNKNOWN;

		if (order != null)
		{
			if (order instanceof OrderModel)
			{
				assignedToType = ProductConfigurationRelatedObjectType.ORDER_ENTRY;
			}
			else if (order instanceof QuoteModel)
			{
				assignedToType = ProductConfigurationRelatedObjectType.QUOTE_ENTRY;
			}
			else if (order instanceof CartModel)
			{
				assignedToType = ProductConfigurationRelatedObjectType.CART_ENTRY;
			}
		}
		return assignedToType;
	}


	protected AbstractOrderEntryModel retrieveOrderEntry(final String configId)
	{
		final List<AbstractOrderEntryModel> allEntries = getPersistenceService().getAllOrderEntriesByConfigId(configId);
		// filter out all OrderEntires where the OrderVersionID != null and expect max one entry
		return allEntries.stream()
				.filter(entry -> !(entry instanceof OrderEntryModel) || ((OrderModel) entry.getOrder()).getVersionID() == null)
				.collect(toSingelton(configId));
	}

	public static Collector<AbstractOrderEntryModel, Object, AbstractOrderEntryModel> toSingelton(final String configId)
	{
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			if (list.size() > 1)
			{
				throw new AmbiguousIdentifierException(configId + " is assigned to more than one abstract order entry models");
			}
			return list.size() == 1 ? list.get(0) : null;
		});
	}

	@Override
	public Date retrieveCreationDateForRelatedEntry(final String configId)
	{
		final AbstractOrderEntryModel entry = retrieveOrderEntry(configId);
		if (entry != null)
		{
			return entry.getOrder().getCreationtime();
		}
		return null;
	}

	@Override
	public String retrieveRelatedProductCode(final String configId)
	{
		String productCode = null;
		final AbstractOrderEntryModel entry = retrieveOrderEntry(configId);
		if (entry != null)
		{
			final ProductModel product = entry.getProduct();
			if (getConfigurationVariantUtil().isCPQNotChangeableVariantProduct(product))
			{
				productCode = getConfigurationVariantUtil().getBaseProductCode(product);
			}
			else
			{
				productCode = product.getCode();
			}
		}
		else
		{
			final Collection<ProductModel> productModelCollection = getPersistenceService().getByConfigId(configId).getProduct();
			final Optional<ProductModel> productModel = productModelCollection.stream().findFirst();
			if (productModel.isPresent())
			{
				productCode = productModel.get().getCode();
			}
			else
			{
				final ConfigModel model = getConfigurationModelCacheStrategy().getConfigurationModelEngineState(configId);
				if (null != model)
				{
					productCode = model.getKbKey().getProductCode();
				}
			}
		}
		if (null == productCode)
		{
			throw new IllegalStateException(
					String.format("Could not determine a product code related to configuration '%s'", configId));
		}
		return productCode;
	}

	protected boolean isSessionCartEntry(final CartEntryModel entry)
	{
		final String cartCode = entry.getOrder().getCode();
		return doesCartCodeBelongsToSessionCart(cartCode);
	}

	protected boolean isSessionCart(final CartModel cart)
	{
		final String cartCode = cart.getCode();
		return doesCartCodeBelongsToSessionCart(cartCode);
	}

	protected boolean doesCartCodeBelongsToSessionCart(final String cartCode)
	{
		final boolean hasSessionCart = getCartService().hasSessionCart();
		return hasSessionCart ? cartCode.equals(getCartService().getSessionCart().getCode()) : !isSavedCart(cartCode);

	}

	protected boolean isSavedCart(final String cartCode)
	{
		final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(cartCode, getUserService().getCurrentUser());
		Preconditions.checkState(cartModel != null, "We expect to find a cart for code: %s ", cartCode);
		return cartModel.getSaveTime() != null;
	}

	protected ProductConfigurationPersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	/**
	 * @param persistenceService
	 *           the persistence service to access the product configuration persistence
	 */
	@Required
	public void setPersistenceService(final ProductConfigurationPersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cart service to access the session cart
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ConfigurationModelCacheStrategy getConfigurationModelCacheStrategy()
	{
		return configurationModelCacheStrategy;
	}

	@Required
	public void setConfigurationModelCacheStrategy(final ConfigurationModelCacheStrategy configurationModelCacheStrategy)
	{
		this.configurationModelCacheStrategy = configurationModelCacheStrategy;
	}

	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	/**
	 * @param configurationVariantUtil
	 *           utli to handle variants
	 */
	@Required
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return this.commerceCartService;
	}

	/**
	 * Set commerce cart service
	 *
	 * @param commerceCartService
	 */
	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected UserService getUserService()
	{
		return this.userService;
	}

	/**
	 * Set user service
	 *
	 * @param userService
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
