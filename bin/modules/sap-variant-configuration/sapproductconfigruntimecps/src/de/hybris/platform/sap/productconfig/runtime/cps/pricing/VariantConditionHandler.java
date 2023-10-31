/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing;

import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;


/**
 * Handles reading and caching of variant conditions required for value prices.
 */
public interface VariantConditionHandler
{

	/**
	 * Prefetches all pricing conditions for calculating value prices and store them in the provided master data context.
	 *
	 * @param ctxt master data context
	 * @param updateModels
	 * @param onlySelectedValues
	 * @param requestedValuePrices list of update models
	 * @throws PricingEngineException
	 */
	void preFetchValuePrices(final MasterDataContext ctxt, List<PriceValueUpdateModel> updateModels, boolean onlySelectedValues) throws PricingEngineException;

}
