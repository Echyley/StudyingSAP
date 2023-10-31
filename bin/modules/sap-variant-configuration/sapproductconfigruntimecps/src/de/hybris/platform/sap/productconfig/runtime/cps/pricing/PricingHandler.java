/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing;

import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.Collections;
import java.util.stream.Collectors;


/**
 * Obtains and exposes pricing information from CPS
 */
public interface PricingHandler
{

	/**
	 * Retrieves the price summary
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param options
	 *           configuration retrieval options
	 *
	 * @return price summary
	 * @throws PricingEngineException
	 * @throws ConfigurationEngineException
	 */
	PriceSummaryModel getPriceSummary(String configId, ConfigurationRetrievalOptions options)
			throws PricingEngineException, ConfigurationEngineException;

	/**
	 * Attaches value prices to the cstic model
	 *
	 * @param ctxt
	 *           master data context
	 * @param cstic
	 *           cstic model
	 * @throws PricingEngineException
	 */
	void fillValuePrices(MasterDataContext ctxt, CsticModel cstic) throws PricingEngineException;

	/**
	 * Attaches absolute value prices or delta prices for all passed PriceValueUpdateModels dependent setting in
	 * backoffice
	 *
	 * @param ctxt
	 *           master data context
	 *
	 * @param updateModel
	 *           represents a characteristic to be updated with absolute value prices or delta prices for every possible
	 *           value
	 * @throws PricingEngineException
	 */
	void fillValuePrices(MasterDataContext ctxt, PriceValueUpdateModel updateModel) throws PricingEngineException;


	/**
	 * Creates an update model for the given cstic
	 *
	 * @param cstic
	 *           source
	 * @return update model
	 */
	default PriceValueUpdateModel createUpdateModel(final CsticModel cstic, final boolean withSelectedValues)
	{
		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		final CsticQualifier cq = new CsticQualifier();
		cq.setInstanceName(cstic.getInstanceName());
		cq.setCsticName(cstic.getName());
		updateModel.setCsticQualifier(cq);
		if (withSelectedValues)
		{
			updateModel
					.setSelectedValues(cstic.getAssignedValues().stream().map(CsticValueModel::getName).collect(Collectors.toList()));
		}
		else
		{
			updateModel.setSelectedValues(Collections.emptyList());
		}

		return updateModel;
	}
}
