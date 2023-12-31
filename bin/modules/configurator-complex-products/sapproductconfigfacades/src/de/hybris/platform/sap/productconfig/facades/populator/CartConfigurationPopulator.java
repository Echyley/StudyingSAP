/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.localization.Localization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Takes care of populating product configuration relevant attributes
 */
public class CartConfigurationPopulator extends AbstractOrderConfigurationPopulator implements Populator<CartModel, CartData>
{
	protected static final String PRICING_ERROR_DESCRIPTION = "sapproductconfig.config.pricingerror.description";
	protected static final String PRICING_ERROR_TITLE = "sapproductconfig.config.pricingerror.title";
	protected static final String UNRESOLVABLE_ISSUES_ERROR_DESCRIPTION = "sapproductconfig.config.unresolvable.issues.error.description";
	protected static final String UNRESOLVABLE_ISSUES_ERROR_TITLE = "sapproductconfig.config.unresolvable.issues.error.title";
	protected static final String UPDATED_IN_BACKGROUND_DESCRIPTION = "sapproductconfig.config.updated.in.background.description";
	protected static final String UPDATED_IN_BACKGROUND_TITLE = "sapproductconfig.config.updated.in.background.title";
	private static final Logger LOG = Logger.getLogger(CartConfigurationPopulator.class);
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ProductConfigurationService productConfigurationService;
	private ModelService modelService;
	private PriceDataFactory priceDataFactory;
	private ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService;
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;
	private CartPopulator<CartData> cartPopulator;
	private CommerceCartService commerceCartService;
	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;

	/**
	 * @return the cartPopulator
	 */
	protected CartPopulator<CartData> getCartPopulator()
	{
		return cartPopulator;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	/**
	 * Sets strategy that controls how to link configuration and abstract order entry
	 *
	 * @param configurationAbstractOrderEntryLinkStrategy
	 */
	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}


	@Override
	public void populate(final CartModel source, final CartData target)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}


		source.getEntries().stream().forEach(entry -> populateCartEntry(entry, target));

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("CPQ Populating for cart took " + duration + " ms");
		}
	}



	/**
	 * Transfers configuration related attributes from order entry into its DTO representation
	 *
	 * @param entry
	 *           Cart entry model
	 * @param target
	 *           Cart DTO, used to get the cart entry DTO via searching for key
	 * @throws CalculationException
	 * @return Did we change the price?
	 */
	protected boolean populateCartEntry(final AbstractOrderEntryModel entry, final CartData target)
	{
		// In case in parallel the cart entry has been removed
		if (getModelService().isRemoved(entry))
		{
			LOG.warn("Cart entry has been removed!");
			return false;
		}

		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(entry.getProduct()))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is Configurable ==> populating DTO.");
			}
			final ConfigModel configModel = getConfigurationAbstractOrderIntegrationStrategy()
					.getConfigurationForAbstractOrderEntry(entry);
			final OrderEntryData targetEntry = findTargetEntry(target, entry.getEntryNumber());

			writeToTargetEntry(entry, targetEntry);
			validateChangedInBackground(configModel, entry, targetEntry);
			if (hasUnresolvableIssue(configModel))
			{
				return validateUnresolvableIssues(entry, targetEntry);
			}
			else
			{
				return validatePrice(configModel, entry, targetEntry);
			}
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is NOT Configurable ==> skipping population of DTO.");
			}
		}
		return false;
	}

	protected boolean hasUnresolvableIssue(final ConfigModel configModel)
	{
		return (!configModel.isComplete() || !configModel.isConsistent())
				&& 0 == getProductConfigurationService().getTotalNumberOfIssues(configModel);
	}

	protected boolean validateUnresolvableIssues(final AbstractOrderEntryModel entry, final OrderEntryData targetEntry)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("unresolvable issues continues to be present for cart entry key " + entry.getPk());
		}
		targetEntry.setConfigurationConsistent(false);
		targetEntry.setConfigurationInfos(createInlineUnresolvableIssuesError());
		return false;
	}

	protected List<ConfigurationInfoData> createInlineUnresolvableIssuesError()
	{
		return createInlineError(UNRESOLVABLE_ISSUES_ERROR_TITLE, UNRESOLVABLE_ISSUES_ERROR_DESCRIPTION);
	}

	protected List<ConfigurationInfoData> createInlineError(final String title, final String description)
	{
		final List<ConfigurationInfoData> list = new ArrayList<>();
		final ConfigurationInfoData infoData = new ConfigurationInfoData();
		list.add(infoData);
		infoData.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		infoData.setConfigurationLabel(getLocalizedText(title));
		infoData.setConfigurationValue(getLocalizedText(description));
		infoData.setStatus(ProductInfoStatus.ERROR);
		return list;
	}

	/**
	 * Validates price on entry level, triggers a reculaculation of the cart if needed
	 *
	 * @param configModel
	 * @param entry
	 * @param targetEntry
	 * @return Did we do a price change?
	 */
	protected boolean validatePrice(final ConfigModel configModel, final AbstractOrderEntryModel entry,
			final OrderEntryData targetEntry)
	{
		if (getProductConfigurationPricingStrategy().isCartPricingErrorPresent(configModel))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("pricing error continues to be present for cart entry key " + entry.getPk());
			}
			targetEntry.setConfigurationConsistent(false);
			targetEntry.setConfigurationInfos(createInlinePriceError());
			return false;
		}
		return true;

	}

	protected PriceData createPriceData(final Double price, final AbstractOrderEntryModel entry)
	{
		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(price.doubleValue()),
				entry.getOrder().getCurrency());
	}

	protected List<ConfigurationInfoData> createInlinePriceError()
	{
		return createInlineError(PRICING_ERROR_TITLE, PRICING_ERROR_DESCRIPTION);
	}

	protected String getLocalizedText(final String key)
	{
		if (ServicelayerUtils.isSystemInitialized())
		{
			return Localization.getLocalizedString(key);
		}
		else
		{
			LOG.warn("Localized texts are not retrieved - this is ok in unit test mode");
			return key;
		}
	}

	/**
	 * @param modelService
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;

	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected ProductConfigurationOrderIntegrationService getConfigurationPricingOrderIntegrationService()
	{
		return configurationPricingOrderIntegrationService;
	}

	/**
	 * @param configurationPricingOrderIntegrationService
	 *           the configurationPricingOrderIntegrationService to set
	 */
	@Required
	public void setConfigurationPricingOrderIntegrationService(
			final ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService)
	{
		this.configurationPricingOrderIntegrationService = configurationPricingOrderIntegrationService;
	}

	/**
	 * @param cartPopulator
	 */
	public void setCartPopulator(final CartPopulator<CartData> cartPopulator)
	{
		this.cartPopulator = cartPopulator;

	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
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
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	protected ProductConfigurationPricingStrategy getProductConfigurationPricingStrategy()
	{
		return productConfigurationPricingStrategy;
	}

	/**
	 * @param productConfigurationPricingStrategy
	 *           the productConfigurationPricingStrategy to set
	 */
	@Required
	public void setProductConfigurationPricingStrategy(
			final ProductConfigurationPricingStrategy productConfigurationPricingStrategy)
	{
		this.productConfigurationPricingStrategy = productConfigurationPricingStrategy;
	}

	/**
	 * @param configurationAbstractOrderIntegrationStrategy
	 */
	@Required
	public void setConfigurationAbstractOrderIntegrationStrategy(
			final ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy)
	{
		this.configurationAbstractOrderIntegrationStrategy = configurationAbstractOrderIntegrationStrategy;
	}

	protected ConfigurationAbstractOrderIntegrationStrategy getConfigurationAbstractOrderIntegrationStrategy()
	{
		return configurationAbstractOrderIntegrationStrategy;
	}

	protected void validateChangedInBackground(final ConfigModel configModel, final AbstractOrderEntryModel entry,
			final OrderEntryData targetEntry)
	{
		if (configModel.isChangedInBackground())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("product configuration is changed in background for cart entry key " + entry.getPk());
			}
			addChangedInBackgroundWarning(targetEntry);
		}
	}

	protected void addChangedInBackgroundWarning(final OrderEntryData targetEntry)
	{
		final List<ConfigurationInfoData> list = targetEntry.getConfigurationInfos();
		final ConfigurationInfoData infoData = new ConfigurationInfoData();
		list.add(infoData);
		infoData.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		infoData.setConfigurationLabel(getLocalizedText(UPDATED_IN_BACKGROUND_TITLE));
		infoData.setConfigurationValue(getLocalizedText(UPDATED_IN_BACKGROUND_DESCRIPTION));
		infoData.setStatus(ProductInfoStatus.ERROR);
		targetEntry.setConfigurationConsistent(true);
	}
}
