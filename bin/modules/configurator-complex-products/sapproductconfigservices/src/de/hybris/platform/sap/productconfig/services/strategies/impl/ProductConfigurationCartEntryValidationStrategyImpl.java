/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.strategies.intf.ProductConfigurationCartEntryValidationStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ProductConfigurationCartEntryValidationStrategy}
 */
public class ProductConfigurationCartEntryValidationStrategyImpl implements ProductConfigurationCartEntryValidationStrategy
{

	private static final Logger LOG = Logger.getLogger(ProductConfigurationCartEntryValidationStrategyImpl.class);

	/**
	 * Indicates that customer needs to revisit product configuration. Postfix in corresponding resource text
	 */
	public static final String REVIEW_CONFIGURATION = "reviewConfiguration";
	/**
	 * Indicates that prices cannot be retrieved at the moment
	 */
	public static final String PRICING_ERROR = "pricingError";
	/**
	 * Indicates that the KB-Version which was used to create the external configuratiuon, is not known/valid anymore
	 */
	public static final String KB_NOT_VALID = "kbNotValid";
	/**
	 * Indicates that the configuration has some unresolvable issues
	 */
	public static final String UNRESOLVABLE_ISSUES = "unresolvableIssues";
	private ProductConfigurationService productConfigurationService;
	private ModelService modelService;
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;


	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return cpqConfigurableChecker;
	}

	/**
	 * @return the configurationAbstractOrderEntryLinkStrategy
	 */
	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	@Override
	public CommerceCartModification validateConfiguration(final AbstractOrderEntryModel cartEntryModel)
	{
		//No issues so far: We check for consistency and completeness of configuration
		CommerceCartModification configurationModification = null;
		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(cartEntryModel.getProduct()))
		{

			final boolean validKB = getConfigurationAbstractOrderIntegrationStrategy().isKbVersionForEntryExisting(cartEntryModel);
			boolean completeAndConsistent = false;
			boolean pricingError = true;
			boolean unresolvableIssues = false;

			if (validKB)
			{
				final ConfigModel configurationModel = getConfigurationAbstractOrderIntegrationStrategy()
						.getConfigurationForAbstractOrderEntry(cartEntryModel);
				//Prices have been fetched in the beforeValidate hook
				completeAndConsistent = configurationModel.isComplete() && configurationModel.isConsistent();
				unresolvableIssues = !completeAndConsistent
						&& 0 == getProductConfigurationService().getTotalNumberOfIssues(configurationModel);
				pricingError = getProductConfigurationPricingStrategy().isCartPricingErrorPresent(configurationModel);
			}
			else
			{
				LOG.warn("Configuration of cart entry " + cartEntryModel.getPk().toString()
						+ " belongs to an invalid knowledgebase. Hence it will be reset.");
				// delete deprecated configuration and force creation of default configuration on next access
				getConfigurationAbstractOrderIntegrationStrategy().invalidateCartEntryConfiguration(cartEntryModel);
				resetConfigurationInfo(cartEntryModel);
				getModelService().save(cartEntryModel);

			}
			configurationModification = createCommerceCartModification(cartEntryModel, completeAndConsistent, validKB, pricingError,
					unresolvableIssues);


			if (LOG.isDebugEnabled() && configurationModification != null)
			{
				LOG.debug("Validate configuration for product '" + configurationModification.getProduct().getCode()
						+ "' with status '" + configurationModification.getStatusCode() + "'");
			}
		}

		return configurationModification;
	}

	protected void resetConfigurationInfo(final AbstractOrderEntryModel orderEntry)
	{
		final List<AbstractOrderEntryProductInfoModel> configInfos = new ArrayList<>();
		final CPQOrderEntryProductInfoModel configInfo = getModelService().create(CPQOrderEntryProductInfoModel.class);
		configInfo.setOrderEntry(orderEntry);
		configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configInfos.add(configInfo);
		orderEntry.setProductInfos(Collections.unmodifiableList(configInfos));
	}

	/**
	 * Creates modification bean
	 *
	 * @param cartEntryModel
	 * @param completeAndConsistent
	 *           <code>true</code> only if the KB is complete and consistent
	 * @param validKB
	 *           <code>true</code> only if the KB is valid
	 * @param pricingError
	 * @return Modification bean
	 */
	protected CommerceCartModification createCommerceCartModification(final AbstractOrderEntryModel cartEntryModel,
			final boolean completeAndConsistent, final boolean validKB, final boolean pricingError, final boolean unresolvableIssues)
	{
		CommerceCartModification configurationModification = null;
		if (!completeAndConsistent || pricingError || !validKB || unresolvableIssues)
		{
			configurationModification = new CommerceCartModification();
			if (!validKB)
			{
				configurationModification.setStatusCode(KB_NOT_VALID);
			}
			else if (unresolvableIssues)
			{
				configurationModification.setStatusCode(UNRESOLVABLE_ISSUES);
			}
			else if (pricingError)
			{
				configurationModification.setStatusCode(PRICING_ERROR);
			}
			else
			{
				configurationModification.setStatusCode(REVIEW_CONFIGURATION);
			}
			configurationModification.setEntry(cartEntryModel);
			configurationModification.setProduct(cartEntryModel.getProduct());
		}
		return configurationModification;
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
	 * @param configurationAbstractOrderEntryLinkStrategy
	 */
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;

	}

	/**
	 * @param cpqConfigurableChecker
	 */
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;

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
