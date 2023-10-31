/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;

import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Provides mapping from {@link QuoteModel} to {@link SAPCPQOutboundQuoteStatusModel}.
 *
 * @param <SOURCE> the parameter of the class
 * @param <TARGET> the parameter of the class
 */
public interface SapCpqCpiQuoteStatusMapperService<SOURCE extends QuoteModel, TARGET extends SAPCPQOutboundQuoteStatusModel>{

	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Model
	 * @param target
	 *           SAP CPQ Outbound Quote Status Model
	 */
	void map(SOURCE source, TARGET target);
	
}
