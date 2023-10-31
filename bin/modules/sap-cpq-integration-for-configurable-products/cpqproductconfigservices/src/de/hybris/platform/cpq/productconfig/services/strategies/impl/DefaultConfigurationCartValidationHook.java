/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.hooks.CartValidationHook;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;

import java.util.List;


/**
 * Implementation of CartValidationHook for CPQ Configuration
 */
public class DefaultConfigurationCartValidationHook implements CartValidationHook
{

	/**
	 * Indicates that a cart modification refers to an invalid configuration. In that case, the cart has not been
	 * modified but is just in a state that forbids the checkout
	 */
	public static final String REVIEW_CONFIGURATION = "reviewConfiguration";
	private final ConfigurationService configurationService;
	private final ConfigurableChecker configurableChecker;
	private final AbstractOrderIntegrationService abstractOrderIntegrationService;

	/**
	 * Receives mandatory beans
	 *
	 * @param configurationService
	 *           Used for accessing the configuration summary for cart entries
	 * @param configurableChecker
	 *           Checks whether a product is configurable in the sense of CPQ
	 * @param abstractOrderIntegrationService
	 *           Determines configuration ID for cart entry
	 */
	public DefaultConfigurationCartValidationHook(final ConfigurationService configurationService,
			final ConfigurableChecker configurableChecker, final AbstractOrderIntegrationService abstractOrderIntegrationService)
	{
		this.configurationService = configurationService;
		this.configurableChecker = configurableChecker;
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
	}

	@Override
	public void beforeValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		// nothing to do

	}

	@Override
	public void afterValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		final CartModel cart = parameter.getCart();
		cart.getEntries().stream().filter(entry -> getConfigurableChecker().isCloudCPQConfigurableProduct(entry.getProduct()))
				.filter(entry -> isEntrySuccess(entry, modifications))
				.forEach(entryToCheck -> validateConfiguration(modifications, entryToCheck));
	}

	protected void validateConfiguration(final List<CommerceCartModification> modifications,
			final AbstractOrderEntryModel entryToCheck)
	{
		final CommerceCartModification resultfromConfigurationValidation = validateConfigurationAttachedToEntry(entryToCheck);
		if (resultfromConfigurationValidation != null)
		{
			final CommerceCartModification oldModification = retrieveModificationForEntry(entryToCheck, modifications);
			modifications.remove(oldModification);
			modifications.add(resultfromConfigurationValidation);
		}
	}

	protected CommerceCartModification validateConfigurationAttachedToEntry(final AbstractOrderEntryModel cartEntry)
	{
		CommerceCartModification modification = null;
		final String configId = getAbstractOrderIntegrationService().getConfigIdForAbstractOrderEntry(cartEntry);
		getConfigurationService().removeCachedConfigurationSummary(configId);
		if (getConfigurationService().hasConfigurationIssues(configId))
		{
			modification = new CommerceCartModification();
			modification.setEntry(cartEntry);
			modification.setProduct(cartEntry.getProduct());
			modification.setStatusCode(REVIEW_CONFIGURATION);
		}
		return modification;
	}

	protected boolean isEntrySuccess(final AbstractOrderEntryModel entry, final List<CommerceCartModification> modifications)
	{
		final CommerceCartModification matchingModification = retrieveModificationForEntry(entry, modifications);
		return CommerceCartModificationStatus.SUCCESS.equals(matchingModification.getStatusCode());
	}

	protected CommerceCartModification retrieveModificationForEntry(final AbstractOrderEntryModel entry,
			final List<CommerceCartModification> modifications)
	{
		return modifications.stream().filter(modification -> modification.getEntry().getPk().equals(entry.getPk())).findFirst()
				.orElseThrow(() -> new IllegalStateException("No CartModification found for cart entry " + entry.getPk()));
	}


	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected ConfigurableChecker getConfigurableChecker()
	{
		return configurableChecker;
	}

	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return abstractOrderIntegrationService;
	}

}
