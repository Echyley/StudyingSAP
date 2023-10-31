/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpqquoteexchange.service.inbound;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.sap.hybris.sapcpqquoteintegration.inbound.helper.CpqInboundQuoteHelper;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;


/**
 * Inbound hook for processing quote entries received from an external system. If the quote entry is linked to a
 * configuration, the reference will be replaced by a copy of the configuration to ensure independency.
 */
public class DefaultProductConfigCpqInboundQuoteHelper implements CpqInboundQuoteHelper
{

	// Even if the percent discount field is shown with two decimal places, we calculate it with more precision.
	// This is required to avoid value differences between quote and order when the quote is ordered later.
	private static final int SCALE = 6;
	private static final String FIXED = "Fixed";

	private final ConfigurationDeepCopyHandler configurationDeepCopyHandler;
	private final BaseSiteService baseSiteService;
	private final ModelService modelService;
	private final ProductConfigurationPersistenceService productConfigurationPersistenceService;


	public DefaultProductConfigCpqInboundQuoteHelper(final ConfigurationDeepCopyHandler configurationDeepCopyHandler,
			final BaseSiteService baseSiteService, final ModelService modelService,
			final ProductConfigurationPersistenceService productConfigurationPersistenceService)
	{
		this.configurationDeepCopyHandler = configurationDeepCopyHandler;
		this.baseSiteService = baseSiteService;
		this.modelService = modelService;
		this.productConfigurationPersistenceService = productConfigurationPersistenceService;
	}

	@Override
	public QuoteModel processInboundQuote(final QuoteModel inboundQuote)
	{
		inboundQuote.getCpqQuoteEntries().forEach(this::processInboundQuoteEntry);
		return inboundQuote;
	}

	protected void processInboundQuoteEntry(final QuoteEntryModel inboundQuoteEntry)
	{
		final String configId = inboundQuoteEntry.getCpqExternalConfigurationId();
		if (StringUtils.isNotEmpty(configId))
		{
			processProductConfiguration(configId, inboundQuoteEntry);
			processMainEntryPrices(inboundQuoteEntry);
		}
	}

	protected void processProductConfiguration(final String configId, final QuoteEntryModel inboundQuoteEntry)
	{
		ensureBaseSiteIsAvailable(inboundQuoteEntry.getOrder());
		final String productCode = inboundQuoteEntry.getProduct().getCode();
		final String newConfigId = configurationDeepCopyHandler.deepCopyConfiguration(configId, productCode, null, true,
				ProductConfigurationRelatedObjectType.QUOTE_ENTRY);
		final ProductConfigurationModel internalProductConfigModel = productConfigurationPersistenceService
				.getByConfigId(newConfigId);
		internalProductConfigModel.setUser(inboundQuoteEntry.getOrder().getUser());
		inboundQuoteEntry.setProductConfiguration(internalProductConfigModel);
		// required to show the view config link on the quotation page
		createBasicConfigurationInfo(inboundQuoteEntry);
	}

	protected void createBasicConfigurationInfo(final AbstractOrderEntryModel orderEntry)
	{
		final List<AbstractOrderEntryProductInfoModel> configInfos = new ArrayList<>();
		final CPQOrderEntryProductInfoModel configInfo = modelService.create(CPQOrderEntryProductInfoModel.class);
		configInfo.setOrderEntry(orderEntry);
		configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configInfos.add(configInfo);
		orderEntry.setProductInfos(Collections.unmodifiableList(configInfos));
	}

	protected void ensureBaseSiteIsAvailable(final QuoteModel quoteModel)
	{
		if (null == baseSiteService.getCurrentBaseSite())
		{
			baseSiteService.setCurrentBaseSite(quoteModel.getSite(), true);
		}
	}

	protected void processMainEntryPrices(final QuoteEntryModel quoteEntry) {
		if(CollectionUtils.isNotEmpty(quoteEntry.getCpqPricingDetails())) {
			final Optional<CpqPricingDetailModel> priceDetailOptional = quoteEntry.getCpqPricingDetails().stream()
					.filter(priceItem -> FIXED.equals(priceItem.getPricingType())).findFirst();
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

	protected ConfigurationDeepCopyHandler getConfigurationDeepCopyHandler()
	{
		return configurationDeepCopyHandler;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected ProductConfigurationPersistenceService getProductConfigurationPersistenceService()
	{
		return productConfigurationPersistenceService;
	}

}
