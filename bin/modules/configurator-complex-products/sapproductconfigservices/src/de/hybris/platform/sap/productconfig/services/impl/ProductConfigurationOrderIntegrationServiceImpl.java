/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ExternalConfigurationAccess;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationOrderIntegrationService}.
 */

public class ProductConfigurationOrderIntegrationServiceImpl implements ProductConfigurationOrderIntegrationService
{
	private ProductConfigurationService configurationService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private TrackingRecorder recorder;
	private ModelService modelService;
	private CommerceCartService commerceCartService;
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;
	private ExternalConfigurationAccess externalConfigurationAccess;

	private static final Logger LOG = Logger.getLogger(ProductConfigurationOrderIntegrationServiceImpl.class);

	@Override
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final AbstractOrderEntryModel entryModel)
	{
		final CartEntryConfigurationAttributes attributes = new CartEntryConfigurationAttributes();
		final ConfigModel configurationModel = getConfigurationAbstractOrderIntegrationStrategy()
				.getConfigurationForAbstractOrderEntry(entryModel);

		final boolean isConfigurationConsistent = configurationModel.isConsistent() && configurationModel.isComplete();

		attributes.setConfigurationConsistent(Boolean.valueOf(isConfigurationConsistent));
		final int numberOfIssues = getConfigurationService().getTotalNumberOfIssues(configurationModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Number of issues: " + numberOfIssues);
		}
		attributes.setNumberOfErrors(Integer.valueOf(numberOfIssues));

		return attributes;
	}

	@Override
	public boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry)
	{
		final String cartEntryKey = entry.getPk().toString();
		if (LOG.isDebugEnabled())
		{
			final String oldConfigId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntryKey);
			LOG.debug("Removed old configId " + oldConfigId + " for cart entry " + cartEntryKey);
		}
		getAbstractOrderEntryLinkStrategy().removeConfigIdForCartEntry(cartEntryKey);
		final KBKey kbKey = new KBKeyImpl(entry.getProduct().getCode());
		final ConfigModel configurationModel = getConfigurationService().createConfigurationFromExternal(kbKey,
				externalConfiguration, cartEntryKey);
		getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartEntryKey, configurationModel.getId());
		getConfigurationAbstractOrderIntegrationStrategy().updateAbstractOrderEntryOnUpdate(configurationModel.getId(), entry);
		return true;
	}

	@Override
	public boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId)
	{
		if (hasProductChangedForCartItem(product, entry))
		{
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(entry.getPk().toString(), configId);
			entry.setProduct(product);
			return true;
		}
		return false;
	}

	protected boolean hasProductChangedForCartItem(final ProductModel product, final AbstractOrderEntryModel cartItem)
	{
		return !cartItem.getProduct().getCode().equals(product.getCode());
	}

	@Override
	public void fillSummaryMap(final AbstractOrderEntryModel entry)
	{
		final CartEntryConfigurationAttributes configurationAttributes = calculateCartEntryConfigurationAttributes(entry);
		final Map<ProductInfoStatus, Integer> statusSummaryMap = new HashMap<>();
		entry.setCpqStatusSummaryMap(statusSummaryMap);
		if (!configurationAttributes.getConfigurationConsistent().booleanValue())
		{
			statusSummaryMap.put(ProductInfoStatus.ERROR, configurationAttributes.getNumberOfErrors());
		}
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           inject the CPQ tracking recorder for tracking CPQ events
	 */
	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;

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

	/**
	 * @param externalConfigurationAccess
	 *           See {@link ExternalConfigurationAccess}
	 */
	@Required
	public void setExternalConfigurationAccess(final ExternalConfigurationAccess externalConfigurationAccess)
	{
		this.externalConfigurationAccess = externalConfigurationAccess;
	}

	protected ExternalConfigurationAccess getExternalConfigurationAccess()
	{
		return externalConfigurationAccess;
	}


	/**
	 * This method is used by SOM and can therefore not be deleted.
	 * SOM has no access to the model layer of SAP Commerce and can not call the new calculateCartEntryConfigurationAttributes method.
	 * This method can be deleted once SOM is sunsetted. It must not be called outside the SOM context
	 *
	 * @deprecated since 18.08
	 */
	@Override
	@Deprecated(since = "1808")
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final String cartEntryKey,
			final String productCode, final String externalConfiguration)
	{
		final CartEntryConfigurationAttributes attributes = new CartEntryConfigurationAttributes();
		final ConfigModel configurationModel = ensureConfigurationInSession(cartEntryKey, productCode, externalConfiguration);

		final boolean isConfigurationConsistent = configurationModel.isConsistent() && configurationModel.isComplete();

		attributes.setConfigurationConsistent(Boolean.valueOf(isConfigurationConsistent));
		final int numberOfIssues = getConfigurationService().getTotalNumberOfIssues(configurationModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Number of issues: " + numberOfIssues);
		}
		attributes.setNumberOfErrors(Integer.valueOf(numberOfIssues));

		return attributes;
	}

	/**
	 * This method is used by SOM and can therefore not be deleted.
	 * SOM has no access to the model layer of SAP Commerce and can not call the new calculateCartEntryConfigurationAttributes method.
	 * This method can be deleted once SOM is sunsetted. It must not be called outside the SOM context
	 *
	 * @deprecated since 18.08 use
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getConfigurationForAbstractOrderEntry(AbstractOrderEntryModel)}
	 *             instead
	 */
	@Deprecated(since = "1808")
	public ConfigModel ensureConfigurationInSession(final String cartEntryKey, final String productCode,
			final String externalConfiguration)
	{
		final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntryKey);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("ConfigID=" + configId + " is mapped to cartentry with PK=" + cartEntryKey);
		}
		ConfigModel configurationModel = null;
		if (configId != null)
		{
			configurationModel = getConfigurationService().retrieveConfigurationOverview(configId);
		}

		if (configurationModel == null)
		{
			final KBKeyImpl kbKey = new KBKeyImpl(productCode);
			if (externalConfiguration == null)
			{
				// this means the item was put into the cart without touching
				// CPQ, e.g. through order forms
				// as this is not the standard process, log this in info level
				LOG.info(
						"No external configuration provided for cart entry key: " + cartEntryKey + ". Creating default configuration");
				configurationModel = getConfigurationService().createDefaultConfiguration(kbKey);
			}
			else
			{
				LOG.debug("Creating config model form external XML");
				configurationModel = getConfigurationService().createConfigurationFromExternal(kbKey, externalConfiguration,
						cartEntryKey);
			}
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartEntryKey, configurationModel.getId());
		}
		return configurationModel;
	}
}
