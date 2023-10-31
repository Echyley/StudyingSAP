/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionPurpose;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Subtotal;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePriceInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PricingHandler}. Responsible for calling the pricing service through charon, caching
 * price data and calculating delta prices
 */
public class PricingHandlerImpl implements PricingHandler
{
	private static final BigDecimal VALUE_100 = BigDecimal.valueOf(100.00);
	private ContextualConverter<CPSConfiguration, PricingDocumentInput, ConfigurationRetrievalOptions> pricingDocumentInputConverter;
	private CharonPricingFacade charonPricingFacade;
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	private ConfigModelFactory configModelFactory;
	private MasterDataContainerResolver masterDataResolver;
	private CommonI18NService commonI18NService;
	private CharonFacade charonFacade;

	private static final Logger LOG = Logger.getLogger(PricingHandlerImpl.class);

	protected ContextualConverter<CPSConfiguration, PricingDocumentInput, ConfigurationRetrievalOptions> getPricingDocumentInputConverter()
	{
		return pricingDocumentInputConverter;
	}

	/**
	 * @param pricingDocumentInputConverter
	 *           Converter into input for pricing REST service
	 */
	@Required
	public void setPricingDocumentInputConverter(
			final ContextualConverter<CPSConfiguration, PricingDocumentInput, ConfigurationRetrievalOptions> pricingDocumentInputConverter)
	{
		this.pricingDocumentInputConverter = pricingDocumentInputConverter;
	}

	protected CharonPricingFacade getCharonPricingFacade()
	{
		return charonPricingFacade;
	}

	/**
	 * @param charonPricingFacade
	 *           Charon facade, wraps direct REST calls
	 */
	@Required
	public void setCharonPricingFacade(final CharonPricingFacade charonPricingFacade)
	{
		this.charonPricingFacade = charonPricingFacade;
	}

	protected PriceModel createPriceModel(final String currency, final Double valuePrice)
	{
		final PriceModel price = getConfigModelFactory().createInstanceOfPriceModel();
		price.setCurrency(currency);
		price.setPriceValue(BigDecimal.valueOf(valuePrice.doubleValue()));
		return price;
	}


	@Override
	public void fillValuePrices(final MasterDataContext ctxt, final PriceValueUpdateModel updateModel)
	{
		if (ctxt == null)
		{
			throw new IllegalArgumentException("No master data context available.");
		}
		if (updateModel != null)
		{
			final Map<String, CPSValuePriceInfo> valuePrices = getValuePrices(ctxt, updateModel);
			final boolean useDeltaPrices = getPricingConfigurationParameter().showDeltaPrices();
			fillPriceInfos(valuePrices, updateModel, useDeltaPrices);
		}
	}

	protected Map<String, CPSValuePriceInfo> getValuePrices(final MasterDataContext ctxt, final PriceValueUpdateModel updateModel)
	{
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = getSelectedValuePriceAndValuePricesMap(ctxt, updateModel);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		calculateDeltaPrices(pair.getLeft(), mapValuePriceInfo);
		return mapValuePriceInfo;
	}


	protected Pair<BigDecimal, Map<String, CPSValuePriceInfo>> getSelectedValuePriceAndValuePricesMap(final MasterDataContext ctxt,
			final PriceValueUpdateModel updateModel)
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		BigDecimal selectedValuePrice = null;
		final CsticQualifier qualifier = updateModel.getCsticQualifier();
		final String itemKey = qualifier.getInstanceName();
		final String csticId = qualifier.getCsticName();
		final Set<String> possibleValues = getMasterDataResolver().getPossibleValueIds(ctxt.getKbCacheContainer(), csticId);
		final Set<String> specificPossibleValues = getMasterDataResolver().getSpecificPossibleValueIds(ctxt.getKbCacheContainer(),
				itemKey, determineInstanceType(ctxt, itemKey), csticId);
		final boolean multiValued = getMasterDataResolver().getCharacteristic(ctxt.getKbCacheContainer(), csticId).isMultiValued();
		if (!specificPossibleValues.isEmpty())
		{
			final String currency = checkSpecificPossibleValuesForCurrency(specificPossibleValues, ctxt, itemKey, csticId);
			if (currency != null)
			{
				for (final String possibleValue : possibleValues)
				{
					final BigDecimal value = addValueToValuePriceInfoMap(ctxt, mapValuePriceInfo, itemKey, csticId, possibleValue,
							currency);
					selectedValuePrice = updateSelectedValuePrice(multiValued, possibleValue, value, updateModel.getSelectedValues(),
							selectedValuePrice);
				}
			}
		}
		return Pair.of(selectedValuePrice, mapValuePriceInfo);
	}

	protected String checkSpecificPossibleValuesForCurrency(final Set<String> specificPossibleValues, final MasterDataContext ctxt,
			final String itemKey, final String csticId)
	{
		String currency = null;
		final Optional<String> currencyOptional = specificPossibleValues.stream()
				.filter(specificValue -> getCurrencyForValuePrice(ctxt, itemKey, csticId, specificValue) != null).findFirst();
		if (currencyOptional.isPresent())
		{
			currency = getCurrencyForValuePrice(ctxt, itemKey, csticId, currencyOptional.get());
		}
		return currency;
	}

	protected String getCurrencyForValuePrice(final MasterDataContext ctxt, final String itemKey, final String csticId,
			final String specificValue)
	{
		String currency = null;
		final CPSValuePrice valuePrice = getValuePrice(ctxt, itemKey, csticId, specificValue);
		if (valuePrice != null)
		{
			currency = valuePrice.getCurrency();
		}
		return currency;
	}


	protected String determineInstanceType(final MasterDataContext ctxt, final String itemKey)
	{
		final CPSMasterDataKnowledgeBaseContainer masterData = ctxt.getKbCacheContainer();
		final boolean isProduct = masterData.getProducts().containsKey(itemKey);
		final boolean isClassNode = masterData.getClasses().containsKey(itemKey);
		if (isProduct)
		{
			//in case the itemKey is both part of the product list _and_ part of the class list,
			//we return type 'MARA' -> in this case the class probably backs a configurable sub item
			//and does not represent a class node
			if (isClassNode && LOG.isDebugEnabled())
			{
				LOG.debug(itemKey + " is part of the KB as product and as class. Value prices are compiled for product");
			}
			return SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA;
		}
		else if (isClassNode)
		{
			return SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH;
		}
		throw new IllegalStateException("Key not found: " + itemKey);
	}

	protected BigDecimal addValueToValuePriceInfoMap(final MasterDataContext ctxt,
			final Map<String, CPSValuePriceInfo> mapValuePriceInfo, final String productId, final String csticId,
			final String possibleValue, final String currency)
	{
		final CPSValuePriceInfo valuePriceInfo = new CPSValuePriceInfo();

		CPSValuePrice valuePrice = getValuePrice(ctxt, productId, csticId, possibleValue);
		BigDecimal value = null;
		if (valuePrice != null)
		{
			value = valuePrice.getValuePrice();
		}
		else
		{
			value = BigDecimal.ZERO;
			valuePrice = new CPSValuePrice();
			valuePrice.setValuePrice(value);
			valuePrice.setCurrency(currency);
		}
		valuePriceInfo.setValuePrice(valuePrice);

		mapValuePriceInfo.put(possibleValue, valuePriceInfo);
		return value;
	}

	protected BigDecimal updateSelectedValuePrice(final boolean isMultiValued, final String possibleValue,
			final BigDecimal valuePrice, final List<String> selectedValues, final BigDecimal oldSelectedValuePrice)
	{
		if (oldSelectedValuePrice != null)
		{
			return oldSelectedValuePrice;
		}
		if (!selectedValues.contains(possibleValue) || isMultiValued)
		{
			return null;
		}
		return valuePrice;
	}

	protected void calculateDeltaPrices(final BigDecimal selectedValuePrice,
			final Map<String, CPSValuePriceInfo> mapValuePriceInfo)
	{
		for (final CPSValuePriceInfo valuePriceInfo : mapValuePriceInfo.values())
		{
			final CPSValuePrice valuePrice = valuePriceInfo.getValuePrice();
			if (valuePrice != null && valuePrice.getValuePrice() != null)
			{
				final CPSValuePrice deltaPrice = new CPSValuePrice();
				deltaPrice.setCurrency(valuePrice.getCurrency());
				if (selectedValuePrice != null)
				{
					deltaPrice.setValuePrice(valuePrice.getValuePrice().subtract(selectedValuePrice));
					valuePriceInfo.setDeltaPrice(deltaPrice);
				}
				else
				{
					deltaPrice.setValuePrice(valuePrice.getValuePrice());
					deltaPrice.setObsoleteValuePrice(valuePrice.getObsoleteValuePrice());
					valuePriceInfo.setDeltaPrice(deltaPrice);
				}
				//calculate your savings
				if (valuePrice.getObsoleteValuePrice() != null)
				{
					deltaPrice.setObsoleteValuePrice(valuePrice.getObsoleteValuePrice().subtract(valuePrice.getValuePrice()));
				}
			}
		}
	}

	protected CPSValuePrice getValuePrice(final MasterDataContext ctxt, final String productId, final String characteristicId,
			final String valueId)
	{
		final String pricingKey = getMasterDataResolver().getValuePricingKey(ctxt.getKbCacheContainer(), productId,
				characteristicId, valueId);

		if (pricingKey == null)
		{
			return null;
		}

		final CPSMasterDataVariantPriceKey priceKey = new CPSMasterDataVariantPriceKey();
		priceKey.setVariantConditionKey(pricingKey);
		priceKey.setProductId(productId);

		final ProductConfigurationDiscount discount = findDiscount(characteristicId, valueId, ctxt.getDiscountList());
		return applyDiscount(getPrice(ctxt, priceKey), discount);
	}

	protected CPSValuePrice getPrice(final MasterDataContext ctxt, final CPSMasterDataVariantPriceKey priceKey)
	{
		final CPSValuePrice cpsValuePrice = ctxt.getValuePricesMap().get(priceKey);
		return cpsValuePrice == DefaultVariantConditionHandler.NO_PRICE ? null : cpsValuePrice;
	}

	protected ProductConfigurationDiscount findDiscount(final String characteristicId, final String valueId,
			final List<ProductConfigurationDiscount> discountList)
	{
		if (discountList != null)
		{
			final Optional<ProductConfigurationDiscount> optionalDiscount = discountList.stream().filter(
					discount -> (characteristicId.equals(discount.getCsticName()) && valueId.equals(discount.getCsticValueName())))
					.findFirst();
			if (optionalDiscount.isPresent())
			{
				return optionalDiscount.get();
			}
		}
		return null;
	}

	protected CPSValuePrice applyDiscount(final CPSValuePrice cpsValuePrice, final ProductConfigurationDiscount discount)
	{
		CPSValuePrice discountedValuePrice = cpsValuePrice;
		if (null != discount && null != cpsValuePrice)
		{
			final int numFractionDigits = getCommonI18NService().getCurrency(cpsValuePrice.getCurrency()).getDigits().intValue();
			discountedValuePrice = new CPSValuePrice();
			final BigDecimal factor = BigDecimal.ONE.subtract(discount.getDiscount().divide(VALUE_100));
			final BigDecimal newPriceValue = cpsValuePrice.getValuePrice().multiply(factor).setScale(numFractionDigits,
					RoundingMode.HALF_UP);
			discountedValuePrice.setValuePrice(newPriceValue);
			discountedValuePrice.setObsoleteValuePrice(cpsValuePrice.getValuePrice());
			discountedValuePrice.setCurrency(cpsValuePrice.getCurrency());
		}
		return discountedValuePrice;

	}

	protected boolean isValueSelected(final String value, final List<CsticValueModel> cpsValues)
	{

		if (CollectionUtils.isNotEmpty(cpsValues))
		{

			return cpsValues.//
					stream().//
					anyMatch(v -> value.equals(v.getName()));
		}
		return false;
	}

	protected PriceModel createPriceModelFromCPSValue(final CPSValuePrice valuePrice)
	{
		final PriceModel priceModel = getConfigModelFactory().createInstanceOfPriceModel();
		if (valuePrice != null)
		{
			priceModel.setPriceValue(valuePrice.getValuePrice());
			priceModel.setObsoletePriceValue(valuePrice.getObsoleteValuePrice());
			priceModel.setCurrency(valuePrice.getCurrency());
		}
		else
		{
			return PriceModel.NO_PRICE;
		}
		return priceModel;
	}


	protected void fillPriceInfos(final Map<String, CPSValuePriceInfo> valuePrices, final PriceValueUpdateModel updateModel,
			final boolean showDeltaPrices)
	{
		final Map<String, PriceModel> valuePricesMap = new HashMap<>();
		for (final Map.Entry<String, CPSValuePriceInfo> entry : valuePrices.entrySet())
		{
			final String valueName = entry.getKey();
			final CPSValuePriceInfo cpsValuePriceInfo = entry.getValue();
			if (cpsValuePriceInfo != null)
			{
				CPSValuePrice price = null;
				if (showDeltaPrices)
				{
					price = cpsValuePriceInfo.getDeltaPrice();
				}
				else
				{
					price = cpsValuePriceInfo.getValuePrice();
				}
				valuePricesMap.put(valueName, createPriceModelFromCPSValue(price));
			}
			else
			{
				valuePricesMap.put(valueName, createPriceModelFromCPSValue(null));
			}
		}
		updateModel.setValuePrices(valuePricesMap);
		updateModel.setShowDeltaPrices(showDeltaPrices);
	}

	protected PricingConfigurationParameterCPS getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 *           Pricing settings from customizing
	 */
	@Required
	public void setPricingConfigurationParameter(final PricingConfigurationParameterCPS pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return this.configModelFactory;
	}

	/**
	 * @param configModelFactory
	 *           Config model factory, responsible for instantiating service layer models not related to hybris
	 *           persistence
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	@Override
	public void fillValuePrices(final MasterDataContext ctxt, final CsticModel cstic)
	{
		if (cstic != null)
		{
			final PriceValueUpdateModel updateModel = createUpdateModel(cstic, false);
			final Map<String, CPSValuePriceInfo> valuePrices = getValuePrices(ctxt, updateModel);
			fillValuePriceInfos(valuePrices, cstic);
		}

	}

	protected boolean isValuePriceZero(final CPSValuePriceInfo priceInfo)
	{
		return BigDecimal.ZERO.compareTo(priceInfo.getValuePrice().getValuePrice()) == 0;
	}


	protected void fillValuePriceInfos(final Map<String, CPSValuePriceInfo> valuePrices, final CsticModel cstic)
	{
		for (final CsticValueModel value : cstic.getAssignedValues())
		{
			final CPSValuePriceInfo priceInfo = valuePrices.get(value.getName());
			if (priceInfo != null && !isValuePriceZero(priceInfo))
			{

				value.setValuePrice(createPriceModelFromCPSValue(priceInfo.getValuePrice()));
			}
			else
			{
				value.setValuePrice(createPriceModelFromCPSValue(null));
			}
		}

	}

	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException, ConfigurationEngineException
	{
		PricingDocumentResult originalPricingDocumentResult = null;

		// Retrieve CPS configuration
		final CPSConfiguration configuration = getCharonFacade().getConfiguration(configId);
		if (configuration == null)
		{
			throw new IllegalStateException("No CPS configuration for config id " + configId);
		}

		// If discounts exist
		if (options != null && CollectionUtils.isNotEmpty(options.getDiscountList()))
		{
			// Prepare Original Pricing Document Input
			final ConfigurationRetrievalOptions optionsWithoutDiscount = new ConfigurationRetrievalOptions();
			optionsWithoutDiscount.setPricingDate(options.getPricingDate());
			optionsWithoutDiscount.setPricingProduct(options.getPricingProduct());
			final PricingDocumentInput originalPricingDocumentInput = getPricingDocumentInputConverter()
					.convertWithContext(configuration, optionsWithoutDiscount);

			// Prepare Original Pricing Result
			originalPricingDocumentResult = getCharonPricingFacade().createPricingDocument(originalPricingDocumentInput);
		}

		// Prepare Pricing Document Input

		final PricingDocumentInput pricingDocumentInput = getPricingDocumentInputConverter().convertWithContext(configuration,
				options);

		// Prepare Pricing Result
		final PricingDocumentResult pricingDocumentResult = getCharonPricingFacade().createPricingDocument(pricingDocumentInput);

		// Prepare Price summary
		return preparePriceSummary(pricingDocumentResult, originalPricingDocumentResult);
	}

	protected PriceSummaryModel preparePriceSummary(final PricingDocumentResult pricingDocumentResult,
			final PricingDocumentResult originalPricingDocumentResult)
	{
		final PriceSummaryModel priceSummary = getConfigModelFactory().createInstanceOfPriceSummaryModel();
		priceSummary.setBasePrice(getBasePrice(pricingDocumentResult));
		priceSummary.setSelectedOptionsPrice(getSelectedOptionsPrice(pricingDocumentResult));
		priceSummary.setCurrentTotalPrice(getCurrentTotalPrice(pricingDocumentResult));
		priceSummary.setCurrentTotalSavings(getCurrentTotalSavings(pricingDocumentResult, originalPricingDocumentResult));
		return priceSummary;
	}

	protected PriceModel getBasePrice(final PricingDocumentResult pricingDocumentResult)
	{
		PriceModel basePrice = null;
		final SAPProductConfigPricingDetailsMode mode = retrievePricingDetailsMode();

		if (mode != null)
		{
			switch (mode)
			{
				case CONDITIONTYPE:
					basePrice = getPriceForConditionTypes(getPricingConfigurationParameter().getConditionTypesForBasePrice(),
							pricingDocumentResult);
					break;
				case PRICINGSUBTOTAL:
					basePrice = getPriceForSubTotal(getPricingConfigurationParameter().getSubTotalForBasePrice(),
							pricingDocumentResult);
					break;
				case CONDITIONFUNCTION:
				default:
					basePrice = getPriceFromConditionsWithPurpose(getPricingConfigurationParameter().getTargetForBasePrice(),
							pricingDocumentResult);
			}
		}
		else
		{
			basePrice = getPriceFromConditionsWithPurpose(getPricingConfigurationParameter().getTargetForBasePrice(),
					pricingDocumentResult);
		}
		return basePrice;
	}

	protected PriceModel getSelectedOptionsPrice(final PricingDocumentResult pricingDocumentResult)
	{
		PriceModel selectedOptionsPrice = null;
		final SAPProductConfigPricingDetailsMode mode = retrievePricingDetailsMode();
		if (mode != null)
		{
			switch (mode)
			{
				case CONDITIONTYPE:
					selectedOptionsPrice = getPriceForConditionTypes(
							getPricingConfigurationParameter().getConditionTypesForSelectedOptions(), pricingDocumentResult);
					break;
				case PRICINGSUBTOTAL:
					selectedOptionsPrice = getPriceForSubTotal(getPricingConfigurationParameter().getSubTotalForSelectedOptions(),
							pricingDocumentResult);
					break;
				case CONDITIONFUNCTION:
				default:
					selectedOptionsPrice = getPriceFromConditionsWithPurpose(
							getPricingConfigurationParameter().getTargetForSelectedOptions(), pricingDocumentResult);
			}
		}
		else
		{
			selectedOptionsPrice = getPriceFromConditionsWithPurpose(
					getPricingConfigurationParameter().getTargetForSelectedOptions(), pricingDocumentResult);
		}
		return selectedOptionsPrice;
	}


	protected PriceModel getPriceFromConditionsWithPurpose(final String pricingKey,
			final PricingDocumentResult pricingDocumentResult)
	{
		ConditionPurpose purposeFound = null;
		if (pricingDocumentResult == null || pricingDocumentResult.getConditionsWithPurpose() == null || pricingKey == null)
		{
			return PriceModel.NO_PRICE;
		}
		else
		{
			for (final ConditionPurpose purpose : pricingDocumentResult.getConditionsWithPurpose())
			{
				if (pricingKey.equals(purpose.getPurpose()))
				{
					purposeFound = purpose;
					break;
				}
			}
		}
		if (purposeFound == null)
		{
			return PriceModel.NO_PRICE;
		}

		return createPriceModel(pricingDocumentResult.getDocumentCurrencyUnit(), purposeFound.getValue());
	}

	protected PriceModel getCurrentTotalPrice(final PricingDocumentResult pricingDocumentResult)
	{
		if (pricingDocumentResult == null)
		{
			return PriceModel.NO_PRICE;
		}
		return createPriceModel(pricingDocumentResult.getDocumentCurrencyUnit(), pricingDocumentResult.getNetValue());
	}

	protected PriceModel getOriginalCurrentTotalPrice(final PricingDocumentResult originalPricingDocumentResult)
	{
		if (originalPricingDocumentResult == null)
		{
			return PriceModel.NO_PRICE;
		}
		return createPriceModel(originalPricingDocumentResult.getDocumentCurrencyUnit(),
				originalPricingDocumentResult.getNetValue());
	}

	protected PriceModel getCurrentTotalSavings(final PricingDocumentResult pricingDocumentResult,
			final PricingDocumentResult originalPricingDocumentResult)
	{
		final PriceModel obsoleteCurrentTotalPrice = getOriginalCurrentTotalPrice(originalPricingDocumentResult);
		if (obsoleteCurrentTotalPrice == null || obsoleteCurrentTotalPrice == PriceModel.NO_PRICE)
		{
			return PriceModel.NO_PRICE;
		}

		final PriceModel currentTotalPrice = getCurrentTotalPrice(pricingDocumentResult);

		final BigDecimal savings = obsoleteCurrentTotalPrice.getPriceValue().subtract(currentTotalPrice.getPriceValue());

		final PriceModel price = getConfigModelFactory().createInstanceOfPriceModel();
		price.setCurrency(currentTotalPrice.getCurrency());
		price.setPriceValue(savings);
		return price;
	}

	protected SAPProductConfigPricingDetailsMode retrievePricingDetailsMode()
	{
		return getPricingConfigurationParameter().getPricingDetailsMode();
	}

	protected PriceModel getPriceForConditionTypes(final Collection<String> conditionTypes,
			final PricingDocumentResult pricingDocumentResult)
	{
		if (pricingDocumentResult != null && CollectionUtils.isNotEmpty(pricingDocumentResult.getConditions())
				&& CollectionUtils.isNotEmpty(conditionTypes))
		{
			BigDecimal value = BigDecimal.ZERO;
			boolean conditionTypeFound = false;
			for (final ConditionResult condition : pricingDocumentResult.getConditions())
			{
				if (!condition.isStatistical() && condition.getInactiveFlag().isBlank()
						&& conditionTypes.contains(condition.getConditionType()))
				{
					value = value.add(BigDecimal.valueOf(condition.getConditionValue()));
					conditionTypeFound = true;
				}
			}
			if (conditionTypeFound)
			{
				return createPriceModel(pricingDocumentResult.getDocumentCurrencyUnit(), value);
			}
		}
		return PriceModel.NO_PRICE;
	}

	protected PriceModel getPriceForSubTotal(final String subTotalFlag, final PricingDocumentResult pricingDocumentResult)
	{
		Subtotal subTotalFound = null;
		if (pricingDocumentResult != null && CollectionUtils.isNotEmpty(pricingDocumentResult.getSubTotals())
				&& StringUtils.isNotEmpty(subTotalFlag))
		{
			for (final Subtotal subTotal : pricingDocumentResult.getSubTotals())
			{
				if (subTotalFlag.equals(subTotal.getFlag()))
				{
					subTotalFound = subTotal;
					break;
				}
			}
		}
		if (subTotalFound == null)
		{
			return PriceModel.NO_PRICE;
		}
		return createPriceModel(pricingDocumentResult.getDocumentCurrencyUnit(), new BigDecimal(subTotalFound.getValue()));
	}

	protected PriceModel createPriceModel(final String currency, final BigDecimal valuePrice)
	{
		final PriceModel price = getConfigModelFactory().createInstanceOfPriceModel();
		price.setCurrency(currency);
		price.setPriceValue(valuePrice);
		return price;
	}

	protected MasterDataContainerResolver getMasterDataResolver()
	{
		return masterDataResolver;
	}

	@Required
	public void setMasterDataResolver(final MasterDataContainerResolver masterDataResolver)
	{
		this.masterDataResolver = masterDataResolver;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CharonFacade getCharonFacade()
	{
		return charonFacade;
	}

	@Required
	public void setCharonFacade(final CharonFacade charonFacade)
	{
		this.charonFacade = charonFacade;
	}

}
