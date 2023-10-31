/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.validator;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.validator.CommerceQuoteValidator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.QuoteEntryValidationData;
import de.hybris.platform.util.localization.Localization;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.sap.security.core.server.csi.XSSEncoder;


public class DefaultConfigurationQuoteValidator implements CommerceQuoteValidator
{

	private static final Logger LOG = Logger.getLogger(DefaultConfigurationQuoteValidator.class);
	private final ConfigurableChecker configurableChecker;
	private final AbstractOrderIntegrationService abstractOrderIntegrationService;
	private final ConfigurationService configurationService;

	/**
	 * Constructor that receives the needed beans
	 *
	 * @param configurableChecker
	 *           Assessing if a product is configurable in the sense of CPQ
	 * @param abstractOrderIntegrationService
	 *           Order integration service
	 * @param configurationService
	 *           Configuration service
	 */
	public DefaultConfigurationQuoteValidator(final ConfigurableChecker configurableChecker,
			final AbstractOrderIntegrationService abstractOrderIntegrationService, final ConfigurationService configurationService)
	{

		this.configurableChecker = configurableChecker;
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
		this.configurationService = configurationService;
	}

	protected ConfigurableChecker getConfigurableChecker()
	{
		return this.configurableChecker;
	}

	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return this.abstractOrderIntegrationService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return this.configurationService;
	}


	@Override
	public void validate(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		if (quoteAction.equals(QuoteAction.CHECKOUT))
		{
			performConfigurationCheckoutValidation(quoteModel, quoteAction);
		}

	}

	protected void performConfigurationCheckoutValidation(final QuoteModel quoteModel, final QuoteAction quoteAction)
	{
		final Optional<QuoteEntryValidationData> singleConfigurationIssue = getFirstConfigurationIssue(quoteModel);
		if (singleConfigurationIssue.isPresent())
		{
			final String localizedMessage = getLocalizedText(singleConfigurationIssue.get().getProductCode());
			throw new IllegalQuoteStateException(quoteAction, quoteModel.getCode(), quoteModel.getState(), quoteModel.getVersion(),
					localizedMessage, true);
		}
	}

	protected Optional<QuoteEntryValidationData> getFirstConfigurationIssue(final QuoteModel quoteModel)
	{
		Preconditions.checkNotNull(quoteModel, "We expect a quote");
		return quoteModel.getEntries().stream().map(this::getConfigurationIssue)
				.filter(QuoteEntryValidationData::isWithConfigurationIssue).findFirst();
	}

	protected QuoteEntryValidationData getConfigurationIssue(final AbstractOrderEntryModel entry)
	{

		final QuoteEntryValidationData quoteEntryValidationData = new QuoteEntryValidationData();
		quoteEntryValidationData.setProductCode(entry.getProduct().getCode());
		if (getConfigurableChecker().isCloudCPQConfigurableProduct(entry.getProduct()))
		{
			final String configId = getAbstractOrderIntegrationService().getConfigIdForAbstractOrderEntry(entry);
			Preconditions.checkState(configId != null, "We expect a configId for an entry with a configurable product");
			if (getConfigurationService().hasConfigurationIssues(configId))
			{
				quoteEntryValidationData.setWithConfigurationIssue(true);
			}
		}
		return quoteEntryValidationData;
	}

	protected String getLocalizedText(final String productCode)
	{
		if (Registry.hasCurrentTenant())
		{
			try
			{
				return XSSEncoder.encodeHTML(Localization.getLocalizedString("cpqproductconfigservices.quote.issues.msg", new String[]
				{ productCode }));
			}
			catch (final UnsupportedEncodingException e)
			{
				throw new IllegalStateException("Could not encode", e);
			}
		}
		else
		{
			LOG.warn("Localized texts are not retrieved - this is ok in unit test mode");
			return productCode;
		}
	}

}
