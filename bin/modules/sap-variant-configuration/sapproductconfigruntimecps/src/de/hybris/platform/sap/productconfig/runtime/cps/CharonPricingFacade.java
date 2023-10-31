/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;


/**
 * Facilitates interaction with skywalker pricing service
 */
public interface CharonPricingFacade
{
	/**
	 * Create pricing document
	 *
	 * @param pricingInput
	 *           input document needed to call the pricing service
	 * @return result of the pricing call
	 * @throws PricingEngineException
	 *            indicates error during pricing engine call
	 */
	PricingDocumentResult createPricingDocument(PricingDocumentInput pricingInput) throws PricingEngineException;
}
