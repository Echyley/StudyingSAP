/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;

import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Provides mapping from {@link QuoteModel} to {@link SAPCPQOutboundQuoteModel}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */
public interface SapCpqCpiQuoteMapperService<SOURCE extends QuoteModel, TARGET extends SAPCPQOutboundQuoteModel> {

	
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Model
	 * @param target
	 *           SAP CPQ Outbound Quote Model
	 */
	void map(SOURCE source, TARGET target);
	
}



