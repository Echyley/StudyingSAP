/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.OAuth;

import rx.Observable;


/**
 * Charon Client interface for the pricing endpoint.
 */
@OAuth
public interface PricingClientBase
{
	/**
	 * Create and retrieve the pricing document, which contains the pricing result.
	 *
	 * @param sapPassport
	 *           SAP passport as string
	 * @param input
	 *           pricing input
	 * @return pricing document
	 */
	@POST
	@Produces("application/json")
	@Path("/statelesspricing")
	Observable<PricingDocumentResult> createPricingDocument(@HeaderParam("SAP-PASSPORT") String sapPassport,
			PricingDocumentInput input);
}
