/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.VariantConditionHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facilitates pricing calls for CPS engine
 */
public class CPSPricingProvider implements PricingProvider
{
	private PricingHandler pricingHandler;
	private VariantConditionHandler variantConditionHandler;
	private ConfigurationMasterDataService masterDataService;

	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException, ConfigurationEngineException
	{
		return getPricingHandler().getPriceSummary(configId, options);
	}

	@Override
	public boolean isActive()
	{
		return true;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId) throws PricingEngineException
	{
		fillValuePrices(updateModels, kbId, null);
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId,
			final ConfigurationRetrievalOptions options) throws PricingEngineException
	{
		final MasterDataContext ctxt = prepareMasterDataContext(kbId, options);
		getVariantConditionHandler().preFetchValuePrices(ctxt, updateModels, false);
		for (final PriceValueUpdateModel updateModel : updateModels)
		{
			getPricingHandler().fillValuePrices(ctxt, updateModel);
		}
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel) throws PricingEngineException
	{
		fillValuePrices(configModel, null);
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		final String kbId = configModel.getKbId();
		final MasterDataContext ctxt = prepareMasterDataContext(kbId, options);
		getVariantConditionHandler().preFetchValuePrices(ctxt, createUpdateModelList(configModel.getRootInstance()),true);
		final InstanceModel rootInstance = configModel.getRootInstance();
		fillValuePricesForInstance(rootInstance, ctxt);
	}

	protected List<PriceValueUpdateModel> createUpdateModelList(final InstanceModel rootInstance)
	{
		return createUpdateModelStream(rootInstance).collect(Collectors.toList());
	}

	protected Stream<PriceValueUpdateModel> createUpdateModelStream(final InstanceModel instance)
	{
		final Stream<PriceValueUpdateModel> csticStream = instance.getCstics().stream().map(cstic -> getPricingHandler().createUpdateModel(cstic, true));
		final Stream<PriceValueUpdateModel> subInstanceStream = instance.getSubInstances().stream().flatMap(this::createUpdateModelStream);
		return Stream.concat(csticStream, subInstanceStream);
	}

	protected MasterDataContext prepareMasterDataContext(final String kbId, final ConfigurationRetrievalOptions options)
	{
		final MasterDataContext ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(getMasterDataService().getMasterData(kbId));
		if (null == options || null == options.getDiscountList())
		{
			ctxt.setDiscountList(Collections.emptyList());
		}
		else
		{
			ctxt.setDiscountList(options.getDiscountList());
		}
		if (null != options)
		{
			ctxt.setPricingProduct(options.getPricingProduct());
		}
		return ctxt;
	}

	protected void fillValuePricesForInstance(final InstanceModel instance, final MasterDataContext ctxt)
			throws PricingEngineException
	{
		for (final CsticModel cstic : instance.getCstics())
		{
			getPricingHandler().fillValuePrices(ctxt, cstic);
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			fillValuePricesForInstance(subInstance, ctxt);
		}
	}

	protected PricingHandler getPricingHandler()
	{
		return pricingHandler;
	}

	/**
	 * @param pricingHandler
	 *           Bean that handles REST call, delta price calculation and caching
	 */
	@Required
	public void setPricingHandler(final PricingHandler pricingHandler)
	{
		this.pricingHandler = pricingHandler;
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	/**
	 * @param masterDataService
	 *           master data service
	 */
	@Required
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

	protected VariantConditionHandler getVariantConditionHandler()
	{
		return variantConditionHandler;
	}

	@Required
	public void setVariantConditionHandler(final VariantConditionHandler variantConditionHandler)
	{
		this.variantConditionHandler = variantConditionHandler;
	}

}
