/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Releases configuration sessions on placing an order
 *
 */
public class ProductConfigurationPlaceOrderHookImpl implements CommercePlaceOrderMethodHook
{

	private static final Logger LOG = Logger.getLogger(ProductConfigurationPlaceOrderHookImpl.class);

	private ProductConfigurationService productConfigurationService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;


	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{

		if (LOG.isDebugEnabled())
		{
			traceCPQAspectsAfterPlaceOrder(orderModel.getOrder());
		}


		for (final AbstractOrderEntryModel cartEntry : parameter.getCart().getEntries())
		{
			final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntry.getPk().toString());
			if (configId != null && (!configId.isEmpty()))
			{
				try
				{
					getConfigurationAbstractOrderIntegrationStrategy().finalizeCartEntry(cartEntry);
				}
				catch (final RuntimeException runtimeException)
				{
					LOG.error(String.format("Product configurator artifacts for cart entry %s could not be completely released",
							cartEntry.getPk().toString()), runtimeException);
				}
			}
		}
	}

	protected void prepareForOrderReplication(final AbstractOrderEntryModel entry)
	{
		try
		{
			getConfigurationAbstractOrderIntegrationStrategy().prepareForOrderReplication(entry);
		}
		catch (final RuntimeException runtimeException)
		{
			LOG.error(String.format(
					"Entry %s of order %s could not be prepared for order replication! The product configurator aspect might be missing",
					entry.getPk().toString(), entry.getOrder().getCode()), runtimeException);
		}
	}

	protected void traceCPQAspectsAfterPlaceOrder(final AbstractOrderModel orderModel)
	{
		LOG.debug("After place order, target document has code: " + orderModel.getCode());
		orderModel.getEntries().stream().forEach(entry -> traceCPQAspectsAfterPlaceOrder(entry));
	}

	protected void traceCPQAspectsAfterPlaceOrder(final AbstractOrderEntryModel entry)
	{
		LOG.debug("Product configuration: " + entry.getProductConfiguration() + " for entry " + entry.getPk());
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		parameter.getCart().getEntries().stream().filter(this::hasConfigurationAttached)
				.forEach(this::prepareForOrderReplication);
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		// Nothing done here
	}

	/**
	 * @return product configuration service
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return this.productConfigurationService;
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

	protected boolean hasConfigurationAttached(final AbstractOrderEntryModel cartEntry)
	{
		final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntry.getPk().toString());
		return StringUtils.isNotEmpty(configId);
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

}
