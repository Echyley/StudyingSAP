/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.quoteexchange.service.inbound.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Preconditions;
import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqInboundQuoteHelper;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;


/**
 * Inbound hook for processing quote entries received from CPQ. If the quote entry is linked to a configuration, the
 * reference will be replaced by a copy of the configuration to ensure independency.
 */
public class DefaultCpqProductConfigCpqInboundQuoteHelper implements CpqInboundQuoteHelper
{

	// Even if the percent discount field is shown with two decimal places, we calculate it with more precision.
	// This is required to avoid value differences between quote and order when the quote is ordered later.
	private static final int SCALE = 6;
	private static final String FIXED = "Fixed";

	private final AbstractOrderIntegrationService abstractOrderIntegrationService;
	private final ConfigurationService configurationService;
	private final ConfigurableChecker configurableChecker;
	private final BaseSiteService baseSiteService;

	public DefaultCpqProductConfigCpqInboundQuoteHelper(final AbstractOrderIntegrationService abstractOrderIntegrationService,
			final ConfigurationService configurationService, final ConfigurableChecker configurableChecker,
			final BaseSiteService baseSiteService)
	{
		super();
		this.abstractOrderIntegrationService = abstractOrderIntegrationService;
		this.configurationService = configurationService;
		this.configurableChecker = configurableChecker;
		this.baseSiteService = baseSiteService;
	}

	@Override
	public QuoteModel processInboundQuote(final QuoteModel inboundQuote)
	{

		inboundQuote.getCpqQuoteEntries().forEach(this::processInboundQuoteEntry);
		return inboundQuote;
	}

	protected void processInboundQuoteEntry(final QuoteEntryModel inboundQuoteEntry)
	{
		if (configurableChecker.isCloudCPQConfigurableProduct(inboundQuoteEntry.getProduct()))
		{
			processProductConfiguration(inboundQuoteEntry);
			processMainEntryPrices(inboundQuoteEntry);
		}
	}

	protected void processProductConfiguration(final QuoteEntryModel inboundQuoteEntry)
	{
		final boolean isResetBaseSite = ensureBaseSiteIsAvailable(inboundQuoteEntry.getOrder());
		final String configurationId = inboundQuoteEntry.getCpqConfigurationId();
		Preconditions.checkState(configurationId != null, "We expect a configuration ID");
		final String newConfigId = configurationService.cloneConfiguration(configurationId, true);

		if (CollectionUtils.isEmpty(inboundQuoteEntry.getProductInfos()))
		{
			final CloudCPQOrderEntryProductInfoModel infomodel = new CloudCPQOrderEntryProductInfoModel();
			infomodel.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
			inboundQuoteEntry.setProductInfos(Collections.singletonList(infomodel));
		}

		abstractOrderIntegrationService.setConfigIdForAbstractOrderEntry(inboundQuoteEntry, newConfigId);
		resetBaseSite(isResetBaseSite);
	}

	protected void resetBaseSite(final boolean isReset)
	{
		if (isReset)
		{
			final BaseSiteModel baseSite = null;
			baseSiteService.setCurrentBaseSite(baseSite, false);
		}

	}

	protected boolean ensureBaseSiteIsAvailable(final QuoteModel quoteModel)
	{
		if (null == baseSiteService.getCurrentBaseSite())
		{
			baseSiteService.setCurrentBaseSite(quoteModel.getSite(), true);
			return true;
		}
		return false;
	}


	protected void processMainEntryPrices(final QuoteEntryModel quoteEntry) {
		if(CollectionUtils.isNotEmpty(quoteEntry.getCpqPricingDetails())) {
			final Optional<CpqPricingDetailModel> priceDetailOptional = quoteEntry.getCpqPricingDetails().stream()
					.filter(priceItem -> priceItem.getPricingType().equals(FIXED)).findFirst();
			if(priceDetailOptional.isPresent()) {
				final CpqPricingDetailModel priceDetail =  priceDetailOptional.get();
				final BigDecimal rolledUpListPrice = new BigDecimal(priceDetail.getRolledUpListPrice());
				final BigDecimal rolledUpExtendedListPrice = new BigDecimal(priceDetail.getRolledUpExtendedListPrice());
				final BigDecimal rolledUpExtendedAmount = new BigDecimal(priceDetail.getRolledUpExtendedAmount());
				final BigDecimal rolledUpDiscountAmount = new BigDecimal(priceDetail.getRolledUpDiscountAmount());
				// RolledUpDiscountPercent is calculated in CPQ not as we need here. Therefore we recalculate it
				final BigDecimal rolledUpDiscountPercent = rolledUpDiscountAmount.multiply(new BigDecimal("100")).divide(rolledUpExtendedListPrice, SCALE, RoundingMode.HALF_UP);
				quoteEntry.setBasePrice(rolledUpListPrice.doubleValue());
				quoteEntry.setTotalPrice(rolledUpExtendedAmount.doubleValue());
				quoteEntry.setCpqEntryDiscount(rolledUpDiscountAmount.doubleValue());
				quoteEntry.setDiscountPercent(String.valueOf(rolledUpDiscountPercent));
				processMainEntryDiscount(rolledUpDiscountAmount, rolledUpDiscountPercent, quoteEntry);
			}
		}
	}

	protected void processMainEntryDiscount(final BigDecimal rolledUpDiscountAmount, final BigDecimal rolledUpDiscountPercent, final QuoteEntryModel quoteEntry)
	{
		final Boolean isAbsoluteDiscountAllowedSetting = quoteEntry.getOrder().getStore().getSAPConfiguration().getCpqAbsoluteDiscountEnabled();
		boolean isAbsoluteDiscountAllowed = false;
		if (isAbsoluteDiscountAllowedSetting != null)
		{
			isAbsoluteDiscountAllowed = isAbsoluteDiscountAllowedSetting.booleanValue();
		}
		final List<DiscountValue> dvList = new ArrayList<>();
		final String conditionCode = quoteEntry.getOrder().getStore().getSAPConfiguration().getCpqQuoteEntryDiscountConditionCode();
		final String currencyCode = quoteEntry.getOrder().getCurrency().getIsocode();
		final double discountValue = isAbsoluteDiscountAllowed
				? rolledUpDiscountAmount.divide(BigDecimal.valueOf(quoteEntry.getQuantity()), SCALE, RoundingMode.HALF_UP)
						.doubleValue()
				: rolledUpDiscountPercent.doubleValue();
		final double appliedDiscountValue = rolledUpDiscountAmount.doubleValue();
		final DiscountValue dv = new DiscountValue(conditionCode, discountValue, isAbsoluteDiscountAllowed, appliedDiscountValue, currencyCode);
		dvList.add(dv);
		quoteEntry.setDiscountValues(dvList);
		quoteEntry.setCpqEntryDiscountInternal("<" + dv.toString() + ">");
	}

	protected AbstractOrderIntegrationService getAbstractOrderIntegrationService()
	{
		return abstractOrderIntegrationService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected ConfigurableChecker getConfigurableChecker()
	{
		return configurableChecker;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

}
