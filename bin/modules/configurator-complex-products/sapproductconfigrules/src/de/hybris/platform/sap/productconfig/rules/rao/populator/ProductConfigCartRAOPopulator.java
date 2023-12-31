/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.rao.populator;

import static java.util.Objects.nonNull;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigurationRuleAwareService;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ExternalConfigurationAccess;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Simplified Cart Populator, only mapping attributes relevant for rule evaluation within product configuration context
 */
public class ProductConfigCartRAOPopulator implements Populator<CartModel, CartRAO>
{
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private Converter<ConfigModel, ProductConfigRAO> productConfigRaoConverter;
	private Converter<UserModel, UserRAO> userConverter;
	private ProductConfigurationService productConfigService;
	private CPQConfigurableChecker cpqConfigurableChecker;
	private ExternalConfigurationAccess externalConfigurationAccess;

	@Override
	public void populate(final CartModel source, final CartRAO target)
	{
		target.setCode(source.getCode());
		final List<AbstractOrderEntryModel> entries = source.getEntries();
		if (!CollectionUtils.isEmpty(entries))
		{
			final int capa = (int) (entries.size() / 0.75) + 1;
			final Set<OrderEntryRAO> entriesRAO = new HashSet(capa);
			for (final AbstractOrderEntryModel entry : entries)
			{
				final OrderEntryRAO entryRAO = new OrderEntryRAO();
				populateEntry(entry, entryRAO);
				entriesRAO.add(entryRAO);
			}
			target.setEntries(entriesRAO);
		}

		if (nonNull(source.getUser()))
		{
			target.setUser(getUserConverter().convert(source.getUser()));
		}
	}

	protected void populateEntry(final AbstractOrderEntryModel entry, final OrderEntryRAO entryRAO)
	{
		entryRAO.setProductCode(entry.getProduct().getCode());
		entryRAO.setQuantity(entry.getQuantity().intValue());
		entryRAO.setEntryNumber(entry.getEntryNumber());

		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(entry.getProduct()))
		{
			populateProductConfig(entry, entryRAO);
		}
		else
		{
			final ProductConfigRAO productConfiguration = new ProductConfigRAO();
			productConfiguration.setProductCode(entry.getProduct().getCode());
			productConfiguration.setInCart(Boolean.TRUE);
			entryRAO.setProductConfiguration(productConfiguration);
		}

	}

	protected void populateProductConfig(final AbstractOrderEntryModel entry, final OrderEntryRAO entryRAO)
	{
		final String cartEntryKey = entry.getPk().toString();
		final String configIdForCartEntry = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartEntryKey);

		ConfigModel configModel = null;
		if (null == configIdForCartEntry)
		{
			final KBKey kbKey = new KBKeyImpl(entry.getProduct().getCode());
			final String externalConfiguration = getExternalConfigurationAccess().getExternalConfiguration(entry);
			if (externalConfiguration != null)
			{
				configModel = ((ProductConfigurationRuleAwareService) getProductConfigService())
						.createConfigurationFromExternalBypassRules(kbKey, externalConfiguration);
				getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartEntryKey, configModel.getId());
			}
		}
		else
		{
			configModel = ((ProductConfigurationRuleAwareService) getProductConfigService())
					.retrieveConfigurationModelBypassRules(configIdForCartEntry);
		}

		if (configModel != null)
		{
			attachConfigurationToRao(entryRAO, configModel);
		}
	}

	protected void attachConfigurationToRao(final OrderEntryRAO entryRAO, ConfigModel configModel)
	{
		final ProductConfigRAO productConfiguration = getProductConfigRaoConverter().convert(configModel);
		if (productConfiguration != null)
		{
			productConfiguration.setInCart(Boolean.TRUE);
			entryRAO.setProductConfiguration(productConfiguration);
		}
	}

	protected Converter<ConfigModel, ProductConfigRAO> getProductConfigRaoConverter()
	{
		return productConfigRaoConverter;
	}

	/**
	 * @param productConfigRaoConverter
	 */
	@Required
	public void setProductConfigRaoConverter(final Converter<ConfigModel, ProductConfigRAO> productConfigRaoConverter)
	{
		this.productConfigRaoConverter = productConfigRaoConverter;
	}


	protected ProductConfigurationService getProductConfigService()
	{
		return productConfigService;
	}

	/**
	 * @param configService
	 */
	@Required
	public void setProductConfigService(final ProductConfigurationService configService)
	{
		this.productConfigService = configService;
	}

	protected Converter<UserModel, UserRAO> getUserConverter()
	{
		return userConverter;
	}

	/**
	 * @param userConverter
	 */
	@Required
	public void setUserConverter(final Converter<UserModel, UserRAO> userConverter)
	{
		this.userConverter = userConverter;
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

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	/**
	 * @param configurationAbstractOrderEntryLinkStrategy
	 *
	 */
	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	/**
	 * @param externalConfigurationAccess
	 *           Wrapping accesses to {@link AbstractOrderEntryModel} to be able to decouple from SAP integrations
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
}
