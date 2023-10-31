/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.VariantConditionHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;


public class DefaultVariantConditionHandler implements VariantConditionHandler
{
	private static final Logger LOG = Logger.getLogger(DefaultVariantConditionHandler.class);
	static final CPSValuePrice NO_PRICE = new CPSValuePrice();
	private final CharonPricingFacade charonPricingFacade;
	private final CPSCache cache;
	private final MasterDataContainerResolver masterDataResolver;
	private final ContextualConverter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput, MasterDataContext> pricingDocumentInputKBConverter;
	private final Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> pricesMapConverter;

	public DefaultVariantConditionHandler(final CharonPricingFacade charonPricingFacade, final CPSCache cache,
			final MasterDataContainerResolver masterDataResolver,
			final ContextualConverter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput, MasterDataContext> pricingDocumentInputKBConverter,
			final Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> pricesMapConverter)
	{
		super();
		this.charonPricingFacade = charonPricingFacade;
		this.cache = cache;
		this.masterDataResolver = masterDataResolver;
		this.pricesMapConverter = pricesMapConverter;
		this.pricingDocumentInputKBConverter = pricingDocumentInputKBConverter;
	}


	@Override
	public void preFetchValuePrices(final MasterDataContext ctxt, final List<PriceValueUpdateModel> updateModels,
			final boolean onlySelectedValues) throws PricingEngineException
	{
		logDebugIfEnabled(LOG, () -> String.format("Prefetching variant conditions for %d cstic", updateModels.size()));
		populateValuePricesMapFromCache(ctxt);
		determineRequestedValuePricingKeys(ctxt, updateModels, onlySelectedValues);
		if (!ctxt.getRequestedPricesByProductMap().isEmpty())
		{
			logDebugIfEnabled(LOG, () -> String.format("Fetching %d additional variant conditions",
					ctxt.getRequestedPricesByProductMap().values().stream().mapToInt(List::size).sum()));
			logDebugIfEnabled(LOG, () -> "variant conditions: " + ctxt.getRequestedPricesByProductMap().values().toString());

			mergeFetchedConditions(ctxt, retrieveVariantConditions(ctxt));
			cachePricesMap(ctxt);
		}
		else
		{
			logDebugIfEnabled(LOG, () -> "All requested variant conditions are already known, nothing to fetch!");
		}
	}


	protected void logDebugIfEnabled(final Logger log, final Supplier<String> logFn)
	{
		if (log.isDebugEnabled())
		{
			log.debug(logFn.get());
		}
	}


	protected void determineRequestedValuePricingKeys(final MasterDataContext ctxt, final List<PriceValueUpdateModel> updateModels,
			final boolean onlySelectedValues)
	{
		// convert UpdateModel to CPSMasterDataVariantPriceKey
		Stream<CPSMasterDataVariantPriceKey> requiredKeysStream = updateModels.stream()
				.flatMap(updateModel -> mapUpdateModelToPossibleValueList(ctxt, updateModel).stream()
						.filter(possibleValue -> keepValue(onlySelectedValues, updateModel, possibleValue))
						.map(possibleValue -> mapPossibleValueToPricingKey(ctxt, updateModel.getCsticQualifier(), possibleValue)));

		// filter already fetched conditions keys, as well as non existing ones
		requiredKeysStream = requiredKeysStream.filter(
				pricingKey -> null != pricingKey.getVariantConditionKey() && !ctxt.getValuePricesMap().containsKey(pricingKey));

		// group by product id
		final Map<String, List<CPSMasterDataVariantPriceKey>> requiredKeysMap = requiredKeysStream
				.collect(Collectors.toMap(CPSMasterDataVariantPriceKey::getProductId, Collections::singletonList, this::mergeLists));

		ctxt.setRequestedPricesByProductMap(requiredKeysMap);
	}

	private List<CPSMasterDataVariantPriceKey> mergeLists(final List<CPSMasterDataVariantPriceKey> list1,
			final List<CPSMasterDataVariantPriceKey> list2)
	{
		return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
	}

	protected boolean keepValue(final boolean onlySelectedValues, final PriceValueUpdateModel updateModel, final String possibleValue)
	{
		return !onlySelectedValues || updateModel.getSelectedValues().contains(possibleValue);
	}

	protected Set<String> mapUpdateModelToPossibleValueList(final MasterDataContext ctxt, final PriceValueUpdateModel updateModel)
	{
		return getMasterDataResolver().getPossibleValueIds(ctxt.getKbCacheContainer(),
				updateModel.getCsticQualifier().getCsticName());
	}

	protected CPSMasterDataVariantPriceKey mapPossibleValueToPricingKey(final MasterDataContext ctxt,
			final CsticQualifier csticQualifier, final String possibleValue)
	{
		final String conditionKey = getMasterDataResolver().getValuePricingKey(ctxt.getKbCacheContainer(),
				csticQualifier.getInstanceName(), csticQualifier.getCsticName(), possibleValue);
		final CPSMasterDataVariantPriceKey pricingKey = new CPSMasterDataVariantPriceKey();
		pricingKey.setProductId(csticQualifier.getInstanceName());
		pricingKey.setVariantConditionKey(conditionKey);
		return pricingKey;
	}


	protected void populateValuePricesMapFromCache(final MasterDataContext ctxt)
	{
		final String kbId = ctxt.getKbCacheContainer().getHeaderInfo().getId().toString();
		Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap = getCache().getValuePricesMap(kbId,
				ctxt.getPricingProduct());
		if (null == valuePricesMap)
		{
			valuePricesMap = new HashMap<>();
		}
		ctxt.setValuePricesMap(valuePricesMap);
	}

	protected void cachePricesMap(final MasterDataContext ctxt)
	{
		final String kbId = ctxt.getKbCacheContainer().getHeaderInfo().getId().toString();
		getCache().setValuePricesMap(kbId, ctxt.getPricingProduct(), ctxt.getValuePricesMap());
	}

	protected Map<CPSMasterDataVariantPriceKey, CPSValuePrice> retrieveVariantConditions(final MasterDataContext ctxt)
			throws PricingEngineException
	{
		//We prepare here a pricing document input not for the "real" pricing call of a configuration runtime state,
		//but for a "simulated" KB based call.
		//The purpose of this call is to retrieve values of surcharges that are assigned to characteristic values of the related KB products.
		//We use these values for calculation of the delta prices.
		//In this case productId is used as "itemId"
		//(this is OK since even if a product is used several time inside a KB, the surcharge values for the product are always the same)
		//and quantity is always set to 1 independent from the BOM quantity.
		final PricingDocumentInput pricingInput = getPricingDocumentInputKBConverter()
				.convertWithContext(ctxt.getKbCacheContainer(), ctxt);
		final PricingDocumentResult pricingResult = getCharonPricingFacade().createPricingDocument(pricingInput);
		return getPricesMapConverter().convert(pricingResult);
	}

	protected void mergeFetchedConditions(final MasterDataContext ctxt,
			final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> newVariantConditions)
	{
		ctxt.getValuePricesMap().putAll(newVariantConditions);
		ctxt.getRequestedPricesByProductMap().values().stream().flatMap(List::stream)
				.filter(pricingKey -> !ctxt.getValuePricesMap().containsKey(pricingKey))
				.forEach(pricingKey -> ctxt.getValuePricesMap().put(pricingKey, NO_PRICE));
	}

	protected CPSCache getCache()
	{
		return cache;
	}

	protected ContextualConverter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput, MasterDataContext> getPricingDocumentInputKBConverter()
	{
		return pricingDocumentInputKBConverter;
	}

	protected Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> getPricesMapConverter()
	{
		return pricesMapConverter;
	}

	protected CharonPricingFacade getCharonPricingFacade()
	{
		return charonPricingFacade;
	}

	protected MasterDataContainerResolver getMasterDataResolver()
	{
		return masterDataResolver;
	}

}
